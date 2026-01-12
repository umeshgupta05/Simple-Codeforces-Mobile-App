package com.example.codeforces.ui.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codeforces.data.model.CfUser
import com.example.codeforces.viewmodel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: UsersViewModel,
    onUserClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val (searchHandle, setSearchHandle) = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Users") })

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchHandle,
                    onValueChange = setSearchHandle,
                    label = { Text("Handle") },
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { viewModel.searchUser(searchHandle) }) {
                    Text("Search")
                }
            }
            Button(onClick = { viewModel.loadRatedList() }) {
                Text("Top Rated")
            }
        }

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

            else -> UserList(
                users = state.users,
                onUserClick = onUserClick
            )
        }
    }
}

@Composable
private fun UserList(
    users: List<CfUser>,
    onUserClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { user ->
            Surface(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable { onUserClick(user.handle) },
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        user.handle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        user.rating?.let {
                            Text("Rating: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                        user.rank?.let {
                            Text("Rank: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    user.maxRating?.let {
                        Text("Max Rating: $it", style = MaterialTheme.typography.bodySmall)
                    }
                    user.country?.let {
                        Text("Country: $it", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

