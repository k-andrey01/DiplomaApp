package com.bignerdranch.android.safecity

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun loginWithError() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Пароль").performTextInput("djUsuf")
        composeTestRule.onNodeWithText("Email").performTextInput("Юсупов")
        composeTestRule.onNodeWithText("Войти").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Вход").assertIsDisplayed()
    }
}


@RunWith(AndroidJUnit4::class)
class EditPassTest {
    @get:Rule
    val editPassTestRule = createAndroidComposeRule<EditingPasswordActivity>()
    @Test
    fun editPassWithError() {
        editPassTestRule.waitForIdle()
        Thread.sleep(2000)

        editPassTestRule.onNodeWithText("Email").performTextInput("sergeytukalsky@gmail.com")
        editPassTestRule.onNodeWithText("Отправить код").performClick()
        editPassTestRule.onNodeWithText("Код").performTextInput("sergeytukalsky@gmail.com")
        editPassTestRule.onNodeWithText("Новый пароль").performTextInput("sergeytukalsky@gmail.com")
        editPassTestRule.onNodeWithText("Сменить пароль").performClick()

        editPassTestRule.waitForIdle()
        editPassTestRule.onNodeWithText("Смена пароля").assertIsDisplayed()
    }
}

@RunWith(AndroidJUnit4::class)
class RegTest {
    @get:Rule
    val regTestRule = createAndroidComposeRule<RegistrationActivity>()
    @Test
    fun regWithError() {
        regTestRule.waitForIdle()

        regTestRule.onNodeWithText("Email").performTextInput("sergeytukalsky@gmail.com")
        regTestRule.onNodeWithText("Пароль").performClick()
        regTestRule.onNodeWithText("Подтверждение пароля").performTextInput("sergeytukalsky@gmail.cum")
        regTestRule.onNodeWithText("Фамилия").performTextInput("sergeytukalsky@gmail.com")
        regTestRule.onNodeWithText("Имя").performTextInput("sergeytukalsky@gmail.com")
        regTestRule.onNodeWithText("Зарегистрироваться").performClick()

        regTestRule.waitForIdle()
        Thread.sleep(2000)

        regTestRule.onNodeWithText("Регистрация").assertIsDisplayed()
    }
}

@RunWith(AndroidJUnit4::class)
class AddCrimeTest {
    @get:Rule
    val crimeTestRule = createAndroidComposeRule<AddingCrimeActivity>()
    @Test
    fun addCrimeWithError() {
        crimeTestRule.waitForIdle()

        crimeTestRule.onNodeWithText("Комментарий").performTextInput("sergeytukalsky@gmail.com")
        crimeTestRule.onNodeWithText("Добавить").performClick()

        crimeTestRule.waitForIdle()
        Thread.sleep(2000)

        crimeTestRule.onNodeWithText("Добавление опасности").assertIsDisplayed()
    }
}