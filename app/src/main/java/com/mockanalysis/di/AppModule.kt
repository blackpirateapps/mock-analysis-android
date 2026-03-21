package com.mockanalysis.di

import android.content.Context
import androidx.room.Room
import com.mockanalysis.data.local.MockAnalysisDatabase
import com.mockanalysis.data.local.dao.AnalysisDao
import com.mockanalysis.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

/**
 * Hilt module for providing application-wide dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MockAnalysisDatabase {
        return Room.databaseBuilder(
            context,
            MockAnalysisDatabase::class.java,
            "mock_analysis.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideAnalysisDao(database: MockAnalysisDatabase): AnalysisDao = database.analysisDao()

    @Provides
    @Singleton
    fun provideUserDao(database: MockAnalysisDatabase): UserDao = database.userDao()
}
