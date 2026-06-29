package com.example.saksiai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.saksiai.data.local.entity.AuditLogEntity
import com.example.saksiai.databinding.ItemAuditLogBinding
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AuditLogAdapter : ListAdapter<AuditLogEntity, AuditLogAdapter.AuditViewHolder>(AuditDiffCallback()) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditViewHolder {
        val binding = ItemAuditLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AuditViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AuditViewHolder(private val binding: ItemAuditLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AuditLogEntity) {
            binding.tvLogAction.text = item.action
            binding.tvLogTime.text = dateFormat.format(Date(item.createdAt))
            
            // Layer 5 (AI Safety): Binding the secure Transaction Hash
            binding.tvTransactionHash.text = "TX_HASH: ${item.transactionHash}"

            // Explainable AI: Extracting reasoning from metadata path
            val reasoning = try {
                val json = JSONObject(item.metadataJson ?: "{}")
                json.optString("reasoning", "No reasoning data recorded.")
            } catch (e: Exception) {
                "Metadata parsing error."
            }
            binding.tvReasoningText.text = reasoning
        }
    }

    class AuditDiffCallback : DiffUtil.ItemCallback<AuditLogEntity>() {
        override fun areItemsTheSame(oldItem: AuditLogEntity, newItem: AuditLogEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AuditLogEntity, newItem: AuditLogEntity): Boolean = oldItem == newItem
    }
}
