package com.distory.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.distory.app.R
import com.distory.app.databinding.ActivityHomeBinding
import com.distory.app.ui.BaseActivity
import com.distory.app.ui.map.StoryMapsActivity
import com.distory.app.ui.signin.SignInActivity
import com.distory.app.ui.story.StoryActivity

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        observe()
    }

    private fun setupView() {
        binding.tvLoginName.text = getString(R.string.hello_user, myPref.name())
    }

    private fun setupAction() {
        binding.mbSignOut.setOnClickListener {
            myPref.clearLoginData()
            val intent = Intent(applicationContext, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        binding.ivStoryList.setOnClickListener {
            val intent = Intent(applicationContext, StoryActivity::class.java)
            startActivity(intent)
        }
        binding.ivStoryMap.setOnClickListener {
            val intent = Intent(applicationContext, StoryMapsActivity::class.java)
            startActivity(intent)
        }
        binding.ivLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun playAnimation() {

    }

    private fun observe() {

    }
}