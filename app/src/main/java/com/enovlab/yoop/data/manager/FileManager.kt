package com.enovlab.yoop.data.manager

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Single
import java.io.File

interface FileManager {
    fun getEventMediaUri(bitmap: Bitmap): Single<Uri>
    fun saveProfilePhotoBitmap(bitmap: Bitmap): Single<File>
    fun createProfilePhotoFile(): File
}