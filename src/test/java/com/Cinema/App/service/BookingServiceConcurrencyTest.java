package com.Cinema.App.service;

import com.Cinema.App.model.*;
import com.Cinema.App.model.request.BookingRequest;
import com.Cinema.App.model.response.BookingResponse;
import com.Cinema.App.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookingServiceConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser1;
    private User testUser2;
    private Showtime testShowtime;
    private Seat testSeat;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up
        bookingRepository.deleteAll();
        showtimeRepository.deleteAll();
        seatRepository.deleteAll();
        hallRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        testUser1 = new User();
        testUser1.setUserName("user1");
        testUser1.setEmailAddress("user1@test.com");
        testUser1.setPassword("password");
        testUser1.setAccountVerified(true);
        testUser1.setActive(true);
        testUser1.getRoles().add(userRole);
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User();
        testUser2.setUserName("user2");
        testUser2.setEmailAddress("user2@test.com");
        testUser2.setPassword("password");
        testUser2.setAccountVerified(true);
        testUser2.setActive(true);
        testUser2.getRoles().add(userRole);
        testUser2 = userRepository.save(testUser2);

        // Create movie
        Movie movie = Movie.builder()
                .title("Test Movie")
                .genre("Action")
                .durationMinutes(120)
                .build();
        movie = movieRepository.save(movie);

        // Create hall
        Hall hall = Hall.builder()
                .name("Hall 1")
                .rows(5)
                .seatsPerRow(10)
                .build();
        hall = hallRepository.save(hall);

        // Create seat
        testSeat = Seat.builder()
                .hall(hall)
                .rowNumber(1)
                .seatNumber(1)
                .label("A1")
                .build();
        testSeat = seatRepository.save(testSeat);

        // Create showtime
        testShowtime = Showtime.builder()
                .movie(movie)
                .hall(hall)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .price(new BigDecimal("12.50"))
                .build();
        testShowtime = showtimeRepository.save(testShowtime);
    }

    @Test
    void testConcurrentBookingSameSeat_OnlyOneSucceeds() throws InterruptedException {
        // Simulate 10 users trying to book the same seat at the exact same time
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<Exception> exceptions = new CopyOnWriteArrayList<>();

        BookingRequest request = new BookingRequest();
        request.setShowtimeId(testShowtime.getId());
        request.setSeatId(testSeat.getId());

        // Create 10 threads that all try to book at once
        for (int i = 0; i < threadCount; i++) {
            final String userEmail = (i % 2 == 0) ? testUser1.getEmailAddress() : testUser2.getEmailAddress();

            executor.submit(() -> {
                try {
                    // Wait for all threads to be ready
                    startLatch.await();

                    // Set security context for this thread
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(userEmail, null)
                    );

                    // Try to book
                    BookingResponse response = bookingService.createBooking(request);
                    successCount.incrementAndGet();
                    System.out.println("✓ Thread " + Thread.currentThread().getName() + " succeeded");

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    exceptions.add(e);
                    System.out.println("✗ Thread " + Thread.currentThread().getName() + " failed: " + e.getMessage());
                } finally {
                    SecurityContextHolder.clearContext();
                    doneLatch.countDown();
                }
            });
        }

        // Release all threads at once
        startLatch.countDown();

        // Wait for all threads to complete
        boolean completed = doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "All threads should complete within timeout");

        // Assertions
        System.out.println("\n=== Test Results ===");
        System.out.println("Successes: " + successCount.get());
        System.out.println("Failures: " + failureCount.get());

        // Only ONE booking should succeed
        assertEquals(1, successCount.get(), "Exactly one booking should succeed");
        assertEquals(threadCount - 1, failureCount.get(), "All other bookings should fail");

        // Verify only one booking exists in DB
        List<Booking> bookings = bookingRepository.findByShowtimeId(testShowtime.getId());
        assertEquals(1, bookings.size(), "Only one booking should exist in database");
        assertEquals("CONFIRMED", bookings.get(0).getStatus());

        // All failures should be "already reserved" errors
        for (Exception e : exceptions) {
            assertTrue(e.getMessage().contains("already reserved"),
                    "Failed bookings should report seat as already reserved");
        }
    }
}
