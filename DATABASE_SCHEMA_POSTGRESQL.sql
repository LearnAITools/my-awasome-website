# PostgreSQL Database Schema - TicketToMyShow
# Flyway Migration: V1__Initial_schema.sql

-- ============================================
-- ROLES & AUTHENTICATION
-- ============================================

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO roles (name, description) VALUES 
('ROLE_USER', 'Regular user who can book tickets'),
('ROLE_ADMIN', 'Administrator with full access');

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED')),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ============================================
-- MOVIES
-- ============================================

CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    duration INTEGER,
    language VARCHAR(50),
    release_date DATE,
    poster_url VARCHAR(500),
    trailer_url VARCHAR(500),
    description TEXT,
    rating DECIMAL(3,1),
    status VARCHAR(50) DEFAULT 'NOW_SHOWING' CHECK (status IN ('NOW_SHOWING', 'COMING_SOON', 'ENDED')),
    content_rating VARCHAR(20),  -- G, PG, PG-13, R, etc.
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP
);

-- ============================================
-- CINEMAS & SCREENS
-- ============================================

CREATE TABLE theaters (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100),
    address VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE screens (
    id BIGSERIAL PRIMARY KEY,
    theater_id BIGINT NOT NULL,
    screen_number VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL,
    features VARCHAR(500),  -- JSON: ["IMAX","4DX","3D","DOLBY_ATMOS"]
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (theater_id) REFERENCES theaters(id) ON DELETE CASCADE,
    UNIQUE(theater_id, screen_number)
);

-- ============================================
-- SHOWS & SEATS
-- ============================================

CREATE TABLE shows (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    screen_id BIGINT NOT NULL,
    show_time TIMESTAMP NOT NULL,
    price_in_cents INTEGER NOT NULL,
    available_seats INTEGER,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (screen_id) REFERENCES screens(id)
);

CREATE TABLE show_seats (
    id BIGSERIAL PRIMARY KEY,
    show_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    row_num INTEGER NOT NULL,
    column_num INTEGER NOT NULL,
    status VARCHAR(50) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'HELD', 'BOOKED', 'BLOCKED')),
    seat_type VARCHAR(50) DEFAULT 'STANDARD' CHECK (seat_type IN ('STANDARD', 'VIP', 'WHEELCHAIR', 'PREMIUM')),
    price_in_cents INTEGER,
    held_until TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (show_id) REFERENCES shows(id) ON DELETE CASCADE,
    UNIQUE(show_id, seat_number)
);

-- ============================================
-- BOOKINGS
-- ============================================

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL,
    booking_reference VARCHAR(50) UNIQUE NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'RESERVED', 'BOOKED', 'CANCELLED', 'COMPLETED')),
    total_amount_in_cents INTEGER NOT NULL,
    payment_order_id VARCHAR(255),
    qr_code BYTEA,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (show_id) REFERENCES shows(id)
);

CREATE TABLE booking_seats (
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    PRIMARY KEY (booking_id, seat_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES show_seats(id)
);

-- ============================================
-- PAYMENTS
-- ============================================

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT UNIQUE NOT NULL,
    razorpay_order_id VARCHAR(255),
    razorpay_payment_id VARCHAR(255),
    razorpay_signature VARCHAR(500),
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED')),
    amount_in_cents INTEGER NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- ============================================
-- NOTIFICATIONS
-- ============================================

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) CHECK (type IN ('EMAIL', 'SMS')),
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    content TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sent_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- AUDIT LOGGING
-- ============================================

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    action VARCHAR(50),  -- CREATE, UPDATE, DELETE
    details TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_show_id ON bookings(show_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_created_date ON bookings(created_date);
CREATE INDEX idx_shows_movie_id ON shows(movie_id);
CREATE INDEX idx_shows_screen_id ON shows(screen_id);
CREATE INDEX idx_shows_show_time ON shows(show_time);
CREATE INDEX idx_show_seats_show_id ON show_seats(show_id);
CREATE INDEX idx_show_seats_status ON show_seats(status);
CREATE INDEX idx_show_seats_held_until ON show_seats(held_until);
CREATE INDEX idx_payments_razorpay_order_id ON payments(razorpay_order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_booking_id ON payments(booking_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);

-- ============================================
-- SAMPLE DATA FOR TESTING
-- ============================================

INSERT INTO users (email, password, full_name, status, created_date)
VALUES 
    ('admin@tickettomyshow.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy6QIDM', 'Admin User', 'ACTIVE', CURRENT_TIMESTAMP),
    ('user@tickettomyshow.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy6QIDM', 'Test User', 'ACTIVE', CURRENT_TIMESTAMP);

-- Assign roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'admin@tickettomyshow.com' AND r.name = 'ROLE_ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'user@tickettomyshow.com' AND r.name = 'ROLE_USER';

-- Sample theaters
INSERT INTO theaters (name, city, address, phone, email)
VALUES 
    ('PVR Cinemas', 'Mumbai', '123 Main Street, Mumbai', '1800-123-4567', 'pvr@cinemas.com'),
    ('INOX Cinemas', 'Bangalore', '456 Tech Park, Bangalore', '1800-987-6543', 'inox@cinemas.com');

-- Sample screens
INSERT INTO screens (theater_id, screen_number, capacity, features)
SELECT id, 'Screen 1', 100, '["IMAX","4DX"]' FROM theaters WHERE name = 'PVR Cinemas';

INSERT INTO screens (theater_id, screen_number, capacity, features)
SELECT id, 'Screen 2', 80, '["3D"]' FROM theaters WHERE name = 'INOX Cinemas';

-- Sample movies
INSERT INTO movies (title, genre, duration, language, release_date, status, content_rating, created_date)
VALUES 
    ('Inception', 'Science Fiction', 148, 'English', '2010-07-16', 'NOW_SHOWING', 'PG-13', CURRENT_TIMESTAMP),
    ('Avatar 3', 'Science Fiction', 180, 'English', '2025-12-20', 'COMING_SOON', 'PG-13', CURRENT_TIMESTAMP),
    ('Jawan', 'Action', 169, 'Hindi', '2023-09-07', 'NOW_SHOWING', 'PG-13', CURRENT_TIMESTAMP);

-- Sample shows
INSERT INTO shows (movie_id, screen_id, show_time, price_in_cents, available_seats)
SELECT m.id, s.id, CURRENT_TIMESTAMP + INTERVAL '2 days', 30000, 100
FROM movies m, screens s WHERE m.title = 'Inception' AND s.screen_number = 'Screen 1';

-- Sample seats for shows (100 seats = 10x10 grid)
INSERT INTO show_seats (show_id, seat_number, row_num, column_num, status, seat_type, price_in_cents)
SELECT s.id, chr(64 + ROW_NUMBER() / 10 + 1) || (ROW_NUMBER() % 10 + 1), 
       ROW_NUMBER() / 10 + 1, ROW_NUMBER() % 10 + 1, 'AVAILABLE', 'STANDARD', 30000
FROM shows s
WHERE s.id IN (SELECT id FROM shows LIMIT 1)
AND ROW_NUMBER() OVER (ORDER BY s.id) <= 100;
