import Link from "next/link"
import { Clapperboard } from "lucide-react"

const columns = [
  {
    title: "Movies",
    links: [
      { label: "Now Showing", href: "/#now-showing" },
      { label: "Coming Soon", href: "/#coming-soon" },
      { label: "Exclusives", href: "/exclusives" },
      { label: "Film Festivals", href: "/festivals" },
    ],
  },
  {
    title: "Cinemas",
    links: [
      { label: "IMAX", href: "/cinemas/imax" },
      { label: "4DX", href: "/cinemas/4dx" },
      { label: "Recliners", href: "/cinemas/recliners" },
      { label: "Find a Cinema", href: "/cinemas" },
    ],
  },
  {
    title: "Company",
    links: [
      { label: "About Us", href: "/about" },
      { label: "Careers", href: "/careers" },
      { label: "Press", href: "/press" },
      { label: "Contact", href: "/contact" },
    ],
  },
  {
    title: "Help",
    links: [
      { label: "FAQs", href: "/faqs" },
      { label: "Refund Policy", href: "/refund-policy" },
      { label: "Terms of Use", href: "/terms" },
      { label: "Privacy", href: "/privacy" },
    ],
  },
]

export function SiteFooter() {
  return (
    <footer className="border-t border-border/60 bg-card/40">
      <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="grid grid-cols-2 gap-8 md:grid-cols-5">
          <div className="col-span-2 md:col-span-1">
            <Link href="/" className="flex items-center gap-2">
              <span className="flex size-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
                <Clapperboard className="size-5" />
              </span>
              <span className="text-lg font-bold tracking-tight">
                Cine<span className="text-primary">max</span>
              </span>
            </Link>
            <p className="mt-4 text-sm leading-relaxed text-muted-foreground">
              Premium movie booking. Pick your film, pick your seat, skip the line.
            </p>
          </div>
          {columns.map((col) => (
            <div key={col.title}>
              <h3 className="text-sm font-semibold">{col.title}</h3>
              <ul className="mt-4 flex flex-col gap-2.5">
                {col.links.map((link) => (
                  <li key={link.label}>
                    <Link
                      href={link.href}
                      className="text-sm text-muted-foreground transition-colors hover:text-foreground"
                    >
                      {link.label}
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
        <div className="mt-10 flex flex-col items-center justify-between gap-4 border-t border-border/60 pt-6 sm:flex-row">
          <p className="text-sm text-muted-foreground">
            © 2026 Cinemax Entertainment. All rights reserved.
          </p>
          <p className="text-sm text-muted-foreground">Made for movie lovers.</p>
        </div>
      </div>
    </footer>
  )
}
