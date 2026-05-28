package com.ljdit.digitalpublishing.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ljdit.digitalpublishing.data.repository.PhotoRepository
import com.ljdit.digitalpublishing.model.Photo
import com.ljdit.digitalpublishing.model.PhotoFilters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {

    private val repository = PhotoRepository()

    private val _allPhotos =
        MutableStateFlow<List<Photo>>(emptyList())

    val allPhotos: StateFlow<List<Photo>>
        = _allPhotos

    private val _photos =
        MutableStateFlow<List<Photo>>(emptyList())

    val photos: StateFlow<List<Photo>>
        = _photos

    private val _filters =
        MutableStateFlow(PhotoFilters())

    val filters: StateFlow<PhotoFilters>
        = _filters

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean>
        = _isLoading

    private var hasLoadedPhotos = false

    fun loadPhotos(forceRefresh: Boolean = false) {

        if (_isLoading.value) {
            return
        }

        if (!forceRefresh && hasLoadedPhotos) {
            applyFilters(_filters.value)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val result = mutableListOf<Photo>()
                var page = 1
                var hasNextPage = true
                var failed = false

                while (hasNextPage) {
                    val response = repository.getPhotos(
                        page = page,
                        pageSize = PHOTO_PAGE_SIZE
                    )

                    if (!response.isSuccessful) {
                        Log.e("PhotoViewModel", "Error cargando fotos: ${response.code()}")
                        failed = true
                        break
                    }

                    val body = response.body()
                    result += body?.results.orEmpty()
                    hasNextPage = !body?.next.isNullOrBlank()
                    page++
                }

                if (!failed) {
                    _allPhotos.value = result
                    hasLoadedPhotos = true

                    applyFilters(_filters.value)
                }
            } catch (e: Exception) {
                Log.e("PhotoViewModel", "Error cargando fotos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPhotoById(photoId: Int): Photo? {
        return _photos.value.find { it.id == photoId }
    }

    fun applyFilters(filters: PhotoFilters) {

        _filters.value = filters

        var filtered =
            _allPhotos.value

        // FORMATO
        if (filters.formatos.isNotEmpty()) {

            filtered = filtered.filter {

                filters.formatos.contains(
                    it.formato?.lowercase()
                )
            }
        }

        // ORIGEN
        if (filters.origenes.isNotEmpty()) {

            filtered = filtered.filter {

                filters.origenes.contains(
                    it.origen?.lowercase()
                )
            }
        }

        // CONTENIDO
        if (filters.contenidos.isNotEmpty()) {

            filtered = filtered.filter { photo ->

                filters.contenidos.any { content ->

                    when (content) {

                        "solo" ->
                            photo.en_uso == false

                        "en_uso" ->
                            photo.en_uso == true

                        else -> false
                    }
                }
            }
        }

        // BUSQUEDA
        if (filters.busqueda.isNotBlank()) {

            val query =
                filters.busqueda
                    .trim()
                    .lowercase()

            filtered = filtered.filter { photo ->

                photo.producto
                    ?.lowercase()
                    ?.contains(query)
                    ?: false
            }
        }

        // ORDEN
        filtered =
            when (filters.orden) {

                "antiguas" ->

                    filtered.sortedBy {
                        it.fecha_carga
                    }

                else ->

                    filtered.sortedByDescending {
                        it.fecha_carga
                    }
            }

        _photos.value = filtered
    }

    private val _searchSuggestions =
        MutableStateFlow<List<String>>(emptyList())

    val searchSuggestions: StateFlow<List<String>>
        = _searchSuggestions

    fun updateSearchSuggestions(query: String) {

        if (query.isBlank()) {

            _searchSuggestions.value = emptyList()
            return
        }

        val normalized =
            query.trim().lowercase()

        _searchSuggestions.value =

            _allPhotos.value
                .mapNotNull { it.producto }
                .distinct()
                .filter {

                    it.lowercase()
                        .contains(normalized)
                }
                .sorted()
                .take(10)
    }

    private companion object {
        const val PHOTO_PAGE_SIZE = 100
    }
}
