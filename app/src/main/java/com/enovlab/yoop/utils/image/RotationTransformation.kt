package com.enovlab.yoop.utils.image

import android.graphics.Bitmap
import android.media.ExifInterface
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.security.MessageDigest
import com.bumptech.glide.load.Key
import com.enovlab.yoop.utils.image.blurr.BlurTransformation

class RotationTransformation(val path: String, val orientation: Int = 90) : BitmapTransformation() {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val dExif = exifOrientationDegrees(orientation)
        val isRotationRequired = isExifOrientationRequired(exif(path))

        return when {
            isRotationRequired -> TransformationUtils.rotateImageExif(pool, toTransform, dExif)
            else -> toTransform
        }
    }

    private fun exifOrientationDegrees(orientation: Int): Int {
        return when (orientation) {
            90 -> ExifInterface.ORIENTATION_ROTATE_90
            else -> ExifInterface.ORIENTATION_NORMAL
        }
    }

    private fun isExifOrientationRequired(exif: Int): Boolean {
        return when (exif) {
            ExifInterface.ORIENTATION_UNDEFINED,
            ExifInterface.ORIENTATION_NORMAL,
            ExifInterface.ORIENTATION_ROTATE_90,
            ExifInterface.ORIENTATION_ROTATE_270 -> false
            else -> true
        }
    }

    private fun exif(path: String): Int {
        val exif = ExifInterface(path)
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is BlurTransformation
    }

    companion object {
        private const val ID = "com.enovlab.yoop.utils.image.RotationTransformation"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }
}