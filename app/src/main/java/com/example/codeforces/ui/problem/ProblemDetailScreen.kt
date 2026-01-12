package com.example.codeforces.ui.problem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.codeforces.data.model.TestCase
import com.example.codeforces.viewmodel.ProblemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemDetailScreen(
    problemId: String,
    viewModel: ProblemViewModel,
    onOpenEditor: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(problemId) {
        viewModel.load(problemId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(state.problem?.name ?: "Problem") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        when {
            state.isLoading -> Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }

            state.problem == null -> Text(
                text = state.error ?: "Problem not found",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )

            else -> ProblemBody(
                state = state,
                onOpenEditor = onOpenEditor
            )
        }
    }
}

@Composable
private fun ProblemBody(
    state: com.example.codeforces.viewmodel.ProblemDetailUiState,
    onOpenEditor: () -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        state.problem?.let { problem ->
            Text(problem.id, style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                problem.rating?.let { Text("Rating: $it") }
                problem.solvedCount?.let { Text("Solved: $it") }
            }
            if (!problem.statementHtml.isNullOrEmpty()) {
                Text(
                    "Statement preview (HTML render TBD)\n${problem.statementHtml.take(240)}...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(onClick = onOpenEditor, modifier = Modifier.fillMaxWidth()) {
            Text("Open editor")
        }

        if (state.tests.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Community tests", style = MaterialTheme.typography.titleSmall)
                state.tests.forEach { TestCard(it) }
            }
        }
    }
}

@Composable
private fun TestCard(test: TestCase) {
    Surface(
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Input", style = MaterialTheme.typography.labelMedium)
            Text(test.input)
            Text("Expected", style = MaterialTheme.typography.labelMedium)
            Text(test.expectedOutput)
            test.note?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}

