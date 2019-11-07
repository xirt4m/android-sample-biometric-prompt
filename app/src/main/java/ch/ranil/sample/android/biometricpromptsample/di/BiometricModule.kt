package ch.ranil.sample.android.biometricpromptsample.di

import ch.ranil.sample.android.biometricpromptsample.biometric.BiometricPromptManager
import ch.ranil.sample.android.biometricpromptsample.biometric.keystore.BiometricPromptManagerImpl
import ch.ranil.sample.android.biometricpromptsample.biometric.tink.BiometricPromptTinkManager
import dagger.Binds
import dagger.Module
import javax.inject.Qualifier

@Module
abstract class BiometricModule {

    @Binds
    @BiometricPromptManagerKeystore
    abstract fun bindKeystoreBiometricManager(keystore: BiometricPromptManagerImpl): BiometricPromptManager

    @Binds
    @BiometricPromptManagerTink
    abstract fun bindTinkBiometricManager(tink: BiometricPromptTinkManager): BiometricPromptManager

}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class BiometricPromptManagerKeystore

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class BiometricPromptManagerTink