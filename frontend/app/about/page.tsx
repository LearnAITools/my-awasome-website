import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { Sparkles, Users, Award } from 'lucide-react'

export default function AboutPage() {
  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="mb-12">
            <h1 className="text-4xl font-bold tracking-tight mb-4">About Cinemax</h1>
            <p className="text-lg text-muted-foreground max-w-3xl">
              Cinemax is India's leading online movie ticketing platform, dedicated to making movie experiences seamless, affordable, and accessible to everyone.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
            <div className="rounded-xl border border-border/60 bg-card p-8">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4">
                <Sparkles className="size-6 text-primary" />
              </div>
              <h3 className="text-lg font-bold mb-2">Our Mission</h3>
              <p className="text-sm text-muted-foreground">
                To revolutionize the movie ticketing experience and make entertainment accessible to millions of movie lovers.
              </p>
            </div>

            <div className="rounded-xl border border-border/60 bg-card p-8">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4">
                <Users className="size-6 text-primary" />
              </div>
              <h3 className="text-lg font-bold mb-2">Our Team</h3>
              <p className="text-sm text-muted-foreground">
                A passionate team of professionals dedicated to delivering the best movie ticketing experience.
              </p>
            </div>

            <div className="rounded-xl border border-border/60 bg-card p-8">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4">
                <Award className="size-6 text-primary" />
              </div>
              <h3 className="text-lg font-bold mb-2">Awards</h3>
              <p className="text-sm text-muted-foreground">
                Recognized as India's most trusted and innovative movie ticketing platform.
              </p>
            </div>
          </div>

          <div className="rounded-xl border border-border/60 bg-card/40 p-8 mb-12">
            <h2 className="text-2xl font-bold mb-4">Why Choose Cinemax?</h2>
            <ul className="space-y-3 text-muted-foreground">
              <li className="flex gap-3">
                <span className="font-bold text-primary">✓</span>
                <span>Easy and secure online booking</span>
              </li>
              <li className="flex gap-3">
                <span className="font-bold text-primary">✓</span>
                <span>Exclusive member discounts and offers</span>
              </li>
              <li className="flex gap-3">
                <span className="font-bold text-primary">✓</span>
                <span>Wide selection of cinemas and showtimes</span>
              </li>
              <li className="flex gap-3">
                <span className="font-bold text-primary">✓</span>
                <span>24/7 customer support</span>
              </li>
              <li className="flex gap-3">
                <span className="font-bold text-primary">✓</span>
                <span>Fast and reliable payment options</span>
              </li>
            </ul>
          </div>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
