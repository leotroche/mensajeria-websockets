import Dexie from 'dexie'
import { useEffect, useRef } from 'react'

import { MessageType } from '../types/types'

export function useDatabase({ dbName = 'MyDatabase' }) {
  const dbRef = useRef<Dexie | null>(null)

  useEffect(() => {
    const db = new Dexie(dbName)

    db.version(1).stores({
      messages: 'id, text, time, status',
    })

    dbRef.current = db
  }, [dbName])

  const getDb = () => {
    if (!dbRef.current) {
      throw new Error('Database not initialized')
    }
    return dbRef.current
  }

  const getStoredMessages = async () => {
    const db = getDb()
    return await db.table('messages').orderBy('time').toArray()
  }

  const saveMessage = async ({ id, text, time, status }: MessageType) => {
    const db = getDb()
    await db.table('messages').put({ id, text, time, status })
  }

  return {
    getStoredMessages,
    saveMessage,
  }
}
