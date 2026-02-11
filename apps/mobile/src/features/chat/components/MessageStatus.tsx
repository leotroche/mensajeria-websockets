import { StyleSheet, Text, View } from 'react-native'
import type { MessageStatusType } from '../../../types/types'

type MessageStatusProps = {
  status: MessageStatusType
}

export function MessageStatus({ status }: MessageStatusProps) {
  const TEXT = {
    sent: '✓',
    received: '✓✓',
    read: '✓✓',
  }

  return (
    <View style={styles.container}>
      <Text style={[styles.base, statusStyles[status]]}>{TEXT[status]}</Text>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    marginLeft: 4,
  },
  base: {
    fontSize: 12,
    fontWeight: 'bold',
  },
})

const statusStyles = StyleSheet.create({
  sent: {
    color: 'gray',
  },
  received: {
    color: 'blue',
  },
  read: {
    color: 'green',
  },
})
