import React, { memo } from 'react'
import { StyleSheet, Text, View } from 'react-native'

import { useAuthStore } from '@/store/useAuthStore'
import { MessageType } from '@/types/types'

interface MessageBoxProps extends MessageType {}

export const Message = memo(({ userId, text, time }: MessageBoxProps) => {
  const currentUserId = useAuthStore((s) => s.user?.userId)

  const isOwnMessage = userId === currentUserId

  return (
    <View style={[styles.container, isOwnMessage ? styles.ownMessage : styles.incomingMessage]}>
      <Text style={[styles.text, isOwnMessage ? styles.ownText : styles.incomingText]}>{text}</Text>
      <Text style={styles.time}>{time}</Text>
    </View>
  )
})

const styles = StyleSheet.create({
  container: {
    maxWidth: '80%',
    padding: 12,
    borderRadius: 16,
    marginVertical: 4,
  },

  ownMessage: {
    alignSelf: 'flex-end',
    backgroundColor: '#63ADF2',
    borderTopRightRadius: 0,
  },

  incomingMessage: {
    alignSelf: 'flex-start',
    backgroundColor: '#C45BAA',
    borderTopLeftRadius: 0,
  },

  text: {
    fontSize: 15,
    lineHeight: 20,
  },

  ownText: {
    color: '#ffffff',
  },

  incomingText: {
    color: '#e5e7eb',
  },

  time: {
    fontSize: 11,
    marginTop: 6,
    opacity: 0.7,
    color: '#e5e7eb',
    textAlign: 'right',
  },
})
