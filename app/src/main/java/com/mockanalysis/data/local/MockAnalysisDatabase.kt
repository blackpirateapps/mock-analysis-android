package com.mockanalysis.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.mockanalysis.data.local.dao.AnalysisDao
import com.mockanalysis.data.local.dao.UserDao
import com.mockanalysis.data.local.entity.AchievementEntity
import com.mockanalysis.data.local.entity.LinkedPlatformEntity
import com.mockanalysis.data.local.entity.MockAttemptEntity
import com.mockanalysis.data.local.entity.SubjectScoreEntity
import com.mockanalysis.data.local.entity.UserProfileEntity

@Database(
    entities = [
        MockAttemptEntity::class,
        SubjectScoreEntity::class,
        UserProfileEntity::class,
        AchievementEntity::class,
        LinkedPlatformEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(BooleanConverters::class)
abstract class MockAnalysisDatabase : RoomDatabase() {
    abstract fun analysisDao(): AnalysisDao
    abstract fun userDao(): UserDao
}

object BooleanConverters {
    @TypeConverter
    fun fromBoolean(value: Boolean): Int = if (value) 1 else 0

    @TypeConverter
    fun toBoolean(value: Int): Boolean = value == 1
}
