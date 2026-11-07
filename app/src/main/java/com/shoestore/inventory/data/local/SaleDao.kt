package com.shoestore.inventory.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.shoestore.inventory.data.model.SaleTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Insert
    suspend fun insertSale(sale: SaleTransaction): Long

    @Query("SELECT * FROM sales ORDER BY dateSold DESC")
    fun getAllSales(): Flow<List<SaleTransaction>>

    @Query("SELECT * FROM sales WHERE dateSold >= :startOfDayTimestamp ORDER BY dateSold DESC")
    fun getSalesToday(startOfDayTimestamp: Long): Flow<List<SaleTransaction>>

    @Query("SELECT SUM(soldPrice) FROM sales WHERE dateSold >= :startOfDayTimestamp")
    fun getTodayRevenue(startOfDayTimestamp: Long): Flow<Double?>

    @Query("SELECT SUM(profit) FROM sales WHERE dateSold >= :startOfDayTimestamp")
    fun getTodayProfit(startOfDayTimestamp: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM sales WHERE dateSold >= :startOfDayTimestamp")
    fun getTodayPairsSoldCount(startOfDayTimestamp: Long): Flow<Int?>
}
