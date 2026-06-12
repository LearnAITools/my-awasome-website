---
applyTo: frontend/**
description: Frontend React/Vite development guidelines
---

# Frontend Development Instructions

## Quick Commands

```bash
cd frontend

# Start development server (port 5173)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run tests
npm test

# Lint code
npm run lint
```

## Development Server

- **URL**: http://localhost:5173
- **Auto-reload**: Yes (HMR enabled)
- **API Proxy**: `/api/*` → `http://localhost:8080/api/*` (see [vite.config.js](../../frontend/vite.config.js))

## Code Organization

**Directory Structure**:
- `src/api/` - HTTP client ([apiClient.js](../../frontend/src/api/apiClient.js)) and endpoints
- `src/components/` - Reusable UI components
- `src/pages/` - Route pages (one per route)
- `src/context/` - Context API providers (AuthContext, BookingContext)
- `src/` - App.jsx (router), index.css, main.jsx

## Writing Components

### Functional Component
```jsx
import React from 'react';

function MyComponent({ title, onAction }) {
  const handleClick = () => {
    onAction();
  };

  return (
    <div className="p-4 bg-white rounded-lg shadow">
      <h2 className="text-xl font-bold">{title}</h2>
      <button 
        onClick={handleClick}
        className="mt-4 px-4 py-2 bg-primary text-white rounded hover:bg-opacity-90"
      >
        Action
      </button>
    </div>
  );
}

export default MyComponent;
```

### Using AuthContext
```jsx
import { useAuth } from '../context/AuthContext';

export default function MyPage() {
  const { user, isAuthenticated, login, logout } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  return <div>Welcome, {user.email}</div>;
}
```

### Using BookingContext
```jsx
import { useBooking } from '../context/BookingContext';

export default function SeatSelector() {
  const { selectedSeats, selectSeats, selectedShow } = useBooking();

  return (
    <div>
      {/* Render seats */}
    </div>
  );
}
```

### API Calls
```jsx
import { movieAPI, bookingAPI } from '../api/apiClient';

export default function MovieList() {
  const [movies, setMovies] = React.useState([]);

  React.useEffect(() => {
    movieAPI.getAllMovies()
      .then(response => setMovies(response.data))
      .catch(error => console.error('Failed to load movies:', error));
  }, []);

  return (
    <div>
      {movies.map(movie => (
        <MovieCard key={movie.id} movie={movie} />
      ))}
    </div>
  );
}
```

## Styling with Tailwind CSS

### Color Scheme
From [tailwind.config.js](../../frontend/tailwind.config.js):
- `primary` (#4F46E5) - Main action color
- `secondary` (#10B981) - Success/confirmation
- `danger` (#EF4444) - Errors/delete
- `dark` (#1F2937) - Dark backgrounds

### Common Classes
```jsx
// Spacing
<div className="p-4 m-2 gap-4">

// Colors
<button className="bg-primary text-white hover:bg-opacity-90">
<button className="bg-secondary text-white">
<button className="bg-danger text-white">

// Responsive
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">

// Flexbox
<div className="flex justify-center items-center gap-4">

// Forms
<input className="px-4 py-2 border border-gray-300 rounded focus:outline-none focus:border-primary">
```

## Key Patterns

### Context API State Management
- `AuthContext` - Global auth state (user, token)
- `BookingContext` - Show and seat selection state
- Access via `useAuth()` and `useBooking()` hooks

### Local Storage
- Auth token stored in `localStorage.authToken`
- User data stored in `localStorage.userData`
- Used for session persistence across page reloads

### Protected Routes
```jsx
import ProtectedRoute from '../components/ProtectedRoute';

// In Routes
<Route 
  path="/mybookings" 
  element={
    <ProtectedRoute>
      <MyBookingsPage />
    </ProtectedRoute>
  } 
/>
```

### JWT Authentication
- Token automatically added to all API requests via Axios interceptor
- Header: `Authorization: Bearer <token>`
- Backend validates on each request

### Error Handling
```jsx
movieAPI.getMovieById(id)
  .then(response => setMovie(response.data))
  .catch(error => {
    if (error.response?.status === 404) {
      setError('Movie not found');
    } else {
      setError('Failed to load movie');
    }
  });
```

## API Client

[apiClient.js](../../frontend/src/api/apiClient.js) provides namespaced API methods:
- `authAPI.login(email, password)`
- `authAPI.signup(email, password, fullName)`
- `movieAPI.getAllMovies()`
- `movieAPI.getMovieById(id)`
- `showAPI.getShowsByMovie(movieId)`
- `bookingAPI.createBooking(showId, selectedSeats)`
- `bookingAPI.getUserBookings()`
- `paymentAPI.createOrder(bookingReference)`
- `paymentAPI.verifyPayment(...)`

## Testing

```bash
npm test                  # Run Vitest
npm test -- --watch      # Watch mode
npm run lint             # Check for style issues
```

## Building & Deployment

```bash
npm run build
# Output: frontend/dist/

npm run preview
# Test production build locally
```

## Common Issues

- **CORS errors**: Check vite.config.js proxy is correct
- **Auth token missing**: Ensure token is in localStorage
- **API 401 errors**: Token expired; user should log in again
- **Styling not applied**: Check Tailwind class names (not custom CSS)
- **Context hook error**: Must be used inside provider
- **Module not found**: Check import path spelling

## Component Guidelines

- **Keep components small**: One concern per component
- **Prop drilling**: If too deep, use Context API
- **useEffect cleanup**: Always return cleanup function if needed
- **Conditional rendering**: Use `&&` or ternary, not `if` statements
- **Keys in lists**: Always provide unique key prop
