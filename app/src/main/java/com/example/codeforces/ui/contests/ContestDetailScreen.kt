package com.example.codeforces.ui.contests

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.codeforces.viewmodel.ContestsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContestDetailScreen(
    contestId: Int,
    viewModel: ContestsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.detailState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(contestId) {
        viewModel.loadContestDetail(contestId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(state.contest?.name ?: "Contest") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        val url = "https://codeforces.com/contest/$contestId"
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Codeforces: ${state.contest?.name}")
                            putExtra(Intent.EXTRA_TEXT, "Check out this contest: ${state.contest?.name}\n$url")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Contest"))
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
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
                state.contest?.let { contest ->
                    Text("ID: ${contest.id}", style = MaterialTheme.typography.titleMedium)
                    Text("Type: ${contest.type}")
                    Text("Phase: ${contest.phase}")
                    contest.startTimeSeconds?.let {
                        val date = Date(it * 1000)
                        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                        Text("Start: ${formatter.format(date)}")
                    }
                    Text("Duration: ${contest.durationSeconds / 60} minutes")
                }

                state.standings?.let { standings ->
                    Divider()
                    Text("Standings", style = MaterialTheme.typography.titleLarge)
                    standings.rows.take(20).forEach { row ->
                        Text("Rank ${row.rank}: ${row.party.members.joinToString { it.handle }} - ${row.points} points")
                    }
                }

                if (state.hacks.isNotEmpty()) {
                    Divider()
                    Text("Hacks: ${state.hacks.size}", style = MaterialTheme.typography.titleMedium)
                }

                if (state.ratingChanges.isNotEmpty()) {
                    Divider()
                    Text("Rating Changes: ${state.ratingChanges.size}", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

