package com.imcys.bilibilias.core.videoplayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun VideoLoadingIndicator(
    showProgress: Boolean,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (showProgress) {
            CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 3.dp)
        }

        Row(Modifier.padding(top = 8.dp)) {
            val mergedStyle = LocalTextStyle.current.merge(textStyle)
            CompositionLocalProvider(
                LocalTextStyle provides mergedStyle,
                LocalContentColor provides MaterialTheme.colorScheme.onSurface,
            ) {
                text()
            }
        }
    }
}