interface UserData {
  username: string
  password: string
}

export const getToken = async ({ username, password }: UserData) => {
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

  const data = await response.json()
  return data.secret
}
