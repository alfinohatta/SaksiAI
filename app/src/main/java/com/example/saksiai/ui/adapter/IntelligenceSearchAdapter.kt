package com.example.saksiai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.saksiai.data.local.entity.EvidenceEntity
import com.example.saksiai.data.local.entity.EvidenceItemEntity
import com.example.saksiai.databinding.ItemEntityBinding
import com.example.saksiai.databinding.ItemEvidenceBinding

class IntelligenceSearchAdapter(
    private val onEvidenceClick: (EvidenceItemEntity) -> Unit,
    private val onEntityClick: (EvidenceEntity) -> Unit
) : ListAdapter<Any, IntelligenceSearchAdapter.BaseViewHolder>(SearchDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is EvidenceItemEntity -> TYPE_EVIDENCE
            is EvidenceEntity -> TYPE_ENTITY
            else -> throw IllegalArgumentException("Unknown intelligence type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_EVIDENCE -> {
                val binding = ItemEvidenceBinding.inflate(inflater, parent, false)
                EvidenceViewHolder(binding, onEvidenceClick)
            }
            TYPE_ENTITY -> {
                val binding = ItemEntityBinding.inflate(inflater, parent, false)
                EntityViewHolder(binding, onEntityClick)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is EvidenceViewHolder -> holder.bind(item as EvidenceItemEntity)
            is EntityViewHolder -> holder.bind(item as EvidenceEntity)
        }
    }

    abstract class BaseViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    class EvidenceViewHolder(
        private val binding: ItemEvidenceBinding,
        private val onClick: (EvidenceItemEntity) -> Unit
    ) : BaseViewHolder(binding) {
        fun bind(item: EvidenceItemEntity) {
            binding.tvTitle.text = item.title ?: "Capture Intelligence"
            binding.tvFileName.text = "Source: ${item.fileName}"
            binding.tvStatus.text = "STATUS: ${item.status}"
            binding.tvConfidence.text = "Trust: ${item.confidenceScore?.toInt()}%"
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    class EntityViewHolder(
        private val binding: ItemEntityBinding,
        private val onClick: (EvidenceEntity) -> Unit
    ) : BaseViewHolder(binding) {
        fun bind(item: EvidenceEntity) {
            binding.tvEntityName.text = item.entityName
            binding.tvEntityType.text = "GRAPH ENTITY: ${item.entityType}"
            binding.tvExternalId.text = "Identifier: ${item.externalIdentifier ?: "N/A"}"
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    class SearchDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if (oldItem is EvidenceItemEntity && newItem is EvidenceItemEntity) oldItem.id == newItem.id
            else if (oldItem is EvidenceEntity && newItem is EvidenceEntity) oldItem.id == newItem.id
            else false
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean = oldItem == newItem
    }

    companion object {
        private const val TYPE_EVIDENCE = 1
        private const val TYPE_ENTITY = 2
    }
}
