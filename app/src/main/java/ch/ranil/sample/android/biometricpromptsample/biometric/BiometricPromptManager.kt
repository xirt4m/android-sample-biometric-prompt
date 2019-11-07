package ch.ranil.sample.android.biometricpromptsample.biometric

import androidx.fragment.app.FragmentActivity

interface BiometricPromptManager {

    fun decryptPrompt(
        activity: FragmentActivity,
        failedAction: () -> Unit,
        successAction: (ByteArray) -> Unit
    )

    fun encryptPrompt(
        activity: FragmentActivity,
        data: ByteArray,
        failedAction: () -> Unit,
        successAction: (ByteArray) -> Unit
    )

}