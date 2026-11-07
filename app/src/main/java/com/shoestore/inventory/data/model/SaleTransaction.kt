package com.shoestore.inventory.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val shoeId: Long,
    val shoeName: String,
    val articleCode: String,
    val department: String,
    val sizeSold: String,
    val costPrice: Double,
    val soldPrice: Double,
    val profit: Double = soldPrice - costPrice,
    val dateSold: Long = System.currentTimeMillis()
)
