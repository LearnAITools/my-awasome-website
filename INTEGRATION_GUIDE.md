# Frontend Integration Guide - BookMyShow

## 🎬 What Changed

Your React/Vite frontend has been **completely replaced** with a modern **Next.js 16** application featuring:

### ✨ New Features
- **Next.js 16** with App Router (file-based routing)
- **React 19** with latest features
- **TypeScript** for type safety
- **Tailwind CSS 4.2** with dark theme
- **Modern UI Components** using shadcn & @base-ui/react
- **Responsive Design** - Mobile-first approach
- **Beautiful Icon System** using Lucide React
- **Server-Side Rendering** (SSR) for better SEO
- **Static Generation** for optimized performance
- **API Integration** with Axios for backend communication

### 📦 Project Structure

```
frontend/
├── app/                           # Next.js App Router Pages
│   ├── page.tsx                  # 🏠 Home - Movie listings
│   ├── layout.tsx                # Root layout with global styles
│   ├── globals.css               # Global Tailwind styles
│   ├── movies/
│   │   └── [id]/page.tsx        # 🎬 Movie detail page with seat booking
│   ├── payment/
│   │   └── page.tsx             # 💳 Payment page
│   └── admin/
│       └── page.tsx             # 👨‍💼 Admin dashboard
│
├── components/                    # Reusable React Components
│   ├── hero-carousel.tsx         # Auto-rotating movie carousel
│   ├── movie-card.tsx            # Movie card with hover effects
│   ├── seat-booking.tsx          # Interactive seat selection grid
│   ├── site-header.tsx           # Navigation header with search
│   ├── site-footer.tsx           # Footer with links
│   └── ui/
│       └── button.tsx            # Customizable button component
│
├── lib/
│   ├── api.ts                    # 🔌 **NEW** Backend API client with axios
│   ├── movies.ts                 # Movie data and mock data
│   ├── utils.ts                  # Tailwind CSS utilities
│
├── public/                        # Static assets
│   ├── posters/                  # Movie poster images
│   └── backdrops/                # Movie backdrop images
│
├── next.config.mjs               # Next.js config with API proxy
├── .env.local                    # Environment variables
├── .npmrc                         # NPM configuration
├── package.json                  # Dependencies
└── FRONTEND_README.md            # Frontend documentation
```

## 🔌 Backend API Integration

### API Client Configuration

A new **`lib/api.ts`** file provides services for all backend communication:

```typescript
// lib/api.ts - Main API client with Axios
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' }
})

// Automatically adds JWT token to all requests
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
```

### Available Services

```typescript
// Movie Service
import { movieService } from '@/lib/api'
const movies = await movieService.getAll()
const movie = await movieService.getById('movie-id')

// Booking Service
import { bookingService } from '@/lib/api'
const seats = await bookingService.getShowSeats('show-id')
const booking = await bookingService.createBooking(data)

// Payment Service
import { paymentService } from '@/lib/api'
const order = await paymentService.createOrder(amount, bookingId)
const verified = await paymentService.verifyPayment(data)

// Auth Service
import { authService } from '@/lib/api'
await authService.login(email, password)
await authService.signup(userData)
authService.logout()
```

## 🚀 How to Run

### Prerequisites
- **Backend**: Running on `http://localhost:8080`
- **Node.js**: 18 or higher
- **npm**: 10 or higher

### Start Everything

**Option 1: Manual Start (Recommended for Development)**

Terminal 1 - Backend:
```bash
cd backend
mvn spring-boot:run
```

Terminal 2 - Frontend:
```bash
cd frontend
npm run dev
```

**Option 2: Automated Start Script**
```bash
./start-app.sh
```

**Result:**
- 🏠 Frontend: http://localhost:3000
- 🔌 Backend: http://localhost:8080
- 📚 API: http://localhost:8080/api

## 🔄 Backend Integration Checklist

### ✅ What's Already Done
- [x] API client with Axios configured
- [x] JWT token handling in requests
- [x] Environment variables setup (.env.local)
- [x] API proxy configured in next.config.mjs
- [x] Components ready for backend data

### ⏳ What You Need to Do (Backend Tasks)

**Update Backend Endpoints** to match the expected data formats:

#### 1. **GET /api/movies** - Get all movies
**Expected Response:**
```json
[
  {
    "id": "movie-1",
    "title": "Movie Title",
    "poster": "/posters/poster-1.png",
    "backdrop": "/backdrops/backdrop-1.png",
    "genres": ["Action", "Sci-Fi"],
    "rating": 9.2,
    "votes": "84.2K",
    "duration": "2h 38m",
    "language": "English",
    "format": ["2D", "3D", "IMAX"],
    "releaseDate": "Jun 12, 2026",
    "certification": "UA",
    "synopsis": "Movie synopsis...",
    "status": "now-showing",
    "price": 14
  }
]
```

#### 2. **GET /api/shows/{showId}/seats** - Get seat availability
**Expected Response:**
```json
{
  "showId": "show-123",
  "movieTitle": "Movie Title",
  "movieGenre": "Action",
  "theater": "PVR Stellar Downtown",
  "showTime": "07:30 PM",
  "totalSeats": 120,
  "priceInCents": 1400,
  "seats": [
    { "seatNumber": 1, "status": "AVAILABLE" },
    { "seatNumber": 2, "status": "AVAILABLE" },
    { "seatNumber": 3, "status": "BOOKED" }
  ]
}
```

#### 3. **POST /api/bookings/reserve** - Create booking
**Request Body:**
```json
{
  "showId": "show-123",
  "seatNumbers": ["A1", "A2", "A3"],
  "customerName": "John Doe"
}
```

**Expected Response:**
```json
{
  "bookingId": "BOOK-123-ABC",
  "showId": "show-123",
  "seatNumber": 1,
  "customerName": "John Doe",
  "bookingTime": "2024-06-12T10:30:00"
}
```

#### 4. **POST /api/payments/create-order** - Create Razorpay order
**Request Body:**
```json
{
  "amount": 4200,
  "bookingId": "BOOK-123-ABC"
}
```

**Expected Response:**
```json
{
  "orderId": "order_123abc",
  "amount": 4200,
  "currency": "INR"
}
```

#### 5. **POST /api/auth/login** - User login
**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "token": "eyJhbGc...",
  "userId": "user-123",
  "name": "John Doe"
}
```

## 🎨 Frontend Pages

### 1. **Home Page** (`/`)
- Hero carousel with featured movies
- Genre filter pills
- "Now Showing" section with movie cards
- "Coming Soon" section
- Promotional banner

### 2. **Movie Detail** (`/movies/[id]`)
- Movie backdrop and poster
- Movie information (rating, duration, language, etc.)
- Genre tags and certification
- Full synopsis
- **Seat Booking Component** with:
  - Date selector
  - Cinema selector
  - Showtime selector
  - 10x12 seat grid with pricing tiers
  - Real-time seat availability

### 3. **Payment Page** (`/payment`)
- Booking summary
- Payment method selection
- Total price calculation with fees
- Booking confirmation display

### 4. **Admin Dashboard** (`/admin`)
- Movie management (add/edit movies)
- Schedule management
- Sales analytics

## 🛠️ Customization

### Update Brand Name
Edit the header and footer components:

```bash
# Header
frontend/components/site-header.tsx  - Change "Cinemax" to your brand

# Footer
frontend/components/footer.tsx       - Update company info

# Metadata
frontend/app/layout.tsx              - Update title and description
```

### Change Colors
Edit the theme in `app/globals.css`:

```css
:root {
  --primary: oklch(0.62 0.24 18);        /* Main brand color */
  --accent: oklch(0.78 0.16 75);         /* Highlight color */
  --background: oklch(0.16 0.012 270);   /* Dark background */
  /* ... more colors ... */
}
```

### Add Custom Images
Place images in `public/` and reference them:

```tsx
<Image 
  src="/images/my-image.png" 
  alt="My image" 
  width={400} 
  height={300}
/>
```

## 📊 Performance Metrics

Build size after optimization:
```
✓ Compiled successfully in 1519ms
✓ Generated 11 static pages
✓ Production ready
```

## 🐛 Troubleshooting

### Issue: "Cannot fetch from backend"
**Solution:**
1. Verify backend is running: `http://localhost:8080`
2. Check `.env.local` has correct API URL
3. Check browser console (F12) for CORS errors

### Issue: "Build fails with TypeScript errors"
**Solution:**
```bash
# Clear cache and rebuild
rm -rf .next node_modules
npm install
npm run build
```

### Issue: "Port 3000/8080 already in use"
**Solution:**
```bash
# Find process on port
lsof -i :3000

# Kill process
kill -9 <PID>
```

## 📚 Additional Resources

- [Next.js Documentation](https://nextjs.org/docs)
- [React 19 Features](https://react.dev)
- [Tailwind CSS](https://tailwindcss.com)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)

## ✅ Verification Steps

After starting both services, verify everything works:

1. **Open Frontend**: http://localhost:3000
2. **Check Movies Load**: See movie cards on home page
3. **Click Movie**: Navigate to movie detail page
4. **Check Seats**: See seat grid appears
5. **Check Backend**: API calls show in Network tab (F12)
6. **Check Auth**: Login functionality (when implemented)

## 🎯 Next Steps

1. **Update Backend Endpoints** to return expected data formats
2. **Test API Integration** with real data
3. **Implement Auth Pages** (login/signup forms)
4. **Add Error Handling** in API client
5. **Implement Loading States** in components
6. **Add Toast Notifications** for user feedback
7. **Deploy to Production** (Vercel for frontend)

---

**Questions?** Check `FRONTEND_README.md` for more details!
