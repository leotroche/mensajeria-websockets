// database.ts
import * as SQLite from 'expo-sqlite'
import { MessageType } from '../types/types'

let db: SQLite.SQLiteDatabase | null = null

export async function getDb() {
  if (db) return db

  db = await SQLite.openDatabaseAsync('chat_v8.db')

  await db.execAsync(`
    CREATE TABLE IF NOT EXISTS message (
      id TEXT PRIMARY KEY NOT NULL,
      text TEXT NOT NULL,
      time TEXT NOT NULL,
      status TEXT NOT NULL
    );
  `)

  return db
}

// store.ts
type Listener = () => void

let listeners = new Set<Listener>()
let cache: MessageType[] = []

// --- store operations ---

export function onStoreChange(listener: Listener) {
  listeners.add(listener)
  return () => listeners.delete(listener)
}

export function getSnapshot() {
  return cache
}

// --- initialization ---

export async function initMessages() {
  const db = await getDb()

  cache = await db.getAllAsync<MessageType>(`
    SELECT * FROM message
    ORDER BY time DESC
  `)

  listeners.forEach((listener) => listener())
}

// --- message operations ---

export async function saveMessage({ id, text, time, status }: MessageType) {
  const db = await getDb()

  await db.runAsync(
    `
    INSERT INTO message (id, text, time, status)
    VALUES (?, ?, ?, ?)
    `,
    [id, text, time, status],
  )

  cache = await db.getAllAsync<MessageType>(`
    SELECT * FROM message
    ORDER BY time DESC
  `)

  listeners.forEach((listener) => listener())
}
