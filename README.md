# Codeforces Android App

A native Android application for browsing and solving Codeforces problems with an integrated code editor, persistent session management, and comprehensive problem details.

## Features

### 📚 Core Features

- **Problem Browsing**: Browse the entire Codeforces problem set with search and filtering
- **Contest Viewing**: Explore active and past contests with problem details
- **User Profiles**: View user profiles, ratings, and submission history
- **Submissions**: Track recent submissions across the platform
- **Blogs**: Browse community blogs and recent activities
- **Gym Problems**: Access gym problems for practice

### 💻 Code Editor

- **Native Code Editor**: Write code directly in the app with syntax highlighting
- **Multi-Language Support**: C++, Java, Python, C#, Go, Rust, JavaScript, TypeScript, PHP, and more
- **Line Numbers**: Visual line numbering for better code organization
- **Persistent Storage**: Automatically save editor sessions to local database
- **Language Templates**: Quick start with language-specific boilerplate code
- **Custom Input**: Test code with custom input/output

### 📝 Problem Details

- **WebView Display**: View complete problem statements with HTML rendering
- **Sample Tests**: View provided test cases with inputs and expected outputs
- **Split Layout**: Side-by-side view of problem statement and code editor
- **Copy to Clipboard**: Quick copy functionality for code and outputs

### 💾 Data Persistence

- **Room Database**: Persistent storage of:
  - Editor sessions (code, language, custom input)
  - Test cases
  - Editor history and metadata
- **Automatic Sync**: Changes saved automatically as you type

### 🎨 Design

- **Material 3 Design**: Modern, responsive UI with dark mode support
- **Jetpack Compose**: 100% declarative UI with reactive state management
- **Adaptive Layouts**: Optimized for phones and tablets

## Technical Stack

### Architecture

- **MVVM Pattern**: Clean separation of concerns with ViewModels
- **Repository Pattern**: Abstracted data access layer
- **Coroutines**: Asynchronous operations with `viewModelScope`

### Technologies

- **Jetpack Compose**: Modern UI toolkit
- **Room Database**: Local data persistence
- **Retrofit 2**: Type-safe HTTP client
- **OkHttp 3**: HTTP client with logging and timeouts
- **Moshi**: JSON serialization
- **Material 3**: Material Design components
- **Android WebKit**: HTML rendering for problem statements

### Dependencies

- `androidx.compose:compose-bom:2024.04.00`
- `androidx.room:room-runtime:2.6.0`
- `com.squareup.retrofit2:retrofit:2.10.0`
- `com.squareup.okhttp3:okhttp:4.11.0`
- `com.squareup.moshi:moshi-kotlin:1.15.1`

## Project Structure

```
app/
├── src/main/java/com/example/codeforces/
│   ├── data/
│   │   ├── api/              # API service definitions
│   │   │   ├── CodeforcesApiService.kt
│   │   │   ├── BackendApiService.kt
│   │   │   └── NetworkClient.kt
│   │   ├── model/            # Data classes
│   │   ├── local/            # Room database
│   │   │   ├── AppDatabase.kt
│   │   │   ├── EditorSession.kt
│   │   │   └── EditorSessionDao.kt
│   │   └── repository/       # Repository implementations
│   │
│   ├── ui/
│   │   ├── components/       # Reusable UI components
│   │   │   ├── CodeEditor.kt
│   │   │   ├── ProblemWebView.kt
│   │   │   └── ...
│   │   ├── home/             # Home screen
│   │   ├── problem/          # Problem detail screens
│   │   ├── contests/         # Contest screens
│   │   ├── users/            # User screens
│   │   ├── blogs/            # Blog screens
│   │   ├── submissions/      # Submissions screen
│   │   ├── editor/           # Editor screen
│   │   ├── navigation/       # Navigation setup
│   │   └── ...
│   │
│   ├── viewmodel/            # ViewModels for state management
│   │
│   ├── utils/
│   │   ├── LanguageTemplates.kt  # Code templates
│   │   ├── PreferencesManager.kt # User preferences
│   │   └── Resource.kt           # Data wrapper
│   │
│   ├── CodeforcesApplication.kt  # App entry point
│   └── AndroidManifest.xml
│
├── build.gradle.kts          # App-level build configuration
└── proguard-rules.pro        # ProGuard/R8 rules
```

## Getting Started

### Prerequisites

- Android Studio Flamingo or newer
- JDK 17 or higher
- Gradle 8.2 or higher
- Android SDK 28+ (API level 28 minimum)

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/yourusername/codeforces-android.git
   cd codeforces-android
   ```

2. **Open in Android Studio**

   - File → Open → Select the project directory
   - Wait for Gradle sync to complete

3. **Configure local.properties** (if needed)

   ```properties
   sdk.dir=/path/to/android/sdk
   ```

4. **Build and run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio: Run → Run 'app'

## Usage

### Browsing Problems

1. Open the app and navigate to "Problems" tab
2. Browse the list of problems by difficulty and topic
3. Tap on a problem to view details

### Solving Problems

1. Open a problem detail screen
2. Tap "Change" to select your preferred programming language
3. Write your code in the editor
4. Your code is automatically saved to the local database
5. Switch problems and return later - your code will still be there

### Viewing Problem Details

- **Left Panel**: Complete problem statement with HTML rendering
- **Right Panel**: Code editor, language selector, and input area

### Language Templates

1. Open the code editor
2. Change language to auto-populate boilerplate code
3. Edit and customize as needed

### Managing Sessions

- Editor sessions are automatically persisted per problem
- Sessions survive app restart and configuration changes
- Clear app data to reset all saved sessions

## API Integration

The app uses the [Codeforces API](https://codeforces.com/apiHelp) for:

- Problem details and statements
- Contest information
- User profiles and ratings
- Submission history
- Recent blog posts

**Network Configuration**:

- Base URL: `https://codeforces.com/api/`
- Timeout: 30 seconds (connect, read, write)
- Logging: BASIC level for debugging

## Database Schema

### EditorSession Table

```
- problemId: String (Primary Key)
- code: String
- languageId: Int
- customInput: String
- lastModified: Long
```

### CustomTestCase Table

```
- id: Int (Primary Key, auto-increment)
- problemId: String
- input: String
- expectedOutput: String
```

## Building and Deployment

### Debug Build

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

```bash
./gradlew assembleRelease
```

### Signed Release (requires keystore)

```bash
./gradlew assembleRelease -Pandroid.injected.signing.store.file=/path/to/keystore \
    -Pandroid.injected.signing.store.password=password \
    -Pandroid.injected.signing.key.alias=alias \
    -Pandroid.injected.signing.key.password=password
```

## Architecture Patterns

### MVVM with Jetpack Compose

```
UI (Composables)
    ↓
ViewModel (State Management)
    ↓
Repository (Data Access)
    ↓
Data Sources (API, Database)
```

### State Management

- **StateFlow**: Reactive state container for ViewModels
- **Composable Recomposition**: Automatic UI updates on state changes
- **LaunchedEffect**: Side effects in composition lifecycle

### Database Operations

- **Room DAO**: Type-safe database access
- **Flow Integration**: Reactive database queries
- **Coroutines**: Async database operations

## Performance Considerations

- **Lazy Loading**: Problems loaded on-demand with pagination
- **Connection Pooling**: OkHttp connection reuse
- **Caching**: API responses cached where appropriate
- **Efficient Composition**: Minimal recompositions in Compose
- **Memory Management**: Proper coroutine cancellation with viewModelScope

## Known Limitations

- **No Code Execution**: Code cannot be run locally (backend not configured)
- **Read-Only**: Direct submission not implemented (use Codeforces website)
- **Offline Mode**: Limited functionality without internet connection
- **API Rate Limiting**: Subject to Codeforces API rate limits

## Future Enhancements

- [ ] Local code execution with multiple language compilers
- [ ] Offline problem caching
- [ ] Code submission directly from app
- [ ] Contest notifications
- [ ] Friend list and follow features
- [ ] Problem filtering and bookmarks
- [ ] Dark theme optimization
- [ ] Unit and integration tests

## Troubleshooting

### Network Timeout Issues

- Check internet connection
- Verify Codeforces API is accessible
- Timeout is set to 30 seconds - may be slow on poor connections

### Build Failures

```bash
# Clean and rebuild
./gradlew clean
./gradlew build

# Check for SDK version issues
./gradlew --version
```

### Database Issues

- Clear app data from Settings → Apps → Codeforces → Storage → Clear Data
- Reinstall app to reset database

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is open source and available under the MIT License. See LICENSE file for details.

## Acknowledgments

- [Codeforces](https://codeforces.com) - Online programming platform
- [Codeforces API](https://codeforces.com/apiHelp) - Public API documentation
- [Android Jetpack](https://developer.android.com/jetpack) - Android development libraries
- [Material Design 3](https://m3.material.io/) - Design system

## Contact & Support

For questions, issues, or suggestions:

- Open an issue on GitHub
- Check existing issues for solutions
- Review the project documentation

## Version History

### v1.0.0

- Initial release
- Problem browsing and viewing
- Code editor with syntax highlighting
- Session persistence with Room database
- Multi-language support
- Contest and user profile viewing

---

**Happy Coding on Codeforces! 🚀**
