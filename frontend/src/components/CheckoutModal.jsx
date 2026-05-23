import React, { useState } from 'react';
import { paymentAPI } from '../api/apiClient';

const CheckoutModal = ({ booking, isOpen, onClose, onPaymentSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handlePayNow = async () => {
    try {
      setLoading(true);
      setError(null);

      // Create Razorpay order
      const orderResponse = await paymentAPI.createOrder(booking.bookingReference);
      const orderId = orderResponse.data.orderId;
      const keyId = orderResponse.data.keyId;

      // Initialize Razorpay
      const options = {
        key: keyId,
        amount: booking.totalAmountInCents,
        currency: 'INR',
        name: 'BookMyShow',
        description: `Booking for ${booking.movieTitle}`,
        order_id: orderId,
        handler: async (response) => {
          try {
            // Verify payment on backend
            await paymentAPI.verifyPayment(
              response.razorpay_payment_id,
              response.razorpay_order_id,
              response.razorpay_signature
            );
            onPaymentSuccess();
          } catch (err) {
            setError('Payment verification failed');
            console.error(err);
          }
        },
        prefill: {
          name: 'User',
          email: 'user@example.com',
          contact: '9999999999',
        },
        theme: {
          color: '#10B981',
        },
      };

      const razorpay = new window.Razorpay(options);
      razorpay.open();
    } catch (err) {
      setError('Failed to create payment order');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold">Order Summary</h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700 text-2xl"
          >
            ×
          </button>
        </div>

        {booking && (
          <div className="space-y-4 mb-6">
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
            <div className="border-t pt-4">
              <div className="flex justify-between items-center">
                <span className="text-lg font-bold">Total Amount:</span>
                <span className="text-2xl font-bold text-green-600">
                  ₹{(booking.totalAmountInCents / 100).toFixed(2)}
                </span>
              </div>
            </div>
          </div>
        )}

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        <div className="flex gap-4">
          <button
            onClick={onClose}
            className="btn-secondary flex-1"
            disabled={loading}
          >
            Cancel
          </button>
          <button
            onClick={handlePayNow}
            className="btn-primary flex-1"
            disabled={loading}
          >
            {loading ? 'Processing...' : 'Pay Now'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default CheckoutModal;
