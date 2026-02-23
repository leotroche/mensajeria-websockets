import { StyleSheet, View } from 'react-native'

import { Message } from '@/features/message/components/Message'

import { MessageType } from '../../../types/types'

type UserChatProps = {
  messages?: MessageType[]
}

export function UserChat({ messages }: UserChatProps) {
  return (
    <View style={styles.container}>
      {messages?.map(({ id, text, time, status }) => (
        <Message key={id} id={id} text={text} time={time} status={status} />
      ))}
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    gap: 8,
  },
})
