package com.bignerdranch.android.safecity

import com.bignerdranch.android.safecity.Managers.GsonApiManager
import com.bignerdranch.android.safecity.Managers.ScalarsApiManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ServerTests {
    private var baseUrl = "YOUR_URL"


    @Before
    fun setup() {
        ScalarsApiManager.initialize(baseUrl)
        GsonApiManager.initialize(baseUrl)
    }

    @Test
    fun testAddAddress() = runBlocking {
        val city = "City"
        val street = "Street"
        val houseNumber = "123"
        val coordX = 1.0
        val coordY = 2.0

        val response = ScalarsApiManager.addressApiService.addAddress(city, street, houseNumber, coordX, coordY)

        assertTrue(response>131)

        val delResponse = ScalarsApiManager.addressApiService.deleteAddress(response)

        assertEquals("Удалено", delResponse)
    }

    @Test
    fun testCrimeGet() = runBlocking {
        val response = GsonApiManager.crimeApiService.getAllCrimesForMap()

        assertTrue(!response.isEmpty())
    }

    @Test
    fun typeTest() = runBlocking {
        val response = ScalarsApiManager.typeApiService.getTypeByName("Мошенничество")

        assertEquals(17, response)
    }

    @Test
    fun userTest() = runBlocking {
        val response = ScalarsApiManager.userApiService.login("bubu", "buu")

        assertEquals("Пользователь не найден", response)
    }
}