import { useEffect, useSyncExternalStore } from 'react'
import { getSnapshot, initMessages, onStoreChange } from '../../../config/db'

export function useChat() {
  const messages = useSyncExternalStore(onStoreChange, getSnapshot)

  useEffect(() => {
    initMessages()
  }, [])

  return { chat: messages }
}
