import { Client } from '@stomp/stompjs'

const stompClient = new Client({
  brokerURL: 'ws://localhost:8080/chats',
  reconnectDelay: 5000,
  debug: (str) => {
    console.log(str)
  },
})

stompClient.onConnect = () => {
  console.log('Connected')

  stompClient.subscribe('/topic/canal1', (message) => {
    const response = JSON.parse(message.body)
    console.log('Received:', response)
  })
}

stompClient.activate()

const sendMessage = () => {
  stompClient.publish({
    destination: '/app/chat1',
    body: JSON.stringify({
      body: 'Hello from React Native 🚀',
    }),
  })
}

export { sendMessage }
