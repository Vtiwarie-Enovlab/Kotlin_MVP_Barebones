package com.enovlab.yoop.data.repository

import com.enovlab.yoop.api.YoopService
import com.enovlab.yoop.api.request.TokenAssignmentRequest
import com.enovlab.yoop.data.dao.EventDao
import com.enovlab.yoop.data.entity.event.TokenAssignment
import com.enovlab.yoop.data.entity.event.TokenInfo
import com.enovlab.yoop.utils.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class AssignmentRepository
@Inject constructor(private val yoopService: YoopService,
                    private val eventDao: EventDao,
                    private val schedulers: RxSchedulers) {

    fun tokenInfo(id: String): Single<TokenInfo> {
        return eventDao.getTokenInfo(id)
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.main)
    }

    fun assignToken(id: String, email: String): Single<TokenAssignment> {
        return yoopService.assignToken(TokenAssignmentRequest(email, arrayOf(id)))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun selfAssignToken(eventId: String, tokenId: String): Completable {
        return yoopService.selfAssignToken(eventId, tokenId)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun cancelAssignment(assignmentId: String): Completable {
        return yoopService.cancelTokenAssignment(assignmentId)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun resendAssignment(assignmentId: String): Single<TokenAssignment> {
        return yoopService.resendTokenAssignment(assignmentId)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun confirmAssignment(token: String): Single<TokenAssignment> {
        return yoopService.confirmTokenAssignment(token)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun verifyAssignmentToken(token: String): Completable {
        return yoopService.validateTokenAssignment(token)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    fun sendAssignmentReminder(assignmentId: String): Completable {
        return yoopService.sendTokenAssignmentReminder(assignmentId)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }
}