package com.example.roomsiswa.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomsiswaa.repositori.RepositoriSiswa
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoriSiswa: RepositoriSiswa
): ViewModel(){
    private val siswaId: Int = checkNotNull(savedStateHandle[DetailsDestination.siswaIdArg])
    val uiState: StateFlow<ItemDetailUiState> =
        repositoriSiswa.getSiswaStream(siswaId)
            .filterNotNull()
            .map {
                ItemDetailUiState(detailSiswa = it.toDetailSiswa())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ItemDetailUiState()
            )
    suspend fun deleteItem(){
        repositoriSiswa.deleteSiswa(uiState.value.detailSiswa.toSiswa())
    }

    companion object{
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ItemDetailUiState(
    val outOfStock: Boolean = true,
    val detailSiswa: DetailSiswa = DetailSiswa()
)