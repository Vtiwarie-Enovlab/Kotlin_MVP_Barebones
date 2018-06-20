package com.enovlab.yoop.data.manager

import com.enovlab.yoop.data.dao.EventDao
import com.enovlab.yoop.data.dao.NotificationDao
import com.enovlab.yoop.data.dao.PaymentMethodDao
import com.enovlab.yoop.data.dao.UserDao
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 1/9/2018.
 */
class DataMangerImpl
@Inject constructor(private val preferences: AppPreferences,
                    private val eventDao: EventDao,
                    private val notificationDao: NotificationDao,
                    private val paymentMethodDao: PaymentMethodDao,
                    private val userDao: UserDao
) : DataManager {

    override fun clear(clearUser: Boolean): Completable {
        return Completable.fromCallable {
            if (clearUser) {
                preferences.clear()
                userDao.deleteUser()
            }
            paymentMethodDao.delete()
            eventDao.deleteUserEvents()
            notificationDao.deleteNotifications()
        }
    }
}