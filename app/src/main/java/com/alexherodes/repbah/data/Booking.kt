package com.alexherodes.repbah.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Booking(
    val id: String = "",
    val clientName: String = "",
    val date: String = "",
    val time: String = "",
    val propertyType: String = "",
    val details: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val comments: String = "",
    val status: String = "pending",
    @ServerTimestamp val createdAt: Date? = null
)