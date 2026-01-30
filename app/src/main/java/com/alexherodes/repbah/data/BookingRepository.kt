package com.alexherodes.repbah.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BookingRepository {
    private val db = FirebaseFirestore.getInstance()

    // 1. Read: Listen for database changes in real-time
    fun getBookings(): Flow<List<Booking>> = callbackFlow {
        val subscription = db.collection("bookings")
            .orderBy("createdAt", Query.Direction.DESCENDING) // Newest bookings first
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Close stream if internet/auth fails
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val bookings = snapshot.documents.mapNotNull { doc ->
                        // Convert database JSON to our Booking object
                        doc.toObject(Booking::class.java)?.copy(id = doc.id)
                    }
                    trySend(bookings) // Send list to the screen
                }
            }

        // Clean up the connection when the screen is closed
        awaitClose { subscription.remove() }
    }

    // 2. Write: Save changes to the database (THIS WAS MISSING)
    suspend fun updateBooking(booking: Booking) {
        try {
            // Find the document by ID and overwrite it with the new data
            db.collection("bookings").document(booking.id).set(booking).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getBlockedDay(date: String): BlockedDay? {
        return try {
            val doc = db.collection("blocked_days").document(date).get().await()
            doc.toObject(BlockedDay::class.java)?.copy(date = date)
        } catch (e: Exception) { null }
    }

    // Save blockage info
    suspend fun setBlockedDay(blockedDay: BlockedDay) {
        db.collection("blocked_days").document(blockedDay.date).set(blockedDay).await()
    }

}