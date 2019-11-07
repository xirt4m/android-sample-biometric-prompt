package ch.ranil.sample.android.biometricpromptsample.ui.main

import android.os.Bundle
import androidx.lifecycle.Observer
import ch.ranil.sample.android.biometricpromptsample.R
import ch.ranil.sample.android.biometricpromptsample.di.base.BaseActivity
import ch.ranil.sample.android.biometricpromptsample.di.base.getViewModelFromFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>() {

    override fun provideViewModel() = getViewModelFromFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupObservers()

        setupKeystoreButtons()
        setupTinkButtons()
    }

    private fun setupObservers() {
        viewModel.text.observe(this, Observer { text ->
            text?.let {
                textView.text = text
            }
        })

        viewModel.alert.observe(this, Observer { message ->
            message?.let {
                Snackbar.make(rootLayout, it, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        })
    }

    private fun setupKeystoreButtons() {
        buttonEncryptKeystore.setOnClickListener {
            viewModel.onEncryptKeystoreButtonClick(
                this,
                SECURE_TEXT_KEYSTORE
            )
        }
        buttonDecryptKeystore.setOnClickListener {
            viewModel.onDecryptKeystoreButtonClick(this)
        }
    }

    private fun setupTinkButtons() {
        buttonEncryptTink.setOnClickListener {
            viewModel.onEncryptTinkButtonClick(
                this,
                SECURE_TEXT_TINK
            )
        }
        buttonDecryptTink.setOnClickListener {
            viewModel.onDecryptTinkButtonClick(this)
        }
    }

    private companion object {
        private const val SECURE_TEXT_KEYSTORE = "Secure Keystore Text!"
        private const val SECURE_TEXT_TINK = "Secure Tink Text!"
    }

}
