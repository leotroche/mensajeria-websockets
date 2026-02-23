import { Pressable, StyleSheet, View } from 'react-native'

import { ChatPage } from './pages/ChatPage'

export default function App() {
  return (
    <View style={styles.root}>
      <View style={styles.page}>
        <ChatPage />
      </View>
    </View>
  )
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },

  page: {
    width: '75%',
    height: '80%',
    borderWidth: 1,
  },
})
