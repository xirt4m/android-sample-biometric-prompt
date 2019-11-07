package ch.ranil.sample.android.biometricpromptsample.di

import ch.ranil.sample.android.biometricpromptsample.util.ResourceProvider
import ch.ranil.sample.android.biometricpromptsample.util.StringResourceProvider
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ProviderModule {

    @Binds
    @Singleton
    abstract fun bindStringProvider(stringResourceProvider: StringResourceProvider): ResourceProvider<String>

}