package com.enovlab.yoop.data.manager

interface KeyStoreManager {
    fun encryptKey(key: String): String
    fun decryptKey(key: String): String
    fun deleteKey()
}