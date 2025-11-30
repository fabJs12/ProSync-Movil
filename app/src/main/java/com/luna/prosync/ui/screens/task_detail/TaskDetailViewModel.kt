package com.luna.prosync.ui.screens.task_detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.prosync.data.repository.AuthRepository
import com.luna.prosync.data.repository.BoardRepository
import com.luna.prosync.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    private val taskId: Int = checkNotNull(savedStateHandle["taskId"])
    private val projectId: Int = savedStateHandle["projectId"] ?: 0


    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val task = boardRepository.getTaskById(taskId)
                val members = try { boardRepository.getProjectMembers(projectId) } catch (e: Exception) { emptyList() }

                val comments = taskRepository.getComments(taskId)
                val profile = authRepository.getProfile()

                val currentUserProject = members.find { it.usuario.id == profile.id }
                val isLeader = currentUserProject?.rol?.rol?.equals("LIDER", ignoreCase = true) == true

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        task = task,
                        members = members,
                        comments = comments,
                        currentUserId = profile.id,
                        isLeader = isLeader
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar tarea: ${e.message}") }
            }
        }
    }

    fun saveTaskChanges(title: String, description: String, statusId: Int, dueDate: String?, responsableId: Int?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val finalDate = if (dueDate == "Sin Fecha" || dueDate.isNullOrBlank()) null else dueDate
                boardRepository.updateTask(taskId, title, description, statusId, finalDate, responsableId)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al guardar: ${e.message}") }
            }
        }
    }

    fun addComment(contenido: String) {
        val userId = _uiState.value.currentUserId
        if (userId == null) {
            _uiState.update { it.copy(error = "No se pudo identificar al usuario") }
            return
        }

        viewModelScope.launch {
            try {
                val newComment = taskRepository.addComment(taskId, userId, contenido)
                _uiState.update { currentState ->
                    val updatedList = currentState.comments + newComment

                    currentState.copy(comments = updatedList)
                }
            } catch (e: Exception) {
                Log.e("TaskDetailVM", "Error al comentar", e)
            }
        }
    }

    fun onNavigationDone() {
        _uiState.update { it.copy(isSaved = false) }
    }

}