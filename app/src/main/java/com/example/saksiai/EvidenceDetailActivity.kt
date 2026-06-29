package com.example.saksiai

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saksiai.data.local.UserSession
import com.example.saksiai.data.repository.EvidenceRepository
import com.example.saksiai.databinding.ActivityEvidenceDetailBinding
import com.example.saksiai.ui.adapter.AuditLogAdapter
import com.example.saksiai.ui.adapter.EntityAdapter
import com.example.saksiai.ui.adapter.RelationshipAdapter
import com.example.saksiai.ui.adapter.RiskAdapter
import com.example.saksiai.ui.viewmodel.EvidenceViewModel
import com.example.saksiai.ui.viewmodel.EvidenceViewModelFactory

class EvidenceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEvidenceDetailBinding
    private lateinit var userSession: UserSession
    
    private val entityAdapter = EntityAdapter { entity ->
        val intent = Intent(this, EntityDetailActivity::class.java).apply {
            putExtra(EntityDetailActivity.EXTRA_ENTITY_ID, entity.id)
            putExtra(EntityDetailActivity.EXTRA_ENTITY_NAME, entity.entityName)
        }
        startActivity(intent)
    }
    private val riskAdapter = RiskAdapter()
    private val auditLogAdapter = AuditLogAdapter()
    private val relationshipAdapter = RelationshipAdapter { targetId ->
        val intent = Intent(this, EntityDetailActivity::class.java).apply {
            putExtra(EntityDetailActivity.EXTRA_ENTITY_ID, targetId)
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
        binding = ActivityEvidenceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        userSession = UserSession(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerViews()
        setupListeners()

        val evidenceId = intent.getLongExtra(EXTRA_EVIDENCE_ID, -1L)
        if (evidenceId != -1L) {
            observeEvidence(evidenceId)
        } else {
            finish()
        }
    }

    private fun setupRecyclerViews() {
        binding.rvEntities.apply {
            layoutManager = LinearLayoutManager(this@EvidenceDetailActivity)
            adapter = entityAdapter
        }
        binding.rvRisks.apply {
            layoutManager = LinearLayoutManager(this@EvidenceDetailActivity)
            adapter = riskAdapter
        }
        binding.rvAuditLogs.apply {
            layoutManager = LinearLayoutManager(this@EvidenceDetailActivity)
            adapter = auditLogAdapter
        }
        binding.rvRelationships.apply {
            layoutManager = LinearLayoutManager(this@EvidenceDetailActivity)
            adapter = relationshipAdapter
        }
    }

    private fun setupListeners() {
        val evidenceId = intent.getLongExtra(EXTRA_EVIDENCE_ID, -1L)

        binding.btnAuditTrail.setOnClickListener {
            val isVisible = binding.rvAuditLogs.visibility == View.VISIBLE
            if (isVisible) {
                binding.rvAuditLogs.visibility = View.GONE
                binding.btnAuditTrail.text = "View Secure Audit Trail"
            } else {
                binding.rvAuditLogs.visibility = View.VISIBLE
                binding.btnAuditTrail.text = "Hide Audit Trail"
            }
        }

        binding.btnLinkCase.setOnClickListener {
            showCaseSelectionDialog(evidenceId)
        }

        binding.btnApprove.setOnClickListener {
            viewModel.resolveReview(evidenceId, true, binding.etReviewerNotes.text.toString())
            Toast.makeText(this, "Evidence Intelligence Verified", Toast.LENGTH_SHORT).show()
            finish()
        }
        
        binding.btnReject.setOnClickListener {
            viewModel.resolveReview(evidenceId, false, binding.etReviewerNotes.text.toString())
            Toast.makeText(this, "Intelligence Rejected", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showCaseSelectionDialog(evidenceId: Long) {
        viewModel.allCases.observe(this) { cases ->
            if (cases.isNullOrEmpty()) {
                Toast.makeText(this, "No active cases found. Create one in Decision Intelligence.", Toast.LENGTH_SHORT).show()
                return@observe
            }

            val titles = cases.map { it.caseTitle }.toTypedArray()
            AlertDialog.Builder(this)
                .setTitle("Link to Decision Case")
                .setItems(titles) { _, which ->
                    viewModel.linkEvidenceToCase(cases[which].id, evidenceId)
                    Toast.makeText(this, "Linked to ${cases[which].caseTitle}", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun observeEvidence(id: Long) {
        viewModel.getEvidenceItem(id).observe(this) { item ->
            item?.let {
                binding.tvDetailTitle.text = it.title
                binding.tvDetailStatus.text = it.status
                binding.tvDetailConfidence.text = "${it.confidenceScore?.toInt()}%"
                binding.tvDetailContent.text = it.contentText ?: "Extraction in progress..."

                val color = when (it.status) {
                    "VERIFIED" -> 0xFF4CAF50.toInt()
                    "FLAGGED" -> 0xFFF44336.toInt()
                    else -> 0xFF2196F3.toInt()
                }
                binding.tvDetailStatus.setTextColor(color)

                if (it.status == "FLAGGED" && userSession.isAuthorizedForSensitiveData()) {
                    binding.panelReviewActions.visibility = View.VISIBLE
                }
            }
        }

        viewModel.getRisksForEvidence(id).observe(this) { risks ->
            riskAdapter.submitList(risks)
        }

        viewModel.getAuditLogsForEvidence(id).observe(this) { logs ->
            auditLogAdapter.submitList(logs)
        }
    }

    companion object {
        const val EXTRA_EVIDENCE_ID = "extra_evidence_id"
    }
}
