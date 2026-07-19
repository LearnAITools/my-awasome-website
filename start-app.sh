#!/bin/bash

# BookMyShow - Full Stack Setup Script
# This script starts both backend and frontend servers

set -e

BACKEND_DIR="/Users/shashikumar/Coding/Intellij/Organization/my-awasome-website/backend"
FRONTEND_DIR="/Users/shashikumar/Coding/Intellij/Organization/my-awasome-website/frontend"

echo "════════════════════════════════════════════════════════"
echo "   BookMyShow - Movie Ticket Booking System"
echo "════════════════════════════════════════════════════════"
echo ""

# Check if processes are already running
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "⚠️  Backend is already running on port 8080"
else
    echo "🚀 Starting Backend (Spring Boot on port 8080)..."
    cd "$BACKEND_DIR"
    mvn spring-boot:run > /tmp/backend.log 2>&1 &
    BACKEND_PID=$!
    echo "✓ Backend started (PID: $BACKEND_PID)"
    echo "  Logs: /tmp/backend.log"
    sleep 5
fi

# Check if frontend is already running
if lsof -Pi :3000 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "⚠️  Frontend is already running on port 3000"
else
    echo "🚀 Starting Frontend (Next.js on port 3000)..."
    cd "$FRONTEND_DIR"
    npm run dev > /tmp/frontend.log 2>&1 &
    FRONTEND_PID=$!
    echo "✓ Frontend started (PID: $FRONTEND_PID)"
    echo "  Logs: /tmp/frontend.log"
    sleep 3
fi

echo ""
echo "════════════════════════════════════════════════════════"
echo "✅ Both services are running!"
echo "════════════════════════════════════════════════════════"
echo ""
echo "📱 Frontend:  http://localhost:3000"
echo "🔌 Backend:   http://localhost:8080"
echo ""
echo "📚 API Docs:  http://localhost:8080/swagger-ui.html"
echo "💾 Database:  http://localhost:8082 (H2 Console)"
echo ""
echo "Press Ctrl+C to stop all services"
echo "════════════════════════════════════════════════════════"
echo ""

# Keep script running
wait
