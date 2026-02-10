import { View, TextInput, Pressable, GestureResponderEvent, Text, StyleSheet } from 'react-native'
import { Message } from '../../components/Message'
import { useState } from 'react'

export function Chat() {
  const [color, setColor] = useState('cyan')

  const socket = new WebSocket('ws://localhost:8080')

  const sendMessage = (evt: GestureResponderEvent) => {}
  return (
    <View style={styles.container}>
      {/* HISTORIAL */}
      <View>
        <Message>Hola, ¿cómo estás?</Message>
        <Message isUserMessage>Estoy bien, gracias. ¿Y tú?</Message>
        <Message>¡Genial! ¿Qué has estado haciendo?</Message>
      </View>

      {/*  */}
      <View>
        <TextInput
          multiline
          placeholder="Escribe aquí..."
          style={{
            height: 'auto',
            borderWidth: 1,
            padding: 10,
            textAlignVertical: 'top', // Android
          }}
        />
        <Pressable
          onPress={sendMessage}
          style={{ backgroundColor: color, padding: 10, marginTop: 10 }}
        >
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
})
