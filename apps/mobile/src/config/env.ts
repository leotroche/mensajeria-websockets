import { z } from 'zod'

const envSchema = z.object({
  EXPO_PUBLIC_WEB_SOCKET_BASE_URL: z.string(),
  EXPO_PUBLIC_USERNAME: z.string(),
  EXPO_PUBLIC_PASSWORD: z.string(),
})

const parsedEnv = envSchema.safeParse({
  EXPO_PUBLIC_WEB_SOCKET_BASE_URL: process.env.EXPO_PUBLIC_WEB_SOCKET_BASE_URL,
  EXPO_PUBLIC_USERNAME: process.env.EXPO_PUBLIC_USERNAME,
  EXPO_PUBLIC_PASSWORD: process.env.EXPO_PUBLIC_PASSWORD,
})

if (!parsedEnv.success) {
  console.error('❌ Invalid environment variables:')
  console.error(parsedEnv.error.issues)
  throw new Error('Invalid environment variables')
}

export const env = {
  WEB_SOCKET_BASE_URL: parsedEnv.data.EXPO_PUBLIC_WEB_SOCKET_BASE_URL,
  USERNAME: parsedEnv.data.EXPO_PUBLIC_USERNAME,
  PASSWORD: parsedEnv.data.EXPO_PUBLIC_PASSWORD,
}
