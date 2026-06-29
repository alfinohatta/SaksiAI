package com.example.saksiai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.saksiai.data.local.entity.ReviewTaskEntity
import com.example.saksiai.databinding.ItemReviewTaskBinding

class ReviewTaskAdapter(private val onReviewClick: (ReviewTaskEntity) -> Unit) :
    ListAdapter<ReviewTaskEntity, ReviewTaskAdapter.ReviewViewHolder>(ReviewTaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding, onReviewClick)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReviewViewHolder(
        private val binding: ItemReviewTaskBinding,
        private val onReviewClick: (ReviewTaskEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReviewTaskEntity) {
            binding.tvTaskTitle.text = "Evidence ID: #${item.evidenceId}"
            binding.tvTaskNotes.text = item.reviewerNotes ?: "No specific notes provided."
            binding.tvTaskStatus.text = item.status

            binding.btnActionReview.setOnClickListener {
                onReviewClick(item)
            }
        }
    }

    class ReviewTaskDiffCallback : DiffUtil.ItemCallback<ReviewTaskEntity>() {
        override fun areItemsTheSame(oldItem: ReviewTaskEntity, newItem: ReviewTaskEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ReviewTaskEntity, newItem: ReviewTaskEntity): Boolean =
            oldItem == newItem
    }
}
