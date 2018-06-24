package com.enovlab.yoop.utils.ext

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.operators.completable.CompletableEmpty
import io.reactivex.subjects.BehaviorSubject

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

fun <T> Flowable<T>.toCompletable(): Completable {
    return flatMapCompletable { CompletableEmpty.complete() }
}

fun <T> BehaviorSubject<T>.send(param: T) {
    onNext(param)
}

fun BehaviorSubject<Unit>.send() {
    onNext(Unit)
}