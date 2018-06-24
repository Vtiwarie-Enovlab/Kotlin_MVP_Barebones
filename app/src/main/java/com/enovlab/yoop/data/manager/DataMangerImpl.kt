package com.enovlab.yoop.data.manager

import io.reactivex.Completable
import javax.inject.Inject

class DataMangerImpl
@Inject constructor(private val preferences: AppPreferences
) : DataManager {

//    override fun clear(clearUser: Boolean): Completable {
////        return Completable.fromCallable {
////            if (clearUser) {
////                preferences.clear()
////                userDao.deleteUser()
////            }
////            paymentMethodDao.delete()
////            eventDao.deleteUserEvents()
////            notificationDao.deleteNotifications()
////        }
//    }
}