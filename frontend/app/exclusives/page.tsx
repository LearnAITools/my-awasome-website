import Link from 'next/link'
import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { MovieCard } from '@/components/movie-card'
import { movies } from '@/lib/movies'

export default function ExclusivesPage() {
  const exclusiveMovies = movies.filter((m) => m.language === 'English')

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="mb-12">
            <h1 className="text-4xl font-bold tracking-tight mb-4">Exclusive Releases</h1>
            <p className="text-lg text-muted-foreground max-w-3xl">
              Discover exclusive movie releases available only on Cinemax. Get early access to premium content and special screenings.
            </p>
          </div>

          <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
            {exclusiveMovies.length > 0 ? (
              exclusiveMovies.map((movie) => (
                <MovieCard key={movie.id} movie={movie} />
              ))
            ) : (
              <div className="col-span-full text-center py-12">
                <p className="text-muted-foreground">No exclusive movies available at the moment</p>
              </div>
            )}
          </div>

          <div className="mt-12 text-center">
            <Link href="/" className="inline-block px-6 py-2 rounded-lg bg-primary text-primary-foreground font-medium hover:opacity-90">
              Browse All Movies
            </Link>
          </div>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
