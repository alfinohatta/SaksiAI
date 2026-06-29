package com.example.saksiai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.saksiai.data.local.entity.EvidenceItemEntity
import com.example.saksiai.databinding.ItemEvidenceBinding

class EvidenceAdapter(private val onItemClick: (EvidenceItemEntity) -> Unit) : 
    ListAdapter<EvidenceItemEntity, EvidenceAdapter.EvidenceViewHolder>(EvidenceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvidenceViewHolder {
        val binding = ItemEvidenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EvidenceViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EvidenceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EvidenceViewHolder(
        private val binding: ItemEvidenceBinding,
        private val onItemClick: (EvidenceItemEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: EvidenceItemEntity) {
            binding.tvTitle.text = item.title ?: "Untitled Evidence"
            binding.tvFileName.text = item.fileName ?: "No file attached"
            binding.tvStatus.text = item.status
            binding.tvConfidence.text = "Confidence: ${item.confidenceScore?.toInt() ?: 0}%"
            
            // Basic color coding for status
            when (item.status) {
                "VERIFIED" -> binding.tvStatus.setBackgroundColor(0xFFC8E6C9.toInt()) // Green 100
                "FLAGGED" -> binding.tvStatus.setBackgroundColor(0xFFFFCDD2.toInt())  // Red 100
                "PROCESSING" -> binding.tvStatus.setBackgroundColor(0xFFFFF9C4.toInt()) // Yellow 100
                else -> binding.tvStatus.setBackgroundColor(0xFFE0E0E0.toInt()) // Grey 300
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class EvidenceDiffCallback : DiffUtil.ItemCallback<EvidenceItemEntity>() {
        override fun areItemsTheSame(oldItem: EvidenceItemEntity, newItem: EvidenceItemEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EvidenceItemEntity, newItem: EvidenceItemEntity): Boolean {
            return oldItem == newItem
        }
    }
}
