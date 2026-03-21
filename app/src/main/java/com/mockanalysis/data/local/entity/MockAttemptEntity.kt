package com.mockanalysis.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "mock_attempts")
data class MockAttemptEntity(
    @PrimaryKey val id: String,
    val mockName: String,
    val examType: String,
    val dateEpochDay: Long,
    val score: Float,
    val maxScore: Float,
    val percentile: Float,
    val averageScore: Float,
    val topperScore: Float,
    val rank: Int,
    val totalParticipants: Int,
    val previousScore: Float?,
    val isPremium: Boolean,
    val createdAt: Long
)

@Entity(
    tableName = "subject_scores",
    foreignKeys = [
        ForeignKey(
            entity = MockAttemptEntity::class,
            parentColumns = ["id"],
            childColumns = ["attemptId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["attemptId"])]
)
data class SubjectScoreEntity(
    @PrimaryKey val id: String,
    val attemptId: String,
    val subjectId: String,
    val subjectName: String,
    val shortName: String,
    val score: Float,
    val maxScore: Float,
    val proficiencyLevel: String,
    val averageTimePerQuestion: Int?
)
