import React, { useRef } from 'react'
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native'

import { UserChat } from '@/features/chat/components/UserChat'
import { useChat } from '@/features/chat/hooks/useChat'

export function Chat() {
  const inputRef = useRef<TextInput>(null)
  const currentText = useRef('')

  const { messages, send } = useChat()

  const handleSendMessage = async () => {
    const text = currentText.current.trim()

    if (!text) return

    send(text)
    currentText.current = ''
    inputRef.current?.clear()
  }

  return (
    <View style={styles.container}>
      <Pressable>Frieren</Pressable>
      <Pressable>Kokun</Pressable>

      <UserChat messages={messages} />

      <View style={styles.messageContainer}>
        <TextInput
          ref={inputRef}
          multiline
          placeholder="Escribe aquí..."
          style={styles.textInput}
          onChangeText={(text) => (currentText.current = text)}
        />
        <Pressable onPress={handleSendMessage} style={styles.pressable}>
          <Text>Enviar</Text>
        </Pressable>
      </View>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'space-between',
    flexDirection: 'column',
  },

  chat: {
    gap: 8,
  },

  messageContainer: {
    gap: 16,
    flexDirection: 'row',
  },

  textInput: {
    width: '80%',
    borderWidth: 1,
    borderColor: 'gray',
    borderRadius: 4,

    paddingLeft: 8,
    paddingTop: 8,
  },

  pressable: {
    backgroundColor: 'cyan',
    padding: 10,
    borderRadius: 4,
  },
})
