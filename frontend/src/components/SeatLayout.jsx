import React, { useState, useEffect, useMemo } from 'react';
import { useBooking } from '../context/BookingContext';

const SeatLayout = ({ show, onSeatsSelected }) => {
  const { selectedSeats, selectSeats } = useBooking();
  const [localSeats, setLocalSeats] = useState(selectedSeats);

  useEffect(() => {
    setLocalSeats(selectedSeats);
  }, [selectedSeats]);

  const handleSeatClick = (seatId) => {
    let newSeats;
    if (localSeats.includes(seatId)) {
      newSeats = localSeats.filter(s => s !== seatId);
    } else {
      newSeats = [...localSeats, seatId];
    }
    setLocalSeats(newSeats);
    selectSeats(newSeats);
    onSeatsSelected(newSeats);
  };

  const totalPrice = useMemo(() => {
    return localSeats.length * (show?.priceInCents || 0) / 100;
  }, [localSeats, show]);

  const createSeatGrid = () => {
    const rows = [];
    const rowLabels = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];

    for (let i = 0; i < 10; i++) {
      const row = [];
      for (let j = 0; j < 10; j++) {
        const seatNumber = (i + 1) * 10 + (j + 1);
        const seat = show?.seats?.find(s => s.seatNumber === seatNumber);
        
        let seatStatus = 'AVAILABLE';
        if (seat) {
          seatStatus = seat.status;
        }

        row.push({
          id: seatNumber,
          row: i + 1,
          col: j + 1,
          label: `${rowLabels[i]}${j + 1}`,
          status: seatStatus,
        });
      }
      rows.push(row);
    }
    return rows;
  };

  const seatGrid = useMemo(() => createSeatGrid(), [show]);

  const getSeatClasses = (seat) => {
    let classes = 'w-8 h-8 rounded cursor-pointer font-bold text-xs transition-all ';
    
    if (seat.status === 'BOOKED') {
      classes += 'bg-gray-400 cursor-not-allowed';
    } else if (localSeats.includes(seat.id)) {
      classes += 'bg-green-500 text-white border-2 border-green-600';
    } else if (seat.status === 'AVAILABLE') {
      classes += 'bg-white border-2 border-green-500 text-green-500 hover:bg-green-100';
    } else if (seat.status === 'RESERVED') {
      classes += 'bg-yellow-400 cursor-not-allowed';
    }
    
    return classes;
  };

  return (
    <div className="w-full">
      <div className="mb-8">
        <h3 className="text-2xl font-bold mb-4">Select Your Seats</h3>
        
        {/* Legend */}
        <div className="flex gap-6 mb-6 flex-wrap">
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 border-2 border-green-500 bg-white"></div>
            <span>Available</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 bg-green-500"></div>
            <span>Selected</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 bg-yellow-400"></div>
            <span>Reserved</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 bg-gray-400"></div>
            <span>Booked</span>
          </div>
        </div>

        {/* Screen */}
        <div className="text-center mb-8">
          <div className="inline-block border-t-4 border-gray-400 px-8">
            <p className="text-gray-500 font-semibold py-2">SCREEN</p>
          </div>
        </div>

        {/* Seat Grid */}
        <div className="overflow-x-auto flex justify-center mb-8">
          <div className="p-4">
            {seatGrid.map((row, rowIdx) => (
              <div key={rowIdx} className="flex gap-3 mb-3 items-center">
                <span className="w-8 text-right font-bold text-gray-600">
                  {String.fromCharCode(65 + rowIdx)}
                </span>
                <div className="flex gap-2">
                  {row.map((seat) => (
                    <button
                      key={seat.id}
                      onClick={() => handleSeatClick(seat.id)}
                      disabled={seat.status === 'BOOKED' || seat.status === 'RESERVED'}
                      className={getSeatClasses(seat)}
                      title={seat.label}
                      aria-label={`Seat ${seat.label}`}
                    >
                      {seat.col}
                    </button>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Selected Seats Summary */}
        <div className="card">
          <h4 className="font-bold mb-3">Selected Seats: {localSeats.length}</h4>
          {localSeats.length > 0 && (
            <div className="mb-3">
              <p className="text-sm text-gray-600">
                {seatGrid.flat()
                  .filter(s => localSeats.includes(s.id))
                  .map(s => s.label)
                  .join(', ')}
              </p>
            </div>
          )}
          <div className="flex justify-between items-center pt-3 border-t">
            <span className="text-lg font-bold">Total: ₹{totalPrice.toFixed(2)}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SeatLayout;
