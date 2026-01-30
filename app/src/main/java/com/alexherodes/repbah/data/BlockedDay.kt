package com.alexherodes.repbah.data

data class BlockedDay(
    val date: String = "",       // ID: "dd.mm.yyyy"
    val fullDay: Boolean = false,
    val times: List<String> = emptyList() // e.g. ["10:00-11:00"]
)