import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { Mail, Phone, MapPin } from 'lucide-react'
import { Button } from '@/components/ui/button'

export default function ContactPage() {
  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="mb-12">
            <h1 className="text-4xl font-bold tracking-tight mb-4">Contact Us</h1>
            <p className="text-lg text-muted-foreground max-w-3xl">
              Have questions or feedback? We'd love to hear from you. Reach out to our support team.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-12">
            <div className="rounded-xl border border-border/60 bg-card p-8">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4">
                <Mail className="size-6 text-primary" />
              </div>
              <h3 className="text-lg font-bold mb-2">Email</h3>
              <p className="text-sm text-muted-foreground mb-2">support@cinemax.com</p>
              <p className="text-xs text-muted-foreground">We'll respond within 24 hours</p>
            </div>

            <div className="rounded-xl border border-border/60 bg-card p-8">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4">
                <Phone className="size-6 text-primary" />
              </div>
              <h3 className="text-lg font-bold mb-2">Phone</h3>
              <p className="text-sm text-muted-foreground mb-2">1800-XXX-CINE</p>
              <p className="text-xs text-muted-foreground">Monday - Friday, 9AM - 9PM</p>
            </div>

            <div className="rounded-xl border border-border/60 bg-card p-8">
              <div className="flex size-12 items-center justify-center rounded-lg bg-primary/15 mb-4">
                <MapPin className="size-6 text-primary" />
              </div>
              <h3 className="text-lg font-bold mb-2">Address</h3>
              <p className="text-sm text-muted-foreground mb-2">Mumbai, India</p>
              <p className="text-xs text-muted-foreground">Headquarters</p>
            </div>
          </div>

          <div className="rounded-xl border border-border/60 bg-card p-8 max-w-2xl">
            <h2 className="text-2xl font-bold mb-6">Get in Touch</h2>
            <form className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-2">Name</label>
                <input
                  type="text"
                  placeholder="Your name"
                  className="w-full rounded-lg border border-border bg-background px-4 py-2 text-sm outline-none focus:border-primary"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">Email</label>
                <input
                  type="email"
                  placeholder="your@email.com"
                  className="w-full rounded-lg border border-border bg-background px-4 py-2 text-sm outline-none focus:border-primary"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-2">Message</label>
                <textarea
                  placeholder="Your message here..."
                  rows={5}
                  className="w-full rounded-lg border border-border bg-background px-4 py-2 text-sm outline-none focus:border-primary"
                ></textarea>
              </div>

              <Button className="w-full">Send Message</Button>
            </form>
          </div>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
