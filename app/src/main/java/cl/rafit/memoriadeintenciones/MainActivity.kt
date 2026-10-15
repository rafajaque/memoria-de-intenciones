package cl.rafit.memoriadeintenciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import kotlinx.coroutines.launch

private val ComponentActivity.preferencesDataStore by preferencesDataStore(name = "preferences")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = true
        setContent {
            val preferences = remember { ThemePreferences(preferencesDataStore) }
            val scope = rememberCoroutineScope()
            var selectedTheme by remember { mutableStateOf(AppTheme.MADERA) }
            LaunchedEffect(preferences) { selectedTheme = preferences.readTheme() }

            MemoriaTheme(theme = selectedTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(Modifier.safeDrawingPadding()) {
                        MemoriaNavigation(
                            selectedTheme = selectedTheme,
                            onThemeSelected = { theme ->
                                selectedTheme = theme
                                scope.launch { preferences.saveTheme(theme) }
                            }
                        )
                    }
                }
            }
        }
    }
}

private object Destinations {
    const val LIBRARY = "library"
    const val SETTINGS = "settings"
    const val SHELF = "shelf/{categoryKey}"

    fun shelf(categoryKey: String) = "shelf/$categoryKey"
}

@Composable
private fun MemoriaNavigation(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinations.LIBRARY) {
        composable(Destinations.LIBRARY) {
            LibraryScreen(
                onOpenSettings = { navController.navigate(Destinations.SETTINGS) },
                onOpenCategory = { category -> navController.navigate(Destinations.shelf(category.key)) }
            )
        }
        composable(Destinations.SETTINGS) {
            SettingsScreen(
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Destinations.SHELF,
            arguments = listOf(navArgument("categoryKey") { type = NavType.StringType })
        ) { backStackEntry ->
            ShelfScreen(
                categoryKey = backStackEntry.arguments?.getString("categoryKey").orEmpty(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
