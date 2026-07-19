"use client"

import { useState, useMemo } from "react"
import { useRouter } from "next/navigation"
import { Armchair, Ticket, Clock, MapPin, Calendar } from "lucide-react"
import { Button } from "@/components/ui/button"
import { showtimes, cinemas, type Movie } from "@/lib/movies"

const ROWS = ["A", "B", "C", "D", "E", "F", "G", "H"]
const COLS = 12
const AISLE_AFTER = 6

// Deterministic "occupied" seats so it doesn't change on re-render
const OCCUPIED = new Set([
  "A3", "A4", "B7", "C2", "C9", "D5", "D6", "E10", "F1", "F8", "G4", "G5", "H11", "B8", "E11",
])

type Tier = { name: string; rows: string[]; price: number }

const dates = [
  { day: "FRI", date: "13", month: "Jun" },
  { day: "SAT", date: "14", month: "Jun" },
  { day: "SUN", date: "15", month: "Jun" },
  { day: "MON", date: "16", month: "Jun" },
  { day: "TUE", date: "17", month: "Jun" },
]

export function SeatBooking({ movie }: { movie: Movie }) {
  const router = useRouter()
  const [dateIdx, setDateIdx] = useState(1)
  const [cinemaIdx, setCinemaIdx] = useState(0)
  const [timeIdx, setTimeIdx] = useState(1)
  const [selected, setSelected] = useState<string[]>([])

  const tiers: Tier[] = useMemo(
    () => [
      { name: "Premium", rows: ["A", "B"], price: movie.price + 6 },
      { name: "Executive", rows: ["C", "D", "E"], price: movie.price + 2 },
      { name: "Standard", rows: ["F", "G", "H"], price: movie.price },
    ],
    [movie.price],
  )

  const priceForRow = (row: string) =>
    tiers.find((t) => t.rows.includes(row))?.price ?? movie.price

  const toggleSeat = (id: string) => {
    if (OCCUPIED.has(id)) return
    setSelected((prev) =>
      prev.includes(id) ? prev.filter((s) => s !== id) : prev.length < 10 ? [...prev, id] : prev,
    )
  }

  const total = selected.reduce((sum, id) => sum + priceForRow(id[0]), 0)
  const convenienceFee = selected.length * 1.5
  const grandTotal = total + convenienceFee

  const proceed = () => {
    const booking = {
      movieId: movie.id,
      movieTitle: movie.title,
      poster: movie.poster,
      cinema: cinemas[cinemaIdx],
      date: `${dates[dateIdx].day} ${dates[dateIdx].date} ${dates[dateIdx].month}`,
      time: showtimes[timeIdx].time,
      format: showtimes[timeIdx].format,
      seats: selected,
      subtotal: total,
      fee: convenienceFee,
      total: grandTotal,
    }
    if (typeof window !== "undefined") {
      sessionStorage.setItem("cinemax_booking", JSON.stringify(booking))
    }
    router.push("/payment")
  }

  return (
    <div className="grid gap-8 lg:grid-cols-[1fr_340px]">
      <div>
        {/* Date selector */}
        <div className="mb-6">
          <h3 className="mb-3 flex items-center gap-2 text-sm font-semibold text-muted-foreground">
            <Calendar className="size-4" /> Select Date
          </h3>
          <div className="flex gap-2 overflow-x-auto pb-1">
            {dates.map((d, i) => (
              <button
                key={i}
                onClick={() => setDateIdx(i)}
                className={`flex min-w-16 flex-col items-center rounded-xl border px-4 py-2 transition-colors ${
                  i === dateIdx
                    ? "border-primary bg-primary text-primary-foreground"
                    : "border-border bg-card text-muted-foreground hover:border-primary/50"
                }`}
              >
                <span className="text-xs font-medium">{d.day}</span>
                <span className="text-lg font-bold">{d.date}</span>
                <span className="text-xs">{d.month}</span>
              </button>
            ))}
          </div>
        </div>

        {/* Cinema + showtimes */}
        <div className="mb-8">
          <h3 className="mb-3 flex items-center gap-2 text-sm font-semibold text-muted-foreground">
            <MapPin className="size-4" /> Cinema & Showtime
          </h3>
          <div className="flex flex-col gap-2">
            {cinemas.map((cinema, ci) => (
              <button
                key={cinema}
                onClick={() => setCinemaIdx(ci)}
                className={`rounded-lg border px-4 py-2 text-left text-sm font-medium transition-colors ${
                  ci === cinemaIdx
                    ? "border-primary bg-primary/10 text-foreground"
                    : "border-border bg-card text-muted-foreground hover:border-primary/40"
                }`}
              >
                {cinema}
              </button>
            ))}
          </div>
          <div className="mt-3 flex flex-wrap gap-2">
            {showtimes.map((s, i) => (
              <button
                key={s.time}
                onClick={() => setTimeIdx(i)}
                className={`flex flex-col items-center rounded-lg border px-3 py-1.5 transition-colors ${
                  i === timeIdx
                    ? "border-primary bg-primary text-primary-foreground"
                    : "border-border bg-card hover:border-primary/50"
                }`}
              >
                <span className="text-sm font-semibold">{s.time}</span>
                <span className={`text-[10px] ${i === timeIdx ? "text-primary-foreground/80" : "text-muted-foreground"}`}>
                  {s.format}
                </span>
              </button>
            ))}
          </div>
        </div>

        {/* Screen + seat map */}
        <div className="rounded-2xl border border-border/60 bg-card/40 p-4 sm:p-6">
          <div className="mx-auto mb-8 max-w-md">
            <div className="h-2 rounded-[100%] bg-gradient-to-b from-primary/60 to-transparent shadow-[0_0_30px_2px] shadow-primary/30" />
            <p className="mt-2 text-center text-xs uppercase tracking-[0.3em] text-muted-foreground">
              Screen this way
            </p>
          </div>

          <div className="flex flex-col items-center gap-2 overflow-x-auto">
            {ROWS.map((row) => (
              <div key={row} className="flex items-center gap-2">
                <span className="w-4 text-center text-xs font-medium text-muted-foreground">{row}</span>
                <div className="flex gap-1.5">
                  {Array.from({ length: COLS }).map((_, c) => {
                    const id = `${row}${c + 1}`
                    const occupied = OCCUPIED.has(id)
                    const isSelected = selected.includes(id)
                    return (
                      <button
                        key={id}
                        onClick={() => toggleSeat(id)}
                        disabled={occupied}
                        aria-label={`Seat ${id}`}
                        className={`flex size-6 items-center justify-center rounded-t-md text-[9px] transition-colors sm:size-7 ${
                          c === AISLE_AFTER ? "ml-4" : ""
                        } ${
                          occupied
                            ? "cursor-not-allowed bg-muted text-muted-foreground/40"
                            : isSelected
                              ? "bg-primary text-primary-foreground"
                              : "bg-secondary text-muted-foreground hover:bg-primary/40 hover:text-foreground"
                        }`}
                      >
                        <Armchair className="size-3.5 sm:size-4" />
                      </button>
                    )
                  })}
                </div>
              </div>
            ))}
          </div>

          {/* Legend */}
          <div className="mt-8 flex flex-wrap items-center justify-center gap-5 text-xs text-muted-foreground">
            <span className="flex items-center gap-1.5">
              <span className="size-3 rounded bg-secondary" /> Available
            </span>
            <span className="flex items-center gap-1.5">
              <span className="size-3 rounded bg-primary" /> Selected
            </span>
            <span className="flex items-center gap-1.5">
              <span className="size-3 rounded bg-muted" /> Sold
            </span>
          </div>

          <div className="mt-4 flex flex-wrap items-center justify-center gap-4 border-t border-border/60 pt-4 text-xs">
            {tiers.map((t) => (
              <span key={t.name} className="text-muted-foreground">
                <span className="font-semibold text-foreground">{t.name}</span> · ${t.price.toFixed(2)}
              </span>
            ))}
          </div>
        </div>
      </div>

      {/* Summary sidebar */}
      <aside className="lg:sticky lg:top-20 lg:h-fit">
        <div className="rounded-2xl border border-border/60 bg-card p-5">
          <h3 className="text-base font-bold">Booking Summary</h3>
          <dl className="mt-4 flex flex-col gap-2.5 text-sm">
            <div className="flex justify-between gap-2">
              <dt className="text-muted-foreground">Movie</dt>
              <dd className="text-right font-medium">{movie.title}</dd>
            </div>
            <div className="flex justify-between gap-2">
              <dt className="text-muted-foreground">Cinema</dt>
              <dd className="text-right font-medium">{cinemas[cinemaIdx]}</dd>
            </div>
            <div className="flex items-center justify-between gap-2">
              <dt className="flex items-center gap-1 text-muted-foreground">
                <Clock className="size-3.5" /> Show
              </dt>
              <dd className="text-right font-medium">
                {dates[dateIdx].day} {dates[dateIdx].date} · {showtimes[timeIdx].time} ({showtimes[timeIdx].format})
              </dd>
            </div>
            <div className="flex justify-between gap-2">
              <dt className="text-muted-foreground">Seats</dt>
              <dd className="text-right font-medium">
                {selected.length ? selected.slice().sort().join(", ") : "—"}
              </dd>
            </div>
          </dl>

          <div className="mt-4 border-t border-border/60 pt-4">
            <div className="flex justify-between text-sm">
              <span className="text-muted-foreground">Subtotal</span>
              <span className="font-medium">${total.toFixed(2)}</span>
            </div>
            <div className="mt-1.5 flex justify-between text-sm">
              <span className="text-muted-foreground">Convenience fee</span>
              <span className="font-medium">${convenienceFee.toFixed(2)}</span>
            </div>
            <div className="mt-3 flex justify-between border-t border-border/60 pt-3 text-base font-bold">
              <span>Total</span>
              <span className="text-primary">${grandTotal.toFixed(2)}</span>
            </div>
          </div>

          <Button
            size="lg"
            className="mt-4 w-full gap-2"
            disabled={selected.length === 0}
            onClick={proceed}
          >
            <Ticket className="size-4" />
            {selected.length ? `Pay $${grandTotal.toFixed(2)}` : "Select seats to continue"}
          </Button>
        </div>
      </aside>
    </div>
  )
}
