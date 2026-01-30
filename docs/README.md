# Repbah Booking System

A professional full-stack booking ecosystem designed for a real estate photography business. The system consists of a **public-facing web portal** for clients and a **native Android Admin App** for business management.



## 🛠 System Architecture
The project follows the **MVVM (Model-View-ViewModel)** pattern and utilizes a serverless architecture for real-time synchronization.

* **Frontend (Web):** HTML5/JavaScript using Firebase JS SDK. Implements dynamic availability logic to prevent double bookings.
* **Backend (Database):** Google Firebase Firestore (NoSQL) with real-time listeners.
* **Admin App (Android):** Built with Jetpack Compose, Kotlin Coroutines, and WorkManager for background notifications.

---

## 🚀 Key Features

### 1. Dynamic Availability Engine
The system solves the "Double Booking" problem by cross-referencing two data sources before showing available times to a client:
* **Existing Bookings:** Any `pending` or `confirmed` slot in the `bookings` collection is automatically hidden from the web portal.
* **Manual Blocks:** The admin can block entire days (holidays) or specific timeslots (personal appointments) via the app, which immediately updates the website UI.

### 2. Admin Automation (Android Intents)
To streamline workflow, the app integrates deeply with the Android OS:
* **Communication:** One-click calling and emailing via `Intent.ACTION_DIAL` and `Intent.ACTION_SENDTO`.
* **Email Templates:** Confirming or cancelling a booking automatically drafts a personalized email to the client, reducing manual typing.
* **Calendar Integration:** Confirmed shoots can be exported to the system's Google Calendar with a single tap.

### 3. Background Notifications
Utilizes `WorkManager` to poll Firestore every 15 minutes. Even if the app is closed, the admin receives a system notification the moment a new booking is submitted on the website.

---

## 📊 Data Model

| Collection | Document ID | Key Fields |
| :--- | :--- | :--- |
| **`bookings`** | Auto-generated | `clientName`, `address`, `email`, `time`, `status`, `propertyType` |
| **`blocked_days`** | `dd.mm.yyyy` | `fullDay` (Boolean), `times` (List of strings) |

---

## 🔐 Security Policy
Implemented granular **Firestore Security Rules** to balance public usability with data privacy:
* **Public:** Can `create` bookings and `read` the availability schedule (to see which days are blocked).
* **Admin:** Full `CRUD` (Create, Read, Update, Delete) access to all collections, protected by **Firebase Authentication**.

---

## 🛠 Technical Challenges & Solutions

### Serialization for Navigation
**Challenge:** Jetpack Compose Navigation does not support passing complex objects between screens.
**Solution:** Implemented `GSON` serialization to convert `Booking` objects into JSON strings. These strings are URL-encoded for safe transmission via NavArgs and de-serialized on the recipient screen, ensuring a seamless data flow without extra database hits.

### NoSQL Availability Logic
**Challenge:** Efficiently querying "available" slots in a non-relational database.
**Solution:** Instead of complex joins, the system uses a "Document ID per Date" strategy for blocked days. This allows the website to perform a single `getDoc()` request to check an entire day's availability, significantly improving frontend performance.

---

## ⚙️ How to Run

### 1. Prerequisites
* **Android Studio** (Ladybug or newer).
* **Firebase Project** (Create one at [console.firebase.google.com](https://console.firebase.google.com)).

### 2. Firebase Setup
1. Enable **Email/Password Authentication**.
2. Create a **Firestore Database** in test mode.
3. In Project Settings, add an **Android App** and download the `google-services.json`.
4. Place `google-services.json` into the `app/` folder of the Android project.

### 3. Web Setup
1. In Firebase Console, add a **Web App**.
2. Copy the `firebaseConfig` object.
3. Replace the configuration constants in the `<script>` tag of `booking.html` with your own keys.

### 4. Deploying Security Rules
Copy the content of `firestore.rules` from this repository and paste it into the **Rules** tab of your Firestore Database in the Firebase Console.

### 5. Build and Launch
* Open the project in Android Studio.
* Sync Gradle files.
* Run the app on an emulator or physical device.
* Open `booking.html` in any browser to submit a test booking.
