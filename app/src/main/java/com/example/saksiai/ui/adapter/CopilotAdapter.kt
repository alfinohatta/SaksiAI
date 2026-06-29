package com.example.saksiai.ui.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.saksiai.data.local.entity.ChatMessage
import com.example.saksiai.databinding.ItemChatMessageBinding

class CopilotAdapter : ListAdapter<ChatMessage, CopilotAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChatViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.tvMessageContent.text = message.content
            
            val params = binding.cardChatBubble.layoutParams as LinearLayout.LayoutParams
            if (message.isUser) {
                params.gravity = Gravity.END
                binding.cardChatBubble.setCardBackgroundColor(0xFFD1E4FF.toInt()) // Light Blue
            } else {
                params.gravity = Gravity.START
                binding.cardChatBubble.setCardBackgroundColor(0xFFFFFFFF.toInt()) // White
            }
            binding.cardChatBubble.layoutParams = params
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean = 
            oldItem.timestamp == newItem.timestamp
        
        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean = 
            oldItem == newItem
    }
}
