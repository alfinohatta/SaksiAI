package com.example.saksiai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.saksiai.data.local.dao.*
import com.example.saksiai.data.local.entity.*

@Database(
    entities = [
        OrganizationEntity::class,
        UserEntity::class,
        EvidenceSourceEntity::class,
        EvidenceItemEntity::class,
        AIJobEntity::class,
        EvidenceEntity::class,
        EvidenceRelationshipEntity::class,
        RiskAssessmentEntity::class,
        ReviewTaskEntity::class,
        AuditLogEntity::class,
        AIModelEntity::class,
        APIKeyEntity::class,
        CaseEntity::class,
        CaseEvidenceCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SaksiDatabase : RoomDatabase() {
    abstract fun evidenceDao(): EvidenceDao
    abstract fun entityDao(): EntityDao
    abstract fun riskDao(): RiskDao
    abstract fun userDao(): UserDao
    abstract fun evidenceSourceDao(): EvidenceSourceDao
    abstract fun aiJobDao(): AIJobDao
    abstract fun organizationDao(): OrganizationDao
    abstract fun auditDao(): AuditDao
    abstract fun reviewTaskDao(): ReviewTaskDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun caseDao(): CaseDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun decisionDao(): DecisionIntelligenceDao
    abstract fun complianceDao(): ComplianceDao
}
