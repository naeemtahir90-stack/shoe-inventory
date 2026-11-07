package com.shoestore.inventory.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.shoestore.inventory.data.model.Department
import com.shoestore.inventory.data.model.ShoeItem
import com.shoestore.inventory.ui.theme.*
import com.shoestore.inventory.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditShoeScreen(
    shoeIdToEdit: Long?,
    viewModel: InventoryViewModel,
    onNavigateBack: () -> Unit
) {
    val allShoes by viewModel.allShoes.collectAsState()
    val existingItem = remember(shoeIdToEdit, allShoes) {
        if (shoeIdToEdit != null && shoeIdToEdit > 0) {
            allShoes.find { it.shoe.id == shoeIdToEdit }
        } else null
    }

    var articleCode by remember { mutableStateOf(existingItem?.shoe?.articleCode ?: "") }
    var shoeName by remember { mutableStateOf(existingItem?.shoe?.name ?: "") }
    var brand by remember { mutableStateOf(existingItem?.shoe?.brand ?: "") }
    var selectedDept by remember { mutableStateOf(existingItem?.shoe?.department ?: "MEN") }
    var category by remember { mutableStateOf(existingItem?.shoe?.category ?: "Sneaker") }
    var colorName by remember { mutableStateOf(existingItem?.shoe?.color ?: "Black") }
    var photoUriString by remember { mutableStateOf(existingItem?.shoe?.photoUri) }
    var costPriceText by remember { mutableStateOf(existingItem?.shoe?.costPrice?.toString() ?: "") }
    var retailPriceText by remember { mutableStateOf(existingItem?.shoe?.retailPrice?.toString() ?: "") }
    var minPriceText by remember { mutableStateOf(existingItem?.shoe?.minSellingPrice?.toString() ?: "") }
    var shelfLocation by remember { mutableStateOf(existingItem?.shoe?.shelfLocation ?: "") }
    var supplierName by remember { mutableStateOf(existingItem?.shoe?.supplierName ?: "") }
    var supplierPhone by remember { mutableStateOf(existingItem?.shoe?.supplierPhone ?: "") }

    // Size run map: Size -> Quantity
    val sizeQuantities = remember {
        mutableStateMapOf<String, Int>().apply {
            if (existingItem != null) {
                existingItem.sizes.forEach { put(it.sizeLabel, it.quantity) }
            } else {
                // Default Men's sizes 39 to 45
                listOf("39", "40", "41", "42", "43", "44", "45").forEach { put(it, 2) }
            }
        }
    }

    fun applyDepartmentPreset(dept: String) {
        selectedDept = dept
        sizeQuantities.clear()
        when (dept) {
            "MEN" -> listOf("39", "40", "41", "42", "43", "44", "45").forEach { sizeQuantities[it] = 2 }
            "LADIES" -> listOf("35", "36", "37", "38", "39", "40", "41").forEach { sizeQuantities[it] = 2 }
            "KIDS" -> listOf("24", "26", "28", "30", "32", "34").forEach { sizeQuantities[it] = 3 }
        }
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUriString = uri.toString()
        }
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingItem == null) "Add New Shoe Stock" else "Edit Shoe Stock", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
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
            // Photo Picker Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
                    .clickable { photoPickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (!photoUriString.isNullOrBlank()) {
                    AsyncImage(
                        model = photoUriString,
                        contentDescription = "Product Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = Emerald500, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap to add 1 Product Photo", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Department Selection Buttons (Men, Ladies, Kids)
            Text("Department (Auto-loads Size Presets)", fontSize = 13.sp, color = TextSecondary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("MEN" to "👔 Men", "LADIES" to "👠 Ladies", "KIDS" to "👶 Kids").forEach { (deptKey, label) ->
                    val isSelected = selectedDept == deptKey
                    Button(
                        onClick = { applyDepartmentPreset(deptKey) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Emerald500 else DarkSurface,
                            contentColor = if (isSelected) DarkBackground else TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Article Code & Shoe Name
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = articleCode,
                    onValueChange = { articleCode = it },
                    label = { Text("Article Code * (e.g. ART-104)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald500)
                )
                OutlinedTextField(
                    value = shoeName,
                    onValueChange = { shoeName = it },
                    label = { Text("Shoe Name *") },
                    modifier = Modifier.weight(1.5f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald500)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Brand & Category
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand / Vendor") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald500)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (Sneaker/Heel/etc)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald500)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cost Price, Retail Price, Minimum Price
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = costPriceText,
                    onValueChange = { costPriceText = it },
                    label = { Text("Cost Price *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald500)
                )
                OutlinedTextField(
                    value = retailPriceText,
                    onValueChange = { retailPriceText = it },
                    label = { Text("Retail Price *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald500)
                )
                OutlinedTextField(
                    value = minPriceText,
                    onValueChange = { minPriceText = it },
                    label = { Text("Min. Price") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald500)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Shelf Location & Supplier Info
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = shelfLocation,
                    onValueChange = { shelfLocation = it },
                    label = { Text("Shelf / Rack Loc.") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald500)
                )
                OutlinedTextField(
                    value = supplierName,
                    onValueChange = { supplierName = it },
                    label = { Text("Supplier Name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald500)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Size Run Matrix Builder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Size Quantities (Pairs)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                TextButton(onClick = {
                    // Quick add carton (+1 to all sizes)
                    sizeQuantities.keys.forEach { sizeQuantities[it] = (sizeQuantities[it] ?: 0) + 1 }
                }) {
                    Text("+1 to All Sizes", color = Emerald500, fontSize = 12.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sizeQuantities.keys.sorted().chunked(4).forEach { rowSizes ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowSizes.forEach { sizeLabel ->
                            val qty = sizeQuantities[sizeLabel] ?: 0
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Size $sizeLabel", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "-",
                                            modifier = Modifier
                                                .clickable { if (qty > 0) sizeQuantities[sizeLabel] = qty - 1 }
                                                .padding(horizontal = 4.dp),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentRed
                                        )
                                        Text(
                                            "$qty",
                                            modifier = Modifier.padding(horizontal = 6.dp),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Emerald500
                                        )
                                        Text(
                                            "+",
                                            modifier = Modifier
                                                .clickable { sizeQuantities[sizeLabel] = qty + 1 }
                                                .padding(horizontal = 4.dp),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Emerald500
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = AccentRed, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (articleCode.isBlank() || shoeName.isBlank()) {
                        errorMessage = "Please enter an Article Code and Shoe Name."
                        return@Button
                    }
                    val cost = costPriceText.toDoubleOrNull()
                    val retail = retailPriceText.toDoubleOrNull()
                    if (cost == null || retail == null) {
                        errorMessage = "Please enter valid Cost and Retail prices."
                        return@Button
                    }
                    val minPrice = minPriceText.toDoubleOrNull() ?: cost

                    val shoeToSave = ShoeItem(
                        id = existingItem?.shoe?.id ?: 0L,
                        articleCode = articleCode.trim(),
                        name = shoeName.trim(),
                        brand = brand.trim(),
                        department = selectedDept,
                        category = category.trim(),
                        color = colorName.trim(),
                        photoUri = photoUriString,
                        costPrice = cost,
                        retailPrice = retail,
                        minSellingPrice = minPrice,
                        shelfLocation = shelfLocation.trim(),
                        supplierName = supplierName.trim(),
                        supplierPhone = supplierPhone.trim()
                    )

                    val sizesList = sizeQuantities.map { (size, qty) -> Pair(size, qty) }
                    viewModel.saveShoe(shoeToSave, sizesList) {
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = DarkBackground),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (existingItem == null) "Save Shoe Stock" else "Update Shoe Stock", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
