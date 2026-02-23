export type ChatType = {
  id: string
  name: string
  messages: MessageType[]
  time: string
  // status: ChatStatusType
}

// type ChatStatusType = 'online' | 'offline' | 'typing'

export type MessageType = {
  id: string
  text: string
  time: string
  // status: MessageStatusType
}

// export type MessageStatusType = 'sent' | 'received' | 'read'
