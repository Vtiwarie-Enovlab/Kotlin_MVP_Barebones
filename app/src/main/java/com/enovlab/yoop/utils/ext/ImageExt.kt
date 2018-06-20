package com.enovlab.yoop.utils.ext

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.enovlab.yoop.inject.GlideApp
import com.enovlab.yoop.inject.GlideRequest
import com.enovlab.yoop.utils.image.RotationTransformation
import com.enovlab.yoop.utils.image.blurr.BlurTransformation
import com.enovlab.yoop.utils.image.svg.SvgSoftwareLayerSetter
import java.io.File

/**
 * Created by Max Toskhoparan on 2/15/2018.
 */

fun ImageView.loadImage(url: String?) {
    GlideApp.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .centerCrop()
        .dontAnimate()
        .placeholder(drawable)
        .transition(GenericTransitionOptions.withNoTransition())
        .into(this)
}

fun ImageView.loadImageRounded(url: String?) {
    GlideApp.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .centerCrop()
        .dontAnimate()
        .placeholder(drawable)
        .transition(GenericTransitionOptions.withNoTransition())
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

fun ImageView.loadGifImage(url: String?) {
    GlideApp.with(this)
        .asGif()
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .centerCrop()
        .into(this)
}

fun ImageView.loadImageBlurred(url: String?, radius: Int = 15) {
    GlideApp.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .centerCrop()
        .dontAnimate()
        .placeholder(drawable)
        .transition(GenericTransitionOptions.withNoTransition())
        .transform(BlurTransformation(radius))
        .into(this)
}

fun ImageView.loadSvgImage(url: String?) {
    GlideApp.with(this)
        .`as`(PictureDrawable::class.java)
        .dontAnimate()
        .transition(GenericTransitionOptions.withNoTransition())
        .listener(SvgSoftwareLayerSetter())
        .load(Uri.parse(url))
        .into(this)
}

fun ImageView.loadImageNoCrop(url: String?) {
    GlideApp.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .dontAnimate()
        .placeholder(drawable)
        .transition(GenericTransitionOptions.withNoTransition())
        .into(this)
}

fun ImageView.loadImageFromFile(file: File) {
    GlideApp.with(this)
        .load(file)
        .transform(RotationTransformation(file.absolutePath))
        .dontAnimate()
        .transition(GenericTransitionOptions.withNoTransition())
        .into(this)
}

fun GlideRequest<Bitmap>.intoBitmap(bitmap: (Bitmap) -> Unit) {
    into(object : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            bitmap(resource)
        }
    })
}

fun ImageView.greyscale(active: Boolean) {
    colorFilter = when {
        active -> ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        else -> null
    }
}

fun ImageView.loadUserImage(url: String?) {
    if (url != null) {
        GlideApp.with(this)
            .load(url)
            .signature(userImageCacheKey(url))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .centerCrop()
            .dontAnimate()
            .placeholder(drawable)
            .error(drawable)
            .transition(GenericTransitionOptions.withNoTransition())
            .into(this)
    }
}

private fun userImageCacheKey(url: String): ObjectKey {
    val ext = ".jpg"
    val keyIdx = url.indexOf(ext)
    return when {
        keyIdx != -1 -> ObjectKey(url.substring(0 until keyIdx + ext.length))
        else -> ObjectKey(url)
    }
}