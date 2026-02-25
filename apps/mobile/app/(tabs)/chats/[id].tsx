import React, { useRef } from 'react'
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native'

import { ChatBox } from '@/components/chat/ChatBox'
import { useChat } from '@/hooks/useChat'
import { useAuthStore } from '@/store/useAuthStore'

export default function ChatRoom() {
  // const { id } = useLocalSearchParams()

  const user = useAuthStore((s) => s.user)

  const inputRef = useRef<TextInput>(null)
  const currentText = useRef('')

  const { messages, sendMessage } = useChat()

  const handleSendMessage = () => {
    const text = currentText.current.trim()
    if (!text || !user) return

    sendMessage(text)

    currentText.current = ''
    inputRef.current?.clear()
  }

  return (
    <View style={styles.container}>
      <>
        <ChatBox messages={messages} />

        <View style={ChatBoxStyles.container}>
          <TextInput
            ref={inputRef}
            style={ChatBoxStyles.textInput}
            placeholder="Escribe un mensaje"
            onChangeText={(text) => (currentText.current = text)}
          />
          <Pressable onPress={handleSendMessage} style={ChatBoxStyles.pressable}>
            <Text style={ChatBoxStyles.pressableText}>➤</Text>
          </Pressable>
        </View>
      </>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'space-between',
    flexDirection: 'column',
  },
})

const ChatBoxStyles = StyleSheet.create({
  container: {
    position: 'relative',
  },
  textInput: {
    borderWidth: 2,
    borderColor: 'gray',
    borderRadius: 9999,

    paddingVertical: 20,
    paddingLeft: 20,
  },
  pressable: {
    position: 'absolute',
    right: 10,
    top: 10,

    width: 42,
    height: 42,

    borderRadius: 20,
    backgroundColor: '#25D366',
    justifyContent: 'center',
    alignItems: 'center',
  },
  pressableText: {
    fontSize: 20,
    color: 'white',
    marginLeft: 2,
    marginBottom: 2,
  },
})
