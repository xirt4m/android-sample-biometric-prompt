package ch.ranil.sample.android.biometricpromptsample.ui.main

import android.util.Base64
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.ranil.sample.android.biometricpromptsample.R
import ch.ranil.sample.android.biometricpromptsample.biometric.BiometricPromptManager
import ch.ranil.sample.android.biometricpromptsample.di.BiometricPromptManagerKeystore
import ch.ranil.sample.android.biometricpromptsample.di.BiometricPromptManagerTink
import ch.ranil.sample.android.biometricpromptsample.util.ResourceProvider
import javax.inject.Inject

class MainViewModel @Inject constructor(
    @BiometricPromptManagerKeystore private val biometricPromptKeystore: BiometricPromptManager,
    @BiometricPromptManagerTink private val biometricPromptTink: BiometricPromptManager,
    private val resourceProvider: ResourceProvider<String>
) : ViewModel() {

    //region LiveData
    val text: LiveData<String> get() = _text

    val alert: LiveData<String> get() = _alert

    private val _text = MutableLiveData<String>()

    private val _alert = MutableLiveData<String>()
    //endregion

    fun onSnackbarShown() {
        _alert.value = null
    }

    fun onEncryptKeystoreButtonClick(activity: FragmentActivity, secureText: String) {
        onEncryptButtonClick(
            activity, secureText,
            Mode.KEYSTORE
        )
    }

    fun onEncryptTinkButtonClick(activity: FragmentActivity, secureText: String) {
        onEncryptButtonClick(
            activity, secureText,
            Mode.TINK
        )
    }

    private fun onEncryptButtonClick(activity: FragmentActivity, secureText: String, mode: Mode) {
        getPromptManager(mode).encryptPrompt(
            activity = activity,
            data = secureText.toByteArray(),
            failedAction = {
                _alert.postValue(resourceProvider.provide(R.string.main_encrypt_failed))
            },
            successAction = {
                _alert.postValue(resourceProvider.provide(R.string.main_encrypt_success))
                _text.postValue(
                    resourceProvider.provide(
                        R.string.main_encrypted,
                        Base64.encodeToString(it, Base64.DEFAULT)
                    )
                )
            }
        )
    }

    fun onDecryptKeystoreButtonClick(activity: FragmentActivity) {
        onDecryptButtonClick(
            activity,
            Mode.KEYSTORE
        )
    }

    fun onDecryptTinkButtonClick(activity: FragmentActivity) {
        onDecryptButtonClick(
            activity,
            Mode.TINK
        )
    }

    private fun onDecryptButtonClick(activity: FragmentActivity, mode: Mode) {
        getPromptManager(mode).decryptPrompt(
            activity = activity,
            failedAction = {
                _alert.postValue(resourceProvider.provide(R.string.main_decrypt_failed))
            },
            successAction = {
                _alert.postValue(resourceProvider.provide(R.string.main_decrypt_success))
                _text.postValue(resourceProvider.provide(R.string.main_decrypted, String(it)))
            }
        )
    }

    private fun getPromptManager(mode: Mode) = when (mode) {
        Mode.TINK -> biometricPromptTink
        Mode.KEYSTORE -> biometricPromptKeystore
    }

    private enum class Mode {
        TINK, KEYSTORE
    }

}