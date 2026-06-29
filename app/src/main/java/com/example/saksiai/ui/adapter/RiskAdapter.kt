package com.example.saksiai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.saksiai.data.local.entity.RiskAssessmentEntity
import com.example.saksiai.databinding.ItemRiskBinding

class RiskAdapter : ListAdapter<RiskAssessmentEntity, RiskAdapter.RiskViewHolder>(RiskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskViewHolder {
        val binding = ItemRiskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RiskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RiskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RiskViewHolder(private val binding: ItemRiskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RiskAssessmentEntity) {
            binding.tvRiskType.text = item.riskType
            binding.tvRiskSeverity.text = item.severity
            binding.tvRiskScore.text = "Score: ${item.riskScore}"
            binding.tvRiskExplanation.text = item.explanation
            
            // Severity color coding
            val color = when(item.severity) {
                "CRITICAL" -> 0xFFB71C1C.toInt()
                "HIGH" -> 0xFFD32F2F.toInt()
                "MEDIUM" -> 0xFFF57C00.toInt()
                else -> 0xFF388E3C.toInt()
            }
            binding.tvRiskSeverity.setBackgroundColor(color)
        }
    }

    class RiskDiffCallback : DiffUtil.ItemCallback<RiskAssessmentEntity>() {
        override fun areItemsTheSame(oldItem: RiskAssessmentEntity, newItem: RiskAssessmentEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: RiskAssessmentEntity, newItem: RiskAssessmentEntity): Boolean = oldItem == newItem
    }
}
