package com.example.codeforces.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.codeforces.data.model.Problem
import com.example.codeforces.viewmodel.HomeViewModel
import com.example.codeforces.viewmodel.ProblemFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onProblemClick: (Problem) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Codeforces Practice") })

        FilterRow(
            filter = state.filter,
            onFilterChange = viewModel::updateFilter,
            onReload = viewModel::reload,
            modifier = Modifier.fillMaxWidth()
        )

        when {
            state.isLoading -> Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text("Loading problems…")
            }

            state.error != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Failed to load: ${state.error}", color = MaterialTheme.colorScheme.error)
            }

            else -> ProblemList(
                problems = state.problems,
                onProblemClick = onProblemClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FilterRow(
    filter: ProblemFilter,
    onFilterChange: (ProblemFilter) -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = filter.search,
            onValueChange = { onFilterChange(filter.copy(search = it)) },
            label = { Text("Search name or ID") },
            modifier = Modifier.weight(1f)
        )
        AssistChip(onClick = onReload, label = { Text("Refresh") })
    }
}

@Composable
private fun ProblemList(
    problems: List<Problem>,
    onProblemClick: (Problem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(problems) { problem ->
            Surface(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable { onProblemClick(problem) },
                tonalElevation = 1.dp,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(problem.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(problem.id, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        problem.rating?.let { Text("★ $it") }
                        problem.solvedCount?.let { Text("Solved: $it") }
                    }
                    if (problem.tags.isNotEmpty()) {
                        Text(problem.tags.joinToString(), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

