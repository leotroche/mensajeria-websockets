export type ChatType = {
  id: string
  name: string
  messages: MessageType[]
  time: string
  // status: ChatStatusType
}

// type ChatStatusType = 'online' | 'offline' | 'typing'

export type MessageType = {
  userId: string
  id: string
  text: string
  time: string
}

// export type MessageStatusType = 'sent' | 'received' | 'read'
