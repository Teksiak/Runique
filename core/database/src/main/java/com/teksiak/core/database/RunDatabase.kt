package com.teksiak.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.teksiak.core.database.dao.AnalyticsDao
import com.teksiak.core.database.dao.RunDao
import com.teksiak.core.database.dao.RunSyncDao
import com.teksiak.core.database.entity.RunDeletedSyncEntity
import com.teksiak.core.database.entity.RunEntity
import com.teksiak.core.database.entity.RunPendingSyncEntity


@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        RunDeletedSyncEntity::class
    ],
    version = 1
)
abstract class RunDatabase: RoomDatabase() {

    abstract val runDao: RunDao
    abstract val runSyncDao: RunSyncDao
    abstract val analyticsDao: AnalyticsDao
}