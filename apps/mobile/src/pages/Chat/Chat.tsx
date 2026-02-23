import React, { useRef, useState } from 'react'
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native'

import { LoginButton } from '@/components/LoginButton'
import { UserChat } from '@/features/chat/components/UserChat'
import { useChat } from '@/hooks/useChat'
import { getToken } from '@/services/getToken'

export function Chat() {
  const inputRef = useRef<TextInput>(null)
  const currentText = useRef('')

  const [token, setToken] = useState('')
  const [username, setUsername] = useState('')

  const { messages, sendMessage } = useChat(token)

  const handleSendMessage = () => {
    const text = currentText.current.trim()
    if (!text || !username) return

    sendMessage({ username, message: text })

    currentText.current = ''
    inputRef.current?.clear()
  }

  const handleOnPress = async (username: string, password: string) => {
    const token = await getToken({ username, password })
    setToken(token)
    setUsername(username)
  }

  return (
    <View style={styles.container}>
      <View style={styles.box}>
        <LoginButton onPress={() => handleOnPress('pepe', 'pepe1234')}>Goku</LoginButton>
        <LoginButton onPress={() => handleOnPress('pepa', '1234pepe')}>Vegeta</LoginButton>
      </View>

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
  box: {
    flexDirection: 'row',
  },
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
