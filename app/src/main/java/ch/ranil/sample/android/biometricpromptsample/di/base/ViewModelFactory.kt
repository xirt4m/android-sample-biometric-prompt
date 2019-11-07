package ch.ranil.sample.android.biometricpromptsample.di.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import ch.ranil.sample.android.biometricpromptsample.application.BiometricApplication

inline fun <A : BaseActivity<VM>, reified VM : ViewModel> A.getViewModelFromFactory(): VM {
    val viewModelFactory = (applicationContext as? BiometricApplication)
        ?.appComponent
        ?.value
        ?.viewModelFactory()
        ?: throw IllegalStateException("BaseActivity should not be used without an Application that inherits from BiometricApplication")

    return ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)
}
