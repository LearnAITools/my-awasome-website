"use client"

import Link from "next/link"
import Image from "next/image"
import {
  Clapperboard,
  LayoutDashboard,
  Film,
  Ticket,
  Users,
  DollarSign,
  TrendingUp,
  Settings,
  Search,
  Plus,
  ArrowUpRight,
  Star,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { movies } from "@/lib/movies"

const stats = [
  { label: "Total Revenue", value: "$248,920", change: "+12.4%", icon: DollarSign },
  { label: "Tickets Sold", value: "18,204", change: "+8.1%", icon: Ticket },
  { label: "Active Movies", value: "24", change: "+3", icon: Film },
  { label: "Unique Visitors", value: "42,318", change: "+19.7%", icon: Users },
]

const weekly = [
  { day: "Mon", value: 42 },
  { day: "Tue", value: 78 },
  { day: "Wed", value: 56 },
  { day: "Thu", value: 64 },
  { day: "Fri", value: 92 },
  { day: "Sat", value: 100 },
  { day: "Sun", value: 84 },
]

const recentBookings = [
  { id: "BK-90213", user: "Aria Patel", movie: "Stellar Horizon", seats: "C4, C5", amount: "$34.50", status: "Confirmed" },
  { id: "BK-90212", user: "Leo Martins", movie: "Crown of Embers", seats: "A2", amount: "$22.00", status: "Confirmed" },
  { id: "BK-90211", user: "Mia Chen", movie: "Neon Veil", seats: "F7, F8, F9", amount: "$40.50", status: "Pending" },
  { id: "BK-90210", user: "Noah Kim", movie: "Redline", seats: "D6", amount: "$13.50", status: "Confirmed" },
  { id: "BK-90209", user: "Zoe Adams", movie: "Stellar Horizon", seats: "B3, B4", amount: "$41.00", status: "Refunded" },
]

const navItems = [
  { label: "Dashboard", icon: LayoutDashboard, active: true },
  { label: "Movies", icon: Film },
  { label: "Bookings", icon: Ticket },
  { label: "Customers", icon: Users },
  { label: "Settings", icon: Settings },
]

const statusStyles: Record<string, string> = {
  Confirmed: "bg-primary/15 text-primary",
  Pending: "bg-accent/20 text-accent",
  Refunded: "bg-muted text-muted-foreground",
}

export default function AdminPage() {
  return (
    <div className="flex min-h-screen bg-background">
      {/* Sidebar */}
      <aside className="sticky top-0 hidden h-screen w-60 shrink-0 flex-col border-r border-border/60 bg-sidebar p-4 lg:flex">
        <Link href="/" className="flex items-center gap-2 px-2 py-2">
          <span className="flex size-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
            <Clapperboard className="size-5" />
          </span>
          <span className="text-lg font-bold tracking-tight">
            Cine<span className="text-primary">max</span>
          </span>
        </Link>

        <nav className="mt-6 flex flex-col gap-1">
          {navItems.map((item) => {
            const Icon = item.icon
            return (
              <button
                key={item.label}
                className={`flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                  item.active
                    ? "bg-primary/15 text-primary"
                    : "text-muted-foreground hover:bg-secondary hover:text-foreground"
                }`}
              >
                <Icon className="size-4" />
                {item.label}
              </button>
            )
          })}
        </nav>

        <div className="mt-auto rounded-xl border border-border/60 bg-card p-4">
          <p className="text-sm font-semibold">Cinemax Black</p>
          <p className="mt-1 text-xs text-muted-foreground">Pro plan · renews Jul 1</p>
          <Button size="sm" variant="outline" className="mt-3 w-full">Manage plan</Button>
        </div>
      </aside>

      {/* Main */}
      <div className="flex min-w-0 flex-1 flex-col">
        {/* Topbar */}
        <header className="sticky top-0 z-40 flex h-16 items-center gap-4 border-b border-border/60 bg-background/80 px-4 backdrop-blur-xl sm:px-6">
          <div>
            <h1 className="text-base font-bold sm:text-lg">Dashboard</h1>
            <p className="hidden text-xs text-muted-foreground sm:block">Welcome back, here is today&apos;s overview</p>
          </div>
          <div className="relative ml-auto hidden sm:block">
            <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
            <input
              placeholder="Search..."
              className="h-9 w-48 rounded-lg border border-border bg-card pl-9 pr-3 text-sm outline-none focus:border-primary"
            />
          </div>
          <Button className="gap-1.5">
            <Plus className="size-4" />
            <span className="hidden sm:inline">Add Movie</span>
          </Button>
        </header>

        <main className="flex-1 space-y-6 p-4 sm:p-6">
          {/* Stats */}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {stats.map((s) => {
              const Icon = s.icon
              return (
                <div key={s.label} className="rounded-2xl border border-border/60 bg-card p-5">
                  <div className="flex items-center justify-between">
                    <span className="flex size-9 items-center justify-center rounded-lg bg-primary/15 text-primary">
                      <Icon className="size-5" />
                    </span>
                    <span className="flex items-center gap-0.5 text-xs font-semibold text-primary">
                      <ArrowUpRight className="size-3" />
                      {s.change}
                    </span>
                  </div>
                  <p className="mt-4 text-2xl font-bold tracking-tight">{s.value}</p>
                  <p className="text-sm text-muted-foreground">{s.label}</p>
                </div>
              )
            })}
          </div>

          <div className="grid gap-6 xl:grid-cols-[1.6fr_1fr]">
            {/* Revenue chart */}
            <div className="rounded-2xl border border-border/60 bg-card p-5">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="text-base font-bold">Weekly Sales</h2>
                  <p className="text-sm text-muted-foreground">Tickets sold per day</p>
                </div>
                <span className="flex items-center gap-1 text-sm font-semibold text-primary">
                  <TrendingUp className="size-4" /> +14.2%
                </span>
              </div>
              <div className="mt-6 flex h-52 items-end justify-between gap-2 sm:gap-4">
                {weekly.map((d) => (
                  <div key={d.day} className="flex flex-1 flex-col items-center gap-2">
                    <div className="flex w-full flex-1 items-end">
                      <div
                        className="w-full rounded-t-md bg-gradient-to-t from-primary/40 to-primary transition-all hover:from-primary/60"
                        style={{ height: `${d.value}%` }}
                      />
                    </div>
                    <span className="text-xs text-muted-foreground">{d.day}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Top movies */}
            <div className="rounded-2xl border border-border/60 bg-card p-5">
              <h2 className="text-base font-bold">Top Performing</h2>
              <p className="text-sm text-muted-foreground">By revenue this week</p>
              <div className="mt-4 flex flex-col gap-3">
                {movies.slice(0, 4).map((m, i) => (
                  <div key={m.id} className="flex items-center gap-3">
                    <span className="w-4 text-sm font-bold text-muted-foreground">{i + 1}</span>
                    <div className="relative size-12 shrink-0 overflow-hidden rounded-md border border-border/60">
                      <Image src={m.poster || "/placeholder.svg"} alt={m.title} fill sizes="48px" className="object-cover" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="truncate text-sm font-medium">{m.title}</p>
                      <p className="flex items-center gap-1 text-xs text-muted-foreground">
                        <Star className="size-3 fill-accent text-accent" /> {m.rating} · {m.votes}
                      </p>
                    </div>
                    <span className="text-sm font-semibold">
                      ${((i + 2) * 7.4).toFixed(1)}K
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Recent bookings */}
          <div className="rounded-2xl border border-border/60 bg-card">
            <div className="flex items-center justify-between border-b border-border/60 p-5">
              <h2 className="text-base font-bold">Recent Bookings</h2>
              <button className="text-sm font-medium text-primary hover:opacity-80">View all</button>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full min-w-[640px] text-left text-sm">
                <thead>
                  <tr className="border-b border-border/60 text-xs uppercase tracking-wide text-muted-foreground">
                    <th className="px-5 py-3 font-medium">Booking ID</th>
                    <th className="px-5 py-3 font-medium">Customer</th>
                    <th className="px-5 py-3 font-medium">Movie</th>
                    <th className="px-5 py-3 font-medium">Seats</th>
                    <th className="px-5 py-3 font-medium">Amount</th>
                    <th className="px-5 py-3 font-medium">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {recentBookings.map((b) => (
                    <tr key={b.id} className="border-b border-border/40 last:border-0 hover:bg-secondary/40">
                      <td className="px-5 py-3 font-mono text-xs">{b.id}</td>
                      <td className="px-5 py-3 font-medium">{b.user}</td>
                      <td className="px-5 py-3 text-muted-foreground">{b.movie}</td>
                      <td className="px-5 py-3 text-muted-foreground">{b.seats}</td>
                      <td className="px-5 py-3 font-semibold">{b.amount}</td>
                      <td className="px-5 py-3">
                        <span className={`rounded-full px-2.5 py-1 text-xs font-medium ${statusStyles[b.status]}`}>
                          {b.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}
