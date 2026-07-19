'use client'

import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { Clapperboard, LogOut, User, Mail, Calendar } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { authService } from '@/lib/api'
import { useEffect, useState } from 'react'

export default function ProfilePage() {
  const router = useRouter()
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('auth_token')
    setIsLoggedIn(!!token)
    setLoading(false)
  }, [])

  const handleLogout = () => {
    authService.logout()
    router.push('/')
  }

  if (loading) {
    return (
      <div className="flex min-h-screen flex-col">
        <SiteHeader />
        <main className="flex-1 flex items-center justify-center">
          <div>Loading...</div>
        </main>
        <SiteFooter />
      </div>
    )
  }

  if (!isLoggedIn) {
    return (
      <div className="flex min-h-screen flex-col">
        <SiteHeader />
        <main className="flex-1 flex items-center justify-center px-4">
          <div className="text-center">
            <h1 className="text-2xl font-bold mb-4">Sign In Required</h1>
            <p className="text-muted-foreground mb-6">Please sign in to view your profile</p>
            <Link href="/auth/login">
              <Button>Sign In</Button>
            </Link>
          </div>
        </main>
        <SiteFooter />
      </div>
    )
  }

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1 mx-auto w-full max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Sidebar */}
          <div className="md:col-span-1">
            <div className="rounded-xl border border-border/60 bg-card p-6">
              <div className="flex items-center justify-center size-16 rounded-full bg-primary/15 mb-4 mx-auto">
                <User className="size-8 text-primary" />
              </div>
              <h2 className="text-lg font-bold text-center mb-1">User Name</h2>
              <p className="text-sm text-muted-foreground text-center mb-6">user@example.com</p>

              <div className="space-y-2">
                <Link href="/profile" className="block">
                  <Button variant="outline" className="w-full justify-start">
                    <User className="size-4 mr-2" />
                    My Profile
                  </Button>
                </Link>
                <Link href="/bookings" className="block">
                  <Button variant="outline" className="w-full justify-start">
                    <Clapperboard className="size-4 mr-2" />
                    My Bookings
                  </Button>
                </Link>
                <button onClick={handleLogout} className="w-full">
                  <Button variant="destructive" className="w-full justify-start">
                    <LogOut className="size-4 mr-2" />
                    Sign Out
                  </Button>
                </button>
              </div>
            </div>
          </div>

          {/* Main Content */}
          <div className="md:col-span-2">
            <div className="rounded-xl border border-border/60 bg-card p-8">
              <h1 className="text-2xl font-bold mb-8">My Profile</h1>

              <div className="space-y-6">
                <div>
                  <label className="block text-sm font-medium mb-2">Full Name</label>
                  <div className="p-3 rounded-lg bg-background border border-border/60">
                    <p className="text-foreground">User Name</p>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Email</label>
                  <div className="p-3 rounded-lg bg-background border border-border/60 flex items-center gap-2">
                    <Mail className="size-4 text-muted-foreground" />
                    <p className="text-foreground">user@example.com</p>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Member Since</label>
                  <div className="p-3 rounded-lg bg-background border border-border/60 flex items-center gap-2">
                    <Calendar className="size-4 text-muted-foreground" />
                    <p className="text-foreground">January 2026</p>
                  </div>
                </div>

                <div className="pt-4 border-t border-border/60">
                  <h3 className="text-lg font-semibold mb-4">Membership</h3>
                  <div className="p-4 rounded-lg bg-primary/10 border border-primary/20">
                    <p className="text-sm text-muted-foreground mb-3">
                      Upgrade to Cinemax Black for exclusive benefits
                    </p>
                    <Button className="w-full sm:w-auto">Upgrade Membership</Button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
