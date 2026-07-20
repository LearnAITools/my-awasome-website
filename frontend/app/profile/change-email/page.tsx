'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { Button } from '@/components/ui/button'
import { authService, type AuthUser } from '@/lib/api'

export default function ChangeEmailPage() {
  const router = useRouter()
  const [user, setUser] = useState<AuthUser | null>(null)
  const [newEmail, setNewEmail] = useState('')
  const [otp, setOtp] = useState('')
  const [step, setStep] = useState<'request' | 'verify'>('request')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    const currentUser = authService.getCurrentUser()
    if (!currentUser) {
      router.push('/auth/login')
      return
    }

    setUser(currentUser)
    setLoading(false)
  }, [router])

  const handleRequestOtp = async (event: React.FormEvent) => {
    event.preventDefault()
    setError('')
    setSubmitting(true)

    try {
      const response = await authService.requestEmailChangeOtp(newEmail)
      setMessage(response.message || 'OTP sent. Check your email.')
      setStep('verify')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to request OTP. Please try again.')
    } finally {
      setSubmitting(false)
    }
  }

  const handleVerifyOtp = async (event: React.FormEvent) => {
    event.preventDefault()
    setError('')
    setSubmitting(true)

    try {
      await authService.verifyEmailChangeOtp(newEmail, otp)
      router.push('/profile')
    } catch (err: any) {
      setError(err.response?.data?.message || 'OTP verification failed. Please try again.')
    } finally {
      setSubmitting(false)
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
          <h1 className="text-2xl font-bold mb-4">Change Email</h1>
          <p className="text-sm text-muted-foreground mb-6">
            Update your email address securely with OTP verification.
          </p>

          {message && (
            <div className="mb-4 rounded-lg bg-primary/10 p-3 text-sm text-primary">
              {message}
            </div>
          )}
          {error && (
            <div className="mb-4 rounded-lg bg-destructive/10 p-3 text-sm text-destructive">
              {error}
            </div>
          )}

          {step === 'request' ? (
            <form className="space-y-6" onSubmit={handleRequestOtp}>
              <div>
                <label htmlFor="newEmail" className="block text-sm font-medium mb-2">
                  New Email Address
                </label>
                <input
                  id="newEmail"
                  type="email"
                  value={newEmail}
                  onChange={(e) => setNewEmail(e.target.value)}
                  placeholder="new-email@example.com"
                  required
                  className="w-full rounded-lg border border-border bg-background px-4 py-2 text-sm outline-none focus:border-primary"
                />
              </div>

              <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <Button type="submit" disabled={submitting || !newEmail}>
                  {submitting ? 'Sending OTP...' : 'Send OTP'}
                </Button>
                <Button type="button" variant="secondary" onClick={() => router.push('/profile')}>
                  Cancel
                </Button>
              </div>
            </form>
          ) : (
            <form className="space-y-6" onSubmit={handleVerifyOtp}>
              <div>
                <label htmlFor="otp" className="block text-sm font-medium mb-2">
                  Enter OTP
                </label>
                <input
                  id="otp"
                  type="text"
                  value={otp}
                  onChange={(e) => setOtp(e.target.value)}
                  placeholder="123456"
                  required
                  className="w-full rounded-lg border border-border bg-background px-4 py-2 text-sm outline-none focus:border-primary"
                />
              </div>

              <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <Button type="submit" disabled={submitting || !otp}>
                  {submitting ? 'Verifying...' : 'Verify OTP'}
                </Button>
                <Button type="button" variant="secondary" onClick={() => setStep('request')}>
                  Change email
                </Button>
              </div>
            </form>
          )}
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
