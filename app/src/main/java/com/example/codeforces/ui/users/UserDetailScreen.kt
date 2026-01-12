package com.example.codeforces.ui.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codeforces.viewmodel.UsersViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    handle: String,
    viewModel: UsersViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.detailState.collectAsState()

    LaunchedEffect(handle) {
        viewModel.loadUserDetail(handle)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(state.user?.handle ?: handle) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            state.error != null -> Text(
                text = "Error: ${state.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
            else -> Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.user?.let { user ->
                    Text(user.handle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    user.rating?.let { Text("Rating: $it", style = MaterialTheme.typography.titleLarge) }
                    user.rank?.let { Text("Rank: $it") }
                    user.maxRating?.let { Text("Max Rating: $it") }
                    user.country?.let { Text("Country: $it") }
                    user.contribution?.let { Text("Contribution: $it") }
                }

                if (state.ratingHistory.isNotEmpty()) {
                    Divider()
                    Text("Rating History", style = MaterialTheme.typography.titleLarge)
                    state.ratingHistory.take(10).forEach { rating ->
                        Text("${rating.contestName}: ${rating.oldRating} → ${rating.newRating}")
                    }
                }

                if (state.submissions.isNotEmpty()) {
                    Divider()
                    Text("Recent Submissions: ${state.submissions.size}", style = MaterialTheme.typography.titleLarge)
                    state.submissions.take(10).forEach { submission ->
                        val date = Date(submission.creationTimeSeconds * 1000)
                        val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        Text(
                            "${submission.problem.name} - ${submission.verdict ?: "Pending"} (${formatter.format(date)})"
                        )
                    }
                }

                if (state.blogEntries.isNotEmpty()) {
                    Divider()
                    Text("Blog Entries: ${state.blogEntries.size}", style = MaterialTheme.typography.titleLarge)
                    state.blogEntries.take(5).forEach { blog ->
                        Text(blog.title)
                    }
                }
            }
        }
    }
}

