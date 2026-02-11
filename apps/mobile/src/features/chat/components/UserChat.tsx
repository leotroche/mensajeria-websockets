import { StyleSheet, View } from 'react-native'
import { useChat } from '../hooks/useChat'
import { Message } from './Message'

export function UserChat() {
  const { chat } = useChat()

  return (
    <View style={styles.container}>
      <Message id="1" text="Hola, ¿cómo estás?" status="read" time={new Date().toISOString()} />
      <Message
        id="2"
        text="Estoy bien, gracias. ¿Y tú?"
        status="sent"
        time={new Date().toISOString()}
      />
      <Message
        id="3"
        text="¡Genial! ¿Qué has estado haciendo?"
        status="received"
        time={new Date().toISOString()}
      />

      {chat?.map(({ id, text, time, status }) => (
        <Message key={id} id={id} text={text} time={time} status={status} />
      ))}
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    gap: 8,
  },
})
