package com.example.codeforces.ui.submissions

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codeforces.data.model.CfSubmission
import com.example.codeforces.viewmodel.SubmissionsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionsScreen(
    viewModel: SubmissionsViewModel
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Recent Submissions") })

        when {
            state.isLoading -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text("Loading submissions…")
            }

            state.error != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
            }

            else -> SubmissionList(submissions = state.recentSubmissions)
        }
    }
}

@Composable
private fun SubmissionList(submissions: List<CfSubmission>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(submissions) { submission ->
            Surface(
                modifier = Modifier.padding(horizontal = 12.dp),
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        "${submission.problem.contestId ?: ""}${submission.problem.index} - ${submission.problem.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Lang: ${submission.programmingLanguage}", style = MaterialTheme.typography.bodySmall)
                        submission.verdict?.let {
                            Text(
                                "Verdict: $it",
                                color = when {
                                    it.contains("OK") -> MaterialTheme.colorScheme.primary
                                    it.contains("WRONG") -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                    Text(
                        "Time: ${submission.timeConsumedMillis}ms • Memory: ${submission.memoryConsumedBytes / 1024}KB",
                        style = MaterialTheme.typography.bodySmall
                    )
                    val date = Date(submission.creationTimeSeconds * 1000)
                    val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                    Text("Date: ${formatter.format(date)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

