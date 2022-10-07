package com.distory.app.ui.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.distory.app.R
import com.distory.app.databinding.ItemStoryBinding
import com.distory.app.domain.story.entity.StoryEntity

class StoryAdapter : PagingDataAdapter<StoryEntity, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    interface Listener {
        fun onTap(story: StoryEntity, itemBinding: ItemStoryBinding)
    }

    private var listener: Listener? = null

    fun setOnTapListener(l: Listener) {
        listener = l
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(private val itemBinding: ItemStoryBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(story: StoryEntity) {
            itemBinding.tvId.text = story.id
            itemBinding.tvName.text = story.name
            itemBinding.tvDesc.text = story.description
            itemBinding.tvDate.text = story.createdAt
            itemBinding.tvLatLong.text = itemBinding.root.context
                .getString(R.string.story_lat_long, story.latitude, story.longitude)
            Glide
                .with(itemBinding.root.context)
                .load(story.photoUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(itemBinding.ivStory)

            itemBinding.cvStory.setOnClickListener {
                listener?.onTap(story, itemBinding)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}