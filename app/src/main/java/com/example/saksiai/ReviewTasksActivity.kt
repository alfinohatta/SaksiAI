package com.example.saksiai

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saksiai.data.repository.EvidenceRepository
import com.example.saksiai.databinding.ActivityReviewTasksBinding
import com.example.saksiai.ui.adapter.ReviewTaskAdapter
import com.example.saksiai.ui.viewmodel.EvidenceViewModel
import com.example.saksiai.ui.viewmodel.EvidenceViewModelFactory

class ReviewTasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewTasksBinding
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
        binding = ActivityReviewTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val adapter = ReviewTaskAdapter { task ->
            // Trust Engine: Navigate to the flagged evidence for manual decision
            val intent = Intent(this, EvidenceDetailActivity::class.java).apply {
                putExtra(EvidenceDetailActivity.EXTRA_EVIDENCE_ID, task.evidenceId)
            }
            startActivity(intent)
        }
        binding.rvReviewTasks.layoutManager = LinearLayoutManager(this)
        binding.rvReviewTasks.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.allReviewTasks.observe(this) { tasks ->
            if (tasks.isEmpty()) {
                binding.tvEmptyReviews.visibility = View.VISIBLE
                binding.rvReviewTasks.visibility = View.GONE
            } else {
                binding.tvEmptyReviews.visibility = View.GONE
                binding.rvReviewTasks.visibility = View.VISIBLE
                (binding.rvReviewTasks.adapter as ReviewTaskAdapter).submitList(tasks)
            }
        }
    }
}
