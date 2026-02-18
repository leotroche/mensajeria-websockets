import { useEffect, useState } from 'react'

import { MessageType } from '@/types/types'

import { useDatabase } from './useDatabase'
import { useWebSocket } from './useWebSocket'

export function useChat() {
  const { getMessages, saveMessage, clearMessages } = useDatabase({ dbName: 'ChatDatabase' })
  const { lastMessage, sendMessage } = useWebSocket({ url: 'ws://localhost:8080/chats' })
  const [messages, setMessages] = useState<MessageType[]>([])

  // Cargar mensajes al inicio
  useEffect(() => {
    getMessages().then(setMessages)
  }, [])

  // Actualizar mensajes cuando llega uno nuevo por WebSocket
  useEffect(() => {
    if (lastMessage) {
      const message = lastMessage as MessageType
      saveMessage(message)
      setMessages((prev) => [...prev, message])
    }
  }, [lastMessage])

  const send = (text: string) => {
    const message: MessageType = {
      id: crypto.randomUUID(),
      text,
      time: new Date().toISOString(),
      status: 'sent',
    }

    sendMessage(message)
    saveMessage(message)
    setMessages((prev) => [...prev, message])
  }

  const clear = async () => {
    await clearMessages()
    setMessages([])
  }

  return {
    messages,
    send,
    clear,
  }
}
