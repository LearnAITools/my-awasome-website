import React, { useState, useEffect } from 'react';
import { bookingAPI } from '../api/apiClient';
import { useAuth } from '../context/AuthContext';
import ProtectedRoute from '../components/ProtectedRoute';

const MyBookingsPage = () => {
  const { isAuthenticated } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isAuthenticated) {
      fetchBookings();
    }
  }, [isAuthenticated]);

  const fetchBookings = async () => {
    try {
      setLoading(true);
      const response = await bookingAPI.getUserBookings();
      setBookings(response.data);
    } catch (err) {
      setError('Failed to load bookings');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (bookingId) => {
    if (window.confirm('Are you sure you want to cancel this booking?')) {
      try {
        await bookingAPI.cancelBooking(bookingId);
        fetchBookings();
      } catch (err) {
        setError('Failed to cancel booking');
      }
    }
  };

  return (
    <ProtectedRoute>
      <div className="container py-8">
        <h2 className="text-3xl font-bold mb-8">My Bookings</h2>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {loading ? (
          <div className="text-center py-8">Loading bookings...</div>
        ) : bookings.length === 0 ? (
          <div className="text-center py-12 text-gray-600">
            <p className="text-xl">No bookings yet</p>
          </div>
        ) : (
          <div className="grid gap-6">
            {bookings.map((booking) => (
              <div key={booking.id} className="card">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <p className="text-sm text-gray-600">Movie</p>
                    <p className="font-bold text-lg">{booking.movieTitle}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Theater</p>
                    <p className="font-bold">{booking.theaterName}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Show Time</p>
                    <p className="font-bold">{new Date(booking.showTime).toLocaleString()}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Seats</p>
                    <p className="font-bold">{booking.seatNumbers.join(', ')}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Amount</p>
                    <p className="font-bold text-green-600">₹{(booking.totalAmountInCents / 100).toFixed(2)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Status</p>
                    <span className={`font-bold px-3 py-1 rounded ${
                      booking.status === 'CONFIRMED'
                        ? 'bg-green-100 text-green-700'
                        : booking.status === 'PENDING'
                        ? 'bg-yellow-100 text-yellow-700'
                        : 'bg-red-100 text-red-700'
                    }`}>
                      {booking.status}
                    </span>
                  </div>
                </div>
                <div className="mt-4 flex gap-4">
                  <p className="text-sm text-gray-600">
                    Booking Reference: <strong>{booking.bookingReference}</strong>
                  </p>
                </div>
                {booking.status === 'PENDING' && (
                  <button
                    onClick={() => handleCancel(booking.id)}
                    className="btn-danger mt-4"
                  >
                    Cancel Booking
                  </button>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </ProtectedRoute>
  );
};

export default MyBookingsPage;
