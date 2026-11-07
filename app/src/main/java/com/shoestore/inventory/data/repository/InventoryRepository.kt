package com.shoestore.inventory.data.repository

import com.shoestore.inventory.data.local.SaleDao
import com.shoestore.inventory.data.local.ShoeDao
import com.shoestore.inventory.data.local.ShoeWithSizes
import com.shoestore.inventory.data.model.SaleTransaction
import com.shoestore.inventory.data.model.ShoeItem
import com.shoestore.inventory.data.model.SizeStock
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class InventoryRepository(
    private val shoeDao: ShoeDao,
    private val saleDao: SaleDao
) {
    val allShoesWithSizes: Flow<List<ShoeWithSizes>> = shoeDao.getAllShoesWithSizes()
    val allSales: Flow<List<SaleTransaction>> = saleDao.getAllSales()

    fun getShoeDetails(shoeId: Long): Flow<ShoeWithSizes?> = shoeDao.getShoeWithSizesById(shoeId)

    suspend fun saveShoe(shoe: ShoeItem, sizes: List<Pair<String, Int>>): Long {
        val shoeId = if (shoe.id == 0L) {
            shoeDao.insertShoe(shoe)
        } else {
            shoeDao.updateShoe(shoe)
            shoeDao.deleteSizesForShoe(shoe.id)
            shoe.id
        }
        val sizeStockEntities = sizes.map { (sizeLabel, qty) ->
            SizeStock(shoeId = shoeId, sizeLabel = sizeLabel, quantity = qty)
        }
        shoeDao.insertSizes(sizeStockEntities)
        return shoeId
    }

    suspend fun deleteShoe(shoe: ShoeItem) {
        shoeDao.deleteShoe(shoe)
    }

    suspend fun recordSale(
        shoe: ShoeItem,
        sizeLabel: String,
        actualSoldPrice: Double
    ): Boolean {
        val rowsAffected = shoeDao.decrementStock(shoe.id, sizeLabel)
        if (rowsAffected > 0) {
            val transaction = SaleTransaction(
                shoeId = shoe.id,
                shoeName = shoe.name,
                articleCode = shoe.articleCode,
                department = shoe.department,
                sizeSold = sizeLabel,
                costPrice = shoe.costPrice,
                soldPrice = actualSoldPrice,
                profit = actualSoldPrice - shoe.costPrice
            )
            saleDao.insertSale(transaction)
            return true
        }
        return false
    }

    private fun getStartOfTodayMillis(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getTodaySales(): Flow<List<SaleTransaction>> = saleDao.getSalesToday(getStartOfTodayMillis())
    fun getTodayRevenue(): Flow<Double?> = saleDao.getTodayRevenue(getStartOfTodayMillis())
    fun getTodayProfit(): Flow<Double?> = saleDao.getTodayProfit(getStartOfTodayMillis())
    fun getTodayPairsSold(): Flow<Int?> = saleDao.getTodayPairsSoldCount(getStartOfTodayMillis())
}
