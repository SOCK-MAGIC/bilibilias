package com.imcys.bilibilias.ui.cache

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.imcys.bilibilias.core.datastore.model.EpisodeMetadata
import com.imcys.bilibilias.core.datastore.model.MediaCacheMetadata
import com.imcys.bilibilias.core.domain.model.CacheEpisodeState
import com.imcys.bilibilias.core.model.DataUnit
import com.imcys.bilibilias.core.model.FileStats
import com.imcys.bilibilias.logic.cache.CacheViewModel
import com.imcys.bilibilias.ui.theme.AsTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@Composable
fun CacheScreen(
    cacheViewModel: CacheViewModel = koinViewModel()
) {
    val state by cacheViewModel.stateFlow.collectAsState()

//    val canMux by cacheViewModel.canProcess.collectAsStateWithLifecycle()

    CaCheContent(
        state,
        onDelete = cacheViewModel::deleteEpisodeCache,
        canMux = true,
        onCombine = cacheViewModel::onCombine
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaCheContent(
    cacheEpisodeState: List<CacheEpisodeState>,
    canMux: Boolean,
    onDelete: (CacheEpisodeState) -> Unit = { },
    onCombine: (CacheEpisodeState) -> Unit = { },
) {
    Scaffold { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(cacheEpisodeState, key = { it.episodeMetadata.cid }) { item ->
                val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        when (it) {
                            SwipeToDismissBoxValue.StartToEnd -> {
                                onCombine(item)
                                false
                            }

                            SwipeToDismissBoxValue.EndToStart -> {
                                onDelete(item)
                                true
                            }

                            SwipeToDismissBoxValue.Settled -> true
                        }
                    },
                    positionalThreshold = { totalDistance -> totalDistance * 0.3f }
                )
                SwipeToDismissBox(
                    state = swipeToDismissBoxState,
                    backgroundContent = {
                        swipeToDismissBoxState.SwipeDismissBackground()
                    },
                    enableDismissFromStartToEnd = canMux,
                    modifier = Modifier.animateItem(),
                ) {
                    CacheEpisodeItem(item)
                    if (cacheEpisodeState.last() != item) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SwipeToDismissBoxState.SwipeDismissBackground() {
    // Cross-fade the background color as the drag gesture progresses.
    val color by animateColorAsState(
        when (targetValue) {
            SwipeToDismissBoxValue.Settled -> Color.LightGray
            SwipeToDismissBoxValue.StartToEnd ->
                lerp(
                    Color.LightGray,
                    Color.Blue,
                    progress
                )

            SwipeToDismissBoxValue.EndToStart ->
                lerp(
                    Color.LightGray,
                    Color.Red,
                    progress
                )
        },
        label = "swipeable card item background color"
    )
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.0.dp))
            .background(color)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        when (dismissDirection) {
            SwipeToDismissBoxValue.EndToStart -> {
                Spacer(modifier = Modifier)
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove item",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(12.dp)
                )
            }

            SwipeToDismissBoxValue.StartToEnd -> {
                Icon(
                    Icons.Default.Merge,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp),
                    tint = Color.White
                )
            }

            SwipeToDismissBoxValue.Settled -> {
            }
        }
    }
}

@Composable
private fun CacheEpisodeItem(state: CacheEpisodeState) {
    OutlinedCard(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.episodeMetadata.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.fileStats.downloadedBytes.toString(DataUnit.MEGABYTES),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AnimatedVisibility(!state.fileStats.isDownloadFinished) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Row {
                Box {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("测试") },
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CacheEpisodeItemPreview() {
    AsTheme {
        CacheEpisodeItem(
            state = CacheEpisodeState(
                episodeMetadata = EpisodeMetadata(
                    bvid = "BV1fx411y7R2",
                    cid = 123456L,
                    title = "Sample Episode Title - A very long title to check how text overflow behaves in the UI design"
                ),
                mediaCacheMetadata = MediaCacheMetadata(
                    metadata = emptyList(),
                    createdAt = Clock.System.now(),
                    extra = emptyMap()
                ),
                fileStats = FileStats.Unspecified,
                canPlay = true,
                canMux = true
            )
        )
    }
}
