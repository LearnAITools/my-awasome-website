import React, { useState, useEffect } from 'react';

const TimerTicker = ({ initialSeconds = 300, onTimeout }) => {
  const [seconds, setSeconds] = useState(initialSeconds);
  const [isActive, setIsActive] = useState(true);

  useEffect(() => {
    let interval = null;

    if (isActive && seconds > 0) {
      interval = setInterval(() => {
        setSeconds(seconds => seconds - 1);
      }, 1000);
    } else if (seconds === 0 && isActive) {
      setIsActive(false);
      onTimeout();
    }

    return () => clearInterval(interval);
  }, [isActive, seconds, onTimeout]);

  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;

  const getColorClass = () => {
    if (seconds > 180) return 'text-green-600';
    if (seconds > 60) return 'text-yellow-600';
    return 'text-red-600';
  };

  return (
    <div className={`text-center font-bold text-xl ${getColorClass()}`}>
      Reservation expires in: {minutes}:{remainingSeconds.toString().padStart(2, '0')}
    </div>
  );
};

export default TimerTicker;
