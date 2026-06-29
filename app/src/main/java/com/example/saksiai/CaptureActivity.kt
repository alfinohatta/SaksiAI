package com.example.saksiai

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.saksiai.data.local.entity.EvidenceItemEntity
import com.example.saksiai.data.repository.EvidenceRepository
import com.example.saksiai.databinding.ActivityCaptureBinding
import com.example.saksiai.ui.viewmodel.EvidenceViewModel
import com.example.saksiai.ui.viewmodel.EvidenceViewModelFactory

class CaptureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCaptureBinding
    private var selectedSourceType: String? = null
    private var simulatedFileName: String? = null

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
        binding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupDropdowns()
        setupListeners()
    }

    private fun setupDropdowns() {
        val industries = arrayOf("BANKING", "INSURANCE", "HEALTHCARE", "GOVERNMENT", "CORPORATE", "OTHER")
        binding.spinnerIndustry.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, industries))
    }

    private fun setupListeners() {
        binding.btnScanDoc.setOnClickListener {
            selectedSourceType = "IMAGE"
            simulatedFileName = "scanned_evidence_${System.currentTimeMillis()}.jpg"
            updateCaptureStatus("Document Scan Captured: $simulatedFileName")
            highlightSelection(source = "IMAGE")
        }

        binding.btnRecordVoice.setOnClickListener {
            selectedSourceType = "AUDIO"
            simulatedFileName = "voice_intel_${System.currentTimeMillis()}.m4a"
            updateCaptureStatus("Voice Intelligence Recorded: $simulatedFileName")
            highlightSelection(source = "AUDIO")
        }

        binding.btnCaptureVideo.setOnClickListener {
            selectedSourceType = "VIDEO"
            simulatedFileName = "cctv_feed_${System.currentTimeMillis()}.mp4"
            updateCaptureStatus("Video Feed Capture initiated: $simulatedFileName")
            highlightSelection(source = "VIDEO")
        }

        binding.btnProcess.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val industry = binding.spinnerIndustry.text.toString()

            if (title.isBlank() || industry.isBlank() || selectedSourceType == null) {
                Toast.makeText(this, "Please provide title, industry, and capture evidence", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Layer 1 Simulation: User captures multimodal data
            val item = EvidenceItemEntity(
                organizationId = 1, // Default for demo
                sourceId = 1,
                title = title,
                fileName = simulatedFileName,
                fileType = selectedSourceType,
                fileUrl = "local://simulated/storage/$simulatedFileName",
                contentText = null, // AI will extract this in Layer 2
                status = "UPLOADED",
                confidenceScore = 0.0
            )

            viewModel.insert(item)
            Toast.makeText(this, "Evidence secure transmission initiated...", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun updateCaptureStatus(message: String) {
        binding.tvCaptureStatus.text = message
        binding.tvCaptureStatus.setTextColor(getColor(android.R.color.holo_green_dark))
    }

    private fun highlightSelection(source: String) {
        val activeColor = getColor(com.google.android.material.R.color.design_default_color_primary)
        val inactiveColor = getColor(android.R.color.darker_gray)
        
        binding.btnScanDoc.strokeColor = if (source == "IMAGE") activeColor else inactiveColor
        binding.btnRecordVoice.strokeColor = if (source == "AUDIO") activeColor else inactiveColor
        binding.btnCaptureVideo.strokeColor = if (source == "VIDEO") activeColor else inactiveColor
        
        binding.btnScanDoc.strokeWidth = if (source == "IMAGE") 4 else 1
        binding.btnRecordVoice.strokeWidth = if (source == "AUDIO") 4 else 1
        binding.btnCaptureVideo.strokeWidth = if (source == "VIDEO") 4 else 1
    }
}
