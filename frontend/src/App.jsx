import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { BookingProvider } from './context/BookingContext';
import Header from './components/Header';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import MovieDetailsPage from './pages/MovieDetailsPage';
import MyBookingsPage from './pages/MyBookingsPage';
import './index.css';

function App() {
  return (
    <Router>
      <AuthProvider>
        <BookingProvider>
          <div className="min-h-screen flex flex-col">
            <Header />
            <main className="flex-1">
              <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/movie/:id" element={<MovieDetailsPage />} />
                <Route path="/mybookings" element={<MyBookingsPage />} />
              </Routes>
            </main>
            <footer className="bg-dark text-white text-center py-4 mt-8">
              <p>&copy; 2024 BookMyShow. All rights reserved.</p>
            </footer>
          </div>
        </BookingProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;
