package com.example.saksiai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.saksiai.data.local.entity.EvidenceEntity
import com.example.saksiai.databinding.ItemEntityBinding

class EntityAdapter(private val onItemClick: (EvidenceEntity) -> Unit) : 
    ListAdapter<EvidenceEntity, EntityAdapter.EntityViewHolder>(EntityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val binding = ItemEntityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntityViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EntityViewHolder(
        private val binding: ItemEntityBinding,
        private val onItemClick: (EvidenceEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EvidenceEntity) {
            binding.tvEntityName.text = item.entityName
            binding.tvEntityType.text = item.entityType
            binding.tvEntityId.text = "ID: ${item.externalIdentifier ?: "N/A"}"
            
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class EntityDiffCallback : DiffUtil.ItemCallback<EvidenceEntity>() {
        override fun areItemsTheSame(oldItem: EvidenceEntity, newItem: EvidenceEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: EvidenceEntity, newItem: EvidenceEntity): Boolean = oldItem == newItem
    }
}
