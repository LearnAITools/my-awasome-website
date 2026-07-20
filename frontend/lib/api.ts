import axios from 'axios'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api'
const AUTH_TOKEN_KEY = 'auth_token'
const AUTH_USER_KEY = 'auth_user'

export type AuthUser = {
  userId: number
  email: string
  fullName: string
  role: string
  profilePictureUrl?: string | null
}

function notifyAuthChanged() {
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new Event('auth_changed'))
  }
}

function persistAuthUser(data: any) {
  if (typeof window === 'undefined') return

  if (data.token) {
    localStorage.setItem(AUTH_TOKEN_KEY, data.token)
  }

  const user: AuthUser = {
    userId: data.userId,
    email: data.email,
    fullName: data.fullName,
    role: data.role,
    profilePictureUrl: data.profilePictureUrl ?? null,
  }
  localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user))
  notifyAuthChanged()
}

function persistProfile(data: any) {
  if (typeof window === 'undefined') return

  const user: AuthUser = {
    userId: data.userId,
    email: data.email,
    fullName: data.fullName,
    role: data.role,
    profilePictureUrl: data.profilePictureUrl ?? null,
  }
  localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user))
  notifyAuthChanged()
}

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Add token to requests if available
apiClient.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
  }
  return config
})

export const movieService = {
  getAll: async () => {
    const response = await apiClient.get('/movies')
    return response.data
  },
  getById: async (id: string) => {
    const response = await apiClient.get(`/movies/${id}`)
    return response.data
  },
}

export const bookingService = {
  getShowSeats: async (showId: string) => {
    const response = await apiClient.get(`/shows/${showId}`)
    return response.data
  },
  createBooking: async (bookingData: any) => {
    const response = await apiClient.post('/bookings', bookingData)
    return response.data
  },
  getMyBookings: async () => {
    const response = await apiClient.get('/bookings/user')
    return response.data
  },
}

export const paymentService = {
  createOrder: async (bookingReference: string) => {
    const response = await apiClient.post('/payments/create-order', {
      bookingReference,
    })
    return response.data
  },
  verifyPayment: async (paymentData: any) => {
    const response = await apiClient.post('/payments/verify', paymentData)
    return response.data
  },
}

export const authService = {
  login: async (email: string, password: string) => {
    const response = await apiClient.post('/auth/login', { email, password })
    persistAuthUser(response.data)
    return response.data
  },
  signup: async (userData: any) => {
    const response = await apiClient.post('/auth/signup', userData)
    persistAuthUser(response.data)
    return response.data
  },
  getCurrentUser: () => {
    if (typeof window === 'undefined') return null

    const raw = localStorage.getItem(AUTH_USER_KEY)
    if (!raw) return null

    try {
      return JSON.parse(raw) as AuthUser
    } catch {
      localStorage.removeItem(AUTH_USER_KEY)
      return null
    }
  },
  fetchCurrentUser: async () => {
    const response = await apiClient.get('/users/me')
    persistProfile(response.data)
    return response.data
  },
  updateProfile: async (profileData: { fullName: string; profilePictureUrl?: string }) => {
    const response = await apiClient.patch('/users/me', profileData)
    persistProfile(response.data)
    return response.data
  },
  requestEmailChangeOtp: async (newEmail: string) => {
    const response = await apiClient.post('/users/me/email/request-otp', { newEmail })
    return response.data
  },
  verifyEmailChangeOtp: async (newEmail: string, otp: string) => {
    const response = await apiClient.post('/users/me/email/verify-otp', { newEmail, otp })
    persistAuthUser(response.data)
    return response.data
  },
  logout: () => {
    localStorage.removeItem(AUTH_TOKEN_KEY)
    localStorage.removeItem(AUTH_USER_KEY)
    notifyAuthChanged()
  },
}

export default apiClient
