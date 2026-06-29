package com.example.saksiai

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.saksiai.data.repository.EvidenceRepository
import com.example.saksiai.databinding.ActivityAnalyticsBinding
import com.example.saksiai.ui.viewmodel.EvidenceViewModel
import com.example.saksiai.ui.viewmodel.EvidenceViewModelFactory
import java.util.Locale

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding
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
                database.decisionDao(),
                database.complianceDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        observeAnalytics()
    }

    private fun observeAnalytics() {
        // A. Trust Intelligence: Aggregate confidence index
        viewModel.avgConfidence.observe(this) { avg ->
            binding.tvTrustScore.text = String.format(Locale.getDefault(), "%.1f%%", avg ?: 0.0)
        }

        // B. Risk Intelligence: Real-time fraud and compliance exposure
        viewModel.criticalRisksCount.observe(this) { count ->
            binding.tvRiskCount.text = count.toString()
            binding.progressBarRisk.progress = (count * 10).coerceAtMost(100)
        }

        // C. Process Intelligence: AI Pipeline performance
        viewModel.automationRate.observe(this) { rate ->
            binding.tvProcessEfficiency.text = String.format(Locale.getDefault(), "%.1f%% Automation Rate", rate ?: 0.0)
        }

        viewModel.avgProcessingTime.observe(this) { time ->
            val seconds = (time ?: 0) / 1000.0
            binding.tvProcessEfficiency.append("\nAvg Speed: ${String.format("%.1fs", seconds)}/record")
        }
        
        // D. Customer Intelligence: Simulated from Fraud trends
        viewModel.fraudExposure.observe(this) { exposure ->
            val sentiment = if (exposure > 5) "High Risk Environment" else "Stable Trust Environment"
            binding.tvCustomerSentiment.text = sentiment
        }

        // E. Institutional Memory: Knowledge Graph metrics
        viewModel.evidenceItems.observe(this) { items ->
            binding.tvMemorySize.text = "${items.size} Verified Evidence Units in Knowledge Graph"
        }

        // --- Regulatory & Compliance Health Index (Blueprint Section 8) ---
        
        viewModel.pdpComplianceScore.observe(this) { score ->
            binding.tvPdpCompliance.text = String.format(Locale.getDefault(), "%.1f%%", score ?: 100.0)
        }

        viewModel.auditCoverage.observe(this) { score ->
            binding.tvAuditCoverage.text = String.format(Locale.getDefault(), "%.1f%%", score ?: 0.0)
        }

        viewModel.criticalExposure.observe(this) { count ->
            binding.tvCriticalExposure.text = count.toString()
        }

        viewModel.orphanedEntities.observe(this) { count ->
            binding.tvOrphanedEntities.text = "$count orphaned entities detected in graph"
        }
    }
}
