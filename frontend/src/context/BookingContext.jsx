import React, { createContext, useState, useContext } from 'react';

const BookingContext = createContext();

export const useBooking = () => {
  const context = useContext(BookingContext);
  if (!context) {
    throw new Error('useBooking must be used within BookingProvider');
  }
  return context;
};

export const BookingProvider = ({ children }) => {
  const [selectedShow, setSelectedShow] = useState(null);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [booking, setBooking] = useState(null);
  const [reservationTimeout, setReservationTimeout] = useState(300); // 5 minutes

  const selectSeats = (seats) => {
    setSelectedSeats(seats);
  };

  const clearSelection = () => {
    setSelectedSeats([]);
    setSelectedShow(null);
    setBooking(null);
  };

  const setCurrentShow = (show) => {
    setSelectedShow(show);
    setSelectedSeats([]);
  };

  const createBooking = (bookingData) => {
    setBooking(bookingData);
  };

  const value = {
    selectedShow,
    selectedSeats,
    booking,
    reservationTimeout,
    selectSeats,
    clearSelection,
    setCurrentShow,
    createBooking,
  };

  return <BookingContext.Provider value={value}>{children}</BookingContext.Provider>;
};
