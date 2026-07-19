import Link from "next/link"
import { SiteHeader } from "@/components/site-header"
import { SiteFooter } from "@/components/site-footer"
import { HeroCarousel } from "@/components/hero-carousel"
import { MovieCard } from "@/components/movie-card"
import { movies } from "@/lib/movies"
import { Flame, Sparkles, ChevronRight } from "lucide-react"

const genrePills = [
  "All",
  "Action",
  "Sci-Fi",
  "Drama",
  "Thriller",
  "Romance",
  "Horror",
  "Fantasy",
  "Comedy",
]

function SectionHeader({
  icon,
  title,
  subtitle,
  seeAllLink,
}: {
  icon: React.ReactNode
  title: string
  subtitle: string
  seeAllLink?: string
}) {
  return (
    <div className="mb-6 flex items-end justify-between gap-4">
      <div className="flex items-center gap-3">
        <span className="flex size-9 items-center justify-center rounded-lg bg-primary/15 text-primary">
          {icon}
        </span>
        <div>
          <h2 className="text-xl font-bold tracking-tight sm:text-2xl">{title}</h2>
          <p className="text-sm text-muted-foreground">{subtitle}</p>
        </div>
      </div>
      {seeAllLink ? (
        <Link href={seeAllLink} className="hidden items-center gap-1 text-sm font-medium text-primary transition-opacity hover:opacity-80 sm:flex">
          See all
          <ChevronRight className="size-4" />
        </Link>
      ) : null}
    </div>
  )
}

export default function HomePage() {
  const nowShowing = movies.filter((m) => m.status === "now-showing")
  const comingSoon = movies.filter((m) => m.status === "coming-soon")

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <HeroCarousel />

        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          {/* Genre filter pills */}
          <div className="-mt-2 mb-12 flex flex-wrap gap-2">
            {genrePills.map((g, i) => (
              <button
                key={g}
                className={`rounded-full border px-4 py-1.5 text-sm font-medium transition-colors ${
                  i === 0
                    ? "border-primary bg-primary text-primary-foreground"
                    : "border-border bg-card text-muted-foreground hover:border-primary/50 hover:text-foreground"
                }`}
              >
                {g}
              </button>
            ))}
          </div>

          {/* Now Showing */}
          <section id="now-showing" className="scroll-mt-20">
            <SectionHeader
              icon={<Flame className="size-5" />}
              title="Now Showing"
              subtitle="Book your seats for films playing this week"
              seeAllLink="/#now-showing"
            />
            <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
              {nowShowing.map((movie) => (
                <MovieCard key={movie.id} movie={movie} />
              ))}
            </div>
          </section>

          {/* Promo banner */}
          <section className="my-16 overflow-hidden rounded-2xl border border-border/60 bg-gradient-to-r from-primary/20 via-card to-card p-8 sm:p-10">
            <div className="flex flex-col items-start justify-between gap-6 sm:flex-row sm:items-center">
              <div>
                <span className="rounded-full bg-accent/20 px-3 py-1 text-xs font-semibold text-accent">
                  Members Only
                </span>
                <h3 className="mt-3 text-2xl font-bold tracking-tight sm:text-3xl">
                  Get 25% off every Tuesday
                </h3>
                <p className="mt-2 max-w-md text-sm text-muted-foreground">
                  Join Cinemax Black and unlock weekly discounts, free upgrades, and early access to blockbuster premieres.
                </p>
              </div>
              <button className="shrink-0 rounded-lg bg-primary px-6 py-3 text-sm font-semibold text-primary-foreground transition-opacity hover:opacity-90">
                Join Cinemax Black
              </button>
            </div>
          </section>

          {/* Coming Soon */}
          <section id="coming-soon" className="mb-20 scroll-mt-20">
            <SectionHeader
              icon={<Sparkles className="size-5" />}
              title="Coming Soon"
              subtitle="Get notified when these films hit the big screen"
              seeAllLink="/#coming-soon"
            />
            <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
              {comingSoon.map((movie) => (
                <MovieCard key={movie.id} movie={movie} />
              ))}
            </div>
          </section>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
