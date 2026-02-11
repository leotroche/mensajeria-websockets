import { getDb } from '../../../config/db'
import { MessageType } from '../../../types/types'

export function useDatabase() {
  const saveMessage = async ({ id, text, time, status }: MessageType) => {
    const db = await getDb()

    const result = await db.runAsync(
      `
      INSERT INTO message (id, text, time, status)
      VALUES (?, ?, ?, ?)
      `,
      [id, text, time, status],
    )

    return {
      success: result.changes > 0,
    }
  }

  const getMessages = async () => {
    const db = await getDb()

    return await db.getAllAsync<MessageType>(`
      SELECT * FROM message
      ORDER BY time DESC
    `)
  }

  return { saveMessage, getMessages }
}

