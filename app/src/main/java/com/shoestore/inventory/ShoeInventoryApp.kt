package com.shoestore.inventory

import android.app.Application
import com.shoestore.inventory.data.local.AppDatabase
import com.shoestore.inventory.data.repository.InventoryRepository

class ShoeInventoryApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { InventoryRepository(database.shoeDao(), database.saleDao()) }
}
