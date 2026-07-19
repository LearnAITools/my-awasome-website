import Link from 'next/link'
import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { MapPin, Star, Users } from 'lucide-react'

export default function CinemasPage() {
  const cinemas = [
    {
      id: 1,
      name: 'IMAX Mumbai',
      location: 'South Mumbai',
      screens: 4,
      rating: 4.8,
      feature: 'IMAX',
    },
    {
      id: 2,
      name: '4DX Experience',
      location: 'Bandra',
      screens: 2,
      rating: 4.7,
      feature: '4DX',
    },
    {
      id: 3,
      name: 'Premium Recliners',
      location: 'Andheri',
      screens: 3,
      rating: 4.9,
      feature: 'Recliners',
    },
    {
      id: 4,
      name: 'PVR Cinemas',
      location: 'Thane',
      screens: 5,
      rating: 4.6,
      feature: 'Standard',
    },
  ]

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="mb-12">
            <h1 className="text-4xl font-bold tracking-tight mb-4">Find a Cinema</h1>
            <p className="text-lg text-muted-foreground max-w-3xl">
              Browse our network of premium cinemas and find the perfect venue for your movie experience.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[
              { link: '/cinemas/imax', title: 'IMAX Cinemas', description: 'Experience movies on massive screens' },
              { link: '/cinemas/4dx', title: '4DX Cinemas', description: 'Feel every moment with motion and effects' },
              { link: '/cinemas/recliners', title: 'Premium Recliners', description: 'Ultimate comfort with luxury seating' },
            ].map((cinema) => (
              <Link
                key={cinema.link}
                href={cinema.link}
                className="rounded-xl border border-border/60 bg-card/40 p-8 hover:border-primary/50 hover:bg-card/60 transition-colors"
              >
                <h3 className="text-lg font-bold mb-2">{cinema.title}</h3>
                <p className="text-sm text-muted-foreground">{cinema.description}</p>
              </Link>
            ))}
          </div>

          <h2 className="text-2xl font-bold mt-12 mb-6">All Cinemas</h2>
          <div className="space-y-4">
            {cinemas.map((cinema) => (
              <div
                key={cinema.id}
                className="rounded-xl border border-border/60 bg-card p-6 hover:border-primary/50 transition-colors"
              >
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                  <div className="flex-1">
                    <h3 className="text-lg font-bold mb-2">{cinema.name}</h3>
                    <div className="space-y-1 text-sm text-muted-foreground">
                      <div className="flex items-center gap-2">
                        <MapPin className="size-4" />
                        {cinema.location}
                      </div>
                      <div className="flex items-center gap-2">
                        <Users className="size-4" />
                        {cinema.screens} Screens
                      </div>
                      <div className="flex items-center gap-2">
                        <Star className="size-4 fill-accent text-accent" />
                        {cinema.rating} Rating
                      </div>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="inline-block rounded-full bg-primary/15 px-3 py-1 text-sm font-medium text-primary">
                      {cinema.feature}
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
