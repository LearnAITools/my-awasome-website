export type Movie = {
  id: string
  title: string
  poster: string
  backdrop?: string
  genres: string[]
  rating: number
  votes: string
  duration: string
  language: string
  format: string[]
  releaseDate: string
  certification: string
  synopsis: string
  status: "now-showing" | "coming-soon"
  price: number
}

export const movies: Movie[] = [
  {
    id: "stellar-horizon",
    title: "Stellar Horizon",
    poster: "/posters/poster-1.png",
    backdrop: "/backdrops/backdrop-1.png",
    genres: ["Sci-Fi", "Adventure", "Drama"],
    rating: 9.2,
    votes: "84.2K",
    duration: "2h 38m",
    language: "English",
    format: ["2D", "3D", "IMAX"],
    releaseDate: "Jun 12, 2026",
    certification: "UA",
    synopsis:
      "A lone astronaut drifts to the edge of a dying star, where a discovery forces humanity to reckon with the price of survival across the cosmos.",
    status: "now-showing",
    price: 14,
  },
  {
    id: "neon-veil",
    title: "Neon Veil",
    poster: "/posters/poster-2.png",
    backdrop: "/backdrops/backdrop-2.png",
    genres: ["Thriller", "Crime", "Noir"],
    rating: 8.6,
    votes: "52.1K",
    duration: "2h 04m",
    language: "English",
    format: ["2D", "IMAX"],
    releaseDate: "Jun 05, 2026",
    certification: "A",
    synopsis:
      "A weary detective chases a ghost through the rain-soaked underbelly of a neon city, where every clue blurs the line between justice and revenge.",
    status: "now-showing",
    price: 12,
  },
  {
    id: "crown-of-embers",
    title: "Crown of Embers",
    poster: "/posters/poster-3.png",
    backdrop: "/backdrops/backdrop-3.png",
    genres: ["Fantasy", "Action", "Epic"],
    rating: 8.9,
    votes: "97.5K",
    duration: "2h 52m",
    language: "English",
    format: ["2D", "3D", "IMAX", "4DX"],
    releaseDate: "Jun 19, 2026",
    certification: "UA",
    synopsis:
      "A warrior queen must choose between her throne and her people as a kingdom burns and ancient powers awaken on the battlefield.",
    status: "now-showing",
    price: 15,
  },
  {
    id: "redline",
    title: "Redline",
    poster: "/posters/poster-4.png",
    genres: ["Action", "Thriller"],
    rating: 8.1,
    votes: "41.7K",
    duration: "1h 58m",
    language: "English",
    format: ["2D", "4DX"],
    releaseDate: "May 29, 2026",
    certification: "UA",
    synopsis:
      "An underground racer takes one final job that spirals into a high-speed game of survival through the city's hidden tunnels.",
    status: "now-showing",
    price: 12,
  },
  {
    id: "after-midnight",
    title: "After Midnight",
    poster: "/posters/poster-5.png",
    genres: ["Romance", "Drama"],
    rating: 8.4,
    votes: "33.9K",
    duration: "2h 11m",
    language: "English",
    format: ["2D"],
    releaseDate: "Jul 03, 2026",
    certification: "U",
    synopsis:
      "Two strangers share a single night across a sleepless city, discovering that the right person can change the course of a lifetime.",
    status: "coming-soon",
    price: 11,
  },
  {
    id: "the-hollow-house",
    title: "The Hollow House",
    poster: "/posters/poster-6.png",
    genres: ["Horror", "Mystery"],
    rating: 7.8,
    votes: "28.3K",
    duration: "1h 47m",
    language: "English",
    format: ["2D", "IMAX"],
    releaseDate: "Jul 10, 2026",
    certification: "A",
    synopsis:
      "A grieving family inherits a remote Victorian mansion, only to learn that the house remembers everyone who has ever entered it.",
    status: "coming-soon",
    price: 13,
  },
]

export const heroSlides = movies.filter((m) => m.backdrop)

export function getMovie(id: string) {
  return movies.find((m) => m.id === id)
}

export const showtimes = [
  { time: "10:15 AM", format: "2D", price: 11, status: "available" },
  { time: "01:30 PM", format: "3D", price: 14, status: "available" },
  { time: "04:45 PM", format: "IMAX", price: 18, status: "filling" },
  { time: "07:30 PM", format: "2D", price: 14, status: "available" },
  { time: "10:45 PM", format: "4DX", price: 20, status: "filling" },
] as const

export const cinemas = [
  "PVR Stellar — Downtown",
  "Cineplex Aurora — Riverside",
  "Grand Reel — Uptown Mall",
]
