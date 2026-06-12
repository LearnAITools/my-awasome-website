import React, { useState } from 'react';
import { movieAPI } from '../../api/apiClient';

export default function AddMovieForm() {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    genre: '',
    duration: '',
    language: '',
    posterUrl: '',
    releaseDate: '',
    rating: '',
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.title || !formData.genre || !formData.duration) {
      setError('Please fill in all required fields');
      return;
    }

    setLoading(true);
    setError(null);
    setMessage(null);

    try {
      await movieAPI.createMovie({
        title: formData.title,
        description: formData.description,
        genre: formData.genre,
        duration: parseInt(formData.duration),
        language: formData.language,
        posterUrl: formData.posterUrl || 'https://via.placeholder.com/300x450?text=No+Image',
        releaseDate: formData.releaseDate,
        rating: parseFloat(formData.rating) || 0,
      });

      setMessage('Movie added successfully! ✅');
      setFormData({
        title: '',
        description: '',
        genre: '',
        duration: '',
        language: '',
        posterUrl: '',
        releaseDate: '',
        rating: '',
      });

      setTimeout(() => setMessage(null), 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add movie');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl">
      <h2 className="text-2xl font-bold mb-6">Add New Movie</h2>

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
              Movie Title *
            </label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
              placeholder="Enter movie title"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Genre *
            </label>
            <select
              name="genre"
              value={formData.genre}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
            >
              <option value="">Select Genre</option>
              <option value="Action">Action</option>
              <option value="Comedy">Comedy</option>
              <option value="Drama">Drama</option>
              <option value="Thriller">Thriller</option>
              <option value="Sci-Fi">Sci-Fi</option>
              <option value="Horror">Horror</option>
              <option value="Romance">Romance</option>
              <option value="Animation">Animation</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Duration (minutes) *
            </label>
            <input
              type="number"
              name="duration"
              value={formData.duration}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
              placeholder="e.g., 148"
              min="30"
              max="300"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Language
            </label>
            <select
              name="language"
              value={formData.language}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
            >
              <option value="">Select Language</option>
              <option value="English">English</option>
              <option value="Hindi">Hindi</option>
              <option value="Tamil">Tamil</option>
              <option value="Telugu">Telugu</option>
              <option value="Kannada">Kannada</option>
              <option value="Malayalam">Malayalam</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Release Date
            </label>
            <input
              type="date"
              name="releaseDate"
              value={formData.releaseDate}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Rating
            </label>
            <input
              type="number"
              name="rating"
              value={formData.rating}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
              placeholder="e.g., 8.5"
              min="0"
              max="10"
              step="0.1"
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Description
          </label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows="4"
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
            placeholder="Enter movie description"
          ></textarea>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Poster URL
          </label>
          <input
            type="url"
            name="posterUrl"
            value={formData.posterUrl}
            onChange={handleChange}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-primary"
            placeholder="https://example.com/poster.jpg"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-primary text-white py-3 rounded-lg font-semibold hover:bg-opacity-90 transition-all disabled:opacity-50"
        >
          {loading ? 'Adding Movie...' : 'Add Movie'}
        </button>
      </form>
    </div>
  );
}
