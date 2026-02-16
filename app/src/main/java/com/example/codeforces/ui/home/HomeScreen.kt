package com.example.codeforces.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codeforces.data.local.BookmarkDao
import com.example.codeforces.data.model.Problem
import com.example.codeforces.viewmodel.BookmarksViewModel
import com.example.codeforces.viewmodel.HomeViewModel
import com.example.codeforces.viewmodel.ProblemFilter
import com.example.codeforces.viewmodel.SortBy

val POPULAR_TAGS = listOf(
    "dp", "greedy", "math", "implementation", "brute force",
    "data structures", "binary search", "graphs", "constructive algorithms",
    "sortings", "strings", "number theory", "geometry", "trees",
    "dfs and similar", "two pointers"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    bookmarksViewModel: BookmarksViewModel,
    onProblemClick: (Problem) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val bookmarks by bookmarksViewModel.bookmarks.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Codeforces Practice") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            actions = {
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(
                        if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
                        contentDescription = "Toggle Filters"
                    )
                }
                IconButton(onClick = { viewModel.reload() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        )

        // Search bar (always visible)
        OutlinedTextField(
            value = state.filter.search,
            onValueChange = { viewModel.updateFilter(state.filter.copy(search = it)) },
            label = { Text("Search name or ID") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            singleLine = true
        )

        // Expandable filter panel
        AnimatedVisibility(visible = showFilters) {
            AdvancedFilterPanel(
                filter = state.filter,
                onFilterChange = { viewModel.updateFilter(it) }
            )
        }

        when {
            state.isLoading -> Column(
                modifier = Modifier.fillMaxSize(),
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

            else -> {
                Text(
                    "${state.problems.size} problems",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                ProblemList(
                    problems = state.problems,
                    bookmarkedIds = bookmarks.map { it.problemId }.toSet(),
                    onProblemClick = onProblemClick,
                    onBookmarkToggle = { bookmarksViewModel.toggleBookmark(it) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedFilterPanel(
    filter: ProblemFilter,
    onFilterChange: (ProblemFilter) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Rating Range
            Text("Rating Range", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            
            val minVal = (filter.minRating ?: 800).toFloat()
            val maxVal = (filter.maxRating ?: 3500).toFloat()
            
            RangeSlider(
                value = minVal..maxVal,
                onValueChange = { range ->
                    onFilterChange(
                        filter.copy(
                            minRating = range.start.toInt(),
                            maxRating = range.endInclusive.toInt()
                        )
                    )
                },
                valueRange = 800f..3500f,
                steps = 26
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${filter.minRating ?: 800}", style = MaterialTheme.typography.bodySmall)
                Text("${filter.maxRating ?: 3500}", style = MaterialTheme.typography.bodySmall)
            }

            Divider()

            // Tags
            Text("Tags", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                POPULAR_TAGS.forEach { tag ->
                    val isSelected = tag in filter.tags
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newTags = if (isSelected) filter.tags - tag else filter.tags + tag
                            onFilterChange(filter.copy(tags = newTags))
                        },
                        label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Divider()

            // Sort
            Text("Sort By", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SortChip("Default", SortBy.DEFAULT, filter.sortBy) {
                    onFilterChange(filter.copy(sortBy = it))
                }
                SortChip("Rating ↑", SortBy.RATING_ASC, filter.sortBy) {
                    onFilterChange(filter.copy(sortBy = it))
                }
                SortChip("Rating ↓", SortBy.RATING_DESC, filter.sortBy) {
                    onFilterChange(filter.copy(sortBy = it))
                }
                SortChip("Solved ↑", SortBy.SOLVED_ASC, filter.sortBy) {
                    onFilterChange(filter.copy(sortBy = it))
                }
                SortChip("Solved ↓", SortBy.SOLVED_DESC, filter.sortBy) {
                    onFilterChange(filter.copy(sortBy = it))
                }
            }

            // Clear all button
            TextButton(
                onClick = { onFilterChange(ProblemFilter()) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear Filters")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortChip(
    label: String,
    sortBy: SortBy,
    currentSort: SortBy,
    onSelect: (SortBy) -> Unit
) {
    FilterChip(
        selected = currentSort == sortBy,
        onClick = { onSelect(sortBy) },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
    )
}

@Composable
private fun ProblemList(
    problems: List<Problem>,
    bookmarkedIds: Set<String>,
    onProblemClick: (Problem) -> Unit,
    onBookmarkToggle: (Problem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(problems) { problem ->
            Card(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable { onProblemClick(problem) },
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            problem.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            problem.id,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            problem.rating?.let { Text("★ $it") }
                            problem.solvedCount?.let { Text("Solved: $it") }
                        }
                        if (problem.tags.isNotEmpty()) {
                            Text(
                                problem.tags.joinToString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { onBookmarkToggle(problem) }) {
                        val isBookmarked = problem.id in bookmarkedIds
                        Icon(
                            if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Remove Bookmark" else "Add Bookmark",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
