package com.mockanalysis.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mockanalysis.data.local.entity.MockAttemptEntity
import com.mockanalysis.data.local.entity.SubjectScoreEntity
import com.mockanalysis.data.local.entity.AttemptWithSubjects
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {

    @Transaction
    @Query("SELECT * FROM mock_attempts ORDER BY createdAt DESC LIMIT 1")
    fun observeLatestAttemptWithSubjects(): Flow<AttemptWithSubjects?>

    @Transaction
    @Query("SELECT * FROM mock_attempts ORDER BY createdAt DESC")
    fun observeAllAttemptsWithSubjects(): Flow<List<AttemptWithSubjects>>

    @Transaction
    @Query("SELECT * FROM mock_attempts WHERE id = :attemptId LIMIT 1")
    suspend fun getAttemptWithSubjectsById(attemptId: String): AttemptWithSubjects?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: MockAttemptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjectScores(subjectScores: List<SubjectScoreEntity>)

    @Query("DELETE FROM subject_scores WHERE attemptId = :attemptId")
    suspend fun deleteSubjectScoresForAttempt(attemptId: String)

    @Query("SELECT COUNT(*) FROM mock_attempts")
    suspend fun getAttemptCount(): Int

    @Transaction
    suspend fun insertAttemptWithSubjects(
        attempt: MockAttemptEntity,
        subjectScores: List<SubjectScoreEntity>
    ) {
        insertAttempt(attempt)
        deleteSubjectScoresForAttempt(attempt.id)
        insertSubjectScores(subjectScores)
    }
}
