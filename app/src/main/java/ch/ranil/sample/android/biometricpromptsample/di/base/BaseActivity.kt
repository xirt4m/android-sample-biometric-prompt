package ch.ranil.sample.android.biometricpromptsample.di.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {

    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = provideViewModel()
    }

    abstract fun provideViewModel(): VM

}
