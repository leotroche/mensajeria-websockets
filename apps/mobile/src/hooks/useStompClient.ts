import { Client, IMessage } from '@stomp/stompjs'
import { useCallback, useEffect, useRef, useState } from 'react'

import { useUserStore } from '@/store/useUserStore'
import { MessageType } from '@/types/types'

interface UseStompClientProps {
  brokerURL: string
}

export function useStompClient({ brokerURL }: UseStompClientProps) {
  const [lastMessage, setLastMessage] = useState<MessageType | null>(null)
  const clientRef = useRef<Client>(null)

  const token = useUserStore((state) => state.token)

  useEffect(() => {
    if (!token) return

    const client = new Client({
      brokerURL,
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,

      debug: (str) => {
        console.log('STOMP:', str)
      },
      onConnect: () => {
        client.subscribe(
          '/topic/canal1',
          (msg: IMessage) => {
            setLastMessage(JSON.parse(msg.body))
          },
          { Authorization: `Bearer ${token}` },
        )
      },
      onStompError: (frame) => {
        console.error('❌ Error en STOMP:', frame.headers['message'], frame.body)
      },
    })

    client.activate()
    clientRef.current = client

    return () => {
      client.deactivate()
    }
  }, [brokerURL, token])

  const sendMessage = useCallback(
    ({ username, message }: { username: string; message: string }) => {
      const client = clientRef.current

      const payload = {
        senderName: username,
        body: message,
      }

      if (client?.connected) {
        client.publish({
          destination: '/app/chat1',
          body: JSON.stringify(payload),
        })
      }
    },
    [],
  )

  return {
    lastMessage,
    sendMessage,
  }
}
