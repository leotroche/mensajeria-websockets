interface LoginResponse {
  userId: string
  username: string
  token: string
  errors: Error[]
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

  console.log('Login API response: >>>>>>>>>>>>>>>>>>', data)

  return data
}
