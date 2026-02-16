package com.example.codeforces.ui.contests

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codeforces.data.model.CfContest
import com.example.codeforces.viewmodel.ContestsViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContestsScreen(
    viewModel: ContestsViewModel,
    onContestClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Contests") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            actions = {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gym", modifier = Modifier.padding(end = 8.dp))
                    Switch(
                        checked = state.showGym,
                        onCheckedChange = { viewModel.toggleGym() }
                    )
                }
            }
        )

        when {
            state.isLoading -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text("Loading contests…")
            }

            state.error != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
            }

            else -> {
                val sortedContests = remember(state.contests) {
                    state.contests.sortedWith(
                        compareBy<CfContest> { phaseOrder(it.phase) }
                            .thenByDescending { it.startTimeSeconds ?: 0L }
                    )
                }
                ContestList(
                    contests = sortedContests,
                    onContestClick = onContestClick
                )
            }
        }
    }
}

private fun phaseOrder(phase: String): Int = when (phase) {
    "CODING" -> 0
    "BEFORE" -> 1
    "PENDING_SYSTEM_TEST" -> 2
    "SYSTEM_TEST" -> 3
    "FINISHED" -> 4
    else -> 5
}

@Composable
private fun ContestList(
    contests: List<CfContest>,
    onContestClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(contests, key = { it.id }) { contest ->
            ContestCard(
                contest = contest,
                onClick = { onContestClick(contest.id) }
            )
        }
    }
}

@Composable
private fun ContestCard(
    contest: CfContest,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    contest.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                PhaseBadge(phase = contest.phase)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "ID: ${contest.id} • ${contest.type}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            contest.startTimeSeconds?.let { startTime ->
                val date = Date(startTime * 1000)
                val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                Text(
                    "Start: ${formatter.format(date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                "Duration: ${contest.durationSeconds / 3600}h ${(contest.durationSeconds % 3600) / 60}m",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Countdown for upcoming contests
            if (contest.phase == "BEFORE" && contest.startTimeSeconds != null) {
                Spacer(modifier = Modifier.height(8.dp))
                ContestCountdown(startTimeSeconds = contest.startTimeSeconds)
            }

            // Live timer for active contests
            if (contest.phase == "CODING" && contest.startTimeSeconds != null) {
                Spacer(modifier = Modifier.height(8.dp))
                ContestElapsedTime(
                    startTimeSeconds = contest.startTimeSeconds,
                    durationSeconds = contest.durationSeconds
                )
            }
        }
    }
}

@Composable
private fun PhaseBadge(phase: String) {
    val (color, label) = when (phase) {
        "CODING" -> Color(0xFF2E7D32) to "🔴 LIVE"
        "BEFORE" -> Color(0xFF1565C0) to "⏳ UPCOMING"
        "PENDING_SYSTEM_TEST", "SYSTEM_TEST" -> Color(0xFFE65100) to "⚙️ TESTING"
        "FINISHED" -> Color(0xFF616161) to "FINISHED"
        else -> Color(0xFF616161) to phase
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = color,
        contentColor = Color.White
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ContestCountdown(startTimeSeconds: Long) {
    var remainingSeconds by remember { mutableLongStateOf(0L) }

    LaunchedEffect(startTimeSeconds) {
        while (true) {
            val now = System.currentTimeMillis() / 1000
            remainingSeconds = startTimeSeconds - now
            if (remainingSeconds <= 0) break
            delay(1000L)
        }
    }

    if (remainingSeconds > 0) {
        val days = remainingSeconds / 86400
        val hours = (remainingSeconds % 86400) / 3600
        val minutes = (remainingSeconds % 3600) / 60
        val seconds = remainingSeconds % 60

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Default.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = buildString {
                    append("Starts in: ")
                    if (days > 0) append("${days}d ")
                    append("${hours}h ${minutes}m ${seconds}s")
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ContestElapsedTime(startTimeSeconds: Long, durationSeconds: Long) {
    var elapsedSeconds by remember { mutableLongStateOf(0L) }

    LaunchedEffect(startTimeSeconds) {
        while (true) {
            val now = System.currentTimeMillis() / 1000
            elapsedSeconds = now - startTimeSeconds
            if (elapsedSeconds >= durationSeconds) break
            delay(1000L)
        }
    }

    val remaining = durationSeconds - elapsedSeconds
    if (remaining > 0) {
        val hours = remaining / 3600
        val minutes = (remaining % 3600) / 60
        val seconds = remaining % 60

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Default.Timer,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Time left: ${hours}h ${minutes}m ${seconds}s",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }
    }
}
