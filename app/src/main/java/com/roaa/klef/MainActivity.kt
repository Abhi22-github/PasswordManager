package com.roaa.klef

import android.os.Bundle
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.NavDisplay
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
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PasswordManagerTheme {
                PasswordManageContent()

            }
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


