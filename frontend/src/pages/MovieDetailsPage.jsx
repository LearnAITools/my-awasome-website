import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { movieAPI, showAPI, bookingAPI } from '../api/apiClient';
import SeatLayout from '../components/SeatLayout';
import CheckoutModal from '../components/CheckoutModal';
import { useAuth } from '../context/AuthContext';
import { useBooking } from '../context/BookingContext';

const MovieDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const { selectedSeats, setCurrentShow } = useBooking();
  
  const [movie, setMovie] = useState(null);
  const [shows, setShows] = useState([]);
  const [selectedShow, setSelectedShow] = useState(null);
  const [selectedShowDetails, setSelectedShowDetails] = useState(null);
  const [booking, setBooking] = useState(null);
  const [isCheckoutOpen, setIsCheckoutOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchMovieDetails();
  }, [id]);

  const fetchMovieDetails = async () => {
    try {
      setLoading(true);
      const movieRes = await movieAPI.getMovieById(id);
      setMovie(movieRes.data);

      const showsRes = await showAPI.getShowsByMovie(id);
      setShows(showsRes.data);
    } catch (err) {
      setError('Failed to load movie details');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleShowSelect = async (show) => {
    setSelectedShow(show.id);
    setSelectedShowDetails(show);
    setCurrentShow(show);
  };

  const handleCheckout = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    if (!selectedShowDetails || selectedSeats.length === 0) {
      setError('Please select at least one seat');
      return;
    }

    try {
      const response = await bookingAPI.createBooking(selectedShowDetails.id, selectedSeats);
      setBooking(response.data);
      setIsCheckoutOpen(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create booking');
      console.error(err);
    }
  };

  const handlePaymentSuccess = () => {
    setIsCheckoutOpen(false);
    navigate('/mybookings');
  };

  if (loading) return <div className="container py-8 text-center">Loading...</div>;
  if (!movie) return <div className="container py-8 text-center">Movie not found</div>;

  return (
    <div className="container py-8">
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">
        <div>
          <img
            src={movie.posterUrl}
            alt={movie.title}
            className="w-full rounded-lg shadow-lg"
            onError={(e) => {
              e.target.src = 'https://via.placeholder.com/300x450?text=' + movie.title;
            }}
          />
        </div>
        <div className="md:col-span-2">
          <h1 className="text-4xl font-bold mb-4">{movie.title}</h1>
          <div className="space-y-2 mb-4">
            <p><strong>Genre:</strong> {movie.genre}</p>
            <p><strong>Language:</strong> {movie.language}</p>
            <p><strong>Duration:</strong> {movie.duration} minutes</p>
            <p><strong>Rating:</strong> ★ {movie.rating}/10</p>
            <p><strong>Release Date:</strong> {movie.releaseDate}</p>
          </div>
          <p className="text-gray-600 mb-6">{movie.description}</p>

          {shows.length === 0 ? (
            <p className="text-gray-600">No shows available for this movie</p>
          ) : (
            <div>
              <h3 className="text-xl font-bold mb-4">Available Shows</h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {shows.map((show) => (
                  <button
                    key={show.id}
                    onClick={() => handleShowSelect(show)}
                    className={`p-4 border-2 rounded-lg transition ${
                      selectedShow === show.id
                        ? 'border-green-500 bg-green-50'
                        : 'border-gray-300 hover:border-green-500'
                    }`}
                  >
                    <p className="font-bold">{show.theaterName}</p>
                    <p className="text-sm text-gray-600">
                      {new Date(show.showTime).toLocaleString()}
                    </p>
                    <p className="text-sm">Available Seats: {show.availableSeats}</p>
                    <p className="font-bold text-green-600 mt-2">
                      ₹{(show.priceInCents / 100).toFixed(2)}
                    </p>
                  </button>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {selectedShowDetails && (
        <div className="card">
          <SeatLayout show={selectedShowDetails} onSeatsSelected={() => {}} />
          <div className="mt-6 flex gap-4">
            <button
              onClick={() => setSelectedShow(null)}
              className="btn-secondary"
            >
              Change Show
            </button>
            <button
              onClick={handleCheckout}
              className="btn-primary flex-1"
              disabled={selectedSeats.length === 0}
            >
              Proceed to Checkout ({selectedSeats.length} seats)
            </button>
          </div>
        </div>
      )}

      {booking && (
        <CheckoutModal
          booking={booking}
          isOpen={isCheckoutOpen}
          onClose={() => setIsCheckoutOpen(false)}
          onPaymentSuccess={handlePaymentSuccess}
        />
      )}
    </div>
  );
};

export default MovieDetailsPage;
