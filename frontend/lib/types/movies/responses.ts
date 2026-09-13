/**
 * Movie service response DTOs
 * Matches frontend Movie type from movies.ts
 */

/**
 * Movie format (2D, 3D, IMAX, 4DX)
 */
export enum MovieFormat {
  FORMAT_2D = '2D',
  FORMAT_3D = '3D',
  IMAX = 'IMAX',
  FORMAT_4DX = '4DX',
}

/**
 * Movie status
 */
export enum MovieStatus {
  NOW_SHOWING = 'now-showing',
  COMING_SOON = 'coming-soon',
}

/**
 * Movie details response
 * Aligns with Movie type from lib/movies.ts
 */
export interface MovieResponse {
  id: string
  title: string
  poster: string
  backdrop?: string
  genres: string[]
  rating: number
  votes: string
  duration: string
  language: string
  format: MovieFormat[]
  releaseDate: string
  certification: string
  synopsis: string
  status: MovieStatus
  price: number
}

/**
 * Movie list response
 */
export interface MovieListResponse {
  movies: MovieResponse[]
  total: number
  page: number
  limit: number
}

/**
 * Movie details page response
 */
export interface MovieDetailsResponse extends MovieResponse {
  releaseDate: string
  director?: string
  cast?: string[]
  runtime: number // in minutes
  budget?: number
  revenue?: number
}
