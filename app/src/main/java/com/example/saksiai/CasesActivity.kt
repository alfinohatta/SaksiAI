package com.example.saksiai

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saksiai.data.repository.EvidenceRepository
import com.example.saksiai.databinding.ActivityCasesBinding
import com.example.saksiai.ui.adapter.CaseAdapter
import com.example.saksiai.ui.viewmodel.EvidenceViewModel
import com.example.saksiai.ui.viewmodel.EvidenceViewModelFactory

class CasesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCasesBinding
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
                database.caseDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCasesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val adapter = CaseAdapter { case ->
            val intent = Intent(this, CaseDetailActivity::class.java).apply {
                putExtra(CaseDetailActivity.EXTRA_CASE_ID, case.id)
            }
            startActivity(intent)
        }
        binding.rvCases.layoutManager = LinearLayoutManager(this)
        binding.rvCases.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAddCase.setOnClickListener {
            viewModel.createCase(
                title = "Decision Package - ${System.currentTimeMillis().toString().takeLast(4)}",
                type = "LENDING Intelligence",
                priority = "HIGH"
            )
        }
    }

    private fun observeViewModel() {
        viewModel.allCases.observe(this) { cases ->
            if (cases.isEmpty()) {
                binding.tvEmptyCases.visibility = View.VISIBLE
                binding.rvCases.visibility = View.GONE
            } else {
                binding.tvEmptyCases.visibility = View.GONE
                binding.rvCases.visibility = View.VISIBLE
                (binding.rvCases.adapter as CaseAdapter).submitList(cases)
            }
        }
    }
}
