package com.enovlab.yoop.ui.base.state

import android.support.annotation.CallSuper
import com.enovlab.yoop.api.state.ErrorType
import com.enovlab.yoop.api.state.LoadingType
import com.enovlab.yoop.api.state.State
import com.enovlab.yoop.data.manager.AppPreferences
import com.enovlab.yoop.data.manager.DataManager
import com.enovlab.yoop.ui.base.BaseViewModel
import com.enovlab.yoop.utils.RxSchedulers
import com.enovlab.yoop.utils.ext.message
import com.enovlab.yoop.utils.ext.plusAssign
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 2/13/2018.
 */
abstract class StateViewModel<V : StateView> : BaseViewModel<V>() {

    @Inject lateinit var preferences: AppPreferences
    @Inject lateinit var schedulers: RxSchedulers
    @Inject lateinit var dataManager: DataManager

    protected val disposables = CompositeDisposable()
    protected var singleSubscription: Disposable? = null
    protected val state: BehaviorSubject<State> = BehaviorSubject.create<State>()

    @CallSuper
    override fun stop() {
        disposables.clear()
        singleSubscription?.dispose()
    }

    protected fun load(source: () -> Completable) {
        source().call(LoadingType.LIST)
    }

    protected fun refresh(source: () -> Completable) {
        source().call(LoadingType.REFRESHING)
    }

    protected fun action(source: () -> Completable) {
        source().call(LoadingType.ACTION)
    }

    private fun Completable.call(loadingType: LoadingType) {
        disposables += this
            .observeOn(schedulers.main)
            .doOnSubscribe {
                state.onNext(State.Loading(loadingType))
                when (loadingType) {
                    LoadingType.LIST -> view?.showLoadingIndicator(true)
                    LoadingType.REFRESHING -> view?.showRefreshingIndicator(true)
                    LoadingType.ACTION -> view?.showActionIndicator(true)
                }
                view?.showInputFieldsEnabled(false)
            }
            .subscribe({
                state.onNext(State.Success)
                hideProgress()
                when (loadingType) {
                    LoadingType.LIST -> view?.showSuccessLoading()
                    LoadingType.REFRESHING -> view?.showSuccessRefreshing()
                    LoadingType.ACTION -> view?.showSuccessAction()
                }
                view?.showInputFieldsEnabled(true)
            }, { error ->
                view?.showInputFieldsEnabled(true)
                hideProgress()
                when (error) {
                    is HttpException -> when {
                        error.code() == HTTP_UNAUTHORIZED || error.code() == HTTP_FORBIDDEN -> {
                            state.onNext(State.Error(ErrorType.UNAUTHORIZED))
                            view?.showErrorUnauthorized()

                            // clear database and prefs
                            disposables += dataManager.clear(error.code() == HTTP_UNAUTHORIZED)
                                .subscribeOn(schedulers.disk).subscribe({
                                    Timber.i("Data cleared")
                                }, {
                                    Timber.e(it, "Error clearing data")
                                })
                        }
                        else -> {
                            val message = error.response().errorBody()?.message() ?: error.message()
                            state.onNext(State.Error(ErrorType.RESPONSE, message))
                            view?.showError(message)
                        }
                    }
                    is UnknownHostException -> {
                        state.onNext(State.Error(ErrorType.NO_NETWORK))
                        view?.showErrorNoConnection()
                    }
                    else -> {
                        state.onNext(State.Error(ErrorType.APPLICATION, error.localizedMessage))
                        view?.showError(error.localizedMessage)
                    }
                }
                Timber.e(error)
            })
    }

    fun <T> observe(subject: BehaviorSubject<T>, onSuccess: (T) -> Unit) {
        disposables += subject
            .observeOn(schedulers.main)
            .subscribe({
                onSuccess(it)
            }, { error ->
                Timber.e(error)
            })
    }

    private fun hideProgress() {
        view?.showLoadingIndicator(false)
        view?.showRefreshingIndicator(false)
        view?.showActionIndicator(false)
    }

    protected var isError: Boolean = false
        get() = state.value is State.Error
        set(value) {
            field = value
            state.onNext(if (value) State.Error(ErrorType.APPLICATION) else State.Success)
        }
}