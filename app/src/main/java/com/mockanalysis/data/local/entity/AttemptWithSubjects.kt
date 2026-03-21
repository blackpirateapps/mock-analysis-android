package com.mockanalysis.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AttemptWithSubjects(
    @Embedded val attempt: MockAttemptEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "attemptId"
    )
    val subjects: List<SubjectScoreEntity>
)
