package ch.ranil.sample.android.biometricpromptsample.biometric.keystore

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import ch.ranil.sample.android.biometricpromptsample.biometric.BaseBiometricPromptManager
import ch.ranil.sample.android.biometricpromptsample.util.getBase64DecodedString
import ch.ranil.sample.android.biometricpromptsample.util.putBase64EncodedString
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class BiometricPromptManagerImpl @Inject constructor(
    context: Context,
    sharedPreferences: SharedPreferences
) :
    BaseBiometricPromptManager(context, sharedPreferences) {

    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }

    //region BiometricPromptManager
    override fun decryptPrompt(
        activity: FragmentActivity,
        failedAction: () -> Unit,
        successAction: (ByteArray) -> Unit
    ) {
        try {
            val secretKey = getKey()
            val initializationVector = getIv()
            if (secretKey != null && initializationVector != null) {
                val cipher = getDecryptCipher(secretKey, initializationVector)
                handleDecrypt(activity, failedAction, successAction, cipher)
            } else {
                failedAction()
            }
        } catch (e: Exception) {
            Log.d(TAG, "Decrypt BiometricPrompt exception", e)
            failedAction()
        }
    }

    override fun encryptPrompt(
        activity: FragmentActivity,
        data: ByteArray,
        failedAction: () -> Unit,
        successAction: (ByteArray) -> Unit
    ) {
        try {
            val cipher = getEncryptCipher(createKey())
            handleEncrypt(activity, data, failedAction, successAction, cipher)
        } catch (e: Exception) {
            Log.d(TAG, "Encrypt BiometricPrompt exception", e)
            failedAction()
        }
    }
    //endregion

    //region BaseBiometricPromptManager
    override fun onAuthenticationSucceededForDecrypt(
        result: BiometricPrompt.AuthenticationResult,
        successAction: (ByteArray) -> Unit
    ) {
        result.cryptoObject?.cipher?.let { cipher ->
            val encrypted = getEncryptedData()
            val data = cipher.doFinal(encrypted)
            successAction(data)
        }
    }

    override fun onAuthenticationFailedForDecrypt(errorCode: Int, errString: CharSequence) {
        // NO-OP
    }

    override fun onAuthenticationSucceededForEncrypt(
        data: ByteArray,
        result: BiometricPrompt.AuthenticationResult,
        successAction: (ByteArray) -> Unit
    ) {
        result.cryptoObject?.cipher?.let { resultCipher ->
            val iv = resultCipher.iv
            val encryptedData = resultCipher.doFinal(data)
            saveEncryptedData(encryptedData, iv)
            successAction(encryptedData)
        }
    }

    override fun onAuthenticationFailedForEncrypt(errorCode: Int, errString: CharSequence) {
        // NO-OP
    }
    //endregion

    private fun getKey(): Key? = keyStore.getKey(KEY_NAME, "myPassword".toCharArray())

    private fun createKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            ALGORITHM,
            KEYSTORE
        )
        val keyGenParameterSpec =
            KeyGenParameterSpec.Builder(
                KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setUserAuthenticationRequired(true)
                .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private fun getIv() = sharedPreferences.getBase64DecodedString(INITIALIZATION_VECTOR)

    private fun getEncryptedData() = sharedPreferences.getBase64DecodedString(DATA_ENCRYPTED)

    private fun saveEncryptedData(dataEncrypted: ByteArray, initializationVector: ByteArray) {
        sharedPreferences.edit {
            putBase64EncodedString(DATA_ENCRYPTED, dataEncrypted)
            putBase64EncodedString(INITIALIZATION_VECTOR, initializationVector)
        }
    }

    private fun getDecryptCipher(key: Key, iv: ByteArray): Cipher =
        Cipher.getInstance(keyTransformation()).apply {
            init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        }

    private fun getEncryptCipher(key: Key): Cipher =
        Cipher.getInstance(keyTransformation()).apply {
            init(Cipher.ENCRYPT_MODE, key)
        }

    companion object {
        private const val TAG = "BiometricPrompt"
        private const val KEYSTORE = "AndroidKeyStore"
        private const val KEY_NAME = "MY_KEY"
        private const val DATA_ENCRYPTED = "DATA_ENCRYPTED"
        private const val INITIALIZATION_VECTOR = "INITIALIZATION_VECTOR"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private fun keyTransformation() =
            listOf(
                ALGORITHM,
                BLOCK_MODE,
                PADDING
            ).joinToString(separator = "/")
    }
}