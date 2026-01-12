package com.example.codeforces.ui.problem

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codeforces.ui.components.CodeEditor
import com.example.codeforces.ui.components.ProblemWebView
import com.example.codeforces.utils.LanguageTemplates
import com.example.codeforces.viewmodel.EditorUiState
import com.example.codeforces.viewmodel.ProblemDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedProblemDetailScreen(
    problemId: String,
    problemState: ProblemDetailUiState,
    editorState: EditorUiState,
    onCodeChange: (String) -> Unit,
    onLanguageChange: (Int) -> Unit,
    onCustomInputChange: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showLanguageMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(problemState.problem?.name ?: "Problem")
                        problemState.problem?.let {
                            Text(
                                it.id,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Code", editorState.code))
                            
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://codeforces.com/problemset/submit")
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Submit on Codeforces")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (problemState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val splitRatio = 0.4f
                
                problemState.problem?.let { problem ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(splitRatio)) {
                        problem.contestId?.let { contestId ->
                            ProblemWebView(
                                contestId = contestId,
                                index = problem.index,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                
                Divider()
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f - splitRatio)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Language: ${editorState.languageName}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { showLanguageMenu = true },
                                colors = ButtonDefaults.outlinedButtonColors()
                            ) {
                                Icon(
                                    Icons.Default.Code,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Change")
                            }
                            
                            DropdownMenu(
                                expanded = showLanguageMenu,
                                onDismissRequest = { showLanguageMenu = false }
                            ) {
                                editorState.languages.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text(lang.name) },
                                        onClick = {
                                            onLanguageChange(lang.id)
                                            showLanguageMenu = false
                                        },
                                        leadingIcon = {
                                            if (lang.id == editorState.languageId) {
                                                Icon(Icons.Default.Check, contentDescription = null)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        CodeEditor(
                            code = editorState.code,
                            onCodeChange = onCodeChange,
                            language = LanguageTemplates.getLanguageExtension(editorState.languageId),
                            modifier = Modifier.fillMaxSize(),
                            showLineNumbers = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        "Custom Input (Optional)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    OutlinedTextField(
                        value = editorState.customInput,
                        onValueChange = onCustomInputChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(horizontal = 16.dp),
                        placeholder = { Text("Enter custom input for testing...") },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SampleTestDialog(
    problemTests: List<com.example.codeforces.data.model.TestCase>,
    customTestInput: String,
    customTestOutput: String,
    onCustomInputChange: (String) -> Unit,
    onCustomOutputChange: (String) -> Unit,
    onRunTest: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sample Tests") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (problemTests.isNotEmpty()) {
                    Text(
                        "Problem Sample Tests",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    problemTests.forEach { test ->
                        SampleTestCard(
                            input = test.input,
                            expectedOutput = test.expectedOutput,
                            onRun = { onRunTest(test.input) }
                        )
                    }
                }
                
                Divider()
                
                Text(
                    "Add Custom Test",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = customTestInput,
                    onValueChange = onCustomInputChange,
                    label = { Text("Input") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                )
                
                OutlinedTextField(
                    value = customTestOutput,
                    onValueChange = onCustomOutputChange,
                    label = { Text("Expected Output") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                )
                
                Button(
                    onClick = { onRunTest(customTestInput) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = customTestInput.isNotBlank()
                ) {
                    Text("Run Custom Test")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun SampleTestCard(
    input: String,
    expectedOutput: String,
    onRun: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Input:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                input,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Text(
                "Expected Output:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                expectedOutput,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Button(
                onClick = onRun,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Run This Test")
            }
        }
    }
}
