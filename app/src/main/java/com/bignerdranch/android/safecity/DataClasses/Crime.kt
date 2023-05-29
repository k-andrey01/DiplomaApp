package com.bignerdranch.android.safecity.DataClasses

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class Crime(
    val id: Integer,
    val coordX: Double,
    val coordY: Double,
    val timeCrime: String,
    val comment: String,
    val city: String,
    val street: String,
    val house: String,
    val type: String,
    val kind: String,
    val victims: List<Victim>
)
