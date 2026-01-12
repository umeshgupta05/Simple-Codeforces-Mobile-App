package com.example.codeforces.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codeforces.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLoginClick: () -> Unit,
    onLogout: (() -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("My Profile") },
            actions = {
                if (state.username != null) {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    onLogout?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                        }
                    }
                }
            }
        )

        when {
            state.username == null -> LoginPrompt(onLoginClick = onLoginClick)
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            state.error != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.refresh() }) {
                    Text("Retry")
                }
            }
            else -> ProfileContent(state = state)
        }
    }
}

@Composable
private fun LoginPrompt(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Login with Codeforces",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Enter your Codeforces username to view your profile",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }
    }
}

@Composable
private fun ProfileContent(state: com.example.codeforces.viewmodel.ProfileUiState) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        state.user?.let { user ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                user.handle,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            user.firstName?.let { firstName ->
                                user.lastName?.let { lastName ->
                                    Text("$firstName $lastName", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                            user.country?.let {
                                Text(it, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            user.rating?.let {
                                Text(
                                    "$it",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Rating", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            user.maxRating?.let {
                                Text("$it", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("Max Rating", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            user.rank?.let {
                                Text(it, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("Rank", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            user.contribution?.let {
                                Text("$it", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("Contribution", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        // Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "Submissions",
                value = state.submissions.size.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Blogs",
                value = state.blogEntries.size.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Contests",
                value = state.ratingHistory.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        // Submission Statistics
        if (state.submissions.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Submission Statistics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val verdicts = state.submissions.groupBy { it.verdict ?: "Unknown" }
                    val accepted = verdicts["OK"]?.size ?: 0
                    val total = state.submissions.size
                    val accuracy = if (total > 0) (accepted * 100.0 / total) else 0.0
                    
                    StatRow("Total Submissions", total.toString())
                    StatRow("Accepted", accepted.toString())
                    StatRow("Accuracy", String.format("%.1f%%", accuracy))
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    Text("Verdicts:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    verdicts.entries.sortedByDescending { it.value.size }.take(5).forEach { (verdict, list) ->
                        StatRow(verdict, list.size.toString())
                    }
                }
            }
        }

        // Rating History
        if (state.ratingHistory.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Rating History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    state.ratingHistory.take(10).forEach { rating ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(rating.contestName, fontWeight = FontWeight.Medium)
                                val date = Date(rating.ratingUpdateTimeSeconds * 1000)
                                val formatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                                Text(formatter.format(date), style = MaterialTheme.typography.bodySmall)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "${rating.oldRating} → ${rating.newRating}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (rating.newRating > rating.oldRating) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.error
                                )
                                Text("Rank: ${rating.rank}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }

        // Recent Submissions
        if (state.submissions.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recent Submissions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    state.submissions.take(10).forEach { submission ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${submission.problem.contestId ?: ""}${submission.problem.index} - ${submission.problem.name}",
                                    fontWeight = FontWeight.Medium
                                )
                                Text(submission.programmingLanguage, style = MaterialTheme.typography.bodySmall)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                submission.verdict?.let {
                                    Text(
                                        it,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            it.contains("OK") -> MaterialTheme.colorScheme.primary
                                            it.contains("WRONG") -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                                val date = Date(submission.creationTimeSeconds * 1000)
                                val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                                Text(formatter.format(date), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(4.dp))
}

