package org.umn.jesslyn0617week07_a

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.umn.jesslyn0617week07_a.di.AppContainer
import org.umn.jesslyn0617week07_a.ui.screen.NoteScreen
import org.umn.jesslyn0617week07_a.ui.theme.Jesslyn0617Week07_aTheme
import org.umn.jesslyn0617week07_a.viewmodel.NoteViewModel
import org.umn.jesslyn0617week07_a.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appContainer = AppContainer(context = this)
        val factory = ViewModelFactory(appContainer.noteRepository)

        setContent {
            Jesslyn0617Week07_aTheme {
                Surface {
                    val viewModel: NoteViewModel = viewModel(factory = factory)
                    NoteScreen(viewModel = viewModel)
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
    Jesslyn0617Week07_aTheme {
        Greeting("Android")
    }
}