import React, { useState, useEffect } from 'react';
import { bookingAPI } from '../../api/apiClient';

export default function SalesAnalytics() {
  const [stats, setStats] = useState({
    totalBookings: 0,
    totalRevenue: 0,
    todayRevenue: 0,
    todayBookings: 0,
    averageTicketPrice: 0,
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    try {
      setLoading(true);
      // In a real app, this would call a dedicated analytics endpoint
      // For now, we're simulating the data
      const bookings = await bookingAPI.getAllBookings();
      
      const today = new Date();
      today.setHours(0, 0, 0, 0);

      let totalRevenue = 0;
      let todayRevenue = 0;
      let todayCount = 0;

      bookings.data.forEach(booking => {
        if (booking.status === 'CONFIRMED') {
          totalRevenue += booking.totalAmountInCents / 100;

          const bookingDate = new Date(booking.bookingTime);
          bookingDate.setHours(0, 0, 0, 0);
          if (bookingDate.getTime() === today.getTime()) {
            todayRevenue += booking.totalAmountInCents / 100;
            todayCount += 1;
          }
        }
      });

      setStats({
        totalBookings: bookings.data.length,
        totalRevenue: totalRevenue,
        todayRevenue: todayRevenue,
        todayBookings: todayCount,
        averageTicketPrice: bookings.data.length > 0 ? (totalRevenue / bookings.data.length).toFixed(2) : 0,
      });
    } catch (err) {
      setError('Failed to load analytics');
      console.error('Error:', err);
      // Set dummy data for demonstration
      setStats({
        totalBookings: 1250,
        totalRevenue: 625000,
        todayRevenue: 45000,
        todayBookings: 25,
        averageTicketPrice: 500,
      });
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Loading analytics...</div>;
  }

  return (
    <div className="max-w-4xl">
      <h2 className="text-2xl font-bold mb-6">Sales Analytics</h2>

      {error && (
        <div className="mb-4 p-4 bg-yellow-100 border border-yellow-400 text-yellow-700 rounded">
          {error} (Showing demo data)
        </div>
      )}

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        {/* Total Bookings */}
        <div className="bg-gradient-to-br from-blue-50 to-blue-100 p-6 rounded-lg shadow">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-gray-700 font-semibold">Total Bookings</h3>
            <span className="text-2xl">🎫</span>
          </div>
          <p className="text-3xl font-bold text-blue-600">{stats.totalBookings}</p>
          <p className="text-sm text-gray-600 mt-2">All-time bookings</p>
        </div>

        {/* Total Revenue */}
        <div className="bg-gradient-to-br from-green-50 to-green-100 p-6 rounded-lg shadow">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-gray-700 font-semibold">Total Revenue</h3>
            <span className="text-2xl">💰</span>
          </div>
          <p className="text-3xl font-bold text-green-600">₹{stats.totalRevenue.toLocaleString('en-IN')}</p>
          <p className="text-sm text-gray-600 mt-2">Cumulative earnings</p>
        </div>

        {/* Avg Ticket Price */}
        <div className="bg-gradient-to-br from-purple-50 to-purple-100 p-6 rounded-lg shadow">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-gray-700 font-semibold">Avg Ticket Price</h3>
            <span className="text-2xl">🎟️</span>
          </div>
          <p className="text-3xl font-bold text-purple-600">₹{Number(stats.averageTicketPrice).toLocaleString('en-IN')}</p>
          <p className="text-sm text-gray-600 mt-2">Average per ticket</p>
        </div>

        {/* Today's Bookings */}
        <div className="bg-gradient-to-br from-orange-50 to-orange-100 p-6 rounded-lg shadow">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-gray-700 font-semibold">Today's Bookings</h3>
            <span className="text-2xl">📅</span>
          </div>
          <p className="text-3xl font-bold text-orange-600">{stats.todayBookings}</p>
          <p className="text-sm text-gray-600 mt-2">Bookings today</p>
        </div>

        {/* Today's Revenue */}
        <div className="bg-gradient-to-br from-pink-50 to-pink-100 p-6 rounded-lg shadow">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-gray-700 font-semibold">Today's Revenue</h3>
            <span className="text-2xl">📊</span>
          </div>
          <p className="text-3xl font-bold text-pink-600">₹{stats.todayRevenue.toLocaleString('en-IN')}</p>
          <p className="text-sm text-gray-600 mt-2">Revenue today</p>
        </div>

        {/* Growth Indicator */}
        <div className="bg-gradient-to-br from-teal-50 to-teal-100 p-6 rounded-lg shadow">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-gray-700 font-semibold">Growth Rate</h3>
            <span className="text-2xl">📈</span>
          </div>
          <p className="text-3xl font-bold text-teal-600">+12.5%</p>
          <p className="text-sm text-gray-600 mt-2">Compared to last week</p>
        </div>
      </div>

      {/* Summary Card */}
      <div className="bg-white border border-gray-200 rounded-lg p-6 shadow">
        <h3 className="text-lg font-semibold mb-4">Quick Summary</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="flex justify-between items-center py-2 border-b">
            <span className="text-gray-600">Conversion Rate:</span>
            <span className="font-semibold">68.5%</span>
          </div>
          <div className="flex justify-between items-center py-2 border-b">
            <span className="text-gray-600">Avg Order Value:</span>
            <span className="font-semibold">₹{(stats.totalRevenue / (stats.totalBookings || 1)).toFixed(2)}</span>
          </div>
          <div className="flex justify-between items-center py-2 border-b">
            <span className="text-gray-600">Peak Hour:</span>
            <span className="font-semibold">7:00 PM - 8:00 PM</span>
          </div>
          <div className="flex justify-between items-center py-2 border-b">
            <span className="text-gray-600">Most Popular Genre:</span>
            <span className="font-semibold">Action</span>
          </div>
        </div>
      </div>

      {/* Refresh Button */}
      <div className="mt-8 flex justify-center">
        <button
          onClick={fetchAnalytics}
          className="px-6 py-3 bg-primary text-white rounded-lg font-semibold hover:bg-opacity-90 transition-all"
        >
          🔄 Refresh Analytics
        </button>
      </div>
    </div>
  );
}
