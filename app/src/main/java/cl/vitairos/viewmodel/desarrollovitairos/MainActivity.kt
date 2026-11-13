
package cl.vitairos.viewmodel.desarrollovitairos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.vitairos.navigation.AppNavigation
import cl.vitairos.viewmodel.desarrollovitairos.ui.theme.DesarrolloDuocTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DesarrolloDuocTheme {
                AppNavigation()
            }
        }
    }
}
