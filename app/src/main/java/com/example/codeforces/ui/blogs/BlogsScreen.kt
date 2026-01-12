package com.example.codeforces.ui.blogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.codeforces.data.model.RecentAction
import com.example.codeforces.viewmodel.BlogsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogsScreen(
    viewModel: BlogsViewModel,
    onBlogClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Blogs & Recent Actions") })

        when {
            state.isLoading -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text("Loading…")
            }

            state.error != null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
            }

            else -> RecentActionsList(
                actions = state.recentActions,
                onBlogClick = onBlogClick
            )
        }
    }
}

@Composable
private fun RecentActionsList(
    actions: List<RecentAction>,
    onBlogClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(actions) { action ->
            Surface(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable {
                        action.blogEntry?.id?.let { onBlogClick(it) }
                    },
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    action.blogEntry?.let { blog ->
                        Text(
                            blog.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("By: ${blog.authorHandle}", style = MaterialTheme.typography.bodySmall)
                        val date = Date(blog.creationTimeSeconds * 1000)
                        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        Text("Date: ${formatter.format(date)}", style = MaterialTheme.typography.bodySmall)
                    }
                    action.comment?.let { comment ->
                        Text(
                            "Comment by ${comment.commentatorHandle}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(comment.text.take(100) + "...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

