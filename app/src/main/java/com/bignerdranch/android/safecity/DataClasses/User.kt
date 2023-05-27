package com.bignerdranch.android.safecity.DataClasses

import java.time.LocalDate

data class User(val id: Int, val login: String, val password: String, val name: String, val surname: String,
                val birthdate: LocalDate, val gender: String)