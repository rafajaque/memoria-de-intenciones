package cl.rafit.memoriadeintenciones

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.rafit.memoriadeintenciones.data.RoomLibraryRepository
import cl.rafit.memoriadeintenciones.data.catalog.FallbackBookCatalogRepository
import cl.rafit.memoriadeintenciones.data.catalog.GoogleBooksBookCatalogRepository
import cl.rafit.memoriadeintenciones.data.catalog.GoogleBooksService
import cl.rafit.memoriadeintenciones.data.catalog.OpenLibraryBookCatalogRepository
import cl.rafit.memoriadeintenciones.data.catalog.OpenLibraryService
import cl.rafit.memoriadeintenciones.data.catalog.IsbnDbBookCatalogRepository
import cl.rafit.memoriadeintenciones.data.catalog.IsbnDbService
import cl.rafit.memoriadeintenciones.data.local.MemoriaDatabase
import cl.rafit.memoriadeintenciones.data.shared.IncomingSharedImage
import cl.rafit.memoriadeintenciones.data.shared.OnDeviceIsbnReader
import cl.rafit.memoriadeintenciones.data.shared.OnDeviceOcrTextReader
import cl.rafit.memoriadeintenciones.data.shared.SharedImageImporter
import cl.rafit.memoriadeintenciones.ui.LibraryUiState
import cl.rafit.memoriadeintenciones.ui.LibraryViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

private val ComponentActivity.preferencesDataStore by preferencesDataStore(name = "preferences")

class MainActivity : ComponentActivity() {
    private val sharedImage = MutableStateFlow<IncomingSharedImage?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedImage.value = intent.toIncomingSharedImage()
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = true

        val database = MemoriaDatabase.getInstance(applicationContext)
        val libraryViewModel = ViewModelProvider(
            this,
            LibraryViewModelFactory(RoomLibraryRepository(database.libraryItemDao()))
        )[LibraryViewModel::class.java]

        setContent {
            val preferences = remember { ThemePreferences(preferencesDataStore) }
            val scope = rememberCoroutineScope()
            var selectedTheme by remember { mutableStateOf(AppTheme.MADERA) }
            val incomingSharedImage by sharedImage.collectAsState()
            LaunchedEffect(preferences) { selectedTheme = preferences.readTheme() }

            MemoriaTheme(theme = selectedTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(Modifier.safeDrawingPadding()) {
                        if (incomingSharedImage == null) {
                            MemoriaNavigation(
                                viewModel = libraryViewModel,
                                selectedTheme = selectedTheme,
                                onThemeSelected = { theme ->
                                    selectedTheme = theme
                                    scope.launch { preferences.saveTheme(theme) }
                                }
                            )
                        } else {
                            SharedCaptureScreen(
                                incoming = incomingSharedImage!!,
                                importer = remember { SharedImageImporter(applicationContext) },
                                ocrReader = remember { OnDeviceOcrTextReader(applicationContext) },
                                onSave = libraryViewModel::add,
                                onClose = { sharedImage.value = null },
                                onFindBook = libraryViewModel::findBookByIsbn,
                                isbnReader = remember { OnDeviceIsbnReader(applicationContext) }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        sharedImage.value = intent.toIncomingSharedImage()
    }
}

@Suppress("DEPRECATION")
private fun Intent.toIncomingSharedImage(): IncomingSharedImage? {
    if (action != Intent.ACTION_SEND) return null
    val stream = getParcelableExtra<Uri>(Intent.EXTRA_STREAM) ?: return null
    return IncomingSharedImage(uri = stream, declaredMimeType = type)
}

private class LibraryViewModelFactory(
    private val repository: RoomLibraryRepository,
    private val bookCatalogRepository: FallbackBookCatalogRepository = FallbackBookCatalogRepository(
        primary = GoogleBooksBookCatalogRepository(GoogleBooksService.create()),
        fallback = FallbackBookCatalogRepository(
            primary = OpenLibraryBookCatalogRepository(OpenLibraryService.create()),
            fallback = IsbnDbBookCatalogRepository(IsbnDbService.create(), BuildConfig.ISBNDB_API_KEY)
        )
    )
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(LibraryViewModel::class.java))
        return LibraryViewModel(repository, bookCatalogRepository) as T
    }
}

private object Destinations {
    const val LIBRARY = "library"
    const val SETTINGS = "settings"
    const val SHELF = "shelf/{categoryKey}"
    const val ADD_ITEM = "add/{categoryKey}"
    const val EDIT_ITEM = "edit/{itemId}"
    const val ISBN_SCAN = "isbn-scan"

    fun shelf(categoryKey: String) = "shelf/$categoryKey"
    fun addItem(categoryKey: String) = "add/$categoryKey"
    fun editItem(itemId: Long) = "edit/$itemId"
}

@Composable
private fun MemoriaNavigation(
    viewModel: LibraryViewModel,
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    val navController = rememberNavController()
    val library by viewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = Destinations.LIBRARY) {
        composable(Destinations.LIBRARY) {
            LibraryScreen(
                library = library,
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
            val key = backStackEntry.arguments?.getString("categoryKey").orEmpty()
            ShelfScreen(
                categoryKey = key,
                items = libraryCategoryForKey(key)?.let { category ->
                    library.itemsFor(category.storedCategory)
                }.orEmpty(),
                onAddItem = { navController.navigate(Destinations.addItem(key)) },
                onOpenItem = { item -> navController.navigate(Destinations.editItem(item.id)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Destinations.ADD_ITEM,
            arguments = listOf(navArgument("categoryKey") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = libraryCategoryForKey(backStackEntry.arguments?.getString("categoryKey").orEmpty())
            if (category != null) {
                AddItemScreen(
                    category = category,
                    existingItem = null,
                    onSave = viewModel::add,
                    onUpdate = viewModel::edit,
                    onDelete = viewModel::delete,
                    onFindBook = viewModel::findBookByIsbn,
                    scannedIsbn = navController.currentBackStackEntry?.savedStateHandle?.get<String>("scannedIsbn"),
                    onScanIsbn = if (category.storedCategory == cl.rafit.memoriadeintenciones.domain.LibraryCategory.BOOK) ({ navController.navigate(Destinations.ISBN_SCAN) }) else null,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
        composable(Destinations.ISBN_SCAN) {
            IsbnScannerScreen(
                onFound = { isbn ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("scannedIsbn", isbn)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Destinations.EDIT_ITEM,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId")
            val item = itemId?.let { id -> library.items.firstOrNull { it.id == id } }
            val category = item?.let { record ->
                libraryCategories.firstOrNull { it.storedCategory == record.category }
            }
            if (item != null && category != null) {
                AddItemScreen(
                    category = category,
                    existingItem = item,
                    onSave = viewModel::add,
                    onUpdate = viewModel::edit,
                    onDelete = viewModel::delete,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
