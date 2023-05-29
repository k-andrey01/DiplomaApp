package com.bignerdranch.android.safecity.DataClasses

data class Address (
    val id: Integer,
    val city: String,
    val street: String,
    val houseNumber: String,
    val coordX: Double,
    val coordY: Double
)