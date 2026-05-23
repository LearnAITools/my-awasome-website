import React, { useState } from 'react';
import MovieGrid from '../components/MovieGrid';
import { useNavigate } from 'react-router-dom';

const HomePage = () => {
  const navigate = useNavigate();

  const handleMovieSelect = (movie) => {
    navigate('/movie/' + movie.id);
  };

  return (
    <div className="container py-8">
      <div className="mb-8">
        <h2 className="text-4xl font-bold mb-2">Welcome to BookMyShow</h2>
        <p className="text-gray-600">Select a movie and book your tickets now!</p>
      </div>
      <MovieGrid onMovieSelect={handleMovieSelect} />
    </div>
  );
};

export default HomePage;
