import Link from 'next/link'
import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { MovieCard } from '@/components/movie-card'
import { movies } from '@/lib/movies'

export default function FilmFestivalsPage() {
  const festivalMovies = movies.slice(0, 8)

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="mb-12">
            <h1 className="text-4xl font-bold tracking-tight mb-4">Film Festivals</h1>
            <p className="text-lg text-muted-foreground max-w-3xl">
              Experience curated selections from major film festivals. Award-winning and critically acclaimed films presented exclusively.
            </p>
          </div>

          <div className="mb-8 grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="rounded-xl border border-border/60 bg-card/40 p-8">
              <h3 className="text-lg font-bold mb-2">International Film Festival</h3>
              <p className="text-sm text-muted-foreground mb-4">
                Discover award-winning international cinema from around the world.
              </p>
              <p className="text-xs text-muted-foreground">June - July 2026</p>
            </div>

            <div className="rounded-xl border border-border/60 bg-card/40 p-8">
              <h3 className="text-lg font-bold mb-2">Indian Cinema Festival</h3>
              <p className="text-sm text-muted-foreground mb-4">
                Celebrate the best of Indian cinema with critically acclaimed films.
              </p>
              <p className="text-xs text-muted-foreground">July - August 2026</p>
            </div>
          </div>

          <h2 className="text-2xl font-bold mb-6">Festival Selection</h2>
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
            {festivalMovies.map((movie) => (
              <MovieCard key={movie.id} movie={movie} />
            ))}
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
