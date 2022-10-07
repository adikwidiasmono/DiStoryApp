package com.distory.app.domain.story.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stories", indices = [Index(value = ["id"], unique = true)])
data class StoryEntity(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "photo_url")
    var photoUrl: String,
    @ColumnInfo(name = "created_at")
    var createdAt: String,
    @ColumnInfo(name = "latitude")
    var latitude: Double,
    @ColumnInfo(name = "longitude")
    var longitude: Double
) : Parcelable