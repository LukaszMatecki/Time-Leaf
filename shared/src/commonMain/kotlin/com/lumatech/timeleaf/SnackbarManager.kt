package com.lumatech.timeleaf

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private var snackbarMessageState by mutableStateOf<String?>(null)

fun showSnackbar(message: String) {
    snackbarMessageState = message
}

@Composable
fun GlobalFloatingSnackbarHost(
    modifier: Modifier = Modifier
) {
    val message = snackbarMessageState

    LaunchedEffect(message) {
        if (message != null) {
            delay(2800)
            if (snackbarMessageState == message) {
                snackbarMessageState = null
            }
        }
    }

    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ) + fadeOut(),
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 12.dp, start = 20.dp, end = 20.dp)
    ) {
        if (message != null) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    shadowElevation = 6.dp,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = message,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}
