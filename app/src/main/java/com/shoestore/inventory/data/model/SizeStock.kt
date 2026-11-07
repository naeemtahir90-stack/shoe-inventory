package com.shoestore.inventory.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "size_stocks",
    foreignKeys = [
        ForeignKey(
            entity = ShoeItem::class,
            parentColumns = ["id"],
            childColumns = ["shoeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("shoeId")]
)
data class SizeStock(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val shoeId: Long,
    val sizeLabel: String, // e.g. "40", "41", "42" (or "28" for kids, "37" for ladies)
    val quantity: Int
)
