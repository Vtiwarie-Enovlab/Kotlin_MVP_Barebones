package com.enovlab.yoop.api

import com.enovlab.yoop.api.request.*
import com.enovlab.yoop.api.response.*
import com.enovlab.yoop.api.response.settings.NotificationSettings
import com.enovlab.yoop.data.entity.City
import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.data.entity.event.Event
import com.enovlab.yoop.data.entity.event.TokenAssignment
import com.enovlab.yoop.data.entity.user.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

interface YoopService {

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Single<User>

    @POST("auth/createAccount")
    fun signup(@Body request: SignupRequest): Single<User>

    @PUT("auth/initiateResetPassword")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Completable

    @GET("auth/isEmailAddressAvailable")
    fun isEmailAvailable(@Query("emailAddress") email: String): Completable

    @PUT("auth/changePassword")
    fun resetPassword(@Body request: ResetPasswordRequest): Single<User>

    @PUT("auth/setPassword")
    fun setNewPassword(@Body request: SetNewPasswordRequest): Single<User>

    @GET("auth/verifyAccountEmail")
    fun verifyAccountEmail(@Query("token") token: String): Single<User>

    @GET("auth/verifyResetPasswordEmail")
    fun verifyResetPasswordEmail(@Query("token") token: String): Single<VerificationResponse>

    @POST("auth/logout")
    fun logout(): Completable

    @PUT("auth/resendVerificationLink")
    fun resendVerificationLink(): Completable

    @POST("user/deviceToken")
    fun sendPushNotificationsToken(): Completable

    @GET("user/profile")
    fun getUser(): Single<User>

    @PUT("user/profile")
    fun updateUser(@Body request: UserUpdateRequest): Single<User>

    @Multipart
    @PUT("user/profile/picture")
    fun uploadProfilePicture(@Part photo: MultipartBody.Part): Completable

    @GET("user/notificationSettings")
    fun getNotificationSettings(): Flowable<List<NotificationSettings>>

    @PUT("user/notificationSettings")
    fun updateNotificationSettings(@Body request: NotificationSettingsUpdateRequest): Completable

    @PUT("event/public/profileList")
    fun getEvents(@Body request: FilterRequest): Flowable<List<Event>>

    @GET("event/public/profileList")
    fun getEvents(): Flowable<List<Event>>

    @GET("event/public/profile")
    fun getEvent(@Query("eventId") id: String): Single<Event>

//    @GET("user/activity")
//    fun getUserEvent(@Query("eventId") id: String): Single<Event>
//
//    @GET("user/activityList")
//    fun getUserEvents(): Flowable<List<Event>>

    @GET("inbox/notifications")
    fun getNotifications(): Flowable<NotificationsResponse>

    @PUT("inbox/setAsRead")
    fun markNotificationsAsRead(@Body request: NotificationsUpdateRequest): Completable

    @PUT("inbox/setAsArchived")
    fun markNotificationsAsArchived(@Body request: NotificationsUpdateRequest): Completable

    @GET("payment/paymentMeans")
    fun getPaymentMethods(): Flowable<List<PaymentMethod>>

    @PUT("payment/paymentMeans/setDefault")
    fun setDefaultPaymentMethod(@Query("paymentMethodId") id: String): Flowable<List<PaymentMethod>>

    @DELETE("payment/paymentMeans")
    fun deletePaymentMethod(@Query("paymentMethodId") id: String): Flowable<List<PaymentMethod>>

    @POST("payment/paymentMeans")
    fun createPaymentMethod(@Body request: CreatePaymentMethodRequest): Single<PaymentMethod>

    @PUT("payment/paymentMeans/update")
    fun updatePaymentMethod(@Body request: UpdatePaymentMethodRequest): Single<PaymentMethod>

    @GET("geoData/public/cities")
    fun getCities(): Flowable<List<City>>

    @GET("event/public/profileList/search")
    fun searchEvents(@Query("query") query: String): Flowable<List<EventSearch>>

    @PUT("draw/public/pricingDetails")
    fun getListPricingDetails(@Body request: ListPricingRequest): Flowable<PricingResponse>

    @PUT("draw/checkout")
    fun getListCheckout(@Body request: ListPricingRequest): Flowable<PricingResponse>

    @POST("draw/entry")
    fun makeListEntry(@Body request: ListPricingRequest): Flowable<PricingResponse>

    @PUT("auction/public/pricingDetails")
    fun getOnSalePricingDetails(@Body request: OnSalePricingRequest): Flowable<PricingResponse>

    @GET("auction/public/chance")
    fun getChances(@Query("offerGroupId") id: String, @Query("numTokens") count: Int): Single<ChancesResponse>

    @PUT("auction/checkout")
    fun getOnSaleCheckout(@Body request: OnSalePricingRequest): Flowable<PricingResponse>

    @POST("auction/makeAuctionOffer")
    fun makeAuctionOffer(@Body request: OnSalePricingRequest): Flowable<OnSaleOfferResponse>

    @PUT("draw/deleteEntry")
    fun deleteListRequest(@Query("offerGroupId") id: String): Completable

    @PUT("auction/deleteOffer")
    fun deleteOnSaleOffer(@Query("offerGroupId") id: String): Completable

    @PUT("marketplace/archiveOfferDisplay")
    fun archiveOffers(@Query("offerIds") ids: List<String>): Completable

    @PUT("user/profile/setSeenProfileVerificationStatus")
    fun seenProfileVerified(): Completable

    @PUT("marketplace/declineOfferPayment")
    fun declinePayment(@Query("offerId") id: String): Completable

    @PUT("payment/marketplace/repayment")
    fun retryPayment(@Query("offerId") id: String): Completable

    @POST("tokenAssignment/create")
    fun assignToken(@Body request: TokenAssignmentRequest): Single<TokenAssignment>

    @PUT("tokenAssignment/selfAssign")
    fun selfAssignToken(@Query("eventId") eventId: String, @Query("tokenId") tokenId: String): Completable

    @DELETE("tokenAssignment/cancel")
    fun cancelTokenAssignment(@Query("tokenAssignmentId") id: String): Completable

    @PUT("tokenAssignment/resend")
    fun resendTokenAssignment(@Query("assignmentId") id: String): Single<TokenAssignment>

    @GET("tokenAssignment/confirm")
    fun confirmTokenAssignment(@Query("assignmentString") token: String): Single<TokenAssignment>

    @GET("tokenAssignment/public/validate")
    fun validateTokenAssignment(@Query("assignmentString") token: String): Completable

    @PUT("tokenAssignment/sendEventReadyReminder")
    fun sendTokenAssignmentReminder(@Query("assignmentId") id: String): Completable

    //v2
    @GET("user/v2/activity")
    fun getUserEventV2(@Query("eventId") id: String): Single<UserEvent>

    @GET("user/v2/activityList")
    fun getUserEventsV2(): Flowable<UserEvents>
}
