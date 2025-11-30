package com.luna.prosync.ui.screens.project_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.prosync.data.remote.dto.BoardDto
import com.luna.prosync.data.repository.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    private val projectId: Int = checkNotNull(savedStateHandle["projectId"])

    init {
        _uiState.update { it.copy(projectId = projectId) }
        initializeProjectBoard()
    }

    fun initializeProjectBoard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val boards = boardRepository.getBoards(projectId)

                if (boards.isNotEmpty()) {
                    selectBoard(boards[0])
                } else {
                    val newBoard = boardRepository.createBoard(projectId, "Tablero general")
                    selectBoard(newBoard)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al cargar el proyecto: ${e.message}")
                }
            }
        }
    }

    fun selectBoard(board: BoardDto) {
        _uiState.update { it.copy( selectedBoard = board) }
        loadTasks(board.id)
    }

    private fun loadTasks(boardId: Int) {
        viewModelScope.launch {
            try {
                val tasks = boardRepository.getTasks(boardId)
                _uiState.update { it.copy(tasks = tasks, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar tareas") }
            }
        }
    }


}