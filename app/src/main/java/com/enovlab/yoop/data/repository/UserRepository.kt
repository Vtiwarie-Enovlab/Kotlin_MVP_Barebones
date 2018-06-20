package com.enovlab.yoop.data.repository

import android.content.Context
import android.provider.ContactsContract
import com.enovlab.yoop.api.YoopService
import com.enovlab.yoop.api.request.NotificationSettingsUpdateRequest
import com.enovlab.yoop.api.request.UserUpdateRequest
import com.enovlab.yoop.api.response.settings.NotificationSettings
import com.enovlab.yoop.data.dao.UserDao
import com.enovlab.yoop.data.entity.Contact
import com.enovlab.yoop.data.entity.user.User
import com.enovlab.yoop.data.manager.AppPreferences
import com.enovlab.yoop.data.query.UserQuery
import com.enovlab.yoop.utils.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

class UserRepository
@Inject constructor(private val yoopService: YoopService,
                    private val userDao: UserDao,
                    private val preferences: AppPreferences,
                    private val schedulers: RxSchedulers,
                    context: Context) {

    private val contentResolver = context.contentResolver

    fun user(): Flowable<User> {
        val source = when {
            preferences.authToken != null -> userDao.getUser()
            else -> Flowable.just(emptyQuery())
        }
        return source
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.disk)
            .map(UserQuery::toUser)
            .observeOn(schedulers.main)
//            .distinctUntilChanged()
    }

    fun refreshUser(): Single<User> {
        return yoopService.getUser()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnSuccess(userDao::saveUser)
    }

    fun updateUser(firstName: String, lastName: String, locale: String): Single<User> {
        return yoopService.updateUser(UserUpdateRequest(firstName, lastName, locale))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnSuccess(userDao::saveUser)
    }

    fun isAuthorized() = preferences.authToken != null

    fun uploadProfilePicture(file: File?): Completable = when {
        file != null -> yoopService.uploadProfilePicture(
            MultipartBody.Part.createFormData("file", "profile_${System.currentTimeMillis()}.jpg",
                RequestBody.create(MediaType.parse(MULTIPART), file)))
        else -> Completable.complete()
    }
        .subscribeOn(schedulers.network)
        .observeOn(schedulers.main)

    fun seenProfileVerification(): Completable =
        yoopService.seenProfileVerified()
            .subscribeOn(schedulers.network)


    fun notificationSettings(): Flowable<List<NotificationSettings>> {
        return yoopService.getNotificationSettings()
            .subscribeOn(schedulers.network)
    }

    fun updateNotificationSettings(type: NotificationSettings.Type, group: NotificationSettings.Group, enabled: Boolean): Completable {
        return yoopService.updateNotificationSettings(NotificationSettingsUpdateRequest(type, group, enabled))
            .subscribeOn(schedulers.network)
    }

    fun contacts(): Flowable<List<Contact>> {
        return Flowable.fromCallable {
            val items = mutableListOf<Contact>()
            val emails = mutableSetOf<String>()

            val projection = arrayOf(ContactsContract.RawContacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Photo.CONTACT_ID)
            val order = ("CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE")
            val filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''"

            contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, filter, null, order).use {
                if (it.moveToFirst()) {
                    do {
                        val name = it.getString(1)
                        val email = it.getString(3)

                        // keep unique only
                        if (emails.add(email.toLowerCase())) {
                            items.add(Contact(name, email))
                        }
                    } while (it.moveToNext())
                }
            }

            return@fromCallable items.sortedBy { it.name }
        }
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.main)
    }

    private fun emptyQuery() = UserQuery().apply { user = emptyUser() }
    private fun emptyUser() = User("", null, null, null, null, null, null, null, null, null, null, null, null)

    companion object {
        private const val MULTIPART = "multipart/form-data"
    }
}
