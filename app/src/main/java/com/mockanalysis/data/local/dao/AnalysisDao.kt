package com.mockanalysis.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mockanalysis.data.local.entity.MockAttemptEntity
import com.mockanalysis.data.local.entity.SubjectScoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {

    @Query("SELECT * FROM mock_attempts ORDER BY createdAt DESC LIMIT 1")
    fun observeLatestAttempt(): Flow<MockAttemptEntity?>

    @Query("SELECT * FROM mock_attempts ORDER BY createdAt DESC")
    fun observeAllAttempts(): Flow<List<MockAttemptEntity>>

    @Query("SELECT * FROM mock_attempts WHERE id = :attemptId LIMIT 1")
    suspend fun getAttemptById(attemptId: String): MockAttemptEntity?

    @Query("SELECT * FROM subject_scores WHERE attemptId = :attemptId")
    suspend fun getSubjectScoresForAttempt(attemptId: String): List<SubjectScoreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: MockAttemptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjectScores(subjectScores: List<SubjectScoreEntity>)

    @Query("DELETE FROM subject_scores WHERE attemptId = :attemptId")
    suspend fun deleteSubjectScoresForAttempt(attemptId: String)

    @Query("SELECT COUNT(*) FROM mock_attempts")
    suspend fun getAttemptCount(): Int
}
