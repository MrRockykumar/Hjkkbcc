package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.StreamViewModel
import com.example.ui.viewmodel.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val viewModel: StreamViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val currentPlayingChannel by viewModel.currentPlayingChannel.collectAsState()

    GlassBackground {
        Scaffold(
            containerColor = Color.Transparent, // Let the mesh gradient background show through
            topBar = {
                val searchQuery by viewModel.searchQuery.collectAsState()
                var isSearchExpanded by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    if (isSearchExpanded) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search channel names...", color = GlassTheme.TextMuted, fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search icon",
                                    tint = GlassTheme.TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = { viewModel.setSearchQuery("") },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear search",
                                                tint = GlassTheme.TextWhite,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { 
                                            isSearchExpanded = false
                                            viewModel.setSearchQuery("")
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Collapse search",
                                            tint = GlassTheme.TextWhite,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GlassTheme.TextWhite,
                                unfocusedTextColor = GlassTheme.TextWhite,
                                focusedContainerColor = Color(0x2AFFFFFF),
                                unfocusedContainerColor = Color(0x15FFFFFF),
                                focusedBorderColor = GlassTheme.AccentColor,
                                unfocusedBorderColor = GlassTheme.GlassBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Title and Icon
                            RtvPlusLogo(
                                logoHeight = 22.dp,
                                showBackground = true
                            )

                            // Top Indicators
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Search Button
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x1AFFFFFF))
                                        .clickable { isSearchExpanded = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = GlassTheme.TextWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Refresh Button
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x1AFFFFFF))
                                        .clickable { viewModel.loadData() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh data",
                                        tint = GlassTheme.TextWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Frosted Glass custom bottom navigation panel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(80.dp)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf(
                        Triple("explore", "Explore", Icons.Default.Home),
                        Triple("countries", "Countries", Icons.Default.Public),
                        Triple("favorites", "My TV", Icons.Default.Bookmarks),
                        Triple("settings", "Account", Icons.Default.Settings)
                    )

                    tabs.forEach { (tabId, tabName, tabIcon) ->
                        val isSelected = activeTab == tabId
                        val activeColor = GlassTheme.AccentColor
                        val inactiveColor = GlassTheme.TextWhite.copy(alpha = 0.4f)

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectTab(tabId) }
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) activeColor.copy(alpha = 0.2f) else Color.Transparent)
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tabIcon,
                                    contentDescription = tabName,
                                    tint = if (isSelected) activeColor else inactiveColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tabName,
                                color = if (isSelected) activeColor else inactiveColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val state = uiState) {
                    is UiState.Loading -> {
                        SkeletonLoaderList(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        )
                    }
                    is UiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            FrostedGlassCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudOff,
                                        contentDescription = "Offline Error",
                                        tint = GlassTheme.AccentPink,
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Connection Failure",
                                        color = GlassTheme.TextWhite,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = state.error,
                                        color = GlassTheme.TextMuted,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(
                                        onClick = { viewModel.loadData() },
                                        colors = ButtonDefaults.buttonColors(containerColor = GlassTheme.AccentColor),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text(text = "Try Again", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Success -> {
                        // Display the selected tab's content
                        when (activeTab) {
                            "explore" -> DiscoverScreen(viewModel = viewModel)
                            "countries" -> CountriesScreen(viewModel = viewModel)
                            "favorites" -> FavoritesScreen(viewModel = viewModel)
                            "settings" -> SettingsScreen(viewModel = viewModel)
                        }
                    }
                }

                // Live Player overlay display (sliding modal)
                AnimatedVisibility(
                    visible = currentPlayingChannel != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    currentPlayingChannel?.let { channel ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.92f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Live Stream player window (16:9 ratio)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.77f)
                                        .background(Color.Black)
                                ) {
                                    ExoPlayerView(
                                        streamUrl = channel.stream?.url ?: "",
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Close controls overlay top-left
                                    IconButton(
                                        onClick = { viewModel.stopPlayback() },
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .align(Alignment.TopStart)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close player",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Stream metadata container
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(24.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            ChannelAvatar(
                                                name = channel.name,
                                                logoUrl = channel.logoUrl,
                                                modifier = Modifier.size(56.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column {
                                                Text(
                                                    text = channel.name,
                                                    color = Color.White,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = channel.categoryName.uppercase(),
                                                        color = GlassTheme.AccentPink,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "${channel.countryFlag} ${channel.countryName}",
                                                        color = GlassTheme.TextMuted,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }

                                        // Favorite button inside player
                                        IconButton(
                                            onClick = { viewModel.toggleFavorite(channel.id) },
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(Color(0x12FFFFFF))
                                        ) {
                                            Icon(
                                                imageVector = if (channel.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "Bookmark",
                                                tint = if (channel.isFavorite) GlassTheme.AccentPink else Color.White
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Additional specs section
                                    FrostedGlassCard(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = "BROADCAST PARAMETERS",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "HLS Stream Quality", color = GlassTheme.TextMuted, fontSize = 12.sp)
                                                Text(text = channel.stream?.quality?.uppercase() ?: "AUTO (1080P)", color = GlassTheme.AccentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "Primary Server Feed", color = GlassTheme.TextMuted, fontSize = 12.sp)
                                                Text(text = channel.stream?.feed ?: "Default Gateway", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "Stream Target URL", color = GlassTheme.TextMuted, fontSize = 12.sp)
                                                Text(
                                                    text = channel.stream?.url?.substringBefore("?") ?: "",
                                                    color = Color.White.copy(alpha = 0.5f),
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    modifier = Modifier.width(180.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
