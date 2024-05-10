package com.teksiak.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RunDeletedSync")
data class RunDeletedSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val runId: String,
    val userId: String
)
