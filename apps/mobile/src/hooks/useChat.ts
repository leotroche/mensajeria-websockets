import { useEffect, useState } from 'react'

import { env } from '@/config/env'
import { useStompClient } from '@/hooks/useStompClient'
import { useAuthStore } from '@/store/useUserStore'
import { MessageType } from '@/types/types'

export function useChat() {
  const token = useAuthStore((s) => s.token)
  const user = useAuthStore((s) => s.user)

  const { lastMessage, sendMessage: publish } = useStompClient({
    brokerURL: `${env.WEB_SOCKET_BASE_URL}/chats`,
  })

  const [messages, setMessages] = useState<MessageType[]>([])

  // Cuando llega mensaje del server
  useEffect(() => {
    if (!lastMessage) return
    setMessages((prev) => [...prev, lastMessage])
  }, [lastMessage])

  // Si se hace logout, limpiar mensajes
  useEffect(() => {
    if (!token) {
      setMessages([])
    }
  }, [token])

  function sendMessage(text: string) {
    if (!user || !token) return

    // Optimistic update simple (esto ya es suficiente)
    setMessages((prev) => [
      ...prev,
      {
        id: crypto.randomUUID(),
        text,
        time: new Date().toISOString(),
        status: 'sent',
      },
    ])

    publish({
      username: user.username,
      message: text,
    })
  }

  return { messages, sendMessage }
}
