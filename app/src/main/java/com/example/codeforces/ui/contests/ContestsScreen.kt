package com.example.codeforces.ui.contests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codeforces.data.model.CfContest
import com.example.codeforces.viewmodel.ContestsViewModel
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

            else -> ContestList(
                contests = state.contests,
                onContestClick = onContestClick
            )
        }
    }
}

@Composable
private fun ContestList(
    contests: List<CfContest>,
    onContestClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(contests) { contest ->
            Surface(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable { onContestClick(contest.id) },
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        contest.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "ID: ${contest.id} • ${contest.type} • ${contest.phase}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    contest.startTimeSeconds?.let {
                        val date = Date(it * 1000)
                        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                        Text("Start: ${formatter.format(date)}", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "Duration: ${contest.durationSeconds / 60} minutes",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

