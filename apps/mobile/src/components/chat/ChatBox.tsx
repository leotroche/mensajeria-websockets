import { StyleSheet, View } from 'react-native'

import { Message } from '@/components/Message'
import { MessageType } from '@/types/types'

type UserChatProps = {
  messages?: MessageType[]
}

export function ChatBox({ messages }: UserChatProps) {
  return (
    <View style={styles.container}>
      {messages?.map(({ userId, id, text, time }) => (
        <Message key={id} userId={userId} id={id} text={text} time={time} />
      ))}
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    gap: 8,
  },
})
