interface LoginResponse {
  userId: string
  username: string
  secret: string
}

export const loginAPI = async (username: string, password: string) => {
  const response = await fetch('http://localhost:8080/signin', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!response.ok) {
    throw new Error('Failed to fetch token')
  }

  const data: LoginResponse = await response.json()

  return {
    userId: data.userId,
    username: data.username,
    token: data.secret,
  }
}
