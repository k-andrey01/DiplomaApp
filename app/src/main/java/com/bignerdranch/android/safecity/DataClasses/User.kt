package com.bignerdranch.android.safecity.DataClasses

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class User(
    val id: Int,
    val login: String,
    val password: String,
    val name: String,
    val surname: String,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val birthdate: LocalDate,
    val gender: String
)