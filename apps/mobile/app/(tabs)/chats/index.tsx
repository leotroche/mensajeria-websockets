import { useRouter } from 'expo-router'
import { View, Text, Pressable } from 'react-native'

export default function ChatsScreen() {
  const router = useRouter()

  return (
    <View>
      <Pressable onPress={() => router.push('/chats/1')}>
        <Text>Chat con Juan</Text>
      </Pressable>

      <Pressable onPress={() => router.push('/chats/2')}>
        <Text>Chat con María</Text>
      </Pressable>
    </View>
  )
}
