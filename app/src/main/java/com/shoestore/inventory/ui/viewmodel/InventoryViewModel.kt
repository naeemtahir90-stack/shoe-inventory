package com.shoestore.inventory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoestore.inventory.data.local.ShoeWithSizes
import com.shoestore.inventory.data.model.Department
import com.shoestore.inventory.data.model.SaleTransaction
import com.shoestore.inventory.data.model.ShoeItem
import com.shoestore.inventory.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardMetrics(
    val totalPairsInStock: Int = 0,
    val totalInvestedCost: Double = 0.0,
    val expectedRevenue: Double = 0.0,
    val potentialProfit: Double = 0.0
)

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDepartment = MutableStateFlow(Department.ALL)
    val selectedDepartment: StateFlow<Department> = _selectedDepartment.asStateFlow()

    private val _isPrivacyPinActive = MutableStateFlow(false)
    val isPrivacyPinActive: StateFlow<Boolean> = _isPrivacyPinActive.asStateFlow()

    val allShoes: StateFlow<List<ShoeWithSizes>> = repository.allShoesWithSizes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredShoes: StateFlow<List<ShoeWithSizes>> = combine(
        allShoes,
        _searchQuery,
        _selectedDepartment
    ) { shoes, query, dept ->
        shoes.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.shoe.name.contains(query, ignoreCase = true) ||
                    item.shoe.articleCode.contains(query, ignoreCase = true) ||
                    item.shoe.brand.contains(query, ignoreCase = true) ||
                    item.shoe.shelfLocation.contains(query, ignoreCase = true)

            val matchesDept = when (dept) {
                Department.ALL -> true
                Department.MEN -> item.shoe.department == "MEN"
                Department.LADIES -> item.shoe.department == "LADIES"
                Department.KIDS -> item.shoe.department == "KIDS"
                Department.LOW_STOCK -> item.totalRemainingPairs in 1..3
            }
            matchesQuery && matchesDept
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboardMetrics: StateFlow<DashboardMetrics> = allShoes.map { shoes ->
        val totalPairs = shoes.sumOf { it.totalRemainingPairs }
        val totalCost = shoes.sumOf { it.totalInvestedValue }
        val totalRetail = shoes.sumOf { it.totalRetailValue }
        DashboardMetrics(
            totalPairsInStock = totalPairs,
            totalInvestedCost = totalCost,
            expectedRevenue = totalRetail,
            potentialProfit = totalRetail - totalCost
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    val todaySales: StateFlow<List<SaleTransaction>> = repository.getTodaySales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val todayRevenue: StateFlow<Double> = repository.getTodayRevenue().map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val todayProfit: StateFlow<Double> = repository.getTodayProfit().map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val todayPairsSold: StateFlow<Int> = repository.getTodayPairsSold().map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onDepartmentSelected(department: Department) {
        _selectedDepartment.value = department
    }

    fun togglePrivacyMode() {
        _isPrivacyPinActive.value = !_isPrivacyPinActive.value
    }

    fun saveShoe(shoe: ShoeItem, sizes: List<Pair<String, Int>>, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.saveShoe(shoe, sizes)
            onComplete(id)
        }
    }

    fun recordSale(shoe: ShoeItem, sizeLabel: String, priceSold: Double, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.recordSale(shoe, sizeLabel, priceSold)
            onResult(success)
        }
    }

    fun deleteShoe(shoe: ShoeItem) {
        viewModelScope.launch {
            repository.deleteShoe(shoe)
        }
    }
}
