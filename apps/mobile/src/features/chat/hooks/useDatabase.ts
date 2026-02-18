import Dexie from 'dexie'
import { useEffect, useRef } from 'react'

import { MessageType } from '../../../types/types'

export function useDatabase({ dbName = 'MyDatabase' }) {
  const dbRef = useRef<Dexie | null>(null)

  useEffect(() => {
    const db = new Dexie(dbName)

    db.version(1).stores({
      messages: 'id, text, time, status',
    })

    dbRef.current = db
  }, [])

  const getDb = () => {
    if (!dbRef.current) {
      throw new Error('Database not initialized')
    }
    return dbRef.current
  }

  const getMessages = async () => {
    const db = getDb()
    return await db.table('messages').orderBy('time').toArray()
  }

  const saveMessage = async ({ id, text, time, status }: MessageType) => {
    const db = getDb()
    await db.table('messages').add({ id, text, time, status })
  }

  const clearMessages = async () => {
    const db = getDb()
    await db.table('messages').clear()
  }

  return {
    getMessages,
    saveMessage,
    clearMessages,
  }
}

// export function useDatabase() {
//   const saveMessage = async ({ id, text, time, status }: MessageType) => {
//     const db = await getDb()

//     const result = await db.runAsync(
//       `
//       INSERT INTO message (id, text, time, status)
//       VALUES (?, ?, ?, ?)
//       `,
//       [id, text, time, status],
//     )

//     return {
//       success: result.changes > 0,
//     }
//   }

//   const getMessages = async () => {
//     const db = await getDb()

//     return await db.getAllAsync<MessageType>(`
//       SELECT * FROM message
//       ORDER BY time DESC
//     `)
//   }

//   return { saveMessage, getMessages }
// }

// -- web hook --
