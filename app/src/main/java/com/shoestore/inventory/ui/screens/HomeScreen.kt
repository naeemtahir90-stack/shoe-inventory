package com.shoestore.inventory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.shoestore.inventory.data.local.ShoeWithSizes
import com.shoestore.inventory.data.model.Department
import com.shoestore.inventory.ui.theme.*
import com.shoestore.inventory.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: InventoryViewModel,
    onNavigateToAddShoe: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToDailyReport: () -> Unit
) {
    val filteredShoes by viewModel.filteredShoes.collectAsState()
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDept by viewModel.selectedDepartment.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyPinActive.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "SoleStock",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Inventory",
                            fontSize = 14.sp,
                            color = Emerald500,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.togglePrivacyMode() }) {
                        Icon(
                            if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Cost Privacy",
                            tint = if (isPrivacyMode) AccentAmber else TextSecondary
                        )
                    }
                    IconButton(onClick = onNavigateToDailyReport) {
                        Icon(Icons.Default.Assessment, contentDescription = "Daily Report", tint = Emerald500)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddShoe,
                containerColor = Emerald500,
                contentColor = DarkBackground
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Shoe")
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Dashboard Metric Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald700)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Stock", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text(
                                "${metrics.totalPairsInStock} pairs",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Cost Invested", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text(
                                if (isPrivacyMode) "••••••" else "$${String.format("%.0f", metrics.totalInvestedCost)}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Expected Retail: $${String.format("%.0f", metrics.expectedRevenue)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            if (isPrivacyMode) "Profit: Hidden" else "Est. Profit: +$${String.format("%.0f", metrics.potentialProfit)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Search by Article, Name, Brand, Shelf...", color = TextSecondary, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedBorderColor = Emerald500,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            // Department Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Department.values()) { dept ->
                    FilterChip(
                        selected = (selectedDept == dept),
                        onClick = { viewModel.onDepartmentSelected(dept) },
                        label = { Text("${dept.icon} ${dept.displayName}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500,
                            selectedLabelColor = DarkBackground,
                            containerColor = DarkSurface,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedDept == dept) Emerald500 else CardBorder,
                            enabled = true,
                            selected = (selectedDept == dept)
                        )
                    )
                }
            }

            // Shoes Inventory List
            if (filteredShoes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No shoes found in this section", color = TextSecondary, fontSize = 16.sp)
                        Text("Tap + to add your first shoe stock", color = Emerald500, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredShoes, key = { it.shoe.id }) { item ->
                        ShoeItemCard(
                            shoeWithSizes = item,
                            isPrivacyMode = isPrivacyMode,
                            onClick = { onNavigateToDetail(item.shoe.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShoeItemCard(
    shoeWithSizes: ShoeWithSizes,
    isPrivacyMode: Boolean,
    onClick: () -> Unit
) {
    val shoe = shoeWithSizes.shoe
    val totalStock = shoeWithSizes.totalRemainingPairs

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shoe Photo Thumbnail
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant),
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
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Shoe Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        shoe.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        "${shoe.department} • ${shoe.category}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        shoe.articleCode,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Emerald500
                    )
                    if (shoe.shelfLocation.isNotBlank()) {
                        Text(
                            " • Loc: ${shoe.shelfLocation}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Price and Profit Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row {
                            Text(
                                "Cost: ${if (isPrivacyMode) "••" else "$${String.format("%.0f", shoe.costPrice)}"} ",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                "Retail: $${String.format("%.0f", shoe.retailPrice)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        if (!isPrivacyMode) {
                            Text(
                                "Profit: +$${String.format("%.0f", shoe.profitPerPair)} (${String.format("%.0f", shoe.marginPercentage)}%)",
                                fontSize = 11.sp,
                                color = Emerald500,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Stock Badge
                    val badgeColor = when {
                        totalStock == 0 -> AccentRed
                        totalStock <= 3 -> AccentAmber
                        else -> Emerald500
                    }
                    val badgeText = when {
                        totalStock == 0 -> "Out of Stock"
                        else -> "$totalStock in stock"
                    }

                    Surface(
                        color = badgeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor)
                    ) {
                        Text(
                            badgeText,
                            color = badgeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Available Sizes Pill Preview
                val availableSizes = shoeWithSizes.sizes.filter { it.quantity > 0 }.map { it.sizeLabel }
                if (availableSizes.isNotEmpty()) {
                    Text(
                        "Sizes: ${availableSizes.joinToString(", ")}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
