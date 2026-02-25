import { Redirect, Tabs } from 'expo-router'
import { Text, TouchableOpacity } from 'react-native'

import { useAuthStore } from '@/store/useAuthStore'

export default function TabsLayout() {
  const token = useAuthStore((s) => s.token)

  if (!token) {
    return <Redirect href="/login" />
  }

  return (
    <Tabs
      screenOptions={{
        headerShown: true,
        headerRight: HeaderRight,
      }}
    >
      <Tabs.Screen
        name="chats"
        options={{
          title: 'Chats',
          tabBarLabel: 'Chats',
          // sceneStyle: { backgroundColor: '#202020' },
        }}
      />
      <Tabs.Screen name="status" options={{ title: 'Estados', tabBarLabel: 'Estados' }} />
      <Tabs.Screen name="settings" options={{ title: 'Ajustes', tabBarLabel: 'Ajustes' }} />
    </Tabs>
  )
}

function HeaderRight() {
  const user = useAuthStore((s) => s.user)
  const logout = useAuthStore((s) => s.logout)

  return (
    <TouchableOpacity onPress={logout} style={{ marginRight: 15 }}>
      <Text>Hola {user?.username}</Text>
      <Text style={{ color: 'red' }}>Salir</Text>
    </TouchableOpacity>
  )
}
