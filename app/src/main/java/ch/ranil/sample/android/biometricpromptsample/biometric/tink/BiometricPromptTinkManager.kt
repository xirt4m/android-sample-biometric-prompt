package ch.ranil.sample.android.biometricpromptsample.biometric.tink

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import ch.ranil.sample.android.biometricpromptsample.biometric.BaseBiometricPromptManager
import ch.ranil.sample.android.biometricpromptsample.util.getBase64DecodedString
import ch.ranil.sample.android.biometricpromptsample.util.putBase64EncodedString
import com.google.crypto.tink.Aead
import com.google.crypto.tink.Config
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadFactory
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import javax.inject.Inject


class BiometricPromptTinkManager @Inject constructor(
    context: Context,
    sharedPreferences: SharedPreferences
) :
    BaseBiometricPromptManager(context, sharedPreferences) {

    private val aead: Aead

    init {
        Config.register(TinkConfig.LATEST)
        aead = AeadFactory.getPrimitive(getOrGenerateNewKeysetHandle())
    }

    //region BiometricPromptManager
    override fun decryptPrompt(
        activity: FragmentActivity,
        failedAction: () -> Unit,
        successAction: (ByteArray) -> Unit
    ) {
        try {
            handleDecrypt(activity, failedAction, successAction)
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
            handleEncrypt(activity, data, failedAction, successAction)
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
        val decryptedData = aead.decrypt(
            getEncryptedData(),
            EMPTY_ASSOCIATED_DATA
        )
        successAction(decryptedData)
    }

    override fun onAuthenticationFailedForDecrypt(errorCode: Int, errString: CharSequence) {
        // NO-OP
    }

    override fun onAuthenticationSucceededForEncrypt(
        data: ByteArray,
        result: BiometricPrompt.AuthenticationResult,
        successAction: (ByteArray) -> Unit
    ) {
        val encryptedData = aead.encrypt(
            data,
            EMPTY_ASSOCIATED_DATA
        )
        saveEncryptedData(encryptedData)
        successAction(encryptedData)
    }

    override fun onAuthenticationFailedForEncrypt(errorCode: Int, errString: CharSequence) {
        // NO-OP
    }
    //endregion

    private fun getOrGenerateNewKeysetHandle(): KeysetHandle {
        return AndroidKeysetManager.Builder()
            .withSharedPref(context, TINK_KEYSET_NAME, null)
            .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
            .withMasterKeyUri(MASTER_KEY_URI)
            .build().keysetHandle
    }

    private fun saveEncryptedData(dataEncrypted: ByteArray) {
        sharedPreferences.edit {
            putBase64EncodedString(DATA_ENCRYPTED, dataEncrypted)
        }
    }

    private fun getEncryptedData() = sharedPreferences.getBase64DecodedString(DATA_ENCRYPTED)

    companion object {
        private const val TAG = "BiometricPrompt"
        private const val TINK_KEYSET_NAME = "tink_keyset"
        private const val DATA_ENCRYPTED = "data_encrypted"
        private const val MASTER_KEY_URI = "android-keystore://tink_master_key"
        private val EMPTY_ASSOCIATED_DATA = ByteArray(0)
    }
}