package com.distory.app.ui.story

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
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.distory.app.databinding.ActivityStoryBinding
import com.distory.app.databinding.ItemStoryBinding
import com.distory.app.domain.story.entity.StoryEntity
import com.distory.app.ui.BaseActivity
import com.distory.app.ui.common.adapter.LoadingStateAdapter
import com.distory.app.ui.story.add.AddStoryActivity
import com.distory.app.ui.story.detail.StoryDetailActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StoryActivity : BaseActivity() {
    private lateinit var binding: ActivityStoryBinding
    private val viewModel: StoryViewModel by viewModels()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        observe()
    }

    override fun onResume() {
        super.onResume()
        fetchStories()
    }

    private fun setupView() {
        storyAdapter = StoryAdapter()

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
    }

    private fun setupAction() {
        binding.fab.setOnClickListener {
            val intent = Intent(applicationContext, AddStoryActivity::class.java)
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        storyAdapter.setOnTapListener(object : StoryAdapter.Listener {
            override fun onTap(story: StoryEntity, itemBinding: ItemStoryBinding) {
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@StoryActivity,
                    Pair(itemBinding.ivStory, "storyImg"),
                    Pair(itemBinding.cvStory, "storyDetail")
                )

                val intent = Intent(applicationContext, StoryDetailActivity::class.java)
                intent.putExtra(StoryDetailActivity.TAG_STORY, story)
                startActivity(intent, optionsCompat.toBundle())
            }
        })
    }

    private fun playAnimation() {

    }

    private fun observe() {
        observeState()
        observeStories()
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { handleState(it) }
            .launchIn(lifecycleScope)
    }

    private fun observeStories() {
        viewModel.stories.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { handleStories(it) }
            .launchIn(lifecycleScope)
    }

    private fun fetchStories() {
        viewModel.fetchStories()
    }

    private fun handleState(state: StoryActivityState) {
        when (state) {
            is StoryActivityState.ShowToast -> Toast.makeText(
                applicationContext,
                state.message,
                Toast.LENGTH_LONG
            ).show()
            is StoryActivityState.IsLoading -> handleLoading(state.isLoading)
            is StoryActivityState.Init -> Unit
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.pbStory.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleStories(stories: PagingData<StoryEntity>) {
        storyAdapter.submitData(lifecycle, stories)

        binding.tvErrMsg.visibility =
            if (storyAdapter.itemCount < 1) View.VISIBLE
            else View.GONE
    }
}