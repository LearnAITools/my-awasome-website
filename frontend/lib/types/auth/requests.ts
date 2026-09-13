/**
 * Authentication service request DTOs
 * Matches backend auth-service DTOs
 */

/**
 * Login request payload
 * Matches LoginRequest in backend
 */
export interface LoginRequest {
  email: string
  password: string
}

/**
 * Signup request payload
 * Matches SignupRequest in backend
 */
export interface SignupRequest {
  email: string
  password: string
  fullName: string
}

/**
 * Update user profile request
 * Matches UpdateProfileRequest in backend
 */
export interface UpdateProfileRequest {
  fullName: string
  profilePictureUrl?: string | null
}

/**
 * Request OTP for email change
 * Matches EmailChangeRequest in backend
 */
export interface EmailChangeRequest {
  newEmail: string
}

/**
 * Verify email change with OTP
 * Matches EmailChangeVerifyRequest in backend
 */
export interface EmailChangeVerifyRequest {
  newEmail: string
  otp: string
}
