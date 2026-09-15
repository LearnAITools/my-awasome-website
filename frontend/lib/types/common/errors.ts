/**
 * Common error types and interfaces for API communication
 */

/**
 * Standard error response from backend API
 * Matches ErrorResponse DTO in backend common module
 */
export interface ApiErrorResponse {
  error_code: string // e.g., "ERR001", "ERR100"
  message: string
  status: number
  error_type: string
  timestamp: string // ISO 8601 datetime
  path: string
  details?: string
}

/**
 * Error types for different service categories
 */
export enum ErrorType {
  // Auth errors
  AUTH_INVALID_CREDENTIALS = 'AUTH_INVALID_CREDENTIALS',
  AUTH_UNAUTHORIZED_ACCESS = 'AUTH_UNAUTHORIZED_ACCESS',
  AUTH_TOKEN_EXPIRED = 'AUTH_TOKEN_EXPIRED',
  AUTH_USER_NOT_FOUND = 'AUTH_USER_NOT_FOUND',
  AUTH_EMAIL_ALREADY_EXISTS = 'AUTH_EMAIL_ALREADY_EXISTS',

  // Booking errors
  BOOKING_NOT_FOUND = 'BOOKING_NOT_FOUND',
  BOOKING_INVALID_STATE = 'BOOKING_INVALID_STATE',
  BOOKING_EXPIRED = 'BOOKING_EXPIRED',
  SEAT_ALREADY_BOOKED = 'SEAT_ALREADY_BOOKED',
  SEAT_INVALID_STATUS = 'SEAT_INVALID_STATUS',
  SHOW_NOT_FOUND = 'SHOW_NOT_FOUND',

  // Payment errors
  PAYMENT_FAILED = 'PAYMENT_FAILED',
  PAYMENT_INVALID_SIGNATURE = 'PAYMENT_INVALID_SIGNATURE',
  PAYMENT_INVALID_AMOUNT = 'PAYMENT_INVALID_AMOUNT',
  PAYMENT_ORDER_NOT_FOUND = 'PAYMENT_ORDER_NOT_FOUND',

  // Common errors
  RESOURCE_NOT_FOUND = 'RESOURCE_NOT_FOUND',
  VALIDATION_FAILED = 'VALIDATION_FAILED',
  INTERNAL_SERVER_ERROR = 'INTERNAL_SERVER_ERROR',
}

/**
 * Error code mapping to HTTP status
 */
export const ERROR_CODE_TO_STATUS: Record<string, number> = {
  ERR001: 401, // Unauthorized
  ERR100: 404, // Not Found
  ERR201: 409, // Seat already booked
  ERR202: 400, // Seat invalid status
  ERR203: 400, // Booking invalid state
  ERR205: 408, // Booking expired
  ERR300: 400, // Payment failed
  ERR301: 400, // Payment invalid signature
  ERR302: 400, // Payment invalid amount
}

/**
 * Parsed API error with structured information
 */
export class ParsedApiError extends Error {
  public readonly errorCode: string
  public readonly errorType: ErrorType
  public readonly status: number
  public readonly timestamp: string
  public readonly path: string
  public readonly details?: string

  constructor(response: ApiErrorResponse) {
    super(response.message)
    this.name = 'ParsedApiError'
    this.errorCode = response.error_code
    this.errorType = (response.error_type as ErrorType) || ErrorType.INTERNAL_SERVER_ERROR
    this.status = response.status
    this.timestamp = response.timestamp
    this.path = response.path
    this.details = response.details
  }
}
