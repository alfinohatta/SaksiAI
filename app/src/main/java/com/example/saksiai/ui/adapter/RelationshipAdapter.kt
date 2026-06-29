package com.example.saksiai.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.saksiai.data.local.entity.EvidenceRelationshipEntity
import com.example.saksiai.databinding.ItemRelationshipBinding

class RelationshipAdapter(private val onEntityClick: (Long) -> Unit) :
    ListAdapter<EvidenceRelationshipEntity, RelationshipAdapter.RelationshipViewHolder>(RelationshipDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelationshipViewHolder {
        val binding = ItemRelationshipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RelationshipViewHolder(binding, onEntityClick)
    }

    override fun onBindViewHolder(holder: RelationshipViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RelationshipViewHolder(
        private val binding: ItemRelationshipBinding,
        private val onEntityClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EvidenceRelationshipEntity) {
            binding.tvRelType.text = item.relationshipType
            binding.tvRelConfidence.text = "${item.confidenceScore?.toInt() ?: 0}% AI Conf."
            
            // Logic for demo: Labeling target based on ID if name isn't fetched
            binding.tvTargetEntity.text = "Connected Intelligence Source (ID: ${item.targetEntityId})"

            // Color Coding based on Strategic Blueprint (The "Trust Engine" logic)
            val color = when (item.relationshipType) {
                "CONTRADICTS" -> Color.parseColor("#D32F2F") // Red for conflict
                "VERIFIED_BY" -> Color.parseColor("#388E3C") // Green for trust
                "OWNS", "PART_OF" -> Color.parseColor("#1976D2") // Blue for structure
                else -> Color.parseColor("#666666") // Default grey
            }

            binding.tvRelType.setTextColor(color)
            binding.layoutRelContainer.backgroundTintList = ColorStateList.valueOf(color).withAlpha(15)
            binding.root.strokeColor = color
            
            binding.root.setOnClickListener {
                onEntityClick(item.targetEntityId)
            }
        }
    }

    class RelationshipDiffCallback : DiffUtil.ItemCallback<EvidenceRelationshipEntity>() {
        override fun areItemsTheSame(oldItem: EvidenceRelationshipEntity, newItem: EvidenceRelationshipEntity): Boolean = 
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: EvidenceRelationshipEntity, newItem: EvidenceRelationshipEntity): Boolean = 
            oldItem == newItem
    }
}
