package com.example.saksiai

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saksiai.data.local.entity.RiskAssessmentEntity
import com.example.saksiai.data.repository.EvidenceRepository
import com.example.saksiai.databinding.ActivityCaseDetailBinding
import com.example.saksiai.ui.adapter.EvidenceAdapter
import com.example.saksiai.ui.adapter.RiskAdapter
import com.example.saksiai.ui.viewmodel.EvidenceViewModel
import com.example.saksiai.ui.viewmodel.EvidenceViewModelFactory
import kotlinx.coroutines.launch

class CaseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCaseDetailBinding
    
    private val evidenceAdapter = EvidenceAdapter { evidence ->
        val intent = Intent(this, EvidenceDetailActivity::class.java).apply {
            putExtra(EvidenceDetailActivity.EXTRA_EVIDENCE_ID, evidence.id)
        }
        startActivity(intent)
    }
    
    private val riskAdapter = RiskAdapter()

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
        binding = ActivityCaseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val caseId = intent.getLongExtra(EXTRA_CASE_ID, -1L)

        setupRecyclerViews()
        setupListeners(caseId)

        if (caseId != -1L) {
            observeCaseData(caseId)
        } else {
            finish()
        }
    }

    private fun setupRecyclerViews() {
        binding.rvCaseEvidence.apply {
            layoutManager = LinearLayoutManager(this@CaseDetailActivity)
            adapter = evidenceAdapter
        }
        binding.rvCaseRisks.apply {
            layoutManager = LinearLayoutManager(this@CaseDetailActivity)
            adapter = riskAdapter
        }
    }

    private fun setupListeners(caseId: Long) {
        binding.btnFinalizeDecision.setOnClickListener {
            // Layer 5 (Decision Intelligence): Committing the decision to Institutional Memory
            val outcome = binding.tvCaseRecommendation.text.toString()
            viewModel.finalizeCaseDecision(caseId, outcome)
            
            Toast.makeText(this, "Business Decision Executed and Witnessed", Toast.LENGTH_LONG).show()
            finish()
        }

        binding.btnExportPackage.setOnClickListener {
            // Institutional Memory: Exporting the Intelligence Package for external audit (OJK/BI)
            viewModel.exportDecisionPackage(caseId).observe(this) { report ->
                showExportDialog(report)
            }
        }
    }

    private fun showExportDialog(report: String) {
        AlertDialog.Builder(this)
            .setTitle("Intelligence Package Export")
            .setMessage(report)
            .setPositiveButton("Share/Save") { _, _ ->
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, report)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun observeCaseData(id: Long) {
        // Load case header information
        lifecycleScope.launch {
            val case = (application as SaksiApplication).database.caseDao().getCaseById(id)
            case?.let {
                binding.tvCaseDetailTitle.text = it.caseTitle
                binding.tvCaseDetailType.text = it.caseType
                binding.tvCaseDetailStatus.text = "Status: ${it.status}"
                binding.tvCaseDetailPriority.text = it.priority
                
                if (it.status == "DECIDED") {
                    binding.btnFinalizeDecision.visibility = View.GONE
                    binding.tvCaseRecommendation.append(" (FINAL)")
                }
            }
        }

        // Observe aggregated evidence bundle for this case
        viewModel.getEvidenceForCase(id).observe(this) { evidenceList ->
            evidenceAdapter.submitList(evidenceList)
        }

        // Observe aggregated risks and generate AI recommendation
        viewModel.getRisksForCase(id).observe(this) { risks ->
            riskAdapter.submitList(risks)
            updateAiRecommendation(risks)
        }
    }

    private fun updateAiRecommendation(risks: List<RiskAssessmentEntity>) {
        val hasCriticalRisk = risks.any { it.severity == "CRITICAL" }
        val hasHighRisk = risks.any { it.severity == "HIGH" }
        
        when {
            risks.isEmpty() -> {
                binding.tvCaseRecommendation.text = "AI Recommendation: PROCEED (Low Risk)"
                binding.tvCaseRecommendation.setTextColor(0xFF4CAF50.toInt()) // Green
            }
            hasCriticalRisk -> {
                binding.tvCaseRecommendation.text = "AI Recommendation: REJECT (Critical Identity Mismatch)"
                binding.tvCaseRecommendation.setTextColor(0xFFF44336.toInt()) // Red
            }
            hasHighRisk -> {
                binding.tvCaseRecommendation.text = "AI Recommendation: MANUAL REVIEW (Contradiction Found)"
                binding.tvCaseRecommendation.setTextColor(0xFFFF9800.toInt()) // Orange
            }
            else -> {
                binding.tvCaseRecommendation.text = "AI Recommendation: PROCEED with Caution"
                binding.tvCaseRecommendation.setTextColor(0xFF2196F3.toInt()) // Blue
            }
        }
    }

    companion object {
        const val EXTRA_CASE_ID = "extra_case_id"
    }
}
