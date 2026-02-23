import { Pressable, StyleSheet, Text, View } from 'react-native'

import { LoginButton } from './components/LoginButton'
import { HomePage } from './pages/HomePage'
import { getToken } from './services/getToken'
import { useAuthStore } from './store/useAuthStore'

export default function App() {
  const user = useAuthStore((s) => s.user)
  const token = useAuthStore((s) => s.token)
  const login = useAuthStore((s) => s.login)
  const logout = useAuthStore((s) => s.logout)

  const isAuthenticated = !!token

  const handleOnPress = async (username: string, password: string) => {
    const userId = crypto.randomUUID()
    const token = await getToken({ username, password })

    login({ userId, username }, token)
  }

  if (!isAuthenticated) {
    return (
      <View style={styles.box}>
        <LoginButton onPress={() => handleOnPress('pepe', 'pepe1234')}>pepE</LoginButton>
        <LoginButton onPress={() => handleOnPress('pepa', '1234pepe')}>pepA</LoginButton>
      </View>
    )
  }

  return (
    <>
      <View>
        <Text>Iniciaste sesión como {user?.username}</Text>
        <Pressable onPress={logout}>
          <Text>Cerrar sesión</Text>
        </Pressable>
      </View>
      <View style={styles.container}>
        <View style={styles.page}>
          <HomePage />
        </View>
      </View>
    </>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',

    backgroundColor: '#f0f0f0',
  },
  box: {
    flexDirection: 'row',
  },
  page: {
    width: '75%',
    height: '80%',
    borderWidth: 1,
    borderRadius: 20,
  },
})
