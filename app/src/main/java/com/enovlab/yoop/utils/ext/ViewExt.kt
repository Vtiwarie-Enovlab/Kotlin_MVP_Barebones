package com.enovlab.yoop.utils.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Rect
import android.support.annotation.LayoutRes
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.*
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.widget.NestedCoordinatorLayout
import com.enovlab.yoop.utils.WeakHandler
import com.warkiz.widget.IndicatorSeekBar


/**
 * Created by Max Toskhoparan on 12/1/2017.
 */

@Suppress("UNCHECKED_CAST")
fun <T : View> inflateView(@LayoutRes layoutResId: Int, parent: ViewGroup, attachToRoot: Boolean = false): T {
    return LayoutInflater.from(parent.context).inflate(layoutResId, parent, attachToRoot) as T
}

fun <V : EditText> V.asText(): String {
    return text.toString()
}

fun <V : TextView> V.textChangeListener(listener: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) = listener(s.toString())
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun <V : TextView> V.delayedTextChangeListener(listener: (String) -> Unit, delay: Long = 200L) {
    var delayedListener: Runnable? = null

    val handler = WeakHandler()
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            handler.removeCallbacks(delayedListener)

            delayedListener = Runnable { listener(s.toString()) }

            handler.postDelayed(delayedListener, delay)
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun RecyclerView.dragScrollListener(listener: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                listener()
            }
        }
    })
}

fun RecyclerView.scrollListener(listener: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                listener()
            }
        }
    })
}

inline fun TabLayout.listener(crossinline selectListener: (Int) -> Unit) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            selectListener(tab.position)
        }
    })
}

inline fun ViewPager.pageChangedListener(crossinline listener: (Int) -> Unit) {
    addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            listener(position)
        }
    })
}

fun RecyclerView.showFromTop() {
    scrollToPosition(0)
}

fun <V : View> V.showKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
}

fun <V : View> V.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)
}

fun <S : SpannableStringBuilder> S.listener(start: Int, end: Int, flags: Int, listener: () -> Unit) {
    setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            listener.invoke()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = ds.linkColor
            ds.isUnderlineText = false
        }
    }, start, end, flags)
}

fun <V : View> CoordinatorLayout.translationYForSnackbar(view: V): Float {
    var minOffset = 0f
    getDependencies(view).forEach {
        if (it is Snackbar.SnackbarLayout && doViewsOverlap(view, it)) {
            minOffset = Math.min(minOffset, it.translationY - it.getHeight())
        }
    }
    return minOffset
}

fun View?.findSuitableParent(): ViewGroup? {
    var view = this
    var fallback: ViewGroup? = null
    do {
        when (view) {
            is NestedCoordinatorLayout -> fallback = view
            is CoordinatorLayout -> return view
            is FrameLayout -> fallback = when {
                view.id == android.R.id.content -> return view
                else -> view
            }
        }

        if (view != null) {
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    return fallback
}

fun <V : RecyclerView> V.pagerSnap() {
    val pagerSnap = PagerSnapHelper()
    pagerSnap.attachToRecyclerView(this)
}

fun IndicatorSeekBar.progressChangedListener(listener: (Int) -> Unit) {
    setOnSeekChangeListener(object : IndicatorSeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: IndicatorSeekBar?, progress: Int, progressFloat: Float, fromUserTouch: Boolean) {
            if (fromUserTouch) listener(progress)
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?, thumbPosOnTick: Int) {

        }

        override fun onSectionChanged(seekBar: IndicatorSeekBar?, thumbPosOnTick: Int, textBelowTick: String?, fromUserTouch: Boolean) {
        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
        }
    })
}

fun View.OnClickListener.applyToViews(vararg views: View) {
    views.forEach { view -> view.setOnClickListener(this) }
}

fun <V : BottomNavigationView> V.check(itemId: Int) {
    menu.findItem(itemId)?.isChecked = true
}

fun <V : View> V.isImageViewPartiallyHidden(imageView: ImageView): Boolean {
    val bounds = Rect()
    getHitRect(bounds)
    return !imageView.getLocalVisibleRect(bounds) || bounds.height() < imageView.height
}

fun <V : AppBarLayout> V.disableDragging() {
    doOnLayout {
        val params = layoutParams
        if (params is CoordinatorLayout.LayoutParams) {
            val behaviour = params.behavior
            if (behaviour != null && behaviour is AppBarLayout.Behavior) {
                behaviour.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                        return false
                    }
                })
            }
        }
    }
}

fun <V : AppBarLayout> V.scrollYBy(coordinator: CoordinatorLayout, dy: Int) {
    val params = layoutParams
    if (params!= null && params is CoordinatorLayout.LayoutParams) {
        val behaviour = params.behavior
        if (behaviour != null && behaviour is AppBarLayout.Behavior) {
            behaviour.onNestedPreScroll(coordinator, this, this, 0, dy, IntArray(2), ViewCompat.TYPE_NON_TOUCH)
        }
    }
}

fun <V : View> V.fadeIn(duration: Long = 300L, endListener: (() -> Unit)? = null) {
    if (isVisible) return
    isVisible = true
    alpha = 0f
    animate()
        .setDuration(duration)
        .alpha(1f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                endListener?.invoke()
            }
        })
}

fun <V : View> V.fadeOut(duration: Long = 300L, endListener: (() -> Unit)? = null) {
    animate()
        .setDuration(duration)
        .alpha(0f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isVisible = false
                endListener?.invoke()
            }
        })
}

inline fun <V : View> BottomSheetBehavior<V>.listener(crossinline listener: (Int) -> Unit) {
    setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            listener(newState)
        }
    })
}

inline fun <V : View> V.postView(crossinline runnable: (V) -> Unit) {
    post { runnable(this) }
}

fun BottomNavigationView.addBadge(position: Int, @LayoutRes badgeRes: Int) {
    // get badge container (parent)
    val bottomMenu = getChildAt(0) as? BottomNavigationMenuView
    val itemView = bottomMenu?.getChildAt(position) as? BottomNavigationItemView

    // inflate badge from layout
    val badge = LayoutInflater.from(context).inflate(badgeRes, bottomMenu, false)
    badge.tag = BADGE_TAG

    // create badge layout parameter
    val badgeLayout = FrameLayout.LayoutParams(badge?.layoutParams).apply {
        gravity = Gravity.CENTER_HORIZONTAL
        leftMargin = resources.getDimensionPixelSize(R.dimen.margin_large)
        topMargin = resources.getDimensionPixelSize(R.dimen.badge_top_margin)
    }

    // add view to bottom bar with layout parameter
    itemView?.addView(badge, badgeLayout)
}

fun BottomNavigationView.isBadgeVisible(position: Int, visible: Boolean) {
    val bottomMenu = getChildAt(0) as? BottomNavigationMenuView
    val itemView = bottomMenu?.getChildAt(position) as? BottomNavigationItemView

    itemView?.findViewWithTag<View>(BADGE_TAG)?.isVisible = visible
}

private const val BADGE_TAG = "BADGE_TAG"