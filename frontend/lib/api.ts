import axios from 'axios'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api'

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
    const response = await apiClient.get(`/shows/${showId}/seats`)
    return response.data
  },
  createBooking: async (bookingData: any) => {
    const response = await apiClient.post('/bookings/reserve', bookingData)
    return response.data
  },
  getMyBookings: async () => {
    const response = await apiClient.get('/bookings/my-bookings')
    return response.data
  },
}

export const paymentService = {
  createOrder: async (amount: number, bookingId: string) => {
    const response = await apiClient.post('/payments/create-order', {
      amount,
      bookingId,
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
    if (response.data.token) {
      localStorage.setItem('auth_token', response.data.token)
    }
    return response.data
  },
  signup: async (userData: any) => {
    const response = await apiClient.post('/auth/signup', userData)
    if (response.data.token) {
      localStorage.setItem('auth_token', response.data.token)
    }
    return response.data
  },
  logout: () => {
    localStorage.removeItem('auth_token')
  },
}

export default apiClient
