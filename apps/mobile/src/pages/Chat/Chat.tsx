import { View, TextInput, Pressable, GestureResponderEvent, Text, StyleSheet } from 'react-native'
import { Message } from '../../components/Message'
import { useState } from 'react'
import { sendMessage } from '../../config/websocket'

export function Chat() {
  const [color, setColor] = useState('cyan')

  // const socket = new WebSocket('ws://localhost:8080/chats')

  const handleSendMessage = (evt: GestureResponderEvent) => {
    sendMessage()
  }
  return (
    <View style={styles.container}>
      {/* HISTORIAL */}
      <View>
        <Message>Hola, ¿cómo estás?</Message>
        <Message isUserMessage>Estoy bien, gracias. ¿Y tú?</Message>
        <Message>¡Genial! ¿Qué has estado haciendo?</Message>
      </View>

      {/*  */}
      <View style={styles.messageContainer}>
        <TextInput multiline placeholder="Escribe aquí..." style={styles.textInput} />
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
