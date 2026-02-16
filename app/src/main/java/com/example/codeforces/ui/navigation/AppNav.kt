package com.example.codeforces.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.codeforces.data.api.NetworkClient
import com.example.codeforces.data.repository.CodeforcesRepository
import com.example.codeforces.data.repository.ProblemRepositoryImpl
import com.example.codeforces.ui.blogs.BlogDetailScreen
import com.example.codeforces.ui.blogs.BlogsScreen
import com.example.codeforces.ui.bookmarks.BookmarksScreen
import com.example.codeforces.ui.contests.ContestDetailScreen
import com.example.codeforces.ui.contests.ContestsScreen
import com.example.codeforces.ui.editor.EditorScreen
import com.example.codeforces.ui.home.HomeScreen
import com.example.codeforces.ui.login.LoginDialog
import com.example.codeforces.ui.problem.ProblemDetailScreen
import com.example.codeforces.ui.profile.ProfileScreen
import com.example.codeforces.ui.submissions.SubmissionsScreen
import com.example.codeforces.ui.users.CompareUsersScreen
import com.example.codeforces.ui.users.UserDetailScreen
import com.example.codeforces.ui.users.UsersScreen
import com.example.codeforces.utils.PreferencesManager
import com.example.codeforces.viewmodel.*

object Destinations {
    const val PROBLEMS = "problems"
    const val CONTESTS = "contests"
    const val USERS = "users"
    const val BLOGS = "blogs"
    const val SUBMISSIONS = "submissions"
    const val PROFILE = "profile"
    const val PROBLEM_DETAIL = "problem"
    const val EDITOR = "editor"
    const val CONTEST_DETAIL = "contest"
    const val USER_DETAIL = "user"
    const val BLOG_DETAIL = "blog"
    const val BOOKMARKS = "bookmarks"
    const val COMPARE_USERS = "compare_users"
}

@Composable
fun CodeforcesApp(
    preferencesManager: PreferencesManager,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val navController = rememberNavController()
    var showLoginDialog by remember { mutableStateOf(false) }

    val database = remember { com.example.codeforces.data.local.AppDatabase.getDatabase(context) }
    val codeforcesApi = NetworkClient.codeforcesService()
    val codeforcesRepository = CodeforcesRepository(codeforcesApi)
    val problemRepository = ProblemRepositoryImpl(codeforcesApi)

    val savedUsername = remember { preferencesManager.getUsername() }
    val profileViewModel = remember(savedUsername) {
        ProfileViewModel(codeforcesRepository, savedUsername)
    }

    val bookmarksViewModel = remember { BookmarksViewModel(database.bookmarkDao()) }
    val homeViewModel = HomeViewModel(problemRepository)
    val problemViewModel = ProblemViewModel(problemRepository)
    val editorViewModel = remember { EditorViewModel(database.editorSessionDao(), database.problemNoteDao()) }
    val contestsViewModel = ContestsViewModel(codeforcesRepository)
    val usersViewModel = UsersViewModel(codeforcesRepository)
    val blogsViewModel = BlogsViewModel(codeforcesRepository)
    val submissionsViewModel = SubmissionsViewModel(codeforcesRepository)

    fun handleLogin(username: String) {
        preferencesManager.saveUsername(username)
        profileViewModel.loadProfile(username)
        showLoginDialog = false
    }

    fun handleLogout() {
        preferencesManager.clearUsername()
        profileViewModel.loadProfile("")
        navController.navigate(Destinations.PROFILE) {
            popUpTo(Destinations.PROFILE) { inclusive = true }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Problems") },
                    label = { Text("Problems") },
                    selected = currentRoute == Destinations.PROBLEMS,
                    onClick = { navController.navigate(Destinations.PROBLEMS) { popUpTo(Destinations.PROBLEMS) } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Event, contentDescription = "Contests") },
                    label = { Text("Contests") },
                    selected = currentRoute == Destinations.CONTESTS,
                    onClick = { navController.navigate(Destinations.CONTESTS) { popUpTo(Destinations.CONTESTS) } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = "Bookmarks") },
                    label = { Text("Saved") },
                    selected = currentRoute == Destinations.BOOKMARKS,
                    onClick = { navController.navigate(Destinations.BOOKMARKS) { popUpTo(Destinations.BOOKMARKS) } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Users") },
                    label = { Text("Users") },
                    selected = currentRoute == Destinations.USERS,
                    onClick = { navController.navigate(Destinations.USERS) { popUpTo(Destinations.USERS) } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = currentRoute == Destinations.PROFILE,
                    onClick = { navController.navigate(Destinations.PROFILE) { popUpTo(Destinations.PROFILE) } }
                )
            }
        }
    ) { paddingValues ->
        AppNavHost(
            navController = navController,
            homeViewModel = homeViewModel,
            problemViewModel = problemViewModel,
            editorViewModel = editorViewModel,
            contestsViewModel = contestsViewModel,
            usersViewModel = usersViewModel,
            blogsViewModel = blogsViewModel,
            submissionsViewModel = submissionsViewModel,
            profileViewModel = profileViewModel,
            bookmarksViewModel = bookmarksViewModel,
            onLoginClick = { showLoginDialog = true },
            onLogout = ::handleLogout,
            modifier = modifier.padding(paddingValues)
        )
        
        if (showLoginDialog) {
            LoginDialog(
                onDismiss = { showLoginDialog = false },
                onLogin = ::handleLogin
            )
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    problemViewModel: ProblemViewModel,
    editorViewModel: EditorViewModel,
    contestsViewModel: ContestsViewModel,
    usersViewModel: UsersViewModel,
    blogsViewModel: BlogsViewModel,
    submissionsViewModel: SubmissionsViewModel,
    profileViewModel: ProfileViewModel,
    bookmarksViewModel: BookmarksViewModel,
    onLoginClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.PROBLEMS,
        modifier = modifier
    ) {
        composable(Destinations.PROBLEMS) {
            HomeScreen(
                viewModel = homeViewModel,
                bookmarksViewModel = bookmarksViewModel,
                onProblemClick = { problem ->
                    navController.navigate("${Destinations.PROBLEM_DETAIL}/${problem.id}")
                }
            )
        }
        composable("${Destinations.PROBLEM_DETAIL}/{problemId}") { backStackEntry ->
            val problemId = backStackEntry.arguments?.getString("problemId").orEmpty()
            
            LaunchedEffect(problemId) {
                problemViewModel.load(problemId)
                editorViewModel.load(problemId)
            }
            
            val problemState by problemViewModel.state.collectAsState()
            val editorState by editorViewModel.state.collectAsState()
            
            com.example.codeforces.ui.problem.EnhancedProblemDetailScreen(
                problemId = problemId,
                problemState = problemState,
                editorState = editorState,
                onCodeChange = { editorViewModel.onCodeChange(it, problemId) },
                onLanguageChange = { editorViewModel.onLanguageChange(it, problemId) },
                onCustomInputChange = { editorViewModel.onCustomInputChange(it, problemId) },
                onBack = { navController.popBackStack() },
                noteText = editorState.note,
                onNoteChange = { editorViewModel.onNoteChange(it, problemId) }
            )
        }
        composable("${Destinations.EDITOR}/{problemId}") { backStackEntry ->
            val problemId = backStackEntry.arguments?.getString("problemId").orEmpty()
            EditorScreen(
                problemId = problemId,
                editorViewModel = editorViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.CONTESTS) {
            ContestsScreen(
                viewModel = contestsViewModel,
                onContestClick = { contestId ->
                    navController.navigate("${Destinations.CONTEST_DETAIL}/$contestId")
                }
            )
        }
        composable("${Destinations.CONTEST_DETAIL}/{contestId}") { backStackEntry ->
            val contestId = backStackEntry.arguments?.getString("contestId")?.toIntOrNull() ?: 0
            ContestDetailScreen(
                contestId = contestId,
                viewModel = contestsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.BOOKMARKS) {
            BookmarksScreen(
                viewModel = bookmarksViewModel,
                onProblemClick = { problemId ->
                    navController.navigate("${Destinations.PROBLEM_DETAIL}/$problemId")
                }
            )
        }
        composable(Destinations.USERS) {
            UsersScreen(
                viewModel = usersViewModel,
                onUserClick = { handle ->
                    navController.navigate("${Destinations.USER_DETAIL}/$handle")
                },
                onCompareClick = {
                    navController.navigate(Destinations.COMPARE_USERS)
                }
            )
        }
        composable(Destinations.COMPARE_USERS) {
            CompareUsersScreen(
                viewModel = usersViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("${Destinations.USER_DETAIL}/{handle}") { backStackEntry ->
            val handle = backStackEntry.arguments?.getString("handle").orEmpty()
            UserDetailScreen(
                handle = handle,
                viewModel = usersViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.BLOGS) {
            BlogsScreen(
                viewModel = blogsViewModel,
                onBlogClick = { blogId ->
                    navController.navigate("${Destinations.BLOG_DETAIL}/$blogId")
                }
            )
        }
        composable("${Destinations.BLOG_DETAIL}/{blogId}") { backStackEntry ->
            val blogId = backStackEntry.arguments?.getString("blogId")?.toIntOrNull() ?: 0
            BlogDetailScreen(
                blogId = blogId,
                viewModel = blogsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.SUBMISSIONS) {
            SubmissionsScreen(viewModel = submissionsViewModel)
        }
        composable(Destinations.PROFILE) {
            ProfileScreen(
                viewModel = profileViewModel,
                onLoginClick = onLoginClick,
                onLogout = onLogout
            )
        }
    }
}
