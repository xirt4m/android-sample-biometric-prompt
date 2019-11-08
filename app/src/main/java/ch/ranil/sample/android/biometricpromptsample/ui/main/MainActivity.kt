package ch.ranil.sample.android.biometricpromptsample.ui.main

import android.os.Bundle
import androidx.lifecycle.Observer
import ch.ranil.sample.android.biometricpromptsample.databinding.ActivityMainBinding
import ch.ranil.sample.android.biometricpromptsample.di.base.BaseActivity
import ch.ranil.sample.android.biometricpromptsample.di.base.getViewModelFromFactory
import com.google.android.material.snackbar.Snackbar

class MainActivity : BaseActivity<MainViewModel>() {

    private lateinit var binding: ActivityMainBinding

    override fun provideViewModel() = getViewModelFromFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()

        setupKeystoreButtons()
        setupTinkButtons()
    }

    private fun setupObservers() {
        viewModel.text.observe(this, Observer { text ->
            text?.let {
                binding.textView.text = text
            }
        })

        viewModel.alert.observe(this, Observer { message ->
            message?.let {
                Snackbar.make(binding.rootLayout, it, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        })
    }

    private fun setupKeystoreButtons() {
        binding.buttonEncryptKeystore.setOnClickListener {
            viewModel.onEncryptKeystoreButtonClick(
                this,
                SECURE_TEXT_KEYSTORE
            )
        }
        binding.buttonDecryptKeystore.setOnClickListener {
            viewModel.onDecryptKeystoreButtonClick(this)
        }
    }

    private fun setupTinkButtons() {
        binding.buttonEncryptTink.setOnClickListener {
            viewModel.onEncryptTinkButtonClick(
                this,
                SECURE_TEXT_TINK
            )
        }
        binding.buttonDecryptTink.setOnClickListener {
            viewModel.onDecryptTinkButtonClick(this)
        }
    }

    private companion object {
        private const val SECURE_TEXT_KEYSTORE = "Secure Keystore Text!"
        private const val SECURE_TEXT_TINK = "Secure Tink Text!"
    }

}
