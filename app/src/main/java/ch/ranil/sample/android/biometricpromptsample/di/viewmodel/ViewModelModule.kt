package ch.ranil.sample.android.biometricpromptsample.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ch.ranil.sample.android.biometricpromptsample.di.viewmodel.ViewModelFactory
import ch.ranil.sample.android.biometricpromptsample.di.viewmodel.ViewModelKey
import ch.ranil.sample.android.biometricpromptsample.ui.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @Singleton
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}
