package com.shoestore.inventory.data.local

import androidx.room.*
import com.shoestore.inventory.data.model.ShoeItem
import com.shoestore.inventory.data.model.SizeStock
import kotlinx.coroutines.flow.Flow

data class ShoeWithSizes(
    @Embedded val shoe: ShoeItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "shoeId"
    )
    val sizes: List<SizeStock>
) {
    val totalRemainingPairs: Int get() = sizes.sumOf { it.quantity }
    val totalInvestedValue: Double get() = totalRemainingPairs * shoe.costPrice
    val totalRetailValue: Double get() = totalRemainingPairs * shoe.retailPrice
}

@Dao
interface ShoeDao {
    @Transaction
    @Query("SELECT * FROM shoes ORDER BY dateAdded DESC")
    fun getAllShoesWithSizes(): Flow<List<ShoeWithSizes>>

    @Transaction
    @Query("SELECT * FROM shoes WHERE id = :shoeId")
    fun getShoeWithSizesById(shoeId: Long): Flow<ShoeWithSizes?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoe(shoe: ShoeItem): Long

    @Update
    suspend fun updateShoe(shoe: ShoeItem)

    @Delete
    suspend fun deleteShoe(shoe: ShoeItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSizes(sizes: List<SizeStock>)

    @Query("DELETE FROM size_stocks WHERE shoeId = :shoeId")
    suspend fun deleteSizesForShoe(shoeId: Long)

    @Query("UPDATE size_stocks SET quantity = quantity - 1 WHERE shoeId = :shoeId AND sizeLabel = :sizeLabel AND quantity > 0")
    suspend fun decrementStock(shoeId: Long, sizeLabel: String): Int

    @Query("UPDATE size_stocks SET quantity = :newQuantity WHERE id = :sizeStockId")
    suspend fun updateSizeStockQuantity(sizeStockId: Long, newQuantity: Int)
}
