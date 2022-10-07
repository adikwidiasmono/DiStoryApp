package com.distory.app.ui.story.detail

import android.os.Build
import android.os.Bundle
import com.bumptech.glide.Glide
import com.distory.app.R
import com.distory.app.databinding.ActivityStoryDetailBinding
import com.distory.app.domain.story.entity.StoryEntity
import com.distory.app.ui.BaseActivity

class StoryDetailActivity : BaseActivity() {
    companion object {
        const val TAG_STORY = "TAG-STORY"
    }

    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    @Suppress("DEPRECATION")
    private fun setupView() {
        val story = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TAG_STORY, StoryEntity::class.java)
        } else {
            intent.getParcelableExtra(TAG_STORY) as StoryEntity?
        }

        if (story == null)
            finish()

        story?.let {
            with(binding) {
                tvId.text = it.id
                tvName.text = it.name
                tvDesc.text = it.description
                tvDate.text = it.createdAt
                tvLatLong.text =
                    getString(R.string.story_lat_long, story.latitude, story.longitude)
                Glide
                    .with(applicationContext)
                    .load(it.photoUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivStory)
            }
        }
    }

    private fun setupAction() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun playAnimation() {

    }
}