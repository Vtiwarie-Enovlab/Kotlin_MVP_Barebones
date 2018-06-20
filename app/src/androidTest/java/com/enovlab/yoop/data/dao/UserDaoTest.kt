package com.enovlab.yoop.data.dao

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.enovlab.yoop.TestUtils
import com.enovlab.yoop.data.YoopDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: YoopDatabase

    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), YoopDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun getUserWhenNoUsersSaved() {
        database.userDao().getUser()
            .test()
            .assertNoValues()
    }

    @Test
    fun saveAndGetUser() {
        database.userDao().saveUser(USER)

        database.userDao().getUser()
            .test()
            .assertValue { query ->
                val user = query.toUser()
                user.email == USER.email && user.firstName == USER.firstName && user.lastName == USER.lastName
            }
    }

    @Test
    fun saveAndGetUser_verifyMobileCountry() {
        USER.mobileCountry = TestUtils.createMobileCountry("United States", "US", "+1")

        database.userDao().saveUser(USER)

        database.userDao().getUser()
            .test()
            .assertValue { query ->
                val user = query.toUser()
                user.email == USER.email && user.mobileCountry == USER.mobileCountry
            }
    }

    @Test
    fun saveAndGetUser_verifyPaymentMethods() {
        USER.paymentMethods = TestUtils.createPaymentMethods("PaymentID", USER.email, 5)

        database.userDao().saveUser(USER)
        database.paymentMethodDao().savePaymentMethods(USER.paymentMethods!!)

        database.userDao().getUser()
            .test()
            .assertValue { query ->
                val user = query.toUser()
                user.email == USER.email
            }

        database.paymentMethodDao().getPaymentMethods()
            .test()
            .assertValue {
                it.size == USER.paymentMethods?.size
                    && it.all { it.userId == USER.email }
            }
    }

    @Test
    fun updateAndGetUser() {
        database.userDao().saveUser(USER)

        val userUpdate = USER.copy()
        userUpdate.firstName = "Maksym"
        database.userDao().saveUser(userUpdate)

        database.userDao().getUser()
            .test()
            .assertValue { query ->
                val user = query.toUser()
                user.email == USER.email && user.firstName == userUpdate.firstName && user.lastName == USER.lastName
            }
    }

    @Test
    fun deleteAndGetUser() {
        database.userDao().saveUser(USER)

        database.userDao().deleteUser()

        database.userDao().getUser()
            .test()
            .assertNoValues()
    }

    @Test
    fun deleteAndGetUser_verifyNoPaymentMethods() {
        USER.paymentMethods = TestUtils.createPaymentMethods("PaymentID", USER.email, 5)

        database.userDao().saveUser(USER)
        database.paymentMethodDao().savePaymentMethods(USER.paymentMethods!!)

        database.userDao().deleteUser()

        database.userDao().getUser()
            .test()
            .assertNoValues()

        database.paymentMethodDao().getPaymentMethods()
            .test()
            .assertValue {
                it.isEmpty()
            }
    }

    companion object {
        val USER = TestUtils.createUser("max@enovlab.com", "Max", "Toskhoparan")
    }
}
