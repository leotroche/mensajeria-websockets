import { StyleSheet, Text, View } from 'react-native'
import { MessageType } from '../../../types/types'
import { MessageStatus } from './MessageStatus'

export function Message({ id, text, time, status }: MessageType) {
  return (
    <View style={[styles.container, status === 'sent' ? styles.user : styles.other]}>
      <Text>{text}</Text>
      <Text>{time}</Text>
      <MessageStatus status={status} />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    padding: 10,
    borderRadius: 5,
    maxWidth: '75%',
  },

  user: {
    alignSelf: 'flex-end',
    backgroundColor: '#DCF8C6',
  },

  other: {
    alignSelf: 'flex-start',
    backgroundColor: '#E5E5EA',
  },
})
