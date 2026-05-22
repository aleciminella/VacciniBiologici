package it.uninsubria.vaccinibiologici

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import it.uninsubria.vaccinibiologici.ui.VacciniScreen
import it.uninsubria.vaccinibiologici.ui.theme.VacciniBiologiciTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VacciniBiologiciTheme {
                VacciniScreen()
            }
        }
    }
}
