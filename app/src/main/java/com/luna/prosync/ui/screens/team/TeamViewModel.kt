package com.luna.prosync.ui.screens.team

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.prosync.data.repository.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(TeamUiState())
    val uiState: StateFlow<TeamUiState> = _uiState.asStateFlow()

    private val projectId: Int = checkNotNull(savedStateHandle["projectId"])

    init {
        loadMembers()
    }

    fun loadMembers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val members = boardRepository.getProjectMembers(projectId)
                _uiState.update { it.copy(isLoading = false, members = members) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar equipo: ${e.message}") }
            }
        }
    }

    fun addMember(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(error = "Escribe un correo válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                boardRepository.addMemberByEmail(projectId, email)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                loadMembers()
            } catch (e: retrofit2.HttpException) {
                val errorMsg = when(e.code()) {
                    404 -> "Usuario no encontrado"
                    403 -> "No tienes permisos de Líder"
                    else -> "Error: ${e.message()}"
                }
                _uiState.update { it.copy(isLoading = false, error = errorMsg) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }

    fun changeRole(userId: Int, roleId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                boardRepository.changeMemberRole(userId, projectId, roleId)
                loadMembers()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cambiar rol: ${e.message}") }
            }
        }
    }

    fun onSuccessMessageShown() {
        _uiState.update { it.copy(isSuccess = false, error = null) }
    }
}