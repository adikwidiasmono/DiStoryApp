package com.distory.app

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.distory.app.databinding.ActivitySplashBinding
import com.distory.app.ui.BaseActivity
import com.distory.app.ui.home.HomeActivity
import com.distory.app.ui.signin.SignInActivity
import com.distory.app.utils.AppUtil

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        Handler(Looper.getMainLooper()).postDelayed({
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(binding.ivAppIcon, "appIcon"),
                Pair(binding.tvAppName, "appName")
            )

            val intent = if (myPref.token() == null)
                Intent(applicationContext, SignInActivity::class.java)
            else
                Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent, optionsCompat.toBundle())
            finish()
        }, AppUtil.SPLASH_DURATION)
    }

    private fun playAnimation() {
        val appIcon = ObjectAnimator.ofFloat(binding.ivAppIcon, View.ALPHA, 1f).setDuration(500)
        val appName = ObjectAnimator.ofFloat(binding.tvAppName, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(appIcon, appName)
            start()
        }
    }
}