interface LoginResponse {
  errors: Error[]
  data: Data
}

interface Data {
  userId: string
  username: string
  token: string
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

  const json: LoginResponse = await response.json()

  if (json.errors && json.errors.length > 0) {
    throw new Error(json.errors[0].message)
  }

  return json.data
}
