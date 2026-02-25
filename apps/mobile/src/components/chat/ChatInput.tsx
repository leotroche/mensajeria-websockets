import { useState } from 'react'
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native'

interface ChatInputProps {
  onSendMessage: (message: string) => void
}

export function ChatInput({ onSendMessage }: ChatInputProps) {
  const [text, setText] = useState('')

  const handleSendMessage = () => {
    const trimmed = text.trim()
    if (!trimmed) return

    onSendMessage(trimmed)
    setText('')
  }

  return (
    <View style={styles.container}>
      <TextInput
        value={text}
        onChangeText={setText}
        multiline
        placeholder="Escribe aquí..."
        style={styles.textInput}
      />

      <Pressable onPress={handleSendMessage} style={styles.pressable}>
        <Text style={styles.pressableText}>➤</Text>
      </Pressable>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    position: 'relative',
  },
  textInput: {
    borderWidth: 1,
    borderColor: 'gray',
    borderRadius: 9999,

    paddingVertical: 16,
    paddingHorizontal: 24,
  },
  pressable: {
    position: 'absolute',
    right: 8,
    top: 6,

    width: 40,
    height: 40,

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
