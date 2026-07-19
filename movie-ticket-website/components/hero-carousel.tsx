"use client"

import { useState, useEffect, useCallback } from "react"
import Link from "next/link"
import Image from "next/image"
import { Star, Play, ChevronLeft, ChevronRight, Ticket } from "lucide-react"
import { Button } from "@/components/ui/button"
import { heroSlides } from "@/lib/movies"

export function HeroCarousel() {
  const [index, setIndex] = useState(0)
  const count = heroSlides.length

  const goTo = useCallback((i: number) => setIndex((i + count) % count), [count])
  const next = useCallback(() => goTo(index + 1), [goTo, index])
  const prev = useCallback(() => goTo(index - 1), [goTo, index])

  useEffect(() => {
    const t = setInterval(() => setIndex((i) => (i + 1) % count), 6000)
    return () => clearInterval(t)
  }, [count])

  return (
    <section className="relative h-[80vh] min-h-[520px] w-full overflow-hidden">
      {heroSlides.map((slide, i) => (
        <div
          key={slide.id}
          className="absolute inset-0 transition-opacity duration-700"
          style={{ opacity: i === index ? 1 : 0, pointerEvents: i === index ? "auto" : "none" }}
        >
          <Image
            src={slide.backdrop || "/placeholder.svg"}
            alt={`${slide.title} backdrop`}
            fill
            priority={i === 0}
            sizes="100vw"
            className="object-cover"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-background via-background/60 to-background/20" />
          <div className="absolute inset-0 bg-gradient-to-r from-background/90 via-background/30 to-transparent" />

          <div className="absolute inset-0 flex items-end">
            <div className="mx-auto w-full max-w-7xl px-4 pb-20 sm:px-6 lg:px-8">
              <div className="max-w-xl">
                <div className="flex flex-wrap items-center gap-2">
                  <span className="flex items-center gap-1 rounded-md bg-accent/20 px-2 py-1 text-xs font-semibold text-accent">
                    <Star className="size-3 fill-accent" />
                    {slide.rating} / 10
                  </span>
                  <span className="rounded-md bg-card/80 px-2 py-1 text-xs font-medium text-muted-foreground backdrop-blur-sm">
                    {slide.certification}
                  </span>
                  {slide.format.map((f) => (
                    <span
                      key={f}
                      className="rounded-md border border-border bg-card/60 px-2 py-1 text-xs font-medium backdrop-blur-sm"
                    >
                      {f}
                    </span>
                  ))}
                </div>

                <h1 className="mt-4 text-balance text-4xl font-bold tracking-tight sm:text-5xl lg:text-6xl">
                  {slide.title}
                </h1>
                <p className="mt-2 text-sm font-medium text-muted-foreground">
                  {slide.genres.join(" • ")} &nbsp;|&nbsp; {slide.duration} &nbsp;|&nbsp; {slide.language}
                </p>
                <p className="mt-4 max-w-lg text-pretty text-sm leading-relaxed text-muted-foreground sm:text-base">
                  {slide.synopsis}
                </p>

                <div className="mt-6 flex flex-wrap items-center gap-3">
                  <Link href={`/movies/${slide.id}`}>
                    <Button size="lg" className="gap-2">
                      <Ticket className="size-4" />
                      Book Tickets
                    </Button>
                  </Link>
                  <Button size="lg" variant="outline" className="gap-2">
                    <Play className="size-4" />
                    Watch Trailer
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </div>
      ))}

      <button
        onClick={prev}
        aria-label="Previous slide"
        className="absolute left-4 top-1/2 z-10 hidden size-10 -translate-y-1/2 items-center justify-center rounded-full border border-border bg-background/60 text-foreground backdrop-blur-sm transition-colors hover:bg-background/90 sm:flex"
      >
        <ChevronLeft className="size-5" />
      </button>
      <button
        onClick={next}
        aria-label="Next slide"
        className="absolute right-4 top-1/2 z-10 hidden size-10 -translate-y-1/2 items-center justify-center rounded-full border border-border bg-background/60 text-foreground backdrop-blur-sm transition-colors hover:bg-background/90 sm:flex"
      >
        <ChevronRight className="size-5" />
      </button>

      <div className="absolute bottom-6 left-1/2 z-10 flex -translate-x-1/2 items-center gap-2">
        {heroSlides.map((s, i) => (
          <button
            key={s.id}
            onClick={() => goTo(i)}
            aria-label={`Go to slide ${i + 1}`}
            className={`h-1.5 rounded-full transition-all ${
              i === index ? "w-8 bg-primary" : "w-2 bg-foreground/30 hover:bg-foreground/50"
            }`}
          />
        ))}
      </div>
    </section>
  )
}
