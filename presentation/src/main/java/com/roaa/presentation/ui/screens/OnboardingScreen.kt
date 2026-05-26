package com.roaa.presentation.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.roaa.presentation.R
import kotlinx.coroutines.launch

private data class OnboardingPage(
    @DrawableRes val illustration: Int,
    val title: String,
    val subtitle: String
)

private val Pages = listOf(
    OnboardingPage(
        illustration = R.drawable.illustration_welcome_klef,
        title = "Welcome to Klef",
        subtitle = "Your personal, encrypted password vault. Safe, offline, and always in your pocket."
    ),
    OnboardingPage(
        illustration = R.drawable.illustration_bank_security,
        title = "Bank-Level Security",
        subtitle = "All passwords are encrypted locally on your device. Nothing ever leaves your phone."
    ),
    OnboardingPage(
        illustration = R.drawable.illustration_password_stats,
        title = "Know Your Weak Spots",
        subtitle = "Klef flags compromised, weak, and reused passwords so you always know what to fix."
    ),
    OnboardingPage(
        illustration = R.drawable.illustration_generate_password,
        title = "Never Invent a Password",
        subtitle = "Generate strong, unique passwords with one tap and save them instantly."
    )
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { Pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == Pages.lastIndex

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 8.dp),
//                horizontalArrangement = Arrangement.End
//            ) {
//                TextButton(onClick = onComplete) {
//                    Text(
//                        text = "Skip",
//                        style = MaterialTheme.typography.labelLarge,
//                        fontFamily = ManropeFont,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                PageContent(page = Pages[page])
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(Pages.size) { index ->
                        val selected = pagerState.currentPage == index
                        PageIndicator(selected = index == pagerState.currentPage)
//                        val width by animateDpAsState(
//                            targetValue = if (selected) 24.dp else 8.dp,
//                            animationSpec = tween(300),
//                            label = "DotWidth"
//                        )
//                        Box(
//                            modifier = Modifier
//                                .height(8.dp)
//                                .width(width)
//                                .clip(CircleShape)
//                                .background(
//                                    if (selected) MaterialTheme.colorScheme.primary
//                                    else MaterialTheme.colorScheme.outlineVariant
//                                )
//                        )
                    }
                }

                FilledTonalButton(
                    onClick = {
                        if (isLastPage) {
                            onComplete()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = if (isLastPage) "Get Started" else "Next",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun PageContent(page: OnboardingPage, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(page.illustration),
            contentDescription = null,
            modifier = Modifier.size(240.dp)
        )
        Spacer(Modifier.height(24.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PageIndicator(modifier: Modifier = Modifier, selected: Boolean) {
    val width by animateDpAsState(if (selected) 16.dp else 8.dp, animationSpec = tween(400))
    Box(
        Modifier
            .padding(horizontal = 2.dp)
            .width(width)
            .height(8.dp)
            .background(
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.5f
                ),
                RoundedCornerShape(50)
            )
    ) {}
}