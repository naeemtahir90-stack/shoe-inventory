package com.shoestore.inventory.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shoes")
data class ShoeItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val articleCode: String,          // e.g. "ART-208"
    val name: String,                 // e.g. "Urban Air Runner"
    val brand: String,                // e.g. "Nike / Local Artisan"
    val department: String,           // "MEN", "LADIES", "KIDS"
    val category: String,             // "Sneaker", "Formal", "Sandal", "Heels", "School"
    val color: String = "Black",       // e.g. "Black", "Brown", "White"
    val photoUri: String? = null,     // Local file URI for product photo
    val costPrice: Double,            // Original purchase price from wholesaler
    val retailPrice: Double,          // Selling price to customers
    val minSellingPrice: Double = costPrice, // Bottom bargaining price
    val shelfLocation: String = "",   // e.g. "Shelf B-3"
    val supplierName: String = "",    // Wholesaler name
    val supplierPhone: String = "",   // Wholesaler contact
    val isDisplayPair: Boolean = false, // 1 pair out on display rack
    val dateAdded: Long = System.currentTimeMillis()
) {
    val profitPerPair: Double get() = retailPrice - costPrice
    val marginPercentage: Double get() = if (retailPrice > 0) ((profitPerPair / retailPrice) * 100) else 0.0
}
