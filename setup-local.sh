#!/bin/bash

# BookMyShow Setup Script for macOS/Linux
# This script verifies system requirements and starts both backend and frontend servers

set -e

echo "=========================================="
echo "BookMyShow Local Setup Script"
echo "=========================================="
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check Node.js version
echo "Checking Node.js version..."
if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Node.js is not installed${NC}"
    echo "   Please install Node.js v18 or higher from https://nodejs.org/"
    exit 1
fi

NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$NODE_VERSION" -lt 18 ]; then
    echo -e "${RED}❌ Node.js v18 or higher is required. Current version: $(node -v)${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Node.js $(node -v)${NC}"

# Check Java version
echo ""
echo "Checking Java version..."
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java is not installed${NC}"
    echo "   Please install Java 17 or higher from https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=version ")[^"]*' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo -e "${RED}❌ Java 17 or higher is required. Current version: $(java -version 2>&1)${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Java $(java -version 2>&1 | head -1)${NC}"

# Check Git (optional)
echo ""
echo "Checking Git..."
if command -v git &> /dev/null; then
    echo -e "${GREEN}✅ Git $(git --version)${NC}"
else
    echo -e "${YELLOW}⚠️  Git is not installed (optional)${NC}"
fi

echo ""
echo "=========================================="
echo "System Requirements Verified!"
echo "=========================================="
echo ""

# Install frontend dependencies
echo "Installing frontend dependencies..."
cd frontend
npm install
cd ..
echo -e "${GREEN}✅ Frontend dependencies installed${NC}"

echo ""
echo "=========================================="
echo "Starting BookMyShow Application"
echo "=========================================="
echo ""

# Start backend
echo "Starting backend server on port 8080..."
cd backend
mvn clean compile spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
echo -e "${GREEN}✅ Backend started (PID: $BACKEND_PID)${NC}"
echo "   Logs: backend.log"
cd ..

# Wait for backend to start
echo "Waiting for backend to be ready..."
for i in {1..30}; do
    if curl -s http://localhost:8080/h2-console > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Backend is running!${NC}"
        break
    fi
    if [ $i -eq 30 ]; then
        echo -e "${RED}❌ Backend failed to start. Check backend.log for details${NC}"
        kill $BACKEND_PID 2>/dev/null || true
        exit 1
    fi
    echo "   Attempt $i/30..."
    sleep 1
done

# Start frontend
echo ""
echo "Starting frontend server on port 5173..."
cd frontend
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo -e "${GREEN}✅ Frontend started (PID: $FRONTEND_PID)${NC}"
echo "   Logs: frontend.log"
cd ..

echo ""
echo "=========================================="
echo "✅ BookMyShow is Ready!"
echo "=========================================="
echo ""
echo "📍 Application URLs:"
echo "   Frontend: http://localhost:5173"
echo "   Backend:  http://localhost:8080"
echo "   H2 Console: http://localhost:8080/h2-console"
echo ""
echo "👤 Test Credentials:"
echo "   Email: admin@bookmyshow.com"
echo "   Password: password"
echo ""
echo "📝 Log Files:"
echo "   Backend:  backend.log"
echo "   Frontend: frontend.log"
echo ""
echo "To stop the servers, press Ctrl+C"
echo ""

# Keep the script running
wait $BACKEND_PID $FRONTEND_PID
