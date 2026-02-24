import { Client, IMessage } from '@stomp/stompjs'
import { useEffect, useRef, useState } from 'react'

import { useAuthStore } from '@/store/useAuthStore'
import { MessageType } from '@/types/types'

interface UseStompClientProps {
  brokerURL: string
}

export function useStompClient({ brokerURL }: UseStompClientProps) {
  const user = useAuthStore((s) => s.user)
  const token = useAuthStore((s) => s.token)

  const [lastMessage, setLastMessage] = useState<MessageType | null>(null)
  const clientRef = useRef<Client>(null)

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

  const publish = (message: string) => {
    const client = clientRef.current

    const payload = {
      senderName: user?.username,
      body: message,
    }

    if (client?.connected) {
      client.publish({
        destination: '/app/chat1',
        body: JSON.stringify(payload),
      })
    }
  }

  return {
    lastMessage,
    publish,
  }
}
