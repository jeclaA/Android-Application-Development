package org.umn.jesslyn0617week08_b.ui.states

import androidx.compose.material3.SearchBarState
import org.umn.jesslyn0617week08_b.data.models.Product
import java.time.temporal.TemporalQuery

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedProduct: Product? = null
)