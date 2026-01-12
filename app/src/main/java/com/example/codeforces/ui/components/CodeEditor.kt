package com.example.codeforces.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CodeEditor(
    code: String,
    onCodeChange: (String) -> Unit,
    language: String = "cpp",
    modifier: Modifier = Modifier,
    showLineNumbers: Boolean = true
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(code))
    }

    LaunchedEffect(code) {
        if (textFieldValue.text != code) {
            textFieldValue = TextFieldValue(code)
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val lineNumberColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(modifier = modifier.background(backgroundColor)) {
        if (showLineNumbers) {
            LineNumbers(
                lineCount = textFieldValue.text.count { it == '\n' } + 1,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                color = lineNumberColor
            )
        }

        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                onCodeChange(newValue.text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState()),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = textColor,
                lineHeight = 20.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box {
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = "Write your code here...",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun LineNumbers(
    lineCount: Int,
    modifier: Modifier = Modifier,
    color: Color
) {
    Column(modifier = modifier) {
        for (i in 1..lineCount) {
            Text(
                text = i.toString(),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = color,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

object SyntaxHighlighter {
    private val cppKeywords = setOf(
        "auto", "break", "case", "char", "const", "continue", "default", "do",
        "double", "else", "enum", "extern", "float", "for", "goto", "if",
        "int", "long", "register", "return", "short", "signed", "sizeof", "static",
        "struct", "switch", "typedef", "union", "unsigned", "void", "volatile", "while",
        "class", "namespace", "template", "typename", "public", "private", "protected",
        "virtual", "bool", "true", "false", "nullptr", "this", "new", "delete",
        "using", "include", "define", "ifdef", "ifndef", "endif"
    )
    
    private val javaKeywords = setOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
        "class", "const", "continue", "default", "do", "double", "else", "enum",
        "extends", "final", "finally", "float", "for", "goto", "if", "implements",
        "import", "instanceof", "int", "interface", "long", "native", "new", "package",
        "private", "protected", "public", "return", "short", "static", "strictfp", "super",
        "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
        "volatile", "while", "true", "false", "null"
    )
    
    private val pythonKeywords = setOf(
        "and", "as", "assert", "break", "class", "continue", "def", "del", "elif",
        "else", "except", "False", "finally", "for", "from", "global", "if", "import",
        "in", "is", "lambda", "None", "nonlocal", "not", "or", "pass", "raise",
        "return", "True", "try", "while", "with", "yield", "async", "await"
    )
    
    fun highlight(code: String, language: String, isDark: Boolean): AnnotatedString {
        val keywords = when (language.lowercase()) {
            "cpp", "c++" -> cppKeywords
            "java" -> javaKeywords
            "python", "py" -> pythonKeywords
            else -> emptySet()
        }
        
        val keywordColor = if (isDark) Color(0xFF569CD6) else Color(0xFF0000FF)
        val stringColor = if (isDark) Color(0xFFCE9178) else Color(0xFFA31515)
        val commentColor = if (isDark) Color(0xFF6A9955) else Color(0xFF008000)
        val numberColor = if (isDark) Color(0xFFB5CEA8) else Color(0xFF098658)
        
        return buildAnnotatedString {
            val lines = code.split("\n")
            lines.forEachIndexed { lineIndex, line ->
                if (lineIndex > 0) append("\n")
                highlightLine(line, keywords, keywordColor, stringColor, commentColor, numberColor)
            }
        }
    }
    
    private fun AnnotatedString.Builder.highlightLine(
        line: String,
        keywords: Set<String>,
        keywordColor: Color,
        stringColor: Color,
        commentColor: Color,
        numberColor: Color
    ) {
        var i = 0
        while (i < line.length) {
            when {
                line.substring(i).startsWith("//") -> {
                    withStyle(SpanStyle(color = commentColor)) {
                        append(line.substring(i))
                    }
                    break
                }
                line.substring(i).startsWith("/*") -> {
                    val end = line.indexOf("*/", i)
                    val commentEnd = if (end != -1) end + 2 else line.length
                    withStyle(SpanStyle(color = commentColor)) {
                        append(line.substring(i, commentEnd))
                    }
                    i = commentEnd
                }
                line.substring(i).startsWith("#") -> {
                    withStyle(SpanStyle(color = commentColor)) {
                        append(line.substring(i))
                    }
                    break
                }
                line[i] == '"' || line[i] == '\'' -> {
                    val quote = line[i]
                    var j = i + 1
                    while (j < line.length && line[j] != quote) {
                        if (line[j] == '\\') j++
                        j++
                    }
                    if (j < line.length) j++
                    withStyle(SpanStyle(color = stringColor)) {
                        append(line.substring(i, j))
                    }
                    i = j
                }
                line[i].isDigit() -> {
                    var j = i
                    while (j < line.length && (line[j].isDigit() || line[j] == '.')) j++
                    withStyle(SpanStyle(color = numberColor)) {
                        append(line.substring(i, j))
                    }
                    i = j
                }
                line[i].isLetter() || line[i] == '_' -> {
                    var j = i
                    while (j < line.length && (line[j].isLetterOrDigit() || line[j] == '_')) j++
                    val word = line.substring(i, j)
                    if (word in keywords) {
                        withStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold)) {
                            append(word)
                        }
                    } else {
                        append(word)
                    }
                    i = j
                }
                else -> {
                    append(line[i])
                    i++
                }
            }
        }
    }
}
