import { SiteHeader } from '@/components/site-header'
import { SiteFooter } from '@/components/site-footer'

export default function PrivacyPolicyPage() {
  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main className="flex-1">
        <div className="mx-auto max-w-3xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="mb-12">
            <h1 className="text-4xl font-bold tracking-tight mb-2">Privacy Policy</h1>
            <p className="text-muted-foreground">Last updated: June 2026</p>
          </div>

          <div className="prose prose-invert max-w-none space-y-6 text-muted-foreground">
            <section>
              <h2 className="text-2xl font-bold text-foreground mb-3">Introduction</h2>
              <p>
                Cinemax ("we" or "us" or "our") operates the cinemax.com website (the "Service"). This page
                informs you of our policies regarding the collection, use, and disclosure of personal data when
                you use our Service.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-bold text-foreground mb-3">Information Collection and Use</h2>
              <p>We collect several different types of information for various purposes:</p>
              <ul className="space-y-2 list-disc list-inside">
                <li>
                  <strong>Personal Data:</strong> While using our Service, we may ask you to provide us with
                  certain personally identifiable information that can be used to contact or identify you
                  ("Personal Data")
                </li>
                <li>
                  <strong>Usage Data:</strong> We may also collect information on how the Service is accessed
                  and used ("Usage Data")
                </li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-bold text-foreground mb-3">Use of Data</h2>
              <p>Cinemax uses the collected data for various purposes:</p>
              <ul className="space-y-2 list-disc list-inside">
                <li>To provide and maintain our Service</li>
                <li>To notify you about changes to our Service</li>
                <li>To allow you to participate in interactive features of our Service</li>
                <li>To provide customer support</li>
                <li>To gather analysis or valuable information for improving our Service</li>
              </ul>
            </section>

            <section>
              <h2 className="text-2xl font-bold text-foreground mb-3">Security of Data</h2>
              <p>
                The security of your data is important to us but remember that no method of transmission over
                the Internet or method of electronic storage is 100% secure. While we strive to use commercially
                acceptable means to protect your Personal Data, we cannot guarantee its absolute security.
              </p>
            </section>

            <section>
              <h2 className="text-2xl font-bold text-foreground mb-3">Contact Us</h2>
              <p>
                If you have any questions about this Privacy Policy, please contact us at: privacy@cinemax.com
              </p>
            </section>
          </div>
        </div>
      </main>
      <SiteFooter />
    </div>
  )
}
