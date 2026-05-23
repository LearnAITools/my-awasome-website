import React, { useState, useEffect } from 'react';
import { movieAPI } from '../api/apiClient';
import MovieCard from './MovieCard';

const MovieGrid = ({ onMovieSelect }) => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedGenre, setSelectedGenre] = useState('');
  const [selectedLanguage, setSelectedLanguage] = useState('');

  const genres = ['Sci-Fi', 'Action', 'Thriller', 'Romance', 'Comedy', 'Horror'];
  const languages = ['English', 'Hindi', 'Telugu', 'Tamil', 'Kannada'];

  useEffect(() => {
    fetchMovies();
  }, []);

  const fetchMovies = async () => {
    try {
      setLoading(true);
      const response = await movieAPI.getAllMovies();
      setMovies(response.data);
    } catch (err) {
      setError('Failed to load movies');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) {
      fetchMovies();
      return;
    }
    try {
      const response = await movieAPI.searchMovies(searchTerm);
      setMovies(response.data);
    } catch (err) {
      setError('Failed to search movies');
    }
  };

  const filterMovies = () => {
    let filtered = movies;
    if (selectedGenre) {
      filtered = filtered.filter(m => m.genre === selectedGenre);
    }
    if (selectedLanguage) {
      filtered = filtered.filter(m => m.language === selectedLanguage);
    }
    return filtered;
  };

  const filteredMovies = filterMovies();

  return (
    <div className="w-full">
      {/* Search Bar */}
      <div className="mb-8">
        <form onSubmit={handleSearch} className="mb-4">
          <div className="flex gap-2">
            <input
              type="text"
              placeholder="Search movies..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            />
            <button type="submit" className="btn-primary">
              Search
            </button>
          </div>
        </form>

        {/* Filters */}
        <div className="flex gap-4 flex-wrap">
          <div>
            <label className="block text-sm font-medium mb-2">Genre</label>
            <select
              value={selectedGenre}
              onChange={(e) => setSelectedGenre(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="">All Genres</option>
              {genres.map(genre => (
                <option key={genre} value={genre}>{genre}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium mb-2">Language</label>
            <select
              value={selectedLanguage}
              onChange={(e) => setSelectedLanguage(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="">All Languages</option>
              {languages.map(lang => (
                <option key={lang} value={lang}>{lang}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Movies Grid */}
      {loading ? (
        <div className="text-center py-12">
          <p>Loading movies...</p>
        </div>
      ) : error ? (
        <div className="text-center py-12 text-red-600">
          <p>{error}</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {filteredMovies.map(movie => (
            <MovieCard
              key={movie.id}
              movie={movie}
              onSelect={() => onMovieSelect(movie)}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default MovieGrid;
