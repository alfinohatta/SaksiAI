package com.example.saksiai

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saksiai.data.local.UserSession
import com.example.saksiai.data.local.entity.CaseEntity
import com.example.saksiai.data.local.entity.EvidenceEntity
import com.example.saksiai.data.local.entity.EvidenceItemEntity
import com.example.saksiai.data.repository.EvidenceRepository
import com.example.saksiai.databinding.ActivityMainBinding
import com.example.saksiai.ui.adapter.IntelligenceSearchAdapter
import com.example.saksiai.ui.viewmodel.EvidenceViewModel
import com.example.saksiai.ui.viewmodel.EvidenceViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userSession: UserSession
    
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        userSession = UserSession(this)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        setupSearch()
        applyRoleRestrictions()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        updateToolbarSubtitle()
    }

    private fun updateToolbarSubtitle() {
        supportActionBar?.subtitle = "Role: ${userSession.userRole}"
    }

    private fun setupRecyclerView() {
        binding.rvEvidence.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = IntelligenceSearchAdapter(
                onEvidenceClick = { evidence ->
                    val intent = Intent(this@MainActivity, EvidenceDetailActivity::class.java).apply {
                        putExtra(EvidenceDetailActivity.EXTRA_EVIDENCE_ID, evidence.id)
                    }
                    startActivity(intent)
                },
                onEntityClick = { entity ->
                    val intent = Intent(this@MainActivity, EntityDetailActivity::class.java).apply {
                        putExtra(EntityDetailActivity.EXTRA_ENTITY_ID, entity.id)
                        putExtra(EntityDetailActivity.EXTRA_ENTITY_NAME, entity.entityName)
                    }
                    startActivity(intent)
                }
            )
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.setSearchQuery(text?.toString() ?: "")
        }
    }

    private fun applyRoleRestrictions() {
        val canManage = userSession.isAuthorizedForSensitiveData()
        binding.fabAdd.visibility = if (canManage) View.VISIBLE else View.GONE
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { results ->
            if (results.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvEvidence.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvEvidence.visibility = View.VISIBLE
                (binding.rvEvidence.adapter as IntelligenceSearchAdapter).submitList(results)
            }
        }

        viewModel.avgConfidence.observe(this) { avg ->
            binding.tvAvgConfidence.text = if (avg != null) {
                String.format(Locale.getDefault(), "%.1f%%", avg)
            } else {
                "0.0%"
            }
        }

        viewModel.totalRisks.observe(this) { count ->
            binding.tvRisksCount.text = count.toString()
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, CaptureActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_copilot -> {
                startActivity(Intent(this, CopilotActivity::class.java))
                true
            }
            R.id.action_simulate_fraud -> {
                runFraudSimulation()
                true
            }
            R.id.action_simulate_mortgage -> {
                runMortgageSimulation()
                true
            }
            R.id.action_switch_role -> {
                switchRoleSimulation()
                true
            }
            R.id.action_cases -> {
                startActivity(Intent(this, CasesActivity::class.java))
                true
            }
            R.id.action_knowledge_network -> {
                startActivity(Intent(this, KnowledgeNetworkActivity::class.java))
                true
            }
            R.id.action_review_queue -> {
                startActivity(Intent(this, ReviewTasksActivity::class.java))
                true
            }
            R.id.action_analytics -> {
                startActivity(Intent(this, AnalyticsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Simulation: ID Contradiction (Synthetic Identity)
     */
    private fun runFraudSimulation() {
        lifecycleScope.launch {
            val database = (application as SaksiApplication).database
            
            database.entityDao().insertEntity(EvidenceEntity(
                organizationId = 1,
                entityType = "PERSON",
                entityName = "ALFINO HATTA",
                externalIdentifier = "3273-VERIFIED-NIK-001" 
            ))

            database.caseDao().insertCase(CaseEntity(
                organizationId = 1,
                caseTitle = "Investigation: Identity Conflict (Alfino Hatta)",
                caseType = "FRAUD_INVESTIGATION",
                priority = "CRITICAL"
            ))

            val fraudulentItem = EvidenceItemEntity(
                organizationId = 1,
                sourceId = 1,
                title = "KTP Capture (Suspected Forgery)",
                fileName = "forged_payload.jpg",
                fileType = "IMAGE",
                fileUrl = "local://simulated/forgery.jpg",
                contentText = null,
                status = "UPLOADED",
                confidenceScore = 0.0
            )

            viewModel.insert(fraudulentItem)
            Toast.makeText(this@MainActivity, "Identity Fraud Simulation Started...", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Simulation: Financial Contradiction (Mortgage Use Case)
     * Document A (Historical): Rp 20M vs Document B (Current): Rp 50M
     */
    private fun runMortgageSimulation() {
        lifecycleScope.launch {
            val database = (application as SaksiApplication).database

            // 1. Seed historical verified data for the entity
            val entityId = database.entityDao().insertEntity(EvidenceEntity(
                organizationId = 1,
                entityType = "PERSON",
                entityName = "ALFINO HATTA",
                externalIdentifier = "3273010101950001"
            ))

            database.evidenceDao().insertEvidenceItem(EvidenceItemEntity(
                organizationId = 1,
                sourceId = 1,
                title = "Historical Salary Slip (2023)",
                fileName = "old_slip.pdf",
                fileType = "DOCUMENT",
                status = "VERIFIED",
                contentText = "INCOME EVIDENCE INTELLIGENCE:\nEntity: ALFINO HATTA\nExtracted Monthly Income: Rp 20,000,000",
                confidenceScore = 99.0
            ))

            // 2. Trigger new capture for Mortgage application
            val currentSlip = EvidenceItemEntity(
                organizationId = 1,
                sourceId = 1,
                title = "Current Salary Slip (Mortgage App)",
                fileName = "current_slip_gaji.jpg",
                fileType = "IMAGE",
                fileUrl = "local://simulated/current_slip.jpg",
                contentText = null,
                status = "UPLOADED",
                confidenceScore = 0.0
            )

            viewModel.insert(currentSlip)
            Toast.makeText(this@MainActivity, "Mortgage Income Contradiction Simulation Started...", Toast.LENGTH_LONG).show()
            
            // Navigate to Case list to see the impact
            startActivity(Intent(this@MainActivity, CasesActivity::class.java))
        }
    }

    private fun switchRoleSimulation() {
        val roles = arrayOf("ADMIN", "COMPLIANCE", "ANALYST", "VIEWER")
        val currentIndex = roles.indexOf(userSession.userRole)
        val nextIndex = (currentIndex + 1) % roles.size
        userSession.userRole = roles[nextIndex]
        updateToolbarSubtitle()
        applyRoleRestrictions()
        Toast.makeText(this, "Switched to ${userSession.userRole} role", Toast.LENGTH_SHORT).show()
    }
}
