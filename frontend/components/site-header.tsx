"use client"

import { useEffect, useRef, useState } from "react"
import Link from "next/link"
import { usePathname, useRouter } from "next/navigation"
import { Clapperboard, Search, MapPin, User, LogOut, Ticket, Settings, Mail } from "lucide-react"
import { Button } from "@/components/ui/button"
import { authService, type AuthUser } from "@/lib/api"

export function SiteHeader() {
  const router = useRouter()
  const pathname = usePathname()
  const menuRef = useRef<HTMLDivElement>(null)
  const [user, setUser] = useState<AuthUser | null>(null)
  const [open, setOpen] = useState(false)

  useEffect(() => {
    const syncUser = () => {
      setUser(authService.getCurrentUser())
    }

    syncUser()
    window.addEventListener("auth_changed", syncUser)
    window.addEventListener("storage", syncUser)

    return () => {
      window.removeEventListener("auth_changed", syncUser)
      window.removeEventListener("storage", syncUser)
    }
  }, [])

  useEffect(() => {
    setUser(authService.getCurrentUser())
    setOpen(false)
  }, [pathname])

  useEffect(() => {
    const handlePointerDown = (event: PointerEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setOpen(false)
      }
    }

    document.addEventListener("pointerdown", handlePointerDown)
    return () => document.removeEventListener("pointerdown", handlePointerDown)
  }, [])

  const handleLogout = () => {
    authService.logout()
    setOpen(false)
    router.push("/")
  }

  const initial = user?.fullName?.trim()?.charAt(0).toUpperCase() || "U"

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

        {user ? (
          <div className="relative" ref={menuRef}>
            <Button
              type="button"
              size="sm"
              variant="outline"
              className="gap-2"
              aria-expanded={open}
              aria-haspopup="menu"
              onClick={() => setOpen((current) => !current)}
            >
              {user.profilePictureUrl ? (
                <img
                  src={user.profilePictureUrl}
                  alt={user.fullName}
                  className="size-5 rounded-full object-cover"
                />
              ) : (
                <span className="flex size-5 items-center justify-center rounded-full bg-primary text-[11px] font-bold text-primary-foreground">
                  {initial}
                </span>
              )}
              <span className="hidden max-w-28 truncate sm:inline">{user.fullName}</span>
            </Button>

            {open && (
              <div
                role="menu"
                className="absolute right-0 mt-2 w-72 rounded-lg border border-border bg-card p-3 shadow-xl"
              >
                <div className="flex items-center gap-3 border-b border-border/60 pb-3">
                  {user.profilePictureUrl ? (
                    <img
                      src={user.profilePictureUrl}
                      alt={user.fullName}
                      className="size-11 rounded-full object-cover"
                    />
                  ) : (
                    <span className="flex size-11 items-center justify-center rounded-full bg-primary text-base font-bold text-primary-foreground">
                      {initial}
                    </span>
                  )}
                  <div className="min-w-0">
                    <p className="truncate text-sm font-semibold">{user.fullName}</p>
                    <p className="truncate text-xs text-muted-foreground">{user.email}</p>
                    <p className="mt-0.5 text-[11px] uppercase text-muted-foreground">{user.role.replace("ROLE_", "")}</p>
                  </div>
                </div>

                <div className="mt-2 grid gap-1">
                  <Link href="/bookings" onClick={() => setOpen(false)}>
                    <Button variant="ghost" className="w-full justify-start gap-2">
                      <Ticket className="size-4" />
                      My bookings
                    </Button>
                  </Link>
                  <Link href="/profile/edit" onClick={() => setOpen(false)}>
                    <Button variant="ghost" className="w-full justify-start gap-2">
                      <Settings className="size-4" />
                      Edit profile
                    </Button>
                  </Link>
                  <Link href="/profile/change-email" onClick={() => setOpen(false)}>
                    <Button variant="ghost" className="w-full justify-start gap-2">
                      <Mail className="size-4" />
                      Change email
                    </Button>
                  </Link>
                  <Button
                    type="button"
                    variant="destructive"
                    className="mt-2 w-full justify-start gap-2"
                    onClick={handleLogout}
                  >
                    <LogOut className="size-4" />
                    Logout
                  </Button>
                </div>
              </div>
            )}
          </div>
        ) : (
          <Link href="/auth/login">
            <Button size="sm" className="gap-1.5">
              <User className="size-4" />
              <span className="hidden sm:inline">Sign in</span>
            </Button>
          </Link>
        )}
      </div>
    </header>
  )
}
