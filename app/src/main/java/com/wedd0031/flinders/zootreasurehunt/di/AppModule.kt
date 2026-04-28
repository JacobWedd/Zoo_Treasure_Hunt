package com.wedd0031.flinders.zootreasurehunt.di

import android.content.Context
import androidx.room.Room
import com.wedd0031.flinders.zootreasurehunt.data.RoomSightingRepository
import com.wedd0031.flinders.zootreasurehunt.data.SightingDao
import com.wedd0031.flinders.zootreasurehunt.data.SightingRepository
import com.wedd0031.flinders.zootreasurehunt.data.ZooDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideZooDatabase(
        @ApplicationContext context: Context
    ): ZooDatabase {
        return Room.databaseBuilder(
            context,
            ZooDatabase::class.java,
            "zoo_database"
        ).build()
    }

    @Provides
    fun provideSightingDao(database: ZooDatabase): SightingDao {
        return database.sightingDao()
    }

    @Provides
    @Singleton
    fun provideSightingRepository(
        repository: RoomSightingRepository
    ): SightingRepository {
        return repository
    }
}
