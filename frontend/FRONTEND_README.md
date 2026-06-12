# BookMyShow Frontend - Next.js 16

Modern movie ticket booking frontend built with Next.js 16, React 19, TypeScript, and Tailwind CSS.

## Prerequisites

- Node.js 18+ 
- npm 10+
- Backend running on `http://localhost:8080`

## Setup Instructions

### 1. Install Dependencies
```bash
npm install
```

### 2. Configure Environment Variables
The `.env.local` file is already configured to connect to the backend:
```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### 3. Start Development Server
```bash
npm run dev
```

The frontend will be available at: **`http://localhost:3000`**

## Features

✅ **Movie Browsing** - Browse now showing and coming soon movies  
✅ **Movie Details** - View full movie information, ratings, and reviews  
✅ **Seat Selection** - Interactive 10x12 seat grid with real-time availability  
✅ **Booking Management** - Select cinema, date, and showtime  
✅ **Payment Integration** - Process payments through integrated API  
✅ **Responsive Design** - Mobile-first, fully responsive UI  
✅ **Dark Theme** - Modern dark theme with beautiful colors  

## Project Structure

```
frontend/
├── app/                    # Next.js App Router
│   ├── page.tsx           # Home page
│   ├── layout.tsx         # Root layout
│   ├── globals.css        # Global styles
│   ├── movies/
│   │   └── [id]/page.tsx  # Movie detail page
│   ├── payment/
│   │   └── page.tsx       # Payment page
│   └── admin/
│       └── page.tsx       # Admin dashboard
├── components/            # React components
│   ├── hero-carousel.tsx  # Movie carousel
│   ├── movie-card.tsx     # Movie card component
│   ├── seat-booking.tsx   # Seat selection interface
│   ├── site-header.tsx    # Navigation header
│   ├── site-footer.tsx    # Footer
│   └── ui/
│       └── button.tsx     # UI button component
├── lib/
│   ├── api.ts            # Backend API client
│   ├── movies.ts         # Mock movie data
│   └── utils.ts          # Utility functions
├── public/               # Static assets
│   ├── posters/          # Movie posters
│   └── backdrops/        # Movie backdrops
└── next.config.mjs       # Next.js configuration with API proxy
```

## Available Scripts

```bash
# Development server (with hot reload)
npm run dev

# Production build
npm run build

# Start production server
npm start

# Run ESLint
npm run lint
```

## Backend API Integration

The frontend communicates with the backend through REST APIs:

### Key Endpoints
- `GET /api/movies` - Fetch all movies
- `GET /api/movies/{id}` - Get movie details
- `GET /api/shows/{showId}/seats` - Get seat availability
- `POST /api/bookings/reserve` - Create a booking
- `POST /api/auth/login` - User login
- `POST /api/auth/signup` - User registration
- `POST /api/payments/create-order` - Create payment order
- `POST /api/payments/verify` - Verify payment

### API Client Usage

```typescript
import { movieService, bookingService, paymentService } from '@/lib/api'

// Fetch all movies
const movies = await movieService.getAll()

// Get seat availability
const seats = await bookingService.getShowSeats('show-123')

// Create booking
const booking = await bookingService.createBooking({
  showId: 'show-123',
  seatNumbers: ['A1', 'A2'],
  customerName: 'John Doe'
})
```

## Troubleshooting

### Backend Connection Issues
If the frontend can't connect to the backend:
1. Ensure backend is running on `http://localhost:8080`
2. Check that `NEXT_PUBLIC_API_URL` in `.env.local` is correct
3. Check browser console (F12) for CORS errors
4. Verify backend is allowing requests from `http://localhost:3000`

### Build Errors
```bash
# Clear cache and reinstall
rm -rf node_modules .next
npm install
npm run build
```

## Technology Stack

- **Framework**: Next.js 16.2.6
- **Runtime**: React 19
- **Language**: TypeScript 5.7.3
- **Styling**: Tailwind CSS 4.2.0
- **UI Components**: @base-ui/react, shadcn
- **Icons**: Lucide React
- **HTTP Client**: Axios
- **Image Optimization**: Next.js Image

## Performance Optimizations

- Server-side rendering (SSR) for better SEO
- Image optimization with Next.js Image component
- Static generation for movie pages
- Code splitting and lazy loading
- CSS optimization with Tailwind CSS

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Development Workflow

1. **Branch from main**: `git checkout -b feature/your-feature`
2. **Make changes**: Edit components and styles
3. **Test locally**: `npm run dev` and open `http://localhost:3000`
4. **Run linting**: `npm run lint`
5. **Build for production**: `npm run build`
6. **Create pull request**: Describe your changes

## Deployment

### Build for Production
```bash
npm run build
npm start
```

### Deploy to Vercel
```bash
npm install -g vercel
vercel
```

### Environment Variables for Production
Update `NEXT_PUBLIC_API_URL` in `.env.production` to point to your production backend.

## Contributing

1. Follow the project structure
2. Use TypeScript for type safety
3. Write clean, readable code
4. Test your changes locally
5. Ensure linting passes: `npm run lint`

## License

MIT License - See LICENSE file for details

## Support

For issues or questions:
- Check GitHub issues: `https://github.com/yourusername/bookmyshow/issues`
- Create a new issue with detailed description
- Include screenshots/error messages when applicable
