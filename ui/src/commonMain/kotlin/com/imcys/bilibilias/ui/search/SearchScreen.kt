package com.imcys.bilibilias.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.imcys.bilibilias.core.domain.model.EpisodeCacheRequest
import com.imcys.bilibilias.core.domain.model.EpisodeCacheState
import com.imcys.bilibilias.core.domain.model.MediaStream
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
        onEpisodeSelected = searchViewModel::onEpisodeSelected,
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
    onCacheRequest: (TrackInfo?, TrackInfo?) -> Unit = { _, _ -> },
    navigationToLogin: () -> Unit = {},
    navigationToPlayer: () -> Unit = {},
    navigationToSettings: () -> Unit = {},
    onEpisodeSelected: (EpisodeCacheRequest) -> Unit = {},
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
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
                    var showMediaSelector by rememberSaveable { mutableStateOf(false) }

                    var selectedVideoTrack by remember { mutableStateOf<TrackInfo?>(null) }
                    var selectedAudioTrack by remember { mutableStateOf<TrackInfo?>(null) }

                    LaunchedEffect(searchResultUiState) {
                        keyboardController?.hide()
                    }

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        val onEpisodeCacheSelection: (episode: EpisodeCacheState) -> Unit =
                            { episodeCacheState ->
                                val request = EpisodeCacheRequest(episodeCacheState)
                                onEpisodeSelected(request)
                            }
                        Text(
                            "分集(${searchResultUiState.episodes.size})",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        EpisodeList(searchResultUiState.episodes) {
                            onEpisodeCacheSelection(it)
                            showMediaSelector = true
                        }
                        MediaSelectionDialog(
                            showMediaSelector = showMediaSelector,
                            mediaSourceSelectedUiState = mediaSourceSelectedUiState,
                            onVideoTrackSelected = { selectedVideoTrack = it },
                            onAudioTrackSelected = { selectedAudioTrack = it },
                            onConfirm = { onCacheRequest(selectedVideoTrack, selectedAudioTrack) },
                            onDismiss = { showMediaSelector = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaSelectionDialog(
    showMediaSelector: Boolean,
    mediaSourceSelectedUiState: MediaSourceSelectedUiState,
    onVideoTrackSelected: (TrackInfo?) -> Unit,
    onAudioTrackSelected: (TrackInfo?) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showMediaSelector) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onConfirm) {
                    Text("下载")
                }
            },
            dismissButton = {
                TextButton(onDismiss) {
                    Text("取消")
                }
            },
            text = {
                MediaSelectorDialogContent(
                    mediaSourceSelectedUiState = mediaSourceSelectedUiState,
                    onVideoTrackSelected = onVideoTrackSelected,
                    onAudioTrackSelected = onAudioTrackSelected
                )
            },
        )
    }
}

@Composable
fun MediaSelectorDialogContent(
    mediaSourceSelectedUiState: MediaSourceSelectedUiState,
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
                    MediaTrackItems(videoTrack, onVideoTrackSelected == videoTrack) {
                        onVideoTrackSelected(it)
                    }
                }
                if (mediaSourceSelectedUiState.asset.audioStreams.isNotEmpty()) {
                    HorizontalDivider()
                    mediaSourceSelectedUiState.asset.audioStreams.fastForEach { audioTrack ->
                        MediaTrackItems(audioTrack, onAudioTrackSelected == audioTrack) {
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
    modifier: Modifier = Modifier,
    onTrackSelected: (TrackInfo) -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth().selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = { onTrackSelected(trackInfo) },
            ),
    ) {
        Row(
            modifier = Modifier.padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(isSelected, onClick = null)
            Text(trackInfo.trackLabel)

            Text(
                text = trackInfo.codecs ?: "",
                modifier = Modifier.padding(start = 8.dp),
                maxLines = 1,
            )
        }
    }
}

@Composable
fun QualitySelection(
    videoStreams: List<MediaStream>,
    audioStreams: List<MediaStream>,
    selectedVideoOption: MediaStream?,
    onVideoOptionSelected: (MediaStream?) -> Unit,
    selectedAudioOption: MediaStream?,
    onAudioOptionSelected: (MediaStream?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (videoStreams.isNotEmpty()) {
            SelectField(
                label = { Text("画质") }, // Consider R.string.video_quality
                options = videoStreams,
                selectedOption = selectedVideoOption,
                onOptionSelected = onVideoOptionSelected,
                menuItemContent = { stream -> Text(stream!!.description) },
                optionToText = { stream -> stream!!.description },
                modifier = Modifier.weight(1f)
            )
        } else {
            // Optional: Show a placeholder or empty state if no video options
            Spacer(modifier = Modifier.weight(1f)) // To maintain layout
        }

        if (audioStreams.isNotEmpty()) {
            SelectField(
                label = { Text("音质") }, // Consider R.string.audio_quality
                options = audioStreams,
                selectedOption = selectedAudioOption,
                onOptionSelected = onAudioOptionSelected,
                menuItemContent = { stream -> Text(stream!!.description) },
                optionToText = { stream -> stream!!.description },
                modifier = Modifier.weight(1f)
            )
        } else {
            // Optional: Show a placeholder or empty state if no audio options
            Spacer(modifier = Modifier.weight(1f)) // To maintain layout
        }
    }
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
