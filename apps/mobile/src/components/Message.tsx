import { ReactNode } from 'react'
import { StyleSheet, Text, View } from 'react-native'

type MessageProps = {
  children: ReactNode
  isUserMessage?: boolean
}

export function Message({ children, isUserMessage = false }: MessageProps) {
  return (
    <View style={[styles.container, isUserMessage ? styles.user : styles.other]}>
      <Text>{children}</Text>
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
