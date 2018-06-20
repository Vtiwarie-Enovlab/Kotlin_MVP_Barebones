package com.enovlab.yoop.data.manager

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import timber.log.Timber
import java.math.BigInteger
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.util.Calendar
import javax.crypto.Cipher
import javax.inject.Inject
import javax.security.auth.x500.X500Principal

class KeyStoreManagerImpl
@Inject constructor(private val context: Context) : KeyStoreManager {

    private val keyStore by lazy { initKeyStore() }
    private val cipher by lazy { Cipher.getInstance(TRANSFORMATION_ASYMMETRIC) }

    init {
        createAndroidKeyStoreAsymmetricKey(ALIAS)
    }

    override fun encryptKey(key: String): String {
        val masterKey = getAndroidKeyStoreAsymmetricKeyPair(ALIAS)
        return when {
            masterKey != null -> encrypt(key, masterKey.public)
            else -> key // no encryption
        }
    }

    override fun decryptKey(key: String): String {
        val masterKey = getAndroidKeyStoreAsymmetricKeyPair(ALIAS)
        return when {
            masterKey != null -> decrypt(key, masterKey.private)
            else -> key // no encryption
        }
    }

    override fun deleteKey() {
        removeAndroidKeyStoreKey(ALIAS)
    }

    private fun encrypt(data: String, key: Key?): String {
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val bytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun decrypt(data: String, key: Key?): String {
        cipher.init(Cipher.DECRYPT_MODE, key)
        val encryptedData = Base64.decode(data, Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }

    private fun getAndroidKeyStoreAsymmetricKeyPair(alias: String): KeyPair? {
        val privateKey = keyStore.getKey(alias, null) as PrivateKey?
        val publicKey = keyStore.getCertificate(alias)?.publicKey

        return when {
            privateKey != null && publicKey != null -> KeyPair(publicKey, privateKey)
            else -> null
        }
    }

    private fun removeAndroidKeyStoreKey(alias: String) = keyStore.deleteEntry(alias)

    private fun initKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
        return keyStore
    }

    private fun createAndroidKeyStoreAsymmetricKey(alias: String) {
        try {
            val generator = KeyPairGenerator.getInstance(ALGORITHM, ANDROID_KEY_STORE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                initGeneratorWithKeyGenParameterSpec(generator, alias)
            } else {
                initGeneratorWithKeyPairGeneratorSpec(generator, alias)
            }

            // Generates Key with given spec and saves it to the KeyStore
            generator.generateKeyPair()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    @Suppress("DEPRECATION")
    private fun initGeneratorWithKeyPairGeneratorSpec(generator: KeyPairGenerator, alias: String) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.YEAR, 20)

        val builder = KeyPairGeneratorSpec.Builder(context)
            .setAlias(alias)
            .setSerialNumber(BigInteger.ONE)
            .setSubject(X500Principal("CN=$alias CA Certificate"))
            .setStartDate(startDate.time)
            .setEndDate(endDate.time)

        generator.initialize(builder.build())
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initGeneratorWithKeyGenParameterSpec(generator: KeyPairGenerator, alias: String) {
        val builder = KeyGenParameterSpec.Builder(alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
        generator.initialize(builder.build())
    }

    companion object {
        private const val ALGORITHM = "RSA"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val TRANSFORMATION_ASYMMETRIC = "RSA/ECB/PKCS1Padding"
        private const val ALIAS = "EVENT_KEY"
    }
}