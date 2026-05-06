import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ljdit.digitalpublishing.core.session.SessionManager
import com.ljdit.digitalpublishing.ui.navigation.AppNavigation
import com.ljdit.digitalpublishing.ui.screens.LoginScreen

@Composable
fun RootScreen() {

    val isLoggedIn by SessionManager.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        AppNavigation()
    } else {
        LoginScreen()
    }

}