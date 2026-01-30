# Repbah Booking System - Architecture

## Data Model
The app manages **Bookings** for a real estate photographer.
- **Source of Truth:** Firebase Firestore (Cloud Database).
- **Data Object:** `Booking` class containing client details, property type (Apartment/House), date, time, and status (Pending/Confirmed/Cancelled).
- **Conflict Resolution:** Firestore default "Last-Write-Wins" policy is used.

## Architecture Diagram
The app follows the **MVVM (Model-View-ViewModel)** pattern recommended by Google.

[Firestore Cloud]
↕ (Sync)
[BookingRepository] -> Handles data fetching & offline caching
↕ (Flow/Coroutines)
[ViewModel] -> Holds UI State (e.g., DashboardViewModel)
↕ (StateFlow)
[Compose UI] -> Displays data (e.g., DashboardScreen)

## Technical Challenge Solved
**Challenge:** Handling complex navigation while passing data objects.
**Solution:** I implemented Jetpack Navigation with `GSON` serialization. Since complex objects cannot be passed directly between screens in Compose, I serialized the `Booking` object into a JSON string, URL-encoded it to handle special characters, and passed it as a navigation argument to the Details screen.

## Security
- **Public Access:** The web form allows unauthenticated creation of bookings.
- **Admin Access:** The Android app requires Firebase Authentication to read or modify data.