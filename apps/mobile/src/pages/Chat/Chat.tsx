import React, { useState } from 'react'
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native'
import { UserChat } from '../../features/chat/components/UserChat'
import { saveMessage } from '../../config/db'

export function Chat() {
  const [message, setMessage] = useState('')

  const sendMessage = async () => {
    await saveMessage({
      id: crypto.randomUUID(),
      text: message,
      time: new Date().toISOString(),
      status: 'sent',
    })

    console.log('Enviando mensaje:', message)
  }

  return (
    <View style={styles.container}>
      <UserChat />

      <View style={styles.messageContainer}>
        <TextInput
          multiline
          placeholder="Escribe aquí..."
          style={styles.textInput}
          value={message}
          onChangeText={setMessage}
        />
        <Pressable onPress={sendMessage} style={styles.pressable}>
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
