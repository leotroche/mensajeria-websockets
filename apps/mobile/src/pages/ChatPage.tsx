import React, { useRef } from 'react'
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native'

import { LoginButton } from '@/components/LoginButton'
import { UserChat } from '@/features/chat/components/UserChat'
import { useChat } from '@/hooks/useChat'
import { getToken } from '@/services/getToken'
import { useAuthStore } from '@/store/useUserStore'

export function ChatPage() {
  const inputRef = useRef<TextInput>(null)
  const currentText = useRef('')

  // 🔐 Estado global
  const user = useAuthStore((s) => s.user)
  const token = useAuthStore((s) => s.token)
  const login = useAuthStore((s) => s.login)
  const logout = useAuthStore((s) => s.logout)

  const { messages, sendMessage } = useChat()

  const handleSendMessage = () => {
    const text = currentText.current.trim()
    if (!text || !user) return

    sendMessage({
      username: user.username,
      message: text,
    })

    currentText.current = ''
    inputRef.current?.clear()
  }

  const handleOnPress = async (username: string, password: string) => {
    try {
      const token = await getToken({ username, password })

      login({ userId: username, username }, token)
    } catch (error) {
      console.error('Login error', error)
    }
  }

  return (
    <View style={styles.container}>
      {/* 🔐 Si no está autenticado */}
      {!token && (
        <View style={styles.box}>
          <LoginButton onPress={() => handleOnPress('pepe', 'pepe1234')}>Goku</LoginButton>
          <LoginButton onPress={() => handleOnPress('pepa', '1234pepe')}>Vegeta</LoginButton>
        </View>
      )}

      {/* 💬 Chat solo si está autenticado */}
      {token && (
        <>
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

          <Pressable onPress={logout}>
            <Text>Cerrar sesión</Text>
          </Pressable>
        </>
      )}
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
