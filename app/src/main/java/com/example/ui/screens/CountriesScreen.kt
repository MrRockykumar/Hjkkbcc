package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Country
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.GlassTheme
import com.example.ui.viewmodel.StreamViewModel

@Composable
fun CountriesScreen(
    viewModel: StreamViewModel,
    modifier: Modifier = Modifier
) {
    val countries by viewModel.countries.collectAsState()
    val filteredChannels by viewModel.filteredChannels.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()

    var countrySearchQuery by remember { mutableStateOf("") }

    // Filter countries based on search input
    val displayedCountries = remember(countries, countrySearchQuery) {
        countries.filter {
            it.name.contains(countrySearchQuery, ignoreCase = true) ||
            it.code.contains(countrySearchQuery, ignoreCase = true)
        }.sortedBy { it.name }
    }

    // Add an option to clear country filter at the top of the grid
    val countriesWithAll = if (countrySearchQuery.isEmpty()) {
        listOf(Country(name = "All Countries", code = "all", flag = "🌐")) + displayedCountries
    } else {
        displayedCountries
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Country search bar
        item(span = { GridItemSpan(2) }) {
            OutlinedTextField(
                value = countrySearchQuery,
                onValueChange = { countrySearchQuery = it },
                placeholder = { Text("Search countries...", color = GlassTheme.TextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Countries",
                        tint = GlassTheme.TextMuted
                    )
                },
                trailingIcon = {
                    if (countrySearchQuery.isNotEmpty()) {
                        IconButton(onClick = { countrySearchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = GlassTheme.TextWhite
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = GlassTheme.TextWhite,
                    unfocusedTextColor = GlassTheme.TextWhite,
                    focusedContainerColor = Color(0x1F292929),
                    unfocusedContainerColor = Color(0x0F18181B),
                    focusedBorderColor = GlassTheme.AccentColor,
                    unfocusedBorderColor = GlassTheme.GlassBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        // Section header
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BROWSE COUNTRIES",
                    color = GlassTheme.TextWhite,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${displayedCountries.size} Available",
                    color = GlassTheme.AccentColor,
                    fontSize = 11.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Country grid items
        items(countriesWithAll) { country ->
            val isCurrent = selectedCountry.equals(country.code, ignoreCase = true)
            
            FrostedGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clickable {
                        viewModel.selectCountry(country.code)
                        viewModel.selectTab("explore") // Switch back to channel feed
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isCurrent) GlassTheme.AccentColor.copy(alpha = 0.2f) else Color.Transparent)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Flag / Logo
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = country.flag ?: "🌐",
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = country.name,
                            color = GlassTheme.TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = country.code.uppercase(),
                            color = GlassTheme.TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
