package org.umn.jesslyn0617week08_b

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.umn.jesslyn0617week08_b.ui.screens.ProductScreen
import org.umn.jesslyn0617week08_b.ui.theme.Jesslyn0617Week08_bTheme
import org.umn.jesslyn0617week08_b.ui.viewmodels.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Jesslyn0617Week08_bTheme {
                val viewModel: ProductViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                Surface(modifier = Modifier) {
                    ProductScreen(
                        uiState = uiState,
                        onSearchChange = viewModel::searchProducts,
                        onProductClick = viewModel::selectProduct,
                        onBackToList = {
                            viewModel.selectProduct(productId = -1)
                        },
                        onRetry = viewModel::loadProducts
                    )

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Jesslyn0617Week08_bTheme {
        Greeting("Android")
    }
}