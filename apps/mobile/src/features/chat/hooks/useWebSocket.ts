import { useEffect, useRef, useState } from 'react'

type UseWebSocketProps = {
  url: string
}

interface OnOpenMessage {
  type: 'subscribe'
  channel: string
  headers: {
    Authorization: `Bearer ${string}`
  }
}

const onOpenMessage: OnOpenMessage = {
  type: 'subscribe',
  channel: '/topic/canal1',
  headers: {
    Authorization: 'Bearer token',
  },
}

const fetcher = (url: string) =>
  fetch(url, {
    method: 'POST',
    body: JSON.stringify({
      username: 'pepe',
      password: 'pepe1234',
    }),
  }).then((res) => res.json())

export function useAuth() {
  const [token, setToken] = useState('')

  useEffect(() => {
    const pepe = fetcher('http://localhost:8080/signin').then(({ token }) => {
      setToken(token)
      return token
    })
    console.log('pepe', pepe)
  }, [])

  return token
}

export function useWebSocket({ url }: UseWebSocketProps) {
  const wsRef = useRef<WebSocket | null>(null)
  const [lastMessage, setLastMessage] = useState<unknown>(null)
  const token = useAuth()

  useEffect(() => {
    const ws = new WebSocket(url)
    wsRef.current = ws

    ws.onopen = () => {
      console.log('WebSocket connected')
      ws.send(JSON.stringify(onOpenMessage))
    }

    ws.onmessage = (event) => {
      const response = JSON.parse(event.data)
      console.log('WebSocket message received:', response)
      setLastMessage(response)
    }

    ws.onclose = () => {
      console.log('WebSocket disconnected')
    }

    ws.onerror = (error) => {
      console.error('WebSocket error:', error)
    }

    return () => {
      ws.close()
    }
  }, [url])

  const sendMessage = (data: unknown) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(
        JSON.stringify({
          data,
          user: 'pepe',
        }),
      )
    }
  }

  return {
    lastMessage,
    sendMessage,
  }
}
