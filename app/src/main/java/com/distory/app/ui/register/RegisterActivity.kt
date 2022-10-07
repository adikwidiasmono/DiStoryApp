package com.distory.app.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.distory.app.R
import com.distory.app.databinding.ActivityRegisterBinding
import com.distory.app.domain.story.entity.StatusAndMessage
import com.distory.app.ui.BaseActivity
import com.distory.app.utils.AppUtil
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        observe()
    }

    private fun setupView() {

    }

    private fun setupAction() {
        binding.mbSignUp.setOnClickListener {
            val name = binding.tietName.text.toString()
            val email = binding.tietEmail.text.toString()
            val password = binding.tietPassword.text.toString()

            binding.tilName.apply {
                isErrorEnabled = false
                if (name.isEmpty()) {
                    isErrorEnabled = true
                    error = getString(R.string.err_mandatory_field)
                    return@setOnClickListener
                }
            }

            binding.tilEmail.apply {
                isErrorEnabled = false
                if (email.isEmpty()) {
                    isErrorEnabled = true
                    error = getString(R.string.err_mandatory_field)
                    return@setOnClickListener
                }
                if (!AppUtil.isValidEmail(email)) {
                    isErrorEnabled = true
                    error = getString(R.string.err_email_format)
                    return@setOnClickListener
                }
            }

            binding.tilPassword.apply {
                isErrorEnabled = false
                if (password.isEmpty()) {
                    isErrorEnabled = true
                    error = getString(R.string.err_mandatory_field)
                    return@setOnClickListener
                }
                if (password.length < 6) {
                    isErrorEnabled = true
                    error = getString(R.string.err_password_length)
                    return@setOnClickListener
                }
            }

            registerNewUser(name, email, password)
        }
    }

    private fun playAnimation() {

    }

    private fun observe() {
        observeState()
        observeResult()
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { handleState(it) }
            .launchIn(lifecycleScope)
    }

    private fun observeResult() {
        viewModel.result.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { handleResult(it) }
            .launchIn(lifecycleScope)
    }

    private fun registerNewUser(name: String, email: String, password: String) {
        viewModel.registerNewUser(name, email, password)
    }

    private fun handleState(state: RegisterActivityState) {
        when (state) {
            is RegisterActivityState.ShowToast -> Toast.makeText(
                applicationContext,
                state.message,
                Toast.LENGTH_LONG
            ).show()
            is RegisterActivityState.IsLoading -> handleLoading(state.isLoading)
            is RegisterActivityState.Init -> Unit
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mbSignUp.visibility = View.INVISIBLE
            binding.pbRegister.visibility = View.VISIBLE
        } else {
            binding.mbSignUp.visibility = View.VISIBLE
            binding.pbRegister.visibility = View.GONE
        }
    }

    private fun handleResult(result: StatusAndMessage) {
        // Success register
        if (!result.isError)
            finish()
    }
}