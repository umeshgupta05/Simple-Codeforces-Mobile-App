package com.example.codeforces.ui.blogs

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
import com.example.codeforces.viewmodel.BlogsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogDetailScreen(
    blogId: Int,
    viewModel: BlogsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.detailState.collectAsState()

    LaunchedEffect(blogId) {
        viewModel.loadBlogEntry(blogId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(state.blogEntry?.title ?: "Blog") },
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
                state.blogEntry?.let { blog ->
                    Text(blog.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("By: ${blog.authorHandle}")
                    val date = Date(blog.creationTimeSeconds * 1000)
                    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    Text("Date: ${formatter.format(date)}")
                    Text("Rating: ${blog.rating}")
                    if (blog.tags.isNotEmpty()) {
                        Text("Tags: ${blog.tags.joinToString()}")
                    }
                    Divider()
                    Text("Content:", style = MaterialTheme.typography.titleMedium)
                    Text(blog.content ?: "Content not available")
                }

                if (state.comments.isNotEmpty()) {
                    Divider()
                    Text("Comments (${state.comments.size})", style = MaterialTheme.typography.titleLarge)
                    state.comments.forEach { comment ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(comment.commentatorHandle, fontWeight = FontWeight.Bold)
                                Text(comment.text)
                                val date = Date(comment.creationTimeSeconds * 1000)
                                val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                Text("${formatter.format(date)} • Rating: ${comment.rating}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

