// oxlint-disable eslint-plugin-react-hooks/exhaustive-deps

import { useEffect } from 'react'

import { useStompClient } from '@/hooks/useStompClient'
import { useAuthStore } from '@/store/useAuthStore'

import { useDexieDB } from './useDexieDB'

export function useChat() {
  const user = useAuthStore((s) => s.user)

  if (!user) {
    throw new Error('Usuario no autenticado')
  }

  const { lastMessage, publish } = useStompClient({ brokerURL: 'ws://localhost:8080/chats' })
  const { messages, addMessage } = useDexieDB()

  useEffect(() => {
    if (!lastMessage) return

    console.log('==> Nuevo mensaje recibido:', lastMessage)

    addMessage({
      ...lastMessage,
      // userId: user.userId,
      id: crypto.randomUUID(),
      time: new Date().toLocaleTimeString(),
    })
  }, [lastMessage])

  return { messages, sendMessage: publish }
}
