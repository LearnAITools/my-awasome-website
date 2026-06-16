import Link from "next/link"
import Image from "next/image"
import { Star, Play } from "lucide-react"
import type { Movie } from "@/lib/movies"

export function MovieCard({ movie }: { movie: Movie }) {
  const isComingSoon = movie.status === "coming-soon"
  return (
    <Link href={`/movies/${movie.id}`} className="group block">
      <div className="relative aspect-[2/3] overflow-hidden rounded-xl border border-border/60 bg-card">
        <Image
          src={movie.poster || "/placeholder.svg"}
          alt={`${movie.title} poster`}
          fill
          sizes="(max-width: 640px) 50vw, (max-width: 1024px) 33vw, 20vw"
          className="object-cover transition-transform duration-500 group-hover:scale-105"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-background/90 via-background/10 to-transparent opacity-80" />

        <div className="absolute left-2 top-2 flex items-center gap-1 rounded-md bg-background/70 px-1.5 py-0.5 text-xs font-semibold backdrop-blur-sm">
          <Star className="size-3 fill-accent text-accent" />
          {movie.rating}
        </div>

        {isComingSoon && (
          <div className="absolute right-2 top-2 rounded-md bg-accent px-1.5 py-0.5 text-xs font-semibold text-accent-foreground">
            Soon
          </div>
        )}

        <div className="absolute inset-0 flex items-center justify-center opacity-0 transition-opacity duration-300 group-hover:opacity-100">
          <span className="flex size-12 items-center justify-center rounded-full bg-primary text-primary-foreground shadow-lg">
            <Play className="size-5 fill-current" />
          </span>
        </div>
      </div>

      <div className="mt-3">
        <h3 className="truncate text-sm font-semibold transition-colors group-hover:text-primary">
          {movie.title}
        </h3>
        <p className="mt-0.5 truncate text-xs text-muted-foreground">
          {movie.genres.join(" • ")}
        </p>
      </div>
    </Link>
  )
}
