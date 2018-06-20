package com.enovlab.yoop.data.repository

import com.enovlab.yoop.api.YoopService
import com.enovlab.yoop.api.request.*
import com.enovlab.yoop.api.response.VerificationResponse
import com.enovlab.yoop.data.dao.UserDao
import com.enovlab.yoop.data.entity.user.User
import com.enovlab.yoop.data.manager.AppPreferences
import com.enovlab.yoop.data.manager.DataManager
import com.enovlab.yoop.utils.RxSchedulers
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 1/4/2018.
 */
open class AuthRepository
@Inject constructor(private val yoopService: YoopService,
                    private val userDao: UserDao,
                    private val preferences: AppPreferences,
                    private val schedulers: RxSchedulers,
                    private val dataManager: DataManager) {

    fun login(email: String, password: String): Single<User> {
        return yoopService.login(LoginRequest(email, password))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnSuccess {
                userDao.saveUser(it)
                saveAuthToken(it.authToken)
            }
//            .concatWith { sendPushNotificationsToken() }
//            .singleOrError()
    }

    fun signup(email: String, password: String, firstName: String, lastName: String): Single<User> {
        return yoopService.signup(SignupRequest(email, password, firstName, lastName, Locale.getDefault().toString()))
            .subscribeOn(schedulers.network)
            .doOnSuccess {
                userDao.saveUser(it)
                saveAuthToken(it.authToken)
            }
    }

    fun forgotPassword(email: String): Completable {
        return yoopService.forgotPassword(ForgotPasswordRequest(email))
            .subscribeOn(schedulers.network)
    }

    fun isEmailAvailable(email: String): Completable {
        return yoopService.isEmailAvailable(email)
            .subscribeOn(schedulers.network)
    }

    fun resetPassword(password: String, token: String): Single<User> {
        return yoopService.resetPassword(ResetPasswordRequest(password, token))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnSuccess {
                userDao.saveUser(it)
                saveAuthToken(it.authToken)
            }
    }

    fun sendPushNotificationsToken(): Completable {
        val token = FirebaseInstanceId.getInstance().token
        return when {
            token != null -> yoopService
                .sendPushNotificationsToken()
                .subscribeOn(schedulers.network)
            else -> Completable.complete()
        }
    }

    fun logout(): Completable {
        return yoopService.logout()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun setNewPassword(oldPassword: String, newPassword: String): Single<User> {
        return yoopService.setNewPassword(SetNewPasswordRequest(oldPassword, newPassword))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnSuccess {
                userDao.saveUser(it)
                saveAuthToken(it.authToken)
            }
    }

    private fun saveAuthToken(token: String?) {
        preferences.authToken = token
    }

    fun resendVerificationLink(): Completable {
        return yoopService.resendVerificationLink()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun verifySignupEmail(token: String): Single<User> {
        return yoopService.verifyAccountEmail(token)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnSuccess {
                userDao.saveUser(it)
                saveAuthToken(it.authToken)
            }
            .observeOn(schedulers.main)
    }

    fun verifyResetPasswordEmail(token: String): Single<VerificationResponse> {
        return yoopService.verifyResetPasswordEmail(token)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }
}