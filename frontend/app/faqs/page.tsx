import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'
import { ChevronDown } from 'lucide-react'

export default function FAQsPage() {
  const faqs = [
    {
      question: 'How do I book movie tickets?',
      answer:
        'Browse movies, select your preferred date, time, and cinema. Choose your seats and proceed to payment. Once payment is confirmed, your tickets will be available in "My Bookings".',
    },
    {
      question: 'Can I cancel or modify my booking?',
      answer:
        'Yes, you can cancel bookings up to 1 hour before the showtime. Refunds will be processed within 3-5 business days.',
    },
    {
      question: 'What payment methods are accepted?',
      answer:
        'We accept all major credit cards, debit cards, digital wallets (Google Pay, Apple Pay), and net banking options.',
    },
    {
      question: 'How do I use my coupon code?',
      answer:
        'Enter your coupon code at checkout. The discount will be applied automatically if the code is valid.',
    },
    {
      question: 'What is Cinemax Black membership?',
      answer:
        'Cinemax Black is our premium membership program offering exclusive benefits like 25% discount every Tuesday, free upgrades, and early access to premium releases.',
    },
    {
      question: 'How do I download my e-tickets?',
      answer:
        'Once your booking is confirmed, you can download your e-tickets from "My Bookings". They can be used on mobile or printed.',
    },
  ]

  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <div className="mx-auto max-w-3xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="mb-12">
            <h1 className="text-4xl font-bold tracking-tight mb-4">Frequently Asked Questions</h1>
            <p className="text-lg text-muted-foreground">
              Find answers to common questions about Cinemax services and bookings.
            </p>
          </div>

          <div className="space-y-4">
            {faqs.map((faq, index) => (
              <details
                key={index}
                className="group rounded-lg border border-border/60 bg-card"
              >
                <summary className="flex cursor-pointer items-center justify-between p-6 hover:bg-card/80">
                  <h3 className="font-semibold">{faq.question}</h3>
                  <ChevronDown className="size-5 text-muted-foreground transition-transform group-open:rotate-180" />
                </summary>
                <div className="border-t border-border/60 px-6 py-4 text-sm text-muted-foreground">
                  {faq.answer}
                </div>
              </details>
            ))}
          </div>

          <div className="mt-12 rounded-lg border border-border/60 bg-primary/10 p-8 text-center">
            <h3 className="text-lg font-bold mb-2">Still have questions?</h3>
            <p className="text-muted-foreground mb-4">
              Can't find the answer you're looking for? Please contact our support team.
            </p>
            <a
              href="/contact"
              className="inline-block px-6 py-2 rounded-lg bg-primary text-primary-foreground font-medium hover:opacity-90"
            >
              Contact Support
            </a>
          </div>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
