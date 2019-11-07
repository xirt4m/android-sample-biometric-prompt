package ch.ranil.sample.android.biometricpromptsample.application

import android.app.Application
import ch.ranil.sample.android.biometricpromptsample.di.DaggerAppComponent

class BiometricApplication : Application() {

    val appComponent = lazy {
        DaggerAppComponent.factory().create(this)
    }

}
