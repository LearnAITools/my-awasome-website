# TicketToMyShow Agent Guide

This repository already contains the app structure you need. Use the existing Next.js frontend under frontend/ and the Spring Boot backend under backend/ instead of creating a separate Vite project or a second app root.

## Current project shape
- Frontend: Next.js App Router in frontend/app with shared UI in frontend/components
- Backend: Spring Boot services/controllers/repositories in backend/src/main/java
- Auth: JWT-based flows through frontend/lib/api.ts and backend security configuration
- Booking flow: seat selection, booking creation, payment integration, and admin screens

## Working rules
1. Keep changes inside the existing frontend/ and backend/ folders.
2. Prefer Next.js App Router conventions for new pages and components.
3. Keep backend changes in the layered structure: controller -> service -> repository -> model.
4. Use DTOs for API payloads and avoid exposing entities directly.
5. Preserve the existing booking safeguards such as optimistic locking for seat reservations.
6. Do not create duplicate frontend folders, Vite files, or a new ticketing-app root.

## Required implementation checklist
- Add or update features in the existing app rather than starting from scratch.
- Make sure auth-protected routes are gated correctly.
- Keep the UI accessible with meaningful button labels and images that have alt text.
- Keep the documentation set minimal and current.

## Expected output style
- Frontend work should land in frontend/app or frontend/components.
- Backend work should land in backend/src/main/java with matching tests under backend/src/test/java when appropriate.
- If a feature changes the user flow, verify that the existing app still runs cleanly and that no obsolete files remain.
