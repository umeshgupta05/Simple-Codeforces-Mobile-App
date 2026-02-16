package com.example.codeforces.ui.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.codeforces.data.model.CfUser
import com.example.codeforces.viewmodel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareUsersScreen(
    viewModel: UsersViewModel,
    onBack: () -> Unit
) {
    val compareState by viewModel.compareState.collectAsState()

    var handle1 by remember { mutableStateOf("") }
    var handle2 by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Compare Users") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = handle1,
                    onValueChange = { handle1 = it },
                    label = { Text("User 1") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Icon(
                    Icons.Default.CompareArrows,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = handle2,
                    onValueChange = { handle2 = it },
                    label = { Text("User 2") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            Button(
                onClick = { viewModel.compareUsers(handle1.trim(), handle2.trim()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = handle1.isNotBlank() && handle2.isNotBlank() && !compareState.isLoading
            ) {
                Text("Compare")
            }
        }

        when {
            compareState.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            compareState.error != null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Error: ${compareState.error}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            compareState.user1 != null && compareState.user2 != null -> {
                ComparisonContent(
                    user1 = compareState.user1!!,
                    user2 = compareState.user2!!
                )
            }
        }
    }
}

@Composable
private fun ComparisonContent(user1: CfUser, user2: CfUser) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UserHeaderCard(user = user1, modifier = Modifier.weight(1f))
            UserHeaderCard(user = user2, modifier = Modifier.weight(1f))
        }

        Divider()

        // Stats comparison
        Text(
            "Head-to-Head",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        CompareRow(
            label = "Rating",
            value1 = user1.rating?.toString() ?: "N/A",
            value2 = user2.rating?.toString() ?: "N/A",
            winner = compareNullableInts(user1.rating, user2.rating)
        )
        CompareRow(
            label = "Max Rating",
            value1 = user1.maxRating?.toString() ?: "N/A",
            value2 = user2.maxRating?.toString() ?: "N/A",
            winner = compareNullableInts(user1.maxRating, user2.maxRating)
        )
        CompareRow(
            label = "Rank",
            value1 = user1.rank ?: "N/A",
            value2 = user2.rank ?: "N/A",
            winner = 0  // Rank is qualitative
        )
        CompareRow(
            label = "Max Rank",
            value1 = user1.maxRank ?: "N/A",
            value2 = user2.maxRank ?: "N/A",
            winner = 0
        )
        CompareRow(
            label = "Contribution",
            value1 = user1.contribution?.toString() ?: "N/A",
            value2 = user2.contribution?.toString() ?: "N/A",
            winner = compareNullableInts(user1.contribution, user2.contribution)
        )
        CompareRow(
            label = "Friends",
            value1 = user1.friendOfCount?.toString() ?: "N/A",
            value2 = user2.friendOfCount?.toString() ?: "N/A",
            winner = compareNullableInts(user1.friendOfCount, user2.friendOfCount)
        )
    }
}

@Composable
private fun UserHeaderCard(user: CfUser, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                user.handle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            user.rating?.let {
                Text(
                    it.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = ratingColor(it)
                )
            }
            user.rank?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            user.country?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun CompareRow(
    label: String,
    value1: String,
    value2: String,
    winner: Int  // -1 = user1, 0 = tie, 1 = user2
) {
    val winColor = Color(0xFF2E7D32)
    val loseColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                value1,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (winner == -1) FontWeight.Bold else FontWeight.Normal,
                color = when (winner) {
                    -1 -> winColor
                    1 -> loseColor
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                value2,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (winner == 1) FontWeight.Bold else FontWeight.Normal,
                color = when (winner) {
                    1 -> winColor
                    -1 -> loseColor
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}

private fun compareNullableInts(a: Int?, b: Int?): Int {
    if (a == null && b == null) return 0
    if (a == null) return 1
    if (b == null) return -1
    return when {
        a > b -> -1
        a < b -> 1
        else -> 0
    }
}

private fun ratingColor(rating: Int): Color = when {
    rating >= 2400 -> Color(0xFFFF0000)  // Red
    rating >= 2100 -> Color(0xFFFF8C00)  // Orange
    rating >= 1900 -> Color(0xFFAA00AA)  // Violet
    rating >= 1600 -> Color(0xFF0000FF)  // Blue
    rating >= 1400 -> Color(0xFF03A89E)  // Cyan
    rating >= 1200 -> Color(0xFF008000)  // Green
    else -> Color(0xFF808080)            // Gray
}
