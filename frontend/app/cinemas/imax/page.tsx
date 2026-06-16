import Link from 'next/link'
import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { MapPin, Star } from 'lucide-react'
import { Button } from '@/components/ui/button'

export default function IMAxPage() {
  const imaxCinemas = [
    {
      id: 1,
      name: 'IMAX Mumbai Premium',
      location: 'South Mumbai, near Gateway of India',
      screens: 2,
      rating: 4.9,
      shows: ['10:00 AM', '1:30 PM', '5:00 PM', '8:30 PM'],
    },
    {
      id: 2,
      name: 'IMAX Bangalore',
      location: 'Bangalore, Commercial Street',
      screens: 1,
      rating: 4.8,
      shows: ['11:00 AM', '2:30 PM', '6:00 PM', '9:00 PM'],
    },
    {
      id: 3,
      name: 'IMAX Delhi',
      location: 'Delhi, Connaught Place',
      screens: 2,
      rating: 4.7,
      shows: ['10:30 AM', '1:00 PM', '4:30 PM', '8:00 PM'],
    },
  ]

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="mb-12">
            <Link href="/cinemas" className="text-primary hover:underline text-sm mb-4 inline-block">
              ← Back to Cinemas
            </Link>
            <h1 className="text-4xl font-bold tracking-tight mb-4">IMAX Cinemas</h1>
            <p className="text-lg text-muted-foreground max-w-3xl">
              Experience movies on our premium IMAX screens. Immerse yourself in crystal-clear images and
              immersive sound that brings your favorite films to life.
            </p>
          </div>

          <div className="space-y-6">
            {imaxCinemas.map((cinema) => (
              <div
                key={cinema.id}
                className="rounded-xl border border-border/60 bg-card p-8 hover:border-primary/50 transition-colors"
              >
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
                  <div className="md:col-span-2">
                    <h3 className="text-2xl font-bold mb-2">{cinema.name}</h3>
                    <div className="flex items-center gap-2 text-muted-foreground mb-4">
                      <MapPin className="size-4" />
                      {cinema.location}
                    </div>
                    <div className="flex items-center gap-6 text-sm">
                      <div>
                        <span className="font-semibold text-foreground">{cinema.screens}</span>
                        <span className="text-muted-foreground ml-1">IMAX Screens</span>
                      </div>
                      <div className="flex items-center gap-1">
                        <Star className="size-4 fill-accent text-accent" />
                        <span className="font-semibold text-foreground">{cinema.rating}</span>
                      </div>
                    </div>
                  </div>
                  <div>
                    <Button className="w-full">Book Now</Button>
                  </div>
                </div>

                <div className="border-t border-border/60 pt-6">
                  <h4 className="font-semibold mb-3">Showtimes</h4>
                  <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                    {cinema.shows.map((show) => (
                      <button
                        key={show}
                        className="rounded-lg border border-border bg-background px-3 py-2 text-sm hover:border-primary hover:bg-primary/10 transition-colors"
                      >
                        {show}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="mt-12 text-center">
            <Link href="/cinemas">
              <Button variant="outline">View All Cinemas</Button>
            </Link>
          </div>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
