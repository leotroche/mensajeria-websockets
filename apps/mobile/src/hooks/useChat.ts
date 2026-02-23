import { useEffect, useState } from 'react'

import { useStompClient } from '@/hooks/useStompClient'
import { useAuthStore } from '@/store/useAuthStore'
import { MessageType } from '@/types/types'

export function useChat() {
  const user = useAuthStore((s) => s.user)
  const token = useAuthStore((s) => s.token)

  const { lastMessage, publish } = useStompClient({ brokerURL: 'ws://localhost:8080/chats' })

  const [optimisticMessages, setOptimisticMessages] = useState<MessageType[]>([])

  useEffect(() => {
    if (!lastMessage) return
    console.log('Received message:', lastMessage)
    setOptimisticMessages((prev) => [...prev, lastMessage])
  }, [lastMessage])

  function sendMessage(text: string) {
    if (!user || !token) return

    setOptimisticMessages((prev) => [
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

  return { messages: optimisticMessages, sendMessage }
}
