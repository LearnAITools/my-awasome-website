import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'

export default function RefundPolicyPage() {
  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <div className="mx-auto max-w-3xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="mb-12">
            <h1 className="text-4xl font-bold tracking-tight mb-2">Refund Policy</h1>
            <p className="text-muted-foreground">Last updated: June 2026</p>
          </div>

          <div className="prose prose-invert max-w-none space-y-6 text-muted-foreground">
            <section>
              <h2 className="text-2xl font-bold text-foreground mb-3">Overview</h2>
              <p>
                At Cinemax, we want you to be completely satisfied with your movie booking experience. This
                Refund Policy outlines the terms and conditions for refunds and cancellations.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-bold text-foreground mb-3">Cancellation & Refund Terms</h2>
              <ul className="space-y-2 list-disc list-inside">
                <li>Bookings can be cancelled up to 1 hour before the showtime</li>
                <li>Refunds for cancellations made 1+ hour before showtime: Full refund</li>
                <li>Refunds for cancellations within 1 hour: 50% of booking amount</li>
                <li>Refunds are processed within 3-5 business days</li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-bold text-foreground mb-3">Non-Refundable Cases</h2>
              <ul className="space-y-2 list-disc list-inside">
                <li>Bookings for past shows or expired shows</li>
                <li>Cancelled or rescheduled shows by the cinema</li>
                <li>Forfeited tickets (no-show)</li>
                <li>Promo codes or special offers cannot be refunded as cash</li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-bold text-foreground mb-3">Refund Process</h2>
              <p>
                Refunds are issued to the original payment method used for the booking. In case of issues with
                refunds, please contact our support team with your booking reference number.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-bold text-foreground mb-3">Contact Us</h2>
              <p>
                For refund-related queries, please contact: support@cinemax.com or call 1800-XXX-CINE
              </p>
            </section>
          </div>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
