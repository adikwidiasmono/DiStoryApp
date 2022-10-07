package com.distory.app.ui.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.distory.app.R
import com.distory.app.databinding.ActivitySignInBinding
import com.distory.app.domain.story.entity.LoginUser
import com.distory.app.ui.BaseActivity
import com.distory.app.ui.home.HomeActivity
import com.distory.app.ui.register.RegisterActivity
import com.distory.app.utils.AppUtil
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val viewModel: SignInViewModel by viewModels()

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        observe()
    }

    private fun setupView() {
        myPref.email()?.let {
            binding.tietEmail.setText(it)
        }
    }

    private fun setupAction() {
        binding.tvCreateAccount.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.mbSignIn.setOnClickListener {
            email = binding.tietEmail.text.toString()
            val password = binding.tietPassword.text.toString()

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

            validateSignInUser(email, password)
        }
    }

    private fun playAnimation() {

    }

    private fun observe() {
        observeState()
        observeLoginUser()
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { handleState(it) }
            .launchIn(lifecycleScope)
    }

    private fun observeLoginUser() {
        viewModel.loginUser.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { handleLoginUser(it) }
            .launchIn(lifecycleScope)
    }

    private fun validateSignInUser(email: String, password: String) {
        viewModel.validateSignInUser(email, password)
    }

    private fun handleState(state: SignInActivityState) {
        when (state) {
            is SignInActivityState.ShowToast -> Toast.makeText(
                applicationContext,
                state.message,
                Toast.LENGTH_LONG
            ).show()
            is SignInActivityState.IsLoading -> handleLoading(state.isLoading)
            is SignInActivityState.Init -> Unit
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mbSignIn.visibility = View.INVISIBLE
            binding.pbRegister.visibility = View.VISIBLE
        } else {
            binding.mbSignIn.visibility = View.VISIBLE
            binding.pbRegister.visibility = View.GONE
        }
    }

    private fun handleLoginUser(loginUser: LoginUser) {
        if (loginUser.userId != "-99") {
            // Success login
            myPref.email(email)
            myPref.name(loginUser.name)
            myPref.token(loginUser.token)

            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(binding.ivAppIcon, "appIcon"),
                Pair(binding.tvAppName, "appName")
            )

            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent, optionsCompat.toBundle())
        }
    }
}