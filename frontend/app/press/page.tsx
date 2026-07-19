import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { Award, Newspaper, TrendingUp } from 'lucide-react'

export default function PressPage() {
  const pressReleases = [
    {
      id: 1,
      title: 'Cinemax Reaches 10 Million Users',
      date: 'June 1, 2026',
      excerpt: 'Cinemax announces major milestone with 10 million registered users across India.',
    },
    {
      id: 2,
      title: 'New Premium Features Launched',
      date: 'May 15, 2026',
      excerpt: 'Introducing exclusive premiere access and premium membership tiers for Cinemax users.',
    },
    {
      id: 3,
      title: 'Partnership with Leading Cinema Chains',
      date: 'May 1, 2026',
      excerpt: 'Cinemax partners with major cinema chains to expand booking availability.',
    },
  ]

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="mb-12">
            <h1 className="text-4xl font-bold tracking-tight mb-4">Press & Media</h1>
            <p className="text-lg text-muted-foreground max-w-3xl">
              Latest news and updates from Cinemax. For media inquiries, please contact press@cinemax.com
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
            <div className="rounded-xl border border-border/60 bg-card p-8 text-center">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4 mx-auto">
                <Award className="size-6 text-primary" />
              </div>
              <p className="text-3xl font-bold mb-2">25+</p>
              <p className="text-sm text-muted-foreground">Media Awards</p>
            </div>

            <div className="rounded-xl border border-border/60 bg-card p-8 text-center">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4 mx-auto">
                <Newspaper className="size-6 text-primary" />
              </div>
              <p className="text-3xl font-bold mb-2">150+</p>
              <p className="text-sm text-muted-foreground">Press Features</p>
            </div>

            <div className="rounded-xl border border-border/60 bg-card p-8 text-center">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4 mx-auto">
                <TrendingUp className="size-6 text-primary" />
              </div>
              <p className="text-3xl font-bold mb-2">10M+</p>
              <p className="text-sm text-muted-foreground">Active Users</p>
            </div>
          </div>

          <div>
            <h2 className="text-2xl font-bold mb-6">Latest Press Releases</h2>
            <div className="space-y-4">
              {pressReleases.map((release) => (
                <div
                  key={release.id}
                  className="rounded-xl border border-border/60 bg-card p-6 hover:border-primary/50 transition-colors cursor-pointer"
                >
                  <p className="text-xs font-semibold text-primary mb-2">{release.date}</p>
                  <h3 className="text-lg font-bold mb-2">{release.title}</h3>
                  <p className="text-sm text-muted-foreground">{release.excerpt}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
