package ch.ranil.sample.android.biometricpromptsample.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import ch.ranil.sample.android.biometricpromptsample.di.viewmodel.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ViewModelModule::class,
        BiometricModule::class,
        ProviderModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun viewModelFactory(): ViewModelProvider.Factory

}