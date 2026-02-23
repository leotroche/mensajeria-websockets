import { useEffect, useState } from 'react'

import { env } from '@/config/env'
import { useDatabase } from '@/hooks/useDatabase'
import { useStompClient } from '@/hooks/useStompClient'
import { MessageType } from '@/types/types'

export function useChat(token: string) {
  const { lastMessage, sendMessage } = useStompClient({
    brokerURL: `${env.WEB_SOCKET_BASE_URL}/chats`,
    token,
  })

  const { getStoredMessages, saveMessage } = useDatabase({ dbName: 'ChatDatabase' })

  const [messages, setMessages] = useState<MessageType[]>([])

  useEffect(() => {
    getStoredMessages().then(setMessages)
  }, [getStoredMessages])

  useEffect(() => {
    if (lastMessage) {
      saveMessage(lastMessage)
    }
  }, [lastMessage, saveMessage])

  const dispatchMessage = ({ username, message }: { username: string; message: string }) => {
    sendMessage({ username, message })
  }

  return {
    messages,
    sendMessage: dispatchMessage,
  }
}
