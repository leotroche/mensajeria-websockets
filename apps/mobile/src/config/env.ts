import { z } from 'zod'

const envSchema = z.object({
  EXPO_PUBLIC_WEB_SOCKET_BASE_URL: z.string(),

  EXPO_PUBLIC_USERNAME: z.string(),
  EXPO_PUBLIC_PASSWORD: z.string(),
})

const parsedEnv = envSchema.safeParse(process.env)

if (!parsedEnv.success) {
  console.error('❌ Invalid environment variables:')
  console.error(parsedEnv.error.issues)
  process.exit(1)
}

export const env = {
  WEB_SOCKET_BASE_URL: parsedEnv.data.EXPO_PUBLIC_WEB_SOCKET_BASE_URL,

  USERNAME: parsedEnv.data.EXPO_PUBLIC_USERNAME,
  PASSWORD: parsedEnv.data.EXPO_PUBLIC_PASSWORD,
}
