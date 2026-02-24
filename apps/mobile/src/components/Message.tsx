import { StyleSheet, Text, View } from 'react-native'

import { useAuthStore } from '@/store/useAuthStore'
import { MessageType } from '@/types/types'

interface MessageBoxProps extends MessageType {}

export function Message({ userId, id, text, time }: MessageBoxProps) {
  const user = useAuthStore((s) => s.user)

  const props = {
    userId,
    id,
  }

  console.table({
    props,

    user,
  })

  return (
    <View style={[styles.container, userId === user?.userId ? styles.user : styles.other]}>
      <Text>{text}</Text>
      <Text>{time}</Text>
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
