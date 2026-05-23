@echo off
REM BookMyShow Setup Script for Windows
REM This script verifies system requirements and starts both backend and frontend servers

setlocal enabledelayedexpansion

echo.
echo ==========================================
echo BookMyShow Local Setup Script
echo ==========================================
echo.

REM Check Node.js version
echo Checking Node.js version...
node --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Node.js is not installed
    echo    Please install Node.js v18 or higher from https://nodejs.org/
    exit /b 1
)

for /f "tokens=*" %%i in ('node -v') do set NODE_VERSION=%%i
echo ✅ Node.js %NODE_VERSION%

REM Check Java version
echo.
echo Checking Java version...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java is not installed
    echo    Please install Java 17 or higher from https://www.oracle.com/java/technologies/downloads/
    exit /b 1
)

for /f "tokens=*" %%i in ('java -version 2^>^&1') do set JAVA_VERSION=%%i
echo ✅ %JAVA_VERSION:~0,50%

echo.
echo ==========================================
echo System Requirements Verified!
echo ==========================================
echo.

REM Install frontend dependencies
echo Installing frontend dependencies...
cd frontend
call npm install
cd ..
echo ✅ Frontend dependencies installed

echo.
echo ==========================================
echo Starting BookMyShow Application
echo ==========================================
echo.

REM Start backend
echo Starting backend server on port 8080...
cd backend
start "BookMyShow Backend" cmd /k "gradlew.bat bootRun"
cd ..
echo ✅ Backend started in new window

REM Wait for backend
echo Waiting for backend to be ready (up to 30 seconds)...
for /l %%i in (1,1,30) do (
    timeout /t 1 /nobreak > nul
    powershell -Command "try { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $r = Invoke-WebRequest -Uri 'http://localhost:8080/h2-console' -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop } catch { exit 1 }" >nul 2>&1
    if !errorlevel! equ 0 (
        echo ✅ Backend is running!
        goto backend_ok
    )
    echo    Attempt %%i/30...
)
echo ❌ Backend failed to start. Please check the backend window for errors.
exit /b 1

:backend_ok
REM Start frontend
echo.
echo Starting frontend server on port 5173...
cd frontend
start "BookMyShow Frontend" cmd /k "npm run dev"
cd ..
echo ✅ Frontend started in new window

echo.
echo ==========================================
echo ✅ BookMyShow is Ready!
echo ==========================================
echo.
echo 📍 Application URLs:
echo    Frontend: http://localhost:5173
echo    Backend:  http://localhost:8080
echo    H2 Console: http://localhost:8080/h2-console
echo.
echo 👤 Test Credentials:
echo    Email: admin@bookmyshow.com
echo    Password: password
echo.
echo Close these windows to stop the servers.
echo.

pause
