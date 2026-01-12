package com.example.codeforces.ui.editor

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.codeforces.viewmodel.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    problemId: String,
    editorViewModel: EditorViewModel,
    onBack: () -> Unit
) {
    val uiState by editorViewModel.state.collectAsState()
    val (showMenu, setShowMenu) = remember { mutableStateOf(false) }
    val codeScrollState = rememberScrollState()

    LaunchedEffect(problemId) { editorViewModel.load(problemId) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Editor: $problemId") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Language")
            Button(onClick = { setShowMenu(true) }) {
                Text(uiState.languages.firstOrNull { it.id == uiState.languageId }?.name ?: "Select")
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { setShowMenu(false) }) {
                uiState.languages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language.name) },
                        onClick = {
                            editorViewModel.onLanguageChange(language.id, problemId)
                            setShowMenu(false)
                        }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = uiState.code,
                    onValueChange = { editorViewModel.onCodeChange(it, problemId) },
                    modifier = Modifier.fillMaxSize(),
                    label = { Text("Code") },
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                    maxLines = Int.MAX_VALUE,
                    minLines = 10
                )
            }
        }
    }
}

