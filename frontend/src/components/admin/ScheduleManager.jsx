import React, { useState, useEffect } from 'react';
import { movieAPI, showAPI } from '../../api/apiClient';

export default function ScheduleManager() {
  const [movies, setMovies] = useState([]);
  const [theaters, setTheaters] = useState([]);
  const [formData, setFormData] = useState({
    movieId: '',
    theaterId: '',
    showTime: '',
    screen: '',
    priceInCents: '',
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const moviesResponse = await movieAPI.getAllMovies();
      setMovies(moviesResponse.data);
      // Note: Fetch theaters from API when available
      setTheaters([
        { id: 1, name: 'PVR Cinemas - Bengaluru' },
        { id: 2, name: 'IMAX Theater - Mumbai' },
        { id: 3, name: 'Cineplex - Delhi' },
      ]);
    } catch (err) {
      setError('Failed to load data');
      console.error('Error:', err);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.movieId || !formData.theaterId || !formData.showTime || !formData.screen || !formData.priceInCents) {
      setError('Please fill in all fields');
      return;
    }

    // Check for time conflicts (simple validation)
    const selectedDateTime = new Date(formData.showTime);
    const now = new Date();
    if (selectedDateTime < now) {
      setError('Cannot schedule shows in the past');
      return;
    }

    setLoading(true);
    setError(null);
    setMessage(null);

    try {
      // API call would go here
      await showAPI.createShow({
        movieId: parseInt(formData.movieId),
        theaterId: parseInt(formData.theaterId),
        showTime: formData.showTime,
        screen: parseInt(formData.screen),
        priceInCents: parseInt(formData.priceInCents),
      });

      setMessage('Show scheduled successfully! ✅');
      setFormData({
        movieId: '',
        theaterId: '',
        showTime: '',
        screen: '',
        priceInCents: '',
      });

      setTimeout(() => setMessage(null), 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to schedule show');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl">
      <h2 className="text-2xl font-bold mb-6">Schedule New Show</h2>

      {message && (
        <div className="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded">
          {message}
        </div>
      )}

      {error && (
        <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Movie *
            </label>
            <select
              name="movieId"
              value={formData.movieId}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
            >
              <option value="">Select Movie</option>
              {movies.map(movie => (
                <option key={movie.id} value={movie.id}>
                  {movie.title}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Theater *
            </label>
            <select
              name="theaterId"
              value={formData.theaterId}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
            >
              <option value="">Select Theater</option>
              {theaters.map(theater => (
                <option key={theater.id} value={theater.id}>
                  {theater.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Show Date & Time *
            </label>
            <input
              type="datetime-local"
              name="showTime"
              value={formData.showTime}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Screen Number *
            </label>
            <select
              name="screen"
              value={formData.screen}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
            >
              <option value="">Select Screen</option>
              <option value="1">Screen 1</option>
              <option value="2">Screen 2</option>
              <option value="3">Screen 3</option>
              <option value="4">Screen 4</option>
              <option value="5">Screen 5</option>
            </select>
          </div>

          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Ticket Price (in cents) *
            </label>
            <div className="flex items-center">
              <span className="text-gray-600 mr-2">₹</span>
              <input
                type="number"
                name="priceInCents"
                value={formData.priceInCents}
                onChange={handleChange}
                className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
                placeholder="e.g., 50000 (for ₹500)"
                min="1000"
                step="100"
              />
              <span className="text-gray-600 ml-2 text-sm">(100 cents = ₹1)</span>
            </div>
          </div>
        </div>

        <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
          <p className="text-sm text-blue-700">
            <strong>ℹ️ Note:</strong> Make sure the selected show time doesn't conflict with other shows in the same screen. 
            A movie takes {movies.find(m => m.id === parseInt(formData.movieId))?.duration || 'N/A'} minutes.
          </p>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-primary text-white py-3 rounded-lg font-semibold hover:bg-opacity-90 transition-all disabled:opacity-50"
        >
          {loading ? 'Scheduling...' : 'Schedule Show'}
        </button>
      </form>
    </div>
  );
}
