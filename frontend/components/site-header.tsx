import Link from "next/link"
import { Clapperboard, Search, MapPin, User } from "lucide-react"
import { Button } from "@/components/ui/button"

export function SiteHeader() {
  return (
    <header className="sticky top-0 z-50 border-b border-border/60 bg-background/80 backdrop-blur-xl">
      <div className="mx-auto flex h-16 max-w-7xl items-center gap-4 px-4 sm:px-6 lg:px-8">
        <Link href="/" className="flex items-center gap-2">
          <span className="flex size-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
            <Clapperboard className="size-5" />
          </span>
          <span className="text-lg font-bold tracking-tight">
            Cine<span className="text-primary">max</span>
          </span>
        </Link>

        <div className="ml-2 hidden flex-1 items-center md:flex">
          <div className="relative w-full max-w-md">
            <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
            <input
              type="search"
              placeholder="Search for movies, cinemas..."
              className="h-9 w-full rounded-lg border border-border bg-card pl-9 pr-3 text-sm outline-none transition-colors placeholder:text-muted-foreground focus:border-primary"
            />
          </div>
        </div>

        <nav className="ml-auto hidden items-center gap-1 lg:flex">
          <Link href="/#now-showing">
            <Button variant="ghost" size="sm">Now Showing</Button>
          </Link>
          <Link href="/#coming-soon">
            <Button variant="ghost" size="sm">Coming Soon</Button>
          </Link>
          <Link href="/admin">
            <Button variant="ghost" size="sm">Admin</Button>
          </Link>
        </nav>

        <button className="hidden items-center gap-1.5 rounded-lg px-2.5 py-1.5 text-sm text-muted-foreground transition-colors hover:text-foreground sm:flex">
          <MapPin className="size-4" />
          <span>Mumbai</span>
        </button>

        <Button size="sm" className="gap-1.5">
          <User className="size-4" />
          <span className="hidden sm:inline">Sign in</span>
        </Button>
      </div>
    </header>
  )
}
