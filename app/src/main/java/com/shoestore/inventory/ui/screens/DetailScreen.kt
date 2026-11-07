package com.shoestore.inventory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shoestore.inventory.data.model.SizeStock
import com.shoestore.inventory.ui.theme.*
import com.shoestore.inventory.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    shoeId: Long,
    viewModel: InventoryViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    val allShoes by viewModel.allShoes.collectAsState()
    val shoeWithSizes = allShoes.find { it.shoe.id == shoeId }
    val isPrivacyMode by viewModel.isPrivacyPinActive.collectAsState()

    var showSaleDialog by remember { mutableStateOf(false) }
    var selectedSizeForSale by remember { mutableStateOf<SizeStock?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (shoeWithSizes == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Shoe record not found", color = TextSecondary)
        }
        return
    }

    val shoe = shoeWithSizes.shoe

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(shoe.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(shoe.id) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Shoe", tint = Emerald500)
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Shoe", tint = AccentRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Big Shoe Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                if (!shoe.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = shoe.photoUri,
                        contentDescription = shoe.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Article & Department Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(shoe.articleCode, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Emerald500)
                    Text("${shoe.brand} • ${shoe.color}", fontSize = 14.sp, color = TextSecondary)
                }
                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${shoe.department} | ${shoe.category}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pricing & Margins Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Original Cost", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            if (isPrivacyMode) "••••••" else "$${shoe.costPrice}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Column {
                        Text("Retail Price", fontSize = 12.sp, color = TextSecondary)
                        Text("$${shoe.retailPrice}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Emerald500)
                    }
                    Column {
                        Text("Profit / Pair", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            if (isPrivacyMode) "••••" else "+$${shoe.profitPerPair}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald500
                        )
                    }
                }
            }

            // Shelf Location & Supplier Info Card
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Storage Location:", fontSize = 13.sp, color = TextSecondary)
                        Text(
                            if (shoe.shelfLocation.isNotBlank()) shoe.shelfLocation else "Not set",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                    if (shoe.supplierName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Supplier:", fontSize = 13.sp, color = TextSecondary)
                            Text(
                                "${shoe.supplierName} (${shoe.supplierPhone})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                    if (shoe.isDisplayPair) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("📌 1 Pair currently out on display rack", color = AccentAmber, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Size Run Grid
            Text("Size Run & Stock Breakdown", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Tap any in-stock size to record a sale (-1 pair)", fontSize = 12.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.heightIn(max = 280.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(shoeWithSizes.sizes) { sizeStock ->
                    val isAvailable = sizeStock.quantity > 0
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isAvailable) DarkSurface else DarkSurface.copy(alpha = 0.5f))
                            .border(
                                1.dp,
                                if (isAvailable) Emerald500.copy(alpha = 0.6f) else CardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable(enabled = isAvailable) {
                                selectedSizeForSale = sizeStock
                                showSaleDialog = true
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Size ${sizeStock.sizeLabel}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAvailable) TextPrimary else TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (isAvailable) "${sizeStock.quantity} pairs" else "Out of stock",
                                fontSize = 11.sp,
                                color = if (isAvailable) Emerald500 else AccentRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    // Quick Record Sale Dialog
    if (showSaleDialog && selectedSizeForSale != null) {
        var soldPriceText by remember { mutableStateOf(shoe.retailPrice.toString()) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showSaleDialog = false },
            title = { Text("Record Sale: Size ${selectedSizeForSale!!.sizeLabel}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Shoe: ${shoe.name} (${shoe.articleCode})", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = soldPriceText,
                        onValueChange = {
                            soldPriceText = it
                            errorMessage = null
                        },
                        label = { Text("Actual Sold Price ($)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald500)
                    )
                    val enteredPrice = soldPriceText.toDoubleOrNull() ?: 0.0
                    if (enteredPrice < shoe.minSellingPrice) {
                        Text(
                            "⚠️ Warning: Below min price ($${shoe.minSellingPrice}). Profit loss!",
                            color = AccentAmber,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (errorMessage != null) {
                        Text(errorMessage!!, color = AccentRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalPrice = soldPriceText.toDoubleOrNull()
                        if (finalPrice == null || finalPrice <= 0) {
                            errorMessage = "Please enter a valid price"
                            return@Button
                        }
                        viewModel.recordSale(shoe, selectedSizeForSale!!.sizeLabel, finalPrice) { success ->
                            if (success) {
                                showSaleDialog = false
                            } else {
                                errorMessage = "Failed to update stock"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = DarkBackground)
                ) {
                    Text("Confirm Sale (-1 Pair)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaleDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete ${shoe.name}?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove this shoe and its stock records.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteShoe(shoe)
                        showDeleteConfirmDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}
