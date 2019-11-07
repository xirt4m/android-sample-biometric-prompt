package ch.ranil.sample.android.biometricpromptsample.biometric

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import ch.ranil.sample.android.biometricpromptsample.R
import java.util.concurrent.Executors
import javax.crypto.Cipher

abstract class BaseBiometricPromptManager(
    protected val context: Context,
    protected val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
        context
    )
) : BiometricPromptManager {

    //region Decrypt
    abstract fun onAuthenticationSucceededForDecrypt(
        result: BiometricPrompt.AuthenticationResult,
        successAction: (ByteArray) -> Unit
    )

    abstract fun onAuthenticationFailedForDecrypt(errorCode: Int, errString: CharSequence)
    //endregion

    //region Encrypt
    abstract fun onAuthenticationSucceededForEncrypt(
        data: ByteArray,
        result: BiometricPrompt.AuthenticationResult,
        successAction: (ByteArray) -> Unit
    )

    abstract fun onAuthenticationFailedForEncrypt(errorCode: Int, errString: CharSequence)
    //endregion

    protected open fun handleDecrypt(
        activity: FragmentActivity,
        failedAction: () -> Unit,
        successAction: (ByteArray) -> Unit,
        cipher: Cipher? = null
    ) {
        handleCrypto(
            activity,
            failedAction = failedAction,
            successAction = successAction,
            cipher = cipher
        )
    }

    protected open fun handleEncrypt(
        activity: FragmentActivity,
        data: ByteArray,
        failedAction: () -> Unit,
        successAction: (ByteArray) -> Unit,
        cipher: Cipher? = null
    ) {
        handleCrypto(activity, data, failedAction, successAction, cipher)
    }

    private fun handleCrypto(
        activity: FragmentActivity,
        data: ByteArray? = null,
        failedAction: () -> Unit,
        successAction: (ByteArray) -> Unit,
        cipher: Cipher? = null
    ) {
        val executor = Executors.newSingleThreadExecutor()
        val biometricPrompt =
            BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    handleSuccess(data, result, successAction)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    handleError(data, errorCode, errString)

                    Log.d(TAG, "Authentication error. $errString ($errorCode)")
                    activity.runOnUiThread { failedAction() }
                }
            })
        authenticate(biometricPrompt, cipher)
    }

    private fun handleSuccess(
        data: ByteArray?,
        result: BiometricPrompt.AuthenticationResult,
        successAction: (ByteArray) -> Unit
    ) {
        if (data != null) {
            onAuthenticationSucceededForEncrypt(data, result, successAction)
        } else {
            onAuthenticationSucceededForDecrypt(result, successAction)
        }
    }

    private fun handleError(data: ByteArray?, errorCode: Int, errString: CharSequence) {
        if (data != null) {
            onAuthenticationFailedForEncrypt(errorCode, errString)
        } else {
            onAuthenticationFailedForDecrypt(errorCode, errString)
        }
    }

    private fun authenticate(biometricPrompt: BiometricPrompt, cipher: Cipher? = null) {
        val promptInfo = biometricPromptInfo()
        if (cipher != null) {
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        } else {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun biometricPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getText(R.string.prompt_title))
            .setSubtitle(context.getText(R.string.prompt_subtitle))
            .setDescription(context.getText(R.string.prompt_description))
            .setNegativeButtonText(context.getText(R.string.prompt_negative_button))
            .build()
    }

    private companion object {
        private const val TAG = "BiometricPromptManager"
    }

}