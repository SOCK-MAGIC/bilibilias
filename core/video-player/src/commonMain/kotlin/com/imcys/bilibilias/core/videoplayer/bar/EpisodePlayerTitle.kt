package com.imcys.bilibilias.core.videoplayer.bar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.imcys.bilibilias.core.ui.foundation.TextWithBorder

@Composable
fun EpisodePlayerTitle(
    episodeTitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier, horizontalAlignment = Alignment.Start) {
        ProvideTextStyle(MaterialTheme.typography.titleMedium) {
            Row {
                TextWithBorder(
                    episodeTitle,
                    softWrap = false,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}