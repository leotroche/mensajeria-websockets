import { useRouter } from 'expo-router'
import { useState } from 'react'
import { View, Text, TextInput, TouchableOpacity, StyleSheet, StatusBar } from 'react-native'
import { SafeAreaView } from 'react-native-safe-area-context'

import { LoginButton } from '@/components/LoginButton'
import { loginAPI } from '@/services/loginAPI'
import { useAuthStore } from '@/store/useAuthStore'

export default function Login() {
  const login = useAuthStore((s) => s.login)

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')

  const router = useRouter()

  const handleLogin = async () => {
    const { userId, username: name, token } = await loginAPI(username, password)
    login({ userId, username: name }, token)
    router.replace('/chats')
  }

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="#0088CC" />

      <View style={styles.header}>
        <Text style={styles.headerText}>Messenger</Text>
      </View>

      <View style={styles.form}>
        <Text style={styles.title}>Iniciar sesión</Text>

        <TextInput
          style={styles.input}
          placeholder="Usuario"
          placeholderTextColor="#6d8fa3"
          value={username}
          onChangeText={setUsername}
          autoCapitalize="none"
        />

        <TextInput
          style={styles.input}
          placeholder="Contraseña"
          placeholderTextColor="#6d8fa3"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
        />

        <TouchableOpacity style={styles.button} onPress={handleLogin}>
          <Text style={styles.buttonText}>Ingresar</Text>
        </TouchableOpacity>
      </View>

      <View>
        <LoginButton
          onPress={() => {
            setUsername('pepe')
            setPassword('pepe1234')
          }}
        >
          pepE
        </LoginButton>
        <LoginButton
          onPress={() => {
            setUsername('pepa')
            setPassword('1234pepe')
          }}
        >
          pepA
        </LoginButton>
      </View>
    </SafeAreaView>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#E5EFF5',
  },
  header: {
    backgroundColor: '#0088CC',
    paddingVertical: 20,
    alignItems: 'center',
  },
  headerText: {
    color: '#fff',
    fontSize: 20,
    fontWeight: '600',
  },
  form: {
    flex: 1,
    justifyContent: 'center',
    paddingHorizontal: 30,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 30,
    color: '#1c2b33',
    textAlign: 'center',
  },
  input: {
    borderBottomWidth: 1,
    borderBottomColor: '#0088CC',
    paddingVertical: 10,
    marginBottom: 25,
    fontSize: 16,
    color: '#1c2b33',
  },
  button: {
    backgroundColor: '#0088CC',
    paddingVertical: 14,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 10,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
})
