import React from 'react';

const MovieCard = ({ movie, onSelect }) => {
  return (
    <div
      onClick={onSelect}
      className="card cursor-pointer hover:shadow-lg transition-shadow transform hover:scale-105"
    >
      <div className="overflow-hidden rounded-lg mb-4 bg-gray-200 h-48">
        <img
          src={movie.posterUrl}
          alt={movie.title}
          className="w-full h-full object-cover"
          onError={(e) => {
            e.target.src = 'https://via.placeholder.com/300x450?text=' + movie.title;
          }}
        />
      </div>
      <h3 className="font-bold text-lg mb-2 line-clamp-2">{movie.title}</h3>
      <div className="space-y-1 text-sm text-gray-600">
        <p><strong>Genre:</strong> {movie.genre}</p>
        <p><strong>Language:</strong> {movie.language}</p>
        <p><strong>Duration:</strong> {movie.duration} mins</p>
        <div className="flex items-center mt-2">
          <span className="text-yellow-500">★</span>
          <span className="ml-1 font-semibold">{movie.rating}</span>
        </div>
      </div>
      <button className="btn-primary w-full mt-4">
        Book Now
      </button>
    </div>
  );
};

export default MovieCard;
