/**
 * Authentication service response DTOs
 * Matches backend auth-service response DTOs
 */

/**
 * Authentication response with JWT token
 * Matches AuthResponse in backend
 */
export interface AuthResponse {
  token: string
  message: string
  userId: number
  email: string
  fullName: string
  role: string
}

/**
 * User profile response
 * Matches UserProfileResponse in backend
 */
export interface UserProfileResponse {
  userId: number
  email: string
  fullName: string
  role: string
  profilePictureUrl?: string | null
}

/**
 * User summary DTO for inter-service communication
 * Matches UserSummaryDto in backend common module
 */
export interface UserSummary {
  userId: number
  email: string
  fullName: string
  role: string
}

/**
 * User identity summary with active status
 * Matches UserIdentitySummary in backend
 */
export interface UserIdentitySummary extends UserSummary {
  active: boolean
}

/**
 * Email change OTP response
 */
export interface EmailChangeOtpResponse {
  message: string
  expiresIn: number // Time in seconds
}
