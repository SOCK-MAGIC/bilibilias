package com.imcys.bilibilias.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.imcys.bilibilias.core.domain.model.EpisodeCacheRequest
import com.imcys.bilibilias.core.domain.model.EpisodeCacheState
import com.imcys.bilibilias.core.domain.model.SelectedEpisodeContext
import com.imcys.bilibilias.core.domain.model.TrackInfo
import com.imcys.bilibilias.logic.search.MediaSourceSelectedUiState
import com.imcys.bilibilias.logic.search.SearchResultUiState
import com.imcys.bilibilias.logic.search.SearchViewModel
import com.imcys.bilibilias.logic.search.SelfInfoUiState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    navigationToLogin: () -> Unit,
    navigationToPlayer: () -> Unit,
    navigationToSettings: () -> Unit,
    searchViewModel: SearchViewModel = koinViewModel(),
) {
    val searchQuery by searchViewModel.searchQuery.collectAsState()
    val searchResultUiState by searchViewModel.searchResultUiState.collectAsState()
    val selfInfoUiState by searchViewModel.selfInfoUiState.collectAsState()
    val mediaSourceSelectedUiState by searchViewModel.mediaSourceSelectedUiState.collectAsState()
    SearchContent(
        searchQuery = searchQuery,
        searchResultUiState = searchResultUiState,
        selfInfoUiState = selfInfoUiState,
        onSearchQueryChanged = searchViewModel::onSearchQueryChanged,
        onLogout = searchViewModel::onLogout,
        onCacheRequest = searchViewModel::requestCache,
        mediaSourceSelectedUiState = mediaSourceSelectedUiState,
        onEpisodeSelected = searchViewModel::setSelectedEpisode,
        navigationToLogin = navigationToLogin,
        navigationToPlayer = navigationToPlayer,
        navigationToSettings = navigationToSettings,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchContent(
    searchQuery: String,
    searchResultUiState: SearchResultUiState,
    selfInfoUiState: SelfInfoUiState,
    mediaSourceSelectedUiState: MediaSourceSelectedUiState,
    onSearchQueryChanged: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onCacheRequest: (EpisodeCacheRequest) -> Unit = { },
    navigationToLogin: () -> Unit = {},
    navigationToPlayer: () -> Unit = {},
    navigationToSettings: () -> Unit = {},
    onEpisodeSelected: (SelectedEpisodeContext) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(navigationToSettings) {
                        Icon(Icons.Rounded.Settings, "Settings")
                    }
                    SelfAvatar(
                        selfInfoUiState,
                        modifier = Modifier,
                        onLoginClick = navigationToLogin,
                        onLogoutConfirmed = onLogout
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SearchTextField(
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged
            )
            when (searchResultUiState) {
                SearchResultUiState.EmptyQuery -> {}

                SearchResultUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "加载中...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                is SearchResultUiState.LoadFailed -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ErrorOutline,
                                contentDescription = "错误",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "糟糕，出错了！",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = searchResultUiState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    onSearchQueryChanged("")
                                }
                            ) {
                                Text("清空搜索框")
                            }
                        }
                    }
                }

                is SearchResultUiState.Success -> {
                    val keyboardController = LocalSoftwareKeyboardController.current

                    var currentEpisodeIndex by remember { mutableIntStateOf(0) }

                    LaunchedEffect(searchResultUiState) {
                        keyboardController?.hide()
                    }

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        val onEpisodeCacheSelection: (episode: EpisodeCacheState, index: Int) -> Unit =
                            { episodeCacheState, index ->
                                val request = SelectedEpisodeContext(
                                    episodeCacheState,
                                    searchResultUiState.episodeInfo.videoType
                                )
                                currentEpisodeIndex = index
                                onEpisodeSelected(request)
                            }
                        Text(
                            "分集(${searchResultUiState.episodes.size})",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        val mediaSelector = remember { EpisodeMediaSelector(onCacheRequest) }
                        EpisodeList(searchResultUiState.episodes) { state, i ->
                            onEpisodeCacheSelection(state, i)
                            mediaSelector.openDialog(i)
                        }
                        MediaSelectionDialog(
                            mediaSelector,
                            mediaSourceSelectedUiState = mediaSourceSelectedUiState,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaSelectionDialog(
    mediaSelector: EpisodeMediaSelector,
    mediaSourceSelectedUiState: MediaSourceSelectedUiState
) {
    if (mediaSelector.showMediaSelector) {
        AlertDialog(
            onDismissRequest = mediaSelector::dismissDialog,
            confirmButton = {
                TextButton(mediaSelector::onConfirmSelection) {
                    Text("下载")
                }
            },
            dismissButton = {
                TextButton(mediaSelector::dismissDialog) {
                    Text("取消")
                }
            },
            text = {
                MediaSelectorDialogContent(
                    mediaSourceSelectedUiState = mediaSourceSelectedUiState,
                    mediaSelector.selectedVideoTrack,
                    mediaSelector.selectedAudioTrack,
                    onVideoTrackSelected = mediaSelector::onVideoTrackSelected,
                    onAudioTrackSelected = mediaSelector::onAudioTrackSelected
                )
            },
        )
    }
}

@Composable
fun MediaSelectorDialogContent(
    mediaSourceSelectedUiState: MediaSourceSelectedUiState,
    selectedVideoTrack: TrackInfo?,
    selectedAudioTrack: TrackInfo?,
    onVideoTrackSelected: (TrackInfo) -> Unit,
    onAudioTrackSelected: (TrackInfo) -> Unit,
) {
    Column((Modifier.verticalScroll(rememberScrollState()))) {
        when (mediaSourceSelectedUiState) {
            is MediaSourceSelectedUiState.LoadFailed -> {
                Text(mediaSourceSelectedUiState.message ?: "啥都木有")
            }

            MediaSourceSelectedUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Loading...")
                }
            }

            is MediaSourceSelectedUiState.Success -> {
                mediaSourceSelectedUiState.asset.videoStreams.fastForEach { videoTrack ->
                    MediaTrackItems(videoTrack, selectedVideoTrack == videoTrack, 1) {
                        onVideoTrackSelected(it)
                    }
                }
                if (mediaSourceSelectedUiState.asset.audioStreams.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    mediaSourceSelectedUiState.asset.audioStreams.fastForEach { audioTrack ->
                        MediaTrackItems(audioTrack, selectedAudioTrack == audioTrack, 2) {
                            onAudioTrackSelected(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MediaTrackItems(
    trackInfo: TrackInfo,
    isSelected: Boolean,
    type: Int,
    modifier: Modifier = Modifier,
    onTrackSelected: (TrackInfo) -> Unit = {}
) {
    ListItem(
        headlineContent = {
            Text(
                text = trackInfo.trackLabel,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            trackInfo.codecs?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        leadingContent = {
            val icon = if (type == 1) {
                Icons.Filled.Videocam
            } else {
                Icons.Filled.Audiotrack
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            RadioButton(
                selected = isSelected,
                onClick = null
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTrackSelected(trackInfo) },
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            headlineColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            supportingColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            leadingIconColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            trailingIconColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
private fun SearchTextField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val onSearchExplicitlyTriggered = {
        keyboardController?.hide()
        onSearchTriggered(searchQuery)
    }

    TextField(
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "search",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onSearchQueryChanged("")
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "close",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        onValueChange = {
            if ("\n" !in it) onSearchQueryChanged(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .onKeyEvent {
                if (it.key == Key.Enter) {
                    if (searchQuery.isBlank()) return@onKeyEvent false
                    onSearchExplicitlyTriggered()
                    true
                } else {
                    false
                }
            }
            .testTag("searchTextField"),
        shape = RoundedCornerShape(32.dp),
        value = searchQuery,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                if (searchQuery.isBlank()) return@KeyboardActions
                onSearchExplicitlyTriggered()
            },
        ),
        maxLines = 1,
        singleLine = true,
    )
}

@Preview(name = "SearchContent - Load Failed State")
@Composable
fun SearchContentLoadFailedPreview() {
    SearchContent(
        searchQuery = "a very long search query that might cause issues",
        searchResultUiState = SearchResultUiState.LoadFailed("Unable to connect to the server. Please check your internet connection."),
        selfInfoUiState = SelfInfoUiState.Loading,
        mediaSourceSelectedUiState = MediaSourceSelectedUiState.Loading,
    )
}
