import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { Briefcase, Users, Lightbulb } from 'lucide-react'
import { Button } from '@/components/ui/button'

export default function CareersPage() {
  const jobs = [
    { id: 1, title: 'Senior Frontend Developer', location: 'Mumbai', type: 'Full-time' },
    { id: 2, title: 'Backend Engineer', location: 'Bangalore', type: 'Full-time' },
    { id: 3, title: 'Product Manager', location: 'Delhi', type: 'Full-time' },
    { id: 4, title: 'UX/UI Designer', location: 'Mumbai', type: 'Full-time' },
    { id: 5, title: 'Data Analyst', location: 'Bangalore', type: 'Full-time' },
  ]

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="mb-12">
            <h1 className="text-4xl font-bold tracking-tight mb-4">Careers at Cinemax</h1>
            <p className="text-lg text-muted-foreground max-w-3xl">
              Join our dynamic team and help us revolutionize the movie ticketing industry. We're looking for talented individuals who are passionate about technology and entertainment.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
            <div className="rounded-xl border border-border/60 bg-card p-8">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4">
                <Briefcase className="size-6 text-primary" />
              </div>
              <h3 className="text-lg font-bold mb-2">Diverse Roles</h3>
              <p className="text-sm text-muted-foreground">
                From engineering to product, design to data - find your perfect role.
              </p>
            </div>

            <div className="rounded-xl border border-border/60 bg-card p-8">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4">
                <Users className="size-6 text-primary" />
              </div>
              <h3 className="text-lg font-bold mb-2">Great Team</h3>
              <p className="text-sm text-muted-foreground">
                Work with talented professionals who are passionate about their craft.
              </p>
            </div>

            <div className="rounded-xl border border-border/60 bg-card p-8">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4">
                <Lightbulb className="size-6 text-primary" />
              </div>
              <h3 className="text-lg font-bold mb-2">Growth</h3>
              <p className="text-sm text-muted-foreground">
                Continuous learning and development opportunities for every team member.
              </p>
            </div>
          </div>

          <div>
            <h2 className="text-2xl font-bold mb-6">Open Positions</h2>
            <div className="space-y-4">
              {jobs.map((job) => (
                <div
                  key={job.id}
                  className="rounded-xl border border-border/60 bg-card p-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 hover:border-primary/50 transition-colors"
                >
                  <div>
                    <h3 className="text-lg font-bold">{job.title}</h3>
                    <p className="text-sm text-muted-foreground">
                      {job.location} • {job.type}
                    </p>
                  </div>
                  <Button>Apply Now</Button>
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
