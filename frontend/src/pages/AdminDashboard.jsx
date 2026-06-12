import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Navigate } from 'react-router-dom';
import AddMovieForm from '../components/admin/AddMovieForm';
import ScheduleManager from '../components/admin/ScheduleManager';
import SalesAnalytics from '../components/admin/SalesAnalytics';

export default function AdminDashboard() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('movies');

  // Redirect non-admin users
  if (!user || user.role !== 'ROLE_ADMIN') {
    return <Navigate to="/" />;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-dark mb-2">Admin Dashboard</h1>
          <p className="text-gray-600">Manage movies, showtimes, and view analytics</p>
        </div>

        {/* Navigation Tabs */}
        <div className="flex gap-4 mb-8 border-b border-gray-200">
          <button
            onClick={() => setActiveTab('movies')}
            className={`px-4 py-3 font-semibold transition-colors border-b-2 ${
              activeTab === 'movies'
                ? 'border-primary text-primary'
                : 'border-transparent text-gray-600 hover:text-dark'
            }`}
          >
            <span className="text-lg">🎬</span> Add Movies
          </button>
          <button
            onClick={() => setActiveTab('schedule')}
            className={`px-4 py-3 font-semibold transition-colors border-b-2 ${
              activeTab === 'schedule'
                ? 'border-primary text-primary'
                : 'border-transparent text-gray-600 hover:text-dark'
            }`}
          >
            <span className="text-lg">📅</span> Schedule Shows
          </button>
          <button
            onClick={() => setActiveTab('analytics')}
            className={`px-4 py-3 font-semibold transition-colors border-b-2 ${
              activeTab === 'analytics'
                ? 'border-primary text-primary'
                : 'border-transparent text-gray-600 hover:text-dark'
            }`}
          >
            <span className="text-lg">📊</span> Analytics
          </button>
        </div>

        {/* Content Area */}
        <div className="bg-white rounded-lg shadow-lg p-8">
          {activeTab === 'movies' && <AddMovieForm />}
          {activeTab === 'schedule' && <ScheduleManager />}
          {activeTab === 'analytics' && <SalesAnalytics />}
        </div>
      </div>
    </div>
  );
}
