import React from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Header = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="bg-dark text-white shadow-lg">
      <div className="container flex justify-between items-center py-4">
        <div className="flex items-center gap-2 cursor-pointer" onClick={() => navigate('/')}>
          <div className="text-2xl font-bold">🎬</div>
          <h1 className="text-2xl font-bold">BookMyShow</h1>
        </div>

        <nav className="flex items-center gap-6">
          <button
            onClick={() => navigate('/')}
            className="hover:text-green-400 transition"
          >
            Home
          </button>

          {isAuthenticated ? (
            <>
              <button
                onClick={() => navigate('/mybookings')}
                className="hover:text-green-400 transition"
              >
                My Bookings
              </button>
              <div className="flex items-center gap-4">
                <span className="text-sm">{user?.email}</span>
                <button
                  onClick={handleLogout}
                  className="btn-danger"
                >
                  Logout
                </button>
              </div>
            </>
          ) : (
            <div className="flex gap-4">
              <button
                onClick={() => navigate('/login')}
                className="btn-secondary"
              >
                Login
              </button>
              <button
                onClick={() => navigate('/signup')}
                className="btn-primary"
              >
                Sign Up
              </button>
            </div>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;
