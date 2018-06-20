package com.enovlab.yoop.data.manager

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import com.enovlab.yoop.utils.RxSchedulers
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 1/22/2018.
 */
class FileManagerImpl
@Inject constructor(private val schedulers: RxSchedulers,
                    private val context: Context) : FileManager {

    override fun getEventMediaUri(bitmap: Bitmap): Single<Uri> {
        return Single.fromCallable {
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(EVENT_MEDIA_NAME, PHOTO_EXT, storageDir)
            FileOutputStream(file.absolutePath).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }

            return@fromCallable FileProvider.getUriForFile(context, PROVIDER, file)
        }.subscribeOn(schedulers.disk)
    }

    override fun saveProfilePhotoBitmap(bitmap: Bitmap): Single<File> {
        return Single.fromCallable {
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile("${PROFILE_PHOTO_NAME}_${System.currentTimeMillis()}", PHOTO_EXT, storageDir)
            FileOutputStream(file.absolutePath).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            return@fromCallable file
        }.subscribeOn(schedulers.disk)
    }

    override fun createProfilePhotoFile(): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("${PROFILE_PHOTO_NAME}_${System.currentTimeMillis()}", PHOTO_EXT, storageDir)
    }

    companion object {
        private const val PROFILE_PHOTO_NAME = "profile_photo"
        private const val EVENT_MEDIA_NAME = "event_media"
        private const val PHOTO_EXT = ".jpg"
        private const val PROVIDER = "com.enovlab.yoop.fileprovider"
    }
}