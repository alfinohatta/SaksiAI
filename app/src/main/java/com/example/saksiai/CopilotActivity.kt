package com.example.saksiai

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saksiai.data.local.entity.ChatMessage
import com.example.saksiai.data.repository.EvidenceRepository
import com.example.saksiai.databinding.ActivityCopilotBinding
import com.example.saksiai.ui.adapter.CopilotAdapter
import com.example.saksiai.ui.viewmodel.EvidenceViewModel
import com.example.saksiai.ui.viewmodel.EvidenceViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CopilotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCopilotBinding
    private val adapter = CopilotAdapter()
    private val chatMessages = mutableListOf<ChatMessage>()

    private val viewModel: EvidenceViewModel by viewModels {
        val database = (application as SaksiApplication).database
        EvidenceViewModelFactory(
            application,
            EvidenceRepository(
                database.evidenceDao(),
                database.entityDao(),
                database.riskDao(),
                database.auditDao(),
                database.reviewTaskDao(),
                database.relationshipDao(),
                database.caseDao(),
                database.analyticsDao(),
                database.decisionDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopilotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupListeners()

        // Welcome message
        addMessage("Hello! I am your SaksiAI Compliance Copilot. How can I help you today?", false)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.rvChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.rvChat.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabSend.setOnClickListener {
            val query = binding.etQuery.text.toString()
            if (query.isNotBlank()) {
                handleUserQuery(query)
                binding.etQuery.text.clear()
            }
        }
    }

    private fun handleUserQuery(query: String) {
        addMessage(query, true)
        
        lifecycleScope.launch {
            // Simulate AI Processing Delay
            delay(1000)
            
            // --- RAG Logic (Retrieval-Augmented Generation) ---
            // 1. Retrieval Phase: Search Institutional Memory
            val database = (application as SaksiApplication).database
            val matchedEvidence = database.evidenceDao().searchEvidenceItems("%$query%").first()
            val matchedEntities = database.entityDao().searchEntities("%$query%").first()

            // 2. Generation Phase: Construct a response based on retrieved evidence
            val response = when {
                matchedEvidence.isNotEmpty() -> {
                    val first = matchedEvidence.first()
                    "According to evidence ID #${first.id} (${first.title}), the status is ${first.status}. Trust score is ${first.confidenceScore?.toInt() ?: 0}%. " +
                            if (first.status == "FLAGGED") "Approval is NOT recommended without human review." else "This aligns with verified records."
                }
                matchedEntities.isNotEmpty() -> {
                    val entity = matchedEntities.first()
                    "I found a Knowledge Graph entity for '${entity.entityName}' (${entity.entityType}). Identifier: ${entity.externalIdentifier ?: "N/A"}. " +
                            "This entity is part of our Institutional Memory."
                }
                query.contains("approve", ignoreCase = true) -> {
                    "To evaluate an approval, please specify the customer name or document ID. My Trust Engine requires evidence retrieval before answering."
                }
                else -> "I couldn't find specific evidence in our Institutional Memory matching '$query'. Please ensure the data has been captured into SaksiAI."
            }

            addMessage(response, false)
        }
    }

    private fun addMessage(content: String, isUser: Boolean) {
        chatMessages.add(ChatMessage(content, isUser))
        adapter.submitList(chatMessages.toList())
        binding.rvChat.smoothScrollToPosition(chatMessages.size - 1)
    }
}
