'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { Button } from '@/components/ui/button'
import { authService, type AuthUser } from '@/lib/api'

export default function EditProfilePage() {
  const router = useRouter()
  const [user, setUser] = useState<AuthUser | null>(null)
  const [fullName, setFullName] = useState('')
  const [profilePictureUrl, setProfilePictureUrl] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    const currentUser = authService.getCurrentUser()
    if (!currentUser) {
      router.push('/auth/login')
      return
    }

    setUser(currentUser)
    setFullName(currentUser.fullName)
    setProfilePictureUrl(currentUser.profilePictureUrl ?? '')
    setLoading(false)
  }, [router])

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault()
    setError('')
    setSaving(true)

    try {
      const updated = await authService.updateProfile({
        fullName,
        profilePictureUrl: profilePictureUrl || undefined,
      })
      setUser(updated)
      router.push('/profile')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Unable to update profile. Please try again.')
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <div className="flex min-h-screen flex-col">
        <SiteHeader />
        <main className="flex-1 flex items-center justify-center">Loading...</main>
        <SiteFooter />
      </div>
    )
  }

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1 mx-auto w-full max-w-3xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="rounded-xl border border-border/60 bg-card p-8">
          <h1 className="text-2xl font-bold mb-4">Edit Profile</h1>
          <p className="text-sm text-muted-foreground mb-6">
            Update your name or profile picture without leaving the app.
          </p>

          {error ? (
            <div className="mb-4 rounded-lg bg-destructive/10 p-3 text-sm text-destructive">
              {error}
            </div>
          ) : null}

          <form className="space-y-6" onSubmit={handleSubmit}>
            <div>
              <label htmlFor="fullName" className="block text-sm font-medium mb-2">
                Full Name
              </label>
              <input
                id="fullName"
                type="text"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                required
                className="w-full rounded-lg border border-border bg-background px-4 py-2 text-sm outline-none focus:border-primary"
              />
            </div>

            <div>
              <label htmlFor="profilePictureUrl" className="block text-sm font-medium mb-2">
                Profile Picture URL
              </label>
              <input
                id="profilePictureUrl"
                type="url"
                value={profilePictureUrl}
                onChange={(e) => setProfilePictureUrl(e.target.value)}
                placeholder="https://example.com/avatar.jpg"
                className="w-full rounded-lg border border-border bg-background px-4 py-2 text-sm outline-none focus:border-primary"
              />
            </div>

            <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <Button type="submit" disabled={saving}>
                {saving ? 'Saving...' : 'Save changes'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => router.push('/profile')}>
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
