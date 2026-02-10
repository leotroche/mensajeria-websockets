import './App.css'

import { StyleSheet, View } from 'react-native'
import { Chat } from './pages/Chat/Chat'

export default function App() {
  return (
    <View style={styles.container}>
      <View style={styles.pageContainer}>
        <Chat />
      </View>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    // flex: 1,
    // backgroundColor: '#fff',
    borderWidth: 1,
    width: '75%',

    flex: 1,
    alignItems: 'center',
  },

  pageContainer: {
    width: '75%', // ancho de la página
    flex: 1, // ocupa alto disponible
    height: '80%',
  },
})
