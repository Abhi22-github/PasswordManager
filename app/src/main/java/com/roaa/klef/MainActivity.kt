package com.roaa.klef

import android.content.Context
import android.os.Bundle
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.NavDisplay
import com.google.android.gms.common.util.CrashUtils
import com.roaa.klef.navigation.Destinations
import com.roaa.presentation.ui.actions.DashboardActions
import com.roaa.presentation.ui.components.toolbar.HorizontalToolbar
import com.roaa.presentation.ui.screens.*
import com.roaa.presentation.ui.theme.*
import com.roaa.presentation.utils.*
import com.roaa.presentation.utils.BottomAppBarState
import dagger.hilt.android.AndroidEntryPoint

private const val NAV_TRANSITION_DURATION_MS = 300

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PasswordManagerTheme {
                AuthenticatedContent(activity = this@MainActivity)
            }
        }
    }
}

@Composable
fun AuthenticatedContent(activity: FragmentActivity, modifier: Modifier = Modifier) {
    val prefs = remember { activity.getSharedPreferences("klef_prefs", Context.MODE_PRIVATE) }
    var hasSeenOnboarding by remember { mutableStateOf(prefs.getBoolean("has_seen_onboarding", false)) }

    var isAuthenticated by rememberSaveable { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }
    val biometricManager = remember { BiometricPromptManager(activity) }

    fun authenticate() {
        showPopup = false
        biometricManager.showPrompt { result ->
            when (result) {
                BiometricPromptManager.BiometricResult.Success -> {
                    isAuthenticated = true
                    showPopup = false
                    authError = null
                }
                BiometricPromptManager.BiometricResult.NotAvailable,
                BiometricPromptManager.BiometricResult.NotEnrolled -> {
                    isAuthenticated = true
                }
                is BiometricPromptManager.BiometricResult.Error -> {
                    showPopup = true
                    authError = result.message
                }
            }
        }
    }

    // Only trigger auth once onboarding is done and user isn't already authenticated
    LaunchedEffect(hasSeenOnboarding) {
        if (hasSeenOnboarding && !isAuthenticated) authenticate()
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (!activity.isChangingConfigurations) {
                        isAuthenticated = false
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (hasSeenOnboarding && !isAuthenticated) authenticate()
                }
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (!hasSeenOnboarding) {
            OnboardingScreen(
                onComplete = {
                    prefs.edit().putBoolean("has_seen_onboarding", true).apply()
                    hasSeenOnboarding = true
                }
            )
        } else {
            AnimatedContent(
                targetState = isAuthenticated,
                transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(300)) },
                label = "SplashToDashboard"
            ) { authenticated ->
                if (authenticated) {
                    PasswordManageContent()
                } else {
                    SplashScreen()
                }
            }

            // Popup floats over whatever is behind (splash on first open, dashboard on re-lock)
            BiometricAuthPopup(
                visible = !isAuthenticated && showPopup,
                onUnlockClick = ::authenticate,
                errorMessage = authError
            )
        }
    }
}

@Composable
fun PasswordManageContent(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Destinations.DashBoardScreen)

    val navigateBack: () -> Unit = {
        if (backStack.size > 1) backStack.removeLastOrNull()
    }


    val currentDestination by remember(backStack) { derivedStateOf { backStack.lastOrNull() } }

    val selectedAppBarScreen by remember(backStack) {
        derivedStateOf {
            when (currentDestination) {
                is Destinations.PasswordHealthScreen -> BottomAppBarState.HealthScreen
                is Destinations.PasswordGenerateScreen -> BottomAppBarState.PasswordGeneratorScreen
                else -> BottomAppBarState.DashboardScreen
            }
        }
    }

    // for hiding the toolbar.
    val shouldHideToolbar by remember(backStack) {
        derivedStateOf {
            currentDestination is Destinations.PasswordInfoScreen ||
                    currentDestination is Destinations.PasswordEditorScreen
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            AppNavDisplay(
                backStack = backStack,
                onBack = navigateBack
            )

            AnimatedVisibility(
                visible = !shouldHideToolbar,
                enter = fadeIn(tween(300)) + slideInVertically { it },
                exit = fadeOut(tween(300)) + slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomCenter),
            ) {
                HorizontalToolbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    selectedAppBarScreen = selectedAppBarScreen,
                    onSelectedAppBarScreenChange = {
                        backStack.syncBottomBarSelection(it)
                    },
                    onAddPasswordClick = {
                        backStack.add(Destinations.PasswordEditorScreen())
                    }
                )
            }
        }
    }
}

@Composable
fun AppNavDisplay(
    backStack: NavBackStack<NavKey>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val globalModifier = Modifier.padding(
        start = ScreenStartPadding,
        end = ScreenEndPadding,
        top = ScreenTopPadding,
        bottom = ScreenBottomPadding
    )

    val fadeSpec = remember {
        fadeIn(tween(NAV_TRANSITION_DURATION_MS)) togetherWith fadeOut(
            tween(
                NAV_TRANSITION_DURATION_MS
            )
        )
    }
    val copyPassword = rememberPasswordClipboard()

    NavDisplay(
        modifier = Modifier,
        backStack = backStack,
        onBack = { onBack() },
        transitionSpec = { fadeSpec },
        popTransitionSpec = { fadeSpec },
        predictivePopTransitionSpec = { fadeSpec },
        entryProvider = entryProvider {
            entry<Destinations.DashBoardScreen> {
                DashBoardScreen(
                    modifier = globalModifier,
                    onAction = { action ->
                        when (action) {
                            is DashboardActions.OnCardClicked -> {
                                backStack.add(Destinations.PasswordInfoScreen(action.passwordId))
                            }

                            is DashboardActions.OnCopyClicked -> {
                                copyPassword(action.password)
                            }

                            is DashboardActions.OnMoreClicked -> {
                                // TODO
                            }

                            DashboardActions.OnAddPasswordClick -> {
                                backStack.add(Destinations.PasswordEditorScreen())
                            }
                        }
                    }
                )
            }
            entry<Destinations.PasswordInfoScreen> { destination ->
                PasswordInfoScreen(
                    onBackClick = onBack,
                    passwordId = destination.passwordId,
                    onEditClick = { passwordId ->
                        backStack.add(Destinations.PasswordEditorScreen(editingId = passwordId))
                    },
                    modifier = globalModifier
                )
            }
            entry<Destinations.PasswordGenerateScreen> {
                PasswordGenerateScreen(
                    onNavigateToAddPassword = { password ->
                        backStack.add(Destinations.PasswordEditorScreen(prefilledPassword = password))
                    },
                    modifier = globalModifier
                )
            }
            entry<Destinations.PasswordHealthScreen> { destination ->
                PasswordHealthScreen(
                    onBackClick = onBack,
                    onEditPassword = { passwordId ->
                        backStack.add(Destinations.PasswordEditorScreen(editingId = passwordId))
                    },
                    modifier = globalModifier
                )
            }
            entry<Destinations.PasswordEditorScreen> { destination ->
                PasswordEditorScreen(
                    editingId = destination.editingId,
                    onBackClick = onBack,
                    prefilledPassword = destination.prefilledPassword,
                    modifier = globalModifier
                )
            }
        }
    )
}

private fun NavBackStack<NavKey>.syncBottomBarSelection(
    selectedAppBarScreen: BottomAppBarState
) {

    when (selectedAppBarScreen) {
        BottomAppBarState.DashboardScreen -> {
            // Remove everything until DashBoard is on top
            while (lastOrNull() != Destinations.DashBoardScreen) {
                removeLastOrNull()
            }
            if (isEmpty()) {
                add(Destinations.DashBoardScreen)
            }
        }

        BottomAppBarState.PasswordGeneratorScreen -> {
            // First go back to Dashboard root, then add PasswordGenerate
            while (size > 1) {
                removeLastOrNull()
            }
            if (lastOrNull() != Destinations.PasswordGenerateScreen) {
                add(Destinations.PasswordGenerateScreen)
            }
        }

        BottomAppBarState.HealthScreen -> {
            // First go back to Dashboard root, then add PasswordGenerate
            while (size > 1) {
                removeLastOrNull()
            }
            if (lastOrNull() != Destinations.PasswordHealthScreen) {
                add(Destinations.PasswordHealthScreen)
            }
        }
    }
}


