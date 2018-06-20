package com.enovlab.yoop.data.manager

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Single
import java.io.File

/**
 * Created by Max Toskhoparan on 1/22/2018.
 */
interface FileManager {
    fun getEventMediaUri(bitmap: Bitmap): Single<Uri>
    fun saveProfilePhotoBitmap(bitmap: Bitmap): Single<File>
    fun createProfilePhotoFile(): File
}