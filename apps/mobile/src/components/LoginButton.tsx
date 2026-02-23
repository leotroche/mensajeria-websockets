import { ComponentProps, ReactNode } from 'react'
import { Pressable, StyleSheet, Text } from 'react-native'

interface LoginButtonProps extends ComponentProps<typeof Pressable> {
  children: ReactNode
}

export function LoginButton({ children, ...props }: LoginButtonProps) {
  return (
    <Pressable
      {...props}
      style={({ pressed }) => [styles.loginButton, pressed && styles.loginButtonPressed]}
    >
      <Text>{children}</Text>
    </Pressable>
  )
}

const styles = StyleSheet.create({
  loginContainer: {
    flexDirection: 'row',
    justifyContent: 'space-evenly',
    paddingVertical: 16,
  },

  loginButton: {
    backgroundColor: '#2563EB',
    paddingVertical: 12,
    paddingHorizontal: 28,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 5,
  },

  loginButtonPressed: {
    opacity: 0.85,
    transform: [{ scale: 0.97 }],
  },

  loginText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
    letterSpacing: 0.5,
  },
})
