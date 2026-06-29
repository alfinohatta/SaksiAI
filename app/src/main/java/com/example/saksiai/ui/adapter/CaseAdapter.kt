package com.example.saksiai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.saksiai.data.local.entity.CaseEntity
import com.example.saksiai.databinding.ItemCaseBinding
import java.text.SimpleDateFormat
import java.util.*

class CaseAdapter(private val onCaseClick: (CaseEntity) -> Unit) :
    ListAdapter<CaseEntity, CaseAdapter.CaseViewHolder>(CaseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseViewHolder {
        val binding = ItemCaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CaseViewHolder(binding, onCaseClick)
    }

    override fun onBindViewHolder(holder: CaseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CaseViewHolder(
        private val binding: ItemCaseBinding,
        private val onCaseClick: (CaseEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

        fun bind(item: CaseEntity) {
            binding.tvCaseTitle.text = item.caseTitle
            binding.tvCaseType.text = item.caseType
            binding.tvCaseStatus.text = item.status
            binding.tvCasePriority.text = item.priority
            binding.tvCaseUpdated.text = "Last updated: ${dateFormat.format(Date(item.updatedAt))}"

            // Priority color coding
            val priorityColor = when (item.priority) {
                "CRITICAL" -> 0xFFC62828.toInt()
                "HIGH" -> 0xFFD32F2F.toInt()
                "MEDIUM" -> 0xFFF57C00.toInt()
                else -> 0xFF388E3C.toInt()
            }
            binding.tvCasePriority.setTextColor(priorityColor)
            binding.tvCasePriority.backgroundTintList = android.content.res.ColorStateList.valueOf(priorityColor).withAlpha(30)

            binding.root.setOnClickListener {
                onCaseClick(item)
            }
        }
    }

    class CaseDiffCallback : DiffUtil.ItemCallback<CaseEntity>() {
        override fun areItemsTheSame(oldItem: CaseEntity, newItem: CaseEntity): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CaseEntity, newItem: CaseEntity): Boolean =
            oldItem == newItem
    }
}
