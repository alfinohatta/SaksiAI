package com.example.saksiai

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saksiai.data.repository.EvidenceRepository
import com.example.saksiai.databinding.ActivityKnowledgeNetworkBinding
import com.example.saksiai.ui.adapter.EntityAdapter
import com.example.saksiai.ui.viewmodel.EvidenceViewModel
import com.example.saksiai.ui.viewmodel.EvidenceViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class KnowledgeNetworkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKnowledgeNetworkBinding
    private val searchFlow = MutableStateFlow("")
    
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
        binding = ActivityKnowledgeNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val adapter = EntityAdapter { entity ->
            val intent = Intent(this, EntityDetailActivity::class.java).apply {
                putExtra(EntityDetailActivity.EXTRA_ENTITY_ID, entity.id)
                putExtra(EntityDetailActivity.EXTRA_ENTITY_NAME, entity.entityName)
            }
            startActivity(intent)
        }
        binding.rvEntities.layoutManager = LinearLayoutManager(this)
        binding.rvEntities.adapter = adapter
    }

    private fun setupSearch() {
        binding.etEntitySearch.addTextChangedListener { text ->
            // Institutional Memory: Dynamic filtering of the knowledge base
            searchFlow.value = text?.toString() ?: ""
        }
    }

    private fun observeViewModel() {
        // Institutional Memory: Combines graph traversal with real-time search
        searchFlow.flatMapLatest { query ->
            if (query.isEmpty()) {
                (application as SaksiApplication).database.entityDao().getEntitiesByOrg(1)
            } else {
                (application as SaksiApplication).database.entityDao().searchEntities("%$query%")
            }
        }.asLiveData().observe(this) { entities ->
            (binding.rvEntities.adapter as EntityAdapter).submitList(entities)
        }
    }
}
