package org.umn.jesslyn0617week08_b.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.umn.jesslyn0617week08_b.data.repository.ProductRepository
import org.umn.jesslyn0617week08_b.ui.states.ProductUiState

class ProductViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()
    init {
        loadProducts()
    }
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            try {
                val products = repository.getProducts()
                _uiState.update {
                    it.copy(
                    products = products,
                    isLoading = false,
                    selectedProduct = null,
                    errorMessage = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: ClientRequestException) {
                val statusCode = e.response.status.value

                val message = when (statusCode) {
                    400 -> "Request denied (400). Please check your API key and access permissions."
                    404 -> "Request denied (404). Please check your API key and access permissions."
                    else -> "HTTP Error: $statusCode"
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }
            } catch (e: ServerResponseException) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage =
                    "A server error occurred (${e.response.status.value}).")
                }
            }
            catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage =
                    "Failed to fetch data. Please check your connection and configuration.")
                }
            }
        }
    }
    fun searchProducts(query: String) {
        _uiState.update{
            it.copy(searchQuery = query)
        }
    }
    fun selectProduct(productId: Int){
        _uiState.update { state -> 
            state.copy(
                selectedProduct = state.products.find {
                    it.productId == productId
                }
            )
        }
    }
}