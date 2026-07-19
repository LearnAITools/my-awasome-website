# Cinemax Frontend - Quick Reference Guide

## All Modified & Created Files

### Components Modified (3)
```
✅ frontend/components/site-header.tsx
   - Changed Sign-in button from <Button> to <Link href="/auth/login">

✅ frontend/components/site-footer.tsx
   - Replaced all href="#" links with proper routes
   - 20+ footer links now functional

✅ frontend/components/hero-carousel.tsx
   - Updated "Watch Trailer" button to YouTube search link
```

### Pages Modified (1)
```
✅ frontend/app/page.tsx
   - Added seeAllLink prop to SectionHeader
   - See all buttons now navigate properly
```

### New Pages Created (16)

#### Authentication (2)
```
✅ frontend/app/auth/login/page.tsx
   - Email/password login form
   - Uses authService.login()
   - Shows demo credentials

✅ frontend/app/auth/signup/page.tsx
   - Full registration form
   - Password confirmation
   - Uses authService.signup()
```

#### User Account (2)
```
✅ frontend/app/profile/page.tsx
   - Protected route (checks auth token)
   - User profile display
   - Logout functionality

✅ frontend/app/bookings/page.tsx
   - Protected route
   - Booking history with mock data
   - Download and cancel options
```

#### Company Info (4)
```
✅ frontend/app/about/page.tsx
✅ frontend/app/careers/page.tsx
   - Job listings
✅ frontend/app/press/page.tsx
   - Press releases
✅ frontend/app/contact/page.tsx
   - Contact form
```

#### Help & Support (4)
```
✅ frontend/app/faqs/page.tsx
   - 6 expandable FAQs
✅ frontend/app/refund-policy/page.tsx
✅ frontend/app/terms/page.tsx
✅ frontend/app/privacy/page.tsx
```

#### Movies & Content (2)
```
✅ frontend/app/exclusives/page.tsx
   - Exclusive movie listings
✅ frontend/app/festivals/page.tsx
   - Film festival content
```

#### Cinemas (4)
```
✅ frontend/app/cinemas/page.tsx
   - All cinema listings
✅ frontend/app/cinemas/imax/page.tsx
   - IMAX cinemas with showtimes
✅ frontend/app/cinemas/4dx/page.tsx
   - 4DX cinemas with showtimes
✅ frontend/app/cinemas/recliners/page.tsx
   - Premium recliner cinemas
```

---

## Route Map

### Public Routes
```
/ - Home (Now Showing + Coming Soon)
/movies/[id] - Movie detail
/exclusives - Exclusive movies
/festivals - Film festivals
/cinemas - All cinemas
/cinemas/imax - IMAX cinemas
/cinemas/4dx - 4DX cinemas
/cinemas/recliners - Premium recliners
/about - About us
/careers - Careers
/press - Press
/contact - Contact
/faqs - FAQs
/refund-policy - Refund policy
/terms - Terms of use
/privacy - Privacy policy
/payment - Payment checkout
/admin - Admin dashboard
```

### Auth Routes
```
/auth/login - Login page
/auth/signup - Signup page
```

### Protected Routes (Require Token)
```
/profile - User profile
/bookings - Booking history
```

---

## API Services Ready to Use

### Import
```typescript
import { authService, movieService, bookingService, paymentService } from '@/lib/api'
```

### Services

#### authService
```typescript
authService.login(email, password)
  // Returns: { token, user }
  // Stores: token to localStorage.auth_token

authService.signup(userData)
  // Returns: { token, user }
  // Stores: token to localStorage.auth_token

authService.logout()
  // Clears: localStorage.auth_token
```

#### movieService
```typescript
movieService.getAll()
  // Returns: Array of movies

movieService.getById(id)
  // Returns: Movie object
```

#### bookingService
```typescript
bookingService.getShowSeats(showId)
  // Returns: Seat availability data

bookingService.createBooking(bookingData)
  // Returns: Booking confirmation

bookingService.getMyBookings()
  // Returns: User's bookings
```

#### paymentService
```typescript
paymentService.createOrder(amount, bookingId)
  // Returns: Payment order data

paymentService.verifyPayment(paymentData)
  // Returns: Payment verification
```

---

## Protected Route Pattern

```typescript
'use client'
import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'

export default function ProtectedPage() {
  const router = useRouter()
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('auth_token')
    setIsLoggedIn(!!token)
    setLoading(false)
    
    if (!token) {
      router.push('/auth/login')
    }
  }, [router])

  if (loading) return <div>Loading...</div>
  if (!isLoggedIn) return null

  return <div>Protected Content</div>
}
```

---

## Common Navigation Patterns

### Link to Home
```typescript
<Link href="/">Home</Link>
```

### Fragment Links (Scroll to Section)
```typescript
<Link href="/#now-showing">Now Showing</Link>
<Link href="/#coming-soon">Coming Soon</Link>
```

### Movie Detail
```typescript
<Link href={`/movies/${movieId}`}>Book Tickets</Link>
```

### External Link
```typescript
<a href="https://youtube.com" target="_blank">
  Watch Trailer
</a>
```

---

## Testing the Frontend

### Login
1. Navigate to http://localhost:3000/auth/login
2. Enter: user@example.com / password
3. Should redirect to home

### Protected Pages
1. Try to access /profile without login → redirects to /auth/login
2. Login first, then access /profile → shows user content
3. Logout → redirected to home

### Navigation
1. Click all footer links → should navigate to respective pages
2. Click Sign-in → should go to /auth/login
3. Click movie cards → should go to /movies/{id}

---

## API Base URL

```env
# Development
NEXT_PUBLIC_API_URL=http://localhost:8080/api

# Production
NEXT_PUBLIC_API_URL=https://api.cinemax.com/api
```

---

## Deployment

### Build
```bash
npm run build
```

### Start
```bash
npm start
```

### Environment
- Update NEXT_PUBLIC_API_URL in .env.local
- Backend must be running on specified URL
- CORS should be enabled on backend

---

## Known Limitations

### Frontend-Only Features
- Mock booking data (no backend yet)
- Simulated payment (UI only)
- Mock movie data
- No real email notifications

### Awaiting Backend
- Authentication (login/signup)
- Movie data
- Booking persistence
- Payment processing
- User profiles

---

## File Locations

### Main Components
```
frontend/
├── components/
│   ├── site-header.tsx ✅ FIXED
│   ├── site-footer.tsx ✅ FIXED
│   ├── hero-carousel.tsx ✅ FIXED
│   ├── movie-card.tsx
│   └── seat-booking.tsx
├── app/
│   ├── layout.tsx
│   ├── page.tsx ✅ FIXED
│   ├── auth/
│   │   ├── login/page.tsx ✅ NEW
│   │   └── signup/page.tsx ✅ NEW
│   ├── profile/page.tsx ✅ NEW
│   ├── bookings/page.tsx ✅ NEW
│   ├── about/page.tsx ✅ NEW
│   ├── careers/page.tsx ✅ NEW
│   ├── press/page.tsx ✅ NEW
│   ├── contact/page.tsx ✅ NEW
│   ├── faqs/page.tsx ✅ NEW
│   ├── refund-policy/page.tsx ✅ NEW
│   ├── terms/page.tsx ✅ NEW
│   ├── privacy/page.tsx ✅ NEW
│   ├── exclusives/page.tsx ✅ NEW
│   ├── festivals/page.tsx ✅ NEW
│   ├── cinemas/
│   │   ├── page.tsx ✅ NEW
│   │   ├── imax/page.tsx ✅ NEW
│   │   ├── 4dx/page.tsx ✅ NEW
│   │   └── recliners/page.tsx ✅ NEW
│   ├── movies/[id]/page.tsx
│   ├── payment/page.tsx
│   └── admin/page.tsx
└── lib/
    ├── api.ts
    ├── movies.ts
    └── utils.ts
```

---

## Next Steps for Development

1. **Backend Team**
   - Implement /auth/login endpoint
   - Implement /auth/signup endpoint
   - Implement /movies endpoints
   - Implement /bookings endpoints
   - Implement /payments endpoints

2. **Frontend Team**
   - Replace mock data with API calls
   - Add error handling
   - Add loading states
   - Test with real backend

3. **DevOps**
   - Set up CI/CD pipeline
   - Configure production environment
   - Set up monitoring
   - Enable CORS on backend

---

**Last Updated**: June 16, 2026
**Status**: ✅ Ready for Backend Integration
