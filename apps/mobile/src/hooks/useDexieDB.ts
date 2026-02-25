import Dexie from 'dexie'
import { useEffect, useState } from 'react'

import { MessageType } from '@/types/types'

class ChatDB extends Dexie {
  messages!: Dexie.Table<MessageType, string>
  constructor(dbName: string) {
    super(dbName)
    this.version(1).stores({ messages: 'userId, id, message, time' })
  }
}

const db = new ChatDB('ChatDatabase')

export function useDexieDB() {
  const [messages, setMessages] = useState<MessageType[]>([])

  useEffect(() => {
    db.messages.orderBy('time').toArray().then(setMessages)
  }, [])

  const addMessage = async (msg: MessageType) => {
    await db.messages.put(msg)
    setMessages((prev) => [...prev, msg])
  }

  const removeMessage = async (id: string) => {
    await db.messages.delete(id)
    setMessages((prev) => prev.filter((m) => m.id !== id))
  }

  const clearAll = async () => {
    await db.messages.clear()
    setMessages([])
  }

  return { messages, addMessage, removeMessage, clearAll }
}
