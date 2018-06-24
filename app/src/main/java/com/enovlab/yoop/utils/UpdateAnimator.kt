package com.enovlab.yoop.utils

import android.animation.ValueAnimator
import androidx.core.animation.addListener

open class UpdateAnimator {

    private var expanded = true
    private var animator: ValueAnimator? = null

    init {
        animator?.duration = DURATION
    }

    fun expand(listener: (Float) -> Unit) {
        if (!expanded) {
            animator?.cancel()

            animator = ValueAnimator.ofFloat(TO, FROM)

            animator?.addUpdateListener {
                listener(it.animatedValue as Float)
            }

            animator?.addListener(onEnd = {
                expanded = true
            })

            animator?.start()
        }
    }

    fun collapse(listener: (Float) -> Unit) {
        if (expanded && (animator == null || !animator!!.isRunning)) {
            animator?.cancel()

            animator = ValueAnimator.ofFloat(FROM, TO)

            animator?.addUpdateListener {
                listener(it.animatedValue as Float)
            }

            animator?.addListener(onEnd = {
                expanded = false
            })

            animator?.start()
        }
    }

    companion object {
        private const val DURATION = 170L
        private const val FROM = 0F
        private const val TO = 100F
    }
}