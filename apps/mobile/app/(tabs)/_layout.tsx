import { Tabs, Redirect } from 'expo-router'

import { useAuthStore } from '@/store/useAuthStore'

export default function TabsLayout() {
  const token = useAuthStore((s) => s.token)

  if (!token) {
    return <Redirect href="/login" />
  }

  return (
    <Tabs screenOptions={{ headerShown: false }}>
      <Tabs.Screen name="chats" options={{ title: 'Chats' }} />
      <Tabs.Screen name="status" options={{ title: 'Estados' }} />
      <Tabs.Screen name="settings" options={{ title: 'Ajustes' }} />
    </Tabs>
  )
}
