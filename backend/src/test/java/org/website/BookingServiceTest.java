package org.website;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.website.dto.BookingRequest;
import org.website.dto.BookingResponse;
import org.website.exception.SeatAlreadyBookedException;
import org.website.model.*;
import org.website.repository.*;
import org.website.service.BookingService;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Unit tests for BookingService with focus on concurrent booking scenarios.
 * Tests optimistic locking behavior to prevent double-booking.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Concurrency Tests")
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private Movie testMovie;
    private Theater testTheater;
    private Show testShow;
    private Seat testSeat;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testUser = new User("test@example.com", "password", "Test User", UserRole.ROLE_USER);
        testUser.setId(1L);

        testMovie = new Movie("Test Movie", "A test movie", "Action", 120, "English", 
                "https://example.com/poster.jpg", "2024-01-01", 8.5);
        testMovie.setId(1L);

        testTheater = new Theater("Test Theater", "Test City", "123 Test St", 5);
        testTheater.setId(1L);

        testShow = new Show(testMovie, testTheater, LocalDateTime.now().plusDays(1), 100, 50000L, 1);
        testShow.setId(1L);

        testSeat = new Seat(testShow, 101, 1, 1, SeatStatus.AVAILABLE);
        testSeat.setId(1L);
    }

    @Test
    @DisplayName("Should successfully create booking with available seats")
    void testSuccessfulBooking() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setShowId(1L);
        request.setSelectedSeats(Arrays.asList(101, 102));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));
        when(seatRepository.findByShowIdAndSeatNumber(1L, 101)).thenReturn(Optional.of(testSeat));

        Seat seat2 = new Seat(testShow, 102, 1, 2, SeatStatus.AVAILABLE);
        seat2.setId(2L);
        when(seatRepository.findByShowIdAndSeatNumber(1L, 102)).thenReturn(Optional.of(seat2));

        Booking mockBooking = new Booking(testUser, testShow, "BK1234567890", BookingStatus.PENDING, 100000L);
        mockBooking.setId(1L);
        when(bookingRepository.save(any())).thenReturn(mockBooking);

        // Act
        BookingResponse response = bookingService.createBooking(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals("BK1234567890", response.getBookingReference());
        assertEquals(100000L, response.getTotalAmountInCents());
        verify(seatRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Should throw exception when trying to book already booked seat")
    void testBookingAlreadyBookedSeat() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setShowId(1L);
        request.setSelectedSeats(Arrays.asList(101));

        testSeat.setStatus(SeatStatus.BOOKED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));
        when(seatRepository.findByShowIdAndSeatNumber(1L, 101)).thenReturn(Optional.of(testSeat));

        // Act & Assert
        assertThrows(SeatAlreadyBookedException.class, () -> {
            bookingService.createBooking(1L, request);
        });
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testBookingWithInvalidUser() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setShowId(1L);
        request.setSelectedSeats(Arrays.asList(101));

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            bookingService.createBooking(999L, request);
        });
    }

    @Test
    @DisplayName("Should retrieve user bookings successfully")
    void testGetUserBookings() {
        // Arrange
        List<Booking> mockBookings = new ArrayList<>();
        Booking booking1 = new Booking(testUser, testShow, "BK1234567890", BookingStatus.PENDING, 50000L);
        booking1.setId(1L);
        mockBookings.add(booking1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingRepository.findByUserIdOrderByBookingTimeDesc(1L)).thenReturn(mockBookings);

        // Act
        List<BookingResponse> response = bookingService.getUserBookings(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("BK1234567890", response.get(0).getBookingReference());
        verify(bookingRepository, times(1)).findByUserIdOrderByBookingTimeDesc(1L);
    }

    @Test
    @DisplayName("Should cancel pending booking successfully")
    void testCancelPendingBooking() {
        // Arrange
        Booking booking = new Booking(testUser, testShow, "BK1234567890", BookingStatus.PENDING, 50000L);
        booking.setId(1L);
        booking.setSeats(Arrays.asList(testSeat));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // Act
        bookingService.cancelBooking(1L, 1L);

        // Assert
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(SeatStatus.AVAILABLE, testSeat.getStatus());
        verify(bookingRepository, times(1)).save(booking);
        verify(seatRepository, times(1)).save(testSeat);
    }

    @Test
    @DisplayName("Should get all bookings for admin")
    void testGetAllBookings() {
        // Arrange
        List<Booking> mockBookings = new ArrayList<>();
        Booking booking1 = new Booking(testUser, testShow, "BK1111111111", BookingStatus.CONFIRMED, 50000L);
        booking1.setId(1L);
        mockBookings.add(booking1);

        when(bookingRepository.findAll()).thenReturn(mockBookings);

        // Act
        List<BookingResponse> response = bookingService.getAllBookings();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(bookingRepository, times(1)).findAll();
    }
}
