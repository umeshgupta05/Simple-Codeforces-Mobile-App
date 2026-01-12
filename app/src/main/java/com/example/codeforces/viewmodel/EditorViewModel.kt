package com.example.codeforces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeforces.data.local.EditorSession
import com.example.codeforces.data.local.EditorSessionDao
import com.example.codeforces.data.model.LanguageTemplate
import com.example.codeforces.utils.LanguageTemplates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EditorUiState(
    val code: String = "",
    val languageId: Int = 54,
    val languageName: String = "C++",
    val customInput: String = "",
    val languages: List<LanguageTemplate> = LanguageTemplates.templates,
    val lastSaved: Long? = null,
    val isLoading: Boolean = false
)

class EditorViewModel(
    private val editorSessionDao: EditorSessionDao
) : ViewModel() {

    private val _state = MutableStateFlow(EditorUiState())
    val state: StateFlow<EditorUiState> = _state

    fun load(problemId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val session = editorSessionDao.getSession(problemId)
            if (session != null) {
                _state.value = EditorUiState(
                    code = session.code,
                    languageId = session.languageId,
                    languageName = session.languageName,
                    customInput = session.customInput,
                    languages = LanguageTemplates.templates,
                    lastSaved = session.lastModified,
                    isLoading = false
                )
            } else {
                val template = LanguageTemplates.templates.first()
                _state.value = EditorUiState(
                    code = template.boilerplate,
                    languageId = template.id,
                    languageName = template.name,
                    languages = LanguageTemplates.templates,
                    isLoading = false
                )
            }
        }
    }

    fun onCodeChange(newCode: String, problemId: String) {
        _state.value = _state.value.copy(code = newCode)
        persist(problemId)
    }

    fun onCustomInputChange(newInput: String, problemId: String) {
        _state.value = _state.value.copy(customInput = newInput)
        persist(problemId)
    }

    fun onLanguageChange(languageId: Int, problemId: String) {
        val template = LanguageTemplates.getTemplate(languageId)
        _state.value = _state.value.copy(
            languageId = languageId,
            languageName = template.name,
            code = template.boilerplate
        )
        persist(problemId)
    }

    private fun persist(problemId: String) {
        viewModelScope.launch {
            val session = EditorSession(
                problemId = problemId,
                code = _state.value.code,
                languageId = _state.value.languageId,
                languageName = _state.value.languageName,
                customInput = _state.value.customInput,
                lastModified = System.currentTimeMillis()
            )
            editorSessionDao.saveSession(session)
            _state.value = _state.value.copy(lastSaved = session.lastModified)
        }
    }
}


