package com.shoestore.inventory.data.model

enum class Department(val displayName: String, val icon: String) {
    ALL("All", "👟"),
    MEN("Men", "👔"),
    LADIES("Ladies", "👠"),
    KIDS("Kids", "👶"),
    LOW_STOCK("Low Stock", "⚠️")
}
