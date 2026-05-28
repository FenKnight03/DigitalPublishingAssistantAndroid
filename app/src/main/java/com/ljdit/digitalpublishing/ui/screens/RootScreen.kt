import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.ljdit.digitalpublishing.core.session.SessionManager
import com.ljdit.digitalpublishing.core.ui.FusionActionOverlay
import com.ljdit.digitalpublishing.ui.navigation.AppNavigation
import com.ljdit.digitalpublishing.ui.screens.LoginScreen

@Composable
fun RootScreen() {

    val isLoggedIn by SessionManager.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        Box(modifier = Modifier.fillMaxSize()) {
            AppNavigation()
            FusionActionOverlay()
        }
    } else {
        LoginScreen()
    }

}
