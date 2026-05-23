import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth API
export const authAPI = {
  signup: (email, password, fullName) => 
    api.post('/auth/signup', { email, password, fullName }),
  login: (email, password) => 
    api.post('/auth/login', { email, password }),
};

// Movie API
export const movieAPI = {
  getAllMovies: () => api.get('/movies'),
  getMovieById: (id) => api.get(`/movies/${id}`),
  searchMovies: (title) => api.get('/movies/search', { params: { title } }),
  getMoviesByGenre: (genre) => api.get(`/movies/genre/${genre}`),
  getMoviesByLanguage: (language) => api.get(`/movies/language/${language}`),
};

// Show API
export const showAPI = {
  getAllShows: () => api.get('/shows'),
  getShowById: (id) => api.get(`/shows/${id}`),
  getShowsByMovie: (movieId) => api.get(`/shows/movie/${movieId}`),
  getShowsByTheater: (theaterId) => api.get(`/shows/theater/${theaterId}`),
};

// Booking API
export const bookingAPI = {
  createBooking: (showId, selectedSeats) => 
    api.post('/bookings', { showId, selectedSeats }),
  getBookingById: (id) => api.get(`/bookings/${id}`),
  getBookingByReference: (reference) => api.get(`/bookings/reference/${reference}`),
  getUserBookings: () => api.get('/bookings/user'),
  cancelBooking: (id) => api.delete(`/bookings/${id}`),
};

// Payment API
export const paymentAPI = {
  createOrder: (bookingReference) => 
    api.post('/payments/create-order', { bookingReference }),
  verifyPayment: (razorpayPaymentId, razorpayOrderId, razorpaySignature) => 
    api.post('/payments/verify', { razorpayPaymentId, razorpayOrderId, razorpaySignature }),
};

export default api;
