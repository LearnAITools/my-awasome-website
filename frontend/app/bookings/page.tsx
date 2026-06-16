'use client'

import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { Clapperboard, LogOut, User, Ticket, Calendar, MapPin } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { authService } from '@/lib/api'
import { useEffect, useState } from 'react'

const mockBookings = [
  {
    id: 1,
    movieTitle: 'Inception',
    cinema: 'PVR Cinemas',
    date: '2026-06-20',
    time: '19:30',
    seats: ['A1', 'A2'],
    status: 'Confirmed',
    totalPrice: 600,
  },
  {
    id: 2,
    movieTitle: 'Dune: Part Two',
    cinema: 'IMAX Mumbai',
    date: '2026-06-25',
    time: '14:00',
    seats: ['B5', 'B6', 'B7'],
    status: 'Confirmed',
    totalPrice: 1200,
  },
]

export default function BookingsPage() {
  const router = useRouter()
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('auth_token')
    setIsLoggedIn(!!token)
    setLoading(false)
  }, [])

  const handleLogout = () => {
    authService.logout()
    router.push('/')
  }

  if (loading) {
    return (
      <div className="flex min-h-screen flex-col">
        <SiteHeader />
        <main className="flex-1 flex items-center justify-center">
          <div>Loading...</div>
        </main>
        <SiteFooter />
      </div>
    )
  }

  if (!isLoggedIn) {
    return (
      <div className="flex min-h-screen flex-col">
        <SiteHeader />
        <main className="flex-1 flex items-center justify-center px-4">
          <div className="text-center">
            <h1 className="text-2xl font-bold mb-4">Sign In Required</h1>
            <p className="text-muted-foreground mb-6">Please sign in to view your bookings</p>
            <Link href="/auth/login">
              <Button>Sign In</Button>
            </Link>
          </div>
        </main>
        <SiteFooter />
      </div>
    )
  }

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1 mx-auto w-full max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Sidebar */}
          <div className="md:col-span-1">
            <div className="rounded-xl border border-border/60 bg-card p-6">
              <div className="flex items-center justify-center size-16 rounded-full bg-primary/15 mb-4 mx-auto">
                <User className="size-8 text-primary" />
              </div>
              <h2 className="text-lg font-bold text-center mb-1">User Name</h2>
              <p className="text-sm text-muted-foreground text-center mb-6">user@example.com</p>

              <div className="space-y-2">
                <Link href="/profile" className="block">
                  <Button variant="outline" className="w-full justify-start">
                    <User className="size-4 mr-2" />
                    My Profile
                  </Button>
                </Link>
                <Link href="/bookings" className="block">
                  <Button className="w-full justify-start">
                    <Clapperboard className="size-4 mr-2" />
                    My Bookings
                  </Button>
                </Link>
                <button onClick={handleLogout} className="w-full">
                  <Button variant="destructive" className="w-full justify-start">
                    <LogOut className="size-4 mr-2" />
                    Sign Out
                  </Button>
                </button>
              </div>
            </div>
          </div>

          {/* Main Content */}
          <div className="md:col-span-2">
            <h1 className="text-2xl font-bold mb-6">My Bookings</h1>

            {mockBookings.length === 0 ? (
              <div className="rounded-xl border border-border/60 bg-card p-8 text-center">
                <Ticket className="size-12 mx-auto mb-4 text-muted-foreground" />
                <h2 className="text-lg font-semibold mb-2">No bookings yet</h2>
                <p className="text-muted-foreground mb-6">Start booking your favorite movies</p>
                <Link href="/">
                  <Button>Browse Movies</Button>
                </Link>
              </div>
            ) : (
              <div className="space-y-4">
                {mockBookings.map((booking) => (
                  <div
                    key={booking.id}
                    className="rounded-xl border border-border/60 bg-card p-6 hover:border-primary/50 transition-colors"
                  >
                    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                      <div className="flex-1">
                        <h3 className="text-lg font-bold mb-2">{booking.movieTitle}</h3>
                        <div className="space-y-1 text-sm text-muted-foreground">
                          <div className="flex items-center gap-2">
                            <MapPin className="size-4" />
                            {booking.cinema}
                          </div>
                          <div className="flex items-center gap-2">
                            <Calendar className="size-4" />
                            {booking.date} at {booking.time}
                          </div>
                          <div className="flex items-center gap-2">
                            <Ticket className="size-4" />
                            Seats: {booking.seats.join(', ')}
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="inline-block rounded-full bg-green-500/15 px-3 py-1 text-sm font-medium text-green-600 mb-3">
                          {booking.status}
                        </div>
                        <div className="block">
                          <p className="text-sm text-muted-foreground">Total Price</p>
                          <p className="text-xl font-bold">₹{booking.totalPrice}</p>
                        </div>
                      </div>
                    </div>
                    <div className="mt-4 pt-4 border-t border-border/60 flex gap-2">
                      <Button variant="outline" size="sm" className="flex-1">
                        Download Ticket
                      </Button>
                      <Button variant="outline" size="sm" className="flex-1">
                        Cancel Booking
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
