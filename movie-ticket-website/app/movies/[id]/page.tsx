import Image from "next/image"
import Link from "next/link"
import { notFound } from "next/navigation"
import { Star, Clock, Calendar, Globe, ArrowLeft } from "lucide-react"
import { SiteHeader } from "@/components/site-header"
import { SiteFooter } from "@/components/site-footer"
import { SeatBooking } from "@/components/seat-booking"
import { getMovie, movies } from "@/lib/movies"

export function generateStaticParams() {
  return movies.map((m) => ({ id: m.id }))
}

export default async function MoviePage({
  params,
}: {
  params: Promise<{ id: string }>
}) {
  const { id } = await params
  const movie = getMovie(id)
  if (!movie) notFound()

  const backdrop = movie.backdrop ?? movie.poster

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        {/* Movie hero */}
        <section className="relative">
          <div className="absolute inset-0 h-[420px] overflow-hidden">
            <Image
              src={backdrop || "/placeholder.svg"}
              alt={`${movie.title} backdrop`}
              fill
              priority
              sizes="100vw"
              className="object-cover"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-background via-background/70 to-background/40" />
          </div>

          <div className="relative mx-auto max-w-7xl px-4 pt-6 sm:px-6 lg:px-8">
            <Link
              href="/"
              className="inline-flex items-center gap-1.5 text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
            >
              <ArrowLeft className="size-4" /> Back to movies
            </Link>

            <div className="mt-8 flex flex-col gap-6 pb-10 sm:flex-row sm:items-end">
              <div className="relative aspect-[2/3] w-40 shrink-0 overflow-hidden rounded-xl border border-border/60 shadow-2xl sm:w-52">
                <Image
                  src={movie.poster || "/placeholder.svg"}
                  alt={`${movie.title} poster`}
                  fill
                  sizes="208px"
                  className="object-cover"
                />
              </div>

              <div className="flex-1">
                <div className="flex flex-wrap items-center gap-2">
                  {movie.format.map((f) => (
                    <span
                      key={f}
                      className="rounded-md border border-border bg-card/60 px-2 py-1 text-xs font-medium backdrop-blur-sm"
                    >
                      {f}
                    </span>
                  ))}
                </div>
                <h1 className="mt-3 text-3xl font-bold tracking-tight sm:text-5xl">{movie.title}</h1>

                <div className="mt-4 flex flex-wrap items-center gap-x-5 gap-y-2 text-sm text-muted-foreground">
                  <span className="flex items-center gap-1.5 font-semibold text-foreground">
                    <Star className="size-4 fill-accent text-accent" />
                    {movie.rating}
                    <span className="font-normal text-muted-foreground">({movie.votes} votes)</span>
                  </span>
                  <span className="flex items-center gap-1.5">
                    <Clock className="size-4" /> {movie.duration}
                  </span>
                  <span className="flex items-center gap-1.5">
                    <Globe className="size-4" /> {movie.language}
                  </span>
                  <span className="flex items-center gap-1.5">
                    <Calendar className="size-4" /> {movie.releaseDate}
                  </span>
                  <span className="rounded-md bg-card/80 px-2 py-0.5 text-xs font-medium">
                    {movie.certification}
                  </span>
                </div>

                <div className="mt-4 flex flex-wrap gap-2">
                  {movie.genres.map((g) => (
                    <span
                      key={g}
                      className="rounded-full bg-secondary px-3 py-1 text-xs font-medium text-secondary-foreground"
                    >
                      {g}
                    </span>
                  ))}
                </div>

                <p className="mt-5 max-w-2xl text-pretty text-sm leading-relaxed text-muted-foreground">
                  {movie.synopsis}
                </p>
              </div>
            </div>
          </div>
        </section>

        {/* Booking */}
        <section className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
          <h2 className="mb-6 text-2xl font-bold tracking-tight">Select your seats</h2>
          <SeatBooking movie={movie} />
        </section>
      </main>
      <SiteFooter />
    </div>
  )
}
