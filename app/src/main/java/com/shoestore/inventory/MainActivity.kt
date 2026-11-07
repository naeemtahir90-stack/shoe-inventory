package com.shoestore.inventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.shoestore.inventory.ui.navigation.InventoryNavGraph
import com.shoestore.inventory.ui.theme.ShoeStoreInventoryTheme
import com.shoestore.inventory.ui.viewmodel.InventoryViewModel
import com.shoestore.inventory.ui.viewmodel.InventoryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as ShoeInventoryApp
        val factory = InventoryViewModelFactory(app.repository)
        val viewModel = ViewModelProvider(this, factory)[InventoryViewModel::class.java]

        setContent {
            ShoeStoreInventoryTheme {
                InventoryNavGraph(viewModel = viewModel)
            }
        }
    }
}
