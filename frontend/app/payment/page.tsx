"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import Image from "next/image"
import {
  CreditCard,
  Smartphone,
  Wallet,
  ShieldCheck,
  Clock,
  MapPin,
  ArrowLeft,
  CheckCircle2,
  Ticket,
} from "lucide-react"
import { SiteHeader } from "@/components/site-header"
import { SiteFooter } from "@/components/site-footer"
import { Button } from "@/components/ui/button"

type Booking = {
  movieTitle: string
  poster: string
  cinema: string
  date: string
  time: string
  format: string
  seats: string[]
  subtotal: number
  fee: number
  total: number
}

const methods = [
  { id: "card", label: "Credit / Debit Card", icon: CreditCard },
  { id: "upi", label: "UPI", icon: Smartphone },
  { id: "wallet", label: "Wallet", icon: Wallet },
] as const

export default function PaymentPage() {
  const [booking, setBooking] = useState<Booking | null>(null)
  const [method, setMethod] = useState<string>("card")
  const [paid, setPaid] = useState(false)

  useEffect(() => {
    const raw = typeof window !== "undefined" ? sessionStorage.getItem("cinemax_booking") : null
    if (raw) setBooking(JSON.parse(raw))
  }, [])

  const handlePay = (e: React.FormEvent) => {
    e.preventDefault()
    setPaid(true)
  }

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="mx-auto w-full max-w-5xl flex-1 px-4 py-8 sm:px-6 lg:px-8">
        {!booking ? (
          <div className="flex flex-col items-center justify-center gap-4 py-24 text-center">
            <Ticket className="size-10 text-muted-foreground" />
            <h1 className="text-xl font-bold">No booking in progress</h1>
            <p className="text-sm text-muted-foreground">Pick a movie and select your seats to continue.</p>
            <Link href="/">
              <Button>Browse movies</Button>
            </Link>
          </div>
        ) : paid ? (
          <div className="mx-auto flex max-w-md flex-col items-center gap-4 py-20 text-center">
            <span className="flex size-16 items-center justify-center rounded-full bg-primary/15 text-primary">
              <CheckCircle2 className="size-9" />
            </span>
            <h1 className="text-2xl font-bold">Booking confirmed!</h1>
            <p className="text-sm text-muted-foreground">
              Your tickets for <span className="font-semibold text-foreground">{booking.movieTitle}</span> are booked.
              A confirmation has been sent to your email.
            </p>
            <div className="w-full rounded-2xl border border-dashed border-border bg-card p-5 text-left">
              <div className="flex items-center justify-between border-b border-dashed border-border pb-3">
                <span className="font-bold">{booking.movieTitle}</span>
                <span className="rounded bg-primary px-2 py-0.5 text-xs font-semibold text-primary-foreground">
                  {booking.format}
                </span>
              </div>
              <div className="mt-3 grid grid-cols-2 gap-3 text-sm">
                <div>
                  <p className="text-xs text-muted-foreground">Date & Time</p>
                  <p className="font-medium">{booking.date} · {booking.time}</p>
                </div>
                <div>
                  <p className="text-xs text-muted-foreground">Seats</p>
                  <p className="font-medium">{booking.seats.slice().sort().join(", ")}</p>
                </div>
                <div className="col-span-2">
                  <p className="text-xs text-muted-foreground">Cinema</p>
                  <p className="font-medium">{booking.cinema}</p>
                </div>
              </div>
            </div>
            <Link href="/" className="w-full">
              <Button className="w-full" size="lg">Done</Button>
            </Link>
          </div>
        ) : (
          <>
            <Link
              href={`/movies/${""}`}
              onClick={(e) => {
                e.preventDefault()
                history.back()
              }}
              className="inline-flex items-center gap-1.5 text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
            >
              <ArrowLeft className="size-4" /> Back to seat selection
            </Link>
            <h1 className="mt-4 text-2xl font-bold tracking-tight sm:text-3xl">Checkout</h1>

            <div className="mt-6 grid gap-8 lg:grid-cols-[1fr_360px]">
              {/* Payment form */}
              <form onSubmit={handlePay}>
                <div className="rounded-2xl border border-border/60 bg-card p-5">
                  <h2 className="text-base font-bold">Payment Method</h2>
                  <div className="mt-4 flex flex-col gap-2">
                    {methods.map((m) => {
                      const Icon = m.icon
                      const active = method === m.id
                      return (
                        <button
                          type="button"
                          key={m.id}
                          onClick={() => setMethod(m.id)}
                          className={`flex items-center gap-3 rounded-lg border px-4 py-3 text-left text-sm font-medium transition-colors ${
                            active
                              ? "border-primary bg-primary/10"
                              : "border-border bg-background hover:border-primary/40"
                          }`}
                        >
                          <Icon className={`size-5 ${active ? "text-primary" : "text-muted-foreground"}`} />
                          {m.label}
                          <span
                            className={`ml-auto size-4 rounded-full border-2 ${
                              active ? "border-primary bg-primary" : "border-border"
                            }`}
                          />
                        </button>
                      )
                    })}
                  </div>

                  {method === "card" && (
                    <div className="mt-5 flex flex-col gap-4">
                      <Field label="Cardholder Name">
                        <input required placeholder="Jane Doe" className="cinemax-input" />
                      </Field>
                      <Field label="Card Number">
                        <input
                          required
                          inputMode="numeric"
                          placeholder="1234 5678 9012 3456"
                          className="cinemax-input"
                        />
                      </Field>
                      <div className="grid grid-cols-2 gap-4">
                        <Field label="Expiry">
                          <input required placeholder="MM / YY" className="cinemax-input" />
                        </Field>
                        <Field label="CVV">
                          <input required inputMode="numeric" placeholder="•••" className="cinemax-input" />
                        </Field>
                      </div>
                    </div>
                  )}

                  {method === "upi" && (
                    <div className="mt-5">
                      <Field label="UPI ID">
                        <input required placeholder="name@bank" className="cinemax-input" />
                      </Field>
                    </div>
                  )}

                  {method === "wallet" && (
                    <p className="mt-5 rounded-lg border border-border bg-background px-4 py-3 text-sm text-muted-foreground">
                      You will be redirected to your wallet provider to complete the payment.
                    </p>
                  )}
                </div>

                <div className="mt-4 flex items-center gap-2 text-xs text-muted-foreground">
                  <ShieldCheck className="size-4 text-primary" />
                  Payments are encrypted and 100% secure.
                </div>

                <Button type="submit" size="lg" className="mt-4 w-full">
                  Pay ${booking.total.toFixed(2)}
                </Button>
              </form>

              {/* Order summary */}
              <aside className="lg:sticky lg:top-20 lg:h-fit">
                <div className="rounded-2xl border border-border/60 bg-card p-5">
                  <div className="flex gap-4">
                    <div className="relative aspect-[2/3] w-20 shrink-0 overflow-hidden rounded-lg border border-border/60">
                      <Image
                        src={booking.poster || "/placeholder.svg"}
                        alt={booking.movieTitle}
                        fill
                        sizes="80px"
                        className="object-cover"
                      />
                    </div>
                    <div className="min-w-0">
                      <h3 className="font-bold leading-tight">{booking.movieTitle}</h3>
                      <p className="mt-1 flex items-center gap-1 text-xs text-muted-foreground">
                        <MapPin className="size-3" /> {booking.cinema}
                      </p>
                      <p className="mt-1 flex items-center gap-1 text-xs text-muted-foreground">
                        <Clock className="size-3" /> {booking.date} · {booking.time} ({booking.format})
                      </p>
                    </div>
                  </div>

                  <div className="mt-4 border-t border-border/60 pt-4">
                    <p className="text-xs text-muted-foreground">Seats ({booking.seats.length})</p>
                    <div className="mt-1.5 flex flex-wrap gap-1.5">
                      {booking.seats.slice().sort().map((s) => (
                        <span key={s} className="rounded bg-secondary px-2 py-0.5 text-xs font-medium">
                          {s}
                        </span>
                      ))}
                    </div>
                  </div>

                  <dl className="mt-4 flex flex-col gap-2 border-t border-border/60 pt-4 text-sm">
                    <div className="flex justify-between">
                      <dt className="text-muted-foreground">Subtotal</dt>
                      <dd>${booking.subtotal.toFixed(2)}</dd>
                    </div>
                    <div className="flex justify-between">
                      <dt className="text-muted-foreground">Convenience fee</dt>
                      <dd>${booking.fee.toFixed(2)}</dd>
                    </div>
                    <div className="flex justify-between border-t border-border/60 pt-3 text-base font-bold">
                      <dt>Total</dt>
                      <dd className="text-primary">${booking.total.toFixed(2)}</dd>
                    </div>
                  </dl>
                </div>
              </aside>
            </div>
          </>
        )}
      </main>
      <SiteFooter />
    </div>
  )
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-xs font-medium text-muted-foreground">{label}</span>
      {children}
    </label>
  )
}
