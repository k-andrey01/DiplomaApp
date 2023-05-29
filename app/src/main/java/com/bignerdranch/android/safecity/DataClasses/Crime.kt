package com.bignerdranch.android.safecity.DataClasses

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class Crime(
    val id: Integer,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    val timeCrime: LocalDateTime,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    val timeRecord: LocalDateTime,
    val comment: String,
    val address: Int,
    val type: Int,
    val witness: Int
)
