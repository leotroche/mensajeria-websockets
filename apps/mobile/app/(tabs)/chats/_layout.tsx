import { Slot, usePathname } from 'expo-router'
import { View, Text, useWindowDimensions, StyleSheet, ImageBackground } from 'react-native'

import ChatsScreen from './index'

export default function ChatsLayout() {
  const { width } = useWindowDimensions()
  const pathname = usePathname()

  const isDesktop = width >= 900
  const isRootChats = pathname === '/chats'

  // 📱 MOBILE
  if (!isDesktop) {
    return <Slot />
  }

  // 💻 DESKTOP
  return (
    <ImageBackground
      source={require('../../../assets/chat-pattern.svg')}
      resizeMode="repeat"
      style={{ flex: 1, width: '100%', height: '100%' }}
    >
      <View style={styles.container}>
        {/* Sidebar */}
        <View style={styles.sidebar}>
          <ChatsScreen />
        </View>

        {/* Content */}
        <View style={styles.content}>
          {isRootChats ? (
            <View style={styles.noChatSelected}>
              <Text style={styles.noChatSelectedText}>Select a chat to start messaging</Text>
            </View>
          ) : (
            <Slot />
          )}
        </View>
      </View>
    </ImageBackground>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'row',
  },
  sidebar: {
    width: '25%',
    borderRightWidth: 1,
    borderColor: '#ddd',
  },
  content: {
    width: '75%',
  },
  noChatSelected: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  noChatSelectedText: {
    fontSize: 18,
  },
})
