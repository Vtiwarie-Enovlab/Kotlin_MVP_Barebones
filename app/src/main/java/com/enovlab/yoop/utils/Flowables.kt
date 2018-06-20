package com.enovlab.yoop.utils

import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4

/**
 * Created by mtosk on 3/16/2018.
 */
object Flowables {

    inline fun <T1,T2,R> combineLatest(source1: Flowable<T1>, source2: Flowable<T2>, crossinline combineFunction: (T1, T2) -> R) =
        Flowable.combineLatest(source1, source2,
            BiFunction<T1, T2, R> { t1, t2 -> combineFunction(t1,t2) })!!

    inline fun <T1,T2,T3,R> combineLatest(source1: Flowable<T1>, source2: Flowable<T2>, source3: Flowable<T3>, crossinline combineFunction: (T1, T2, T3) -> R) =
        Flowable.combineLatest(source1, source2, source3, Function3<T1, T2, T3, R> { t1, t2, t3 -> combineFunction(t1, t2, t3) })!!

    inline fun <T1,T2,T3,T4,R> combineLatest(source1: Flowable<T1>, source2: Flowable<T2>, source3: Flowable<T3>, source4: Flowable<T4>, crossinline combineFunction: (T1, T2, T3, T4) -> R) =
            Flowable.combineLatest(source1, source2, source3, source4, Function4<T1, T2, T3,T4, R> { t1, t2, t3, t4 -> combineFunction(t1, t2, t3, t4) })!!

    /**
     * Emits `Pair<T1,T2>`
     */
    fun <T1,T2> combineLatest(source1: Flowable<T1>, source2: Flowable<T2>) =
        Flowable.combineLatest(source1, source2,
            BiFunction<T1, T2, Pair<T1,T2>> { t1, t2 -> t1 to t2 })!!

    inline fun <T1,T2,R> zip(source1: Flowable<T1>, source2: Flowable<T2>, crossinline combineFunction: (T1, T2) -> R) =
        Flowable.zip(source1, source2, BiFunction<T1, T2, R> { t1, t2 -> combineFunction(t1,t2) })!!
}