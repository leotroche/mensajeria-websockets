import { StyleSheet, View } from 'react-native'

import { MessageType } from '../../../types/types'
import { Message } from '@/features/message/components/Message'


type UserChatProps = {
  messages?: MessageType[]
}

export function UserChat({ messages }: UserChatProps) {
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

      {messages?.map(({ id, text, time, status }) => (
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
