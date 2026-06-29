package com.example.saksiai

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saksiai.data.repository.EvidenceRepository
import com.example.saksiai.databinding.ActivityEntityDetailBinding
import com.example.saksiai.ui.adapter.EvidenceAdapter
import com.example.saksiai.ui.adapter.RelationshipAdapter
import com.example.saksiai.ui.adapter.RiskAdapter
import com.example.saksiai.ui.viewmodel.EvidenceViewModel
import com.example.saksiai.ui.viewmodel.EvidenceViewModelFactory

class EntityDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntityDetailBinding
    
    private val evidenceAdapter = EvidenceAdapter { evidence ->
        val intent = Intent(this, EvidenceDetailActivity::class.java).apply {
            putExtra(EvidenceDetailActivity.EXTRA_EVIDENCE_ID, evidence.id)
        }
        startActivity(intent)
    }
    
    private val riskAdapter = RiskAdapter()
    
    private val relationshipAdapter = RelationshipAdapter { targetEntityId ->
        // Deep Graph Exploration: Navigate to the connected entity's detail
        val intent = Intent(this, EntityDetailActivity::class.java).apply {
            putExtra(EXTRA_ENTITY_ID, targetEntityId)
            putExtra(EXTRA_ENTITY_NAME, "Entity #$targetEntityId")
        }
        startActivity(intent)
    }

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
        binding = ActivityEntityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerViews()

        val entityId = intent.getLongExtra(EXTRA_ENTITY_ID, -1L)
        val entityName = intent.getStringExtra(EXTRA_ENTITY_NAME)
        
        binding.tvEntityDetailName.text = entityName ?: "Unknown Entity"

        if (entityId != -1L) {
            observeEntityData(entityId)
        } else {
            finish()
        }
    }

    private fun setupRecyclerViews() {
        binding.rvRelationships.apply {
            layoutManager = LinearLayoutManager(this@EntityDetailActivity)
            adapter = relationshipAdapter
        }
        binding.rvConnectedEvidence.apply {
            layoutManager = LinearLayoutManager(this@EntityDetailActivity)
            adapter = evidenceAdapter
        }
        binding.rvEntityRisks.apply {
            layoutManager = LinearLayoutManager(this@EntityDetailActivity)
            adapter = riskAdapter
        }
    }

    private fun observeEntityData(id: Long) {
        // Evidence Graph: Show mapped relationships in the network
        viewModel.getRelationshipsForEntity(id).observe(this) { relationships ->
            relationshipAdapter.submitList(relationships)
        }

        // Institutional Memory: Show all evidence items where this entity is mentioned
        viewModel.getEvidenceForEntity(id).observe(this) { evidenceList ->
            evidenceAdapter.submitList(evidenceList)
        }

        // Decision Intelligence: Show every risk connected to this entity via the Evidence Graph
        // This implements Layer 4: "Show every risk connected to this customer."
        viewModel.getGlobalRiskProfile(id).observe(this) { risks ->
            riskAdapter.submitList(risks)
        }
    }

    companion object {
        const val EXTRA_ENTITY_ID = "extra_entity_id"
        const val EXTRA_ENTITY_NAME = "extra_entity_name"
    }
}
