package com.ljdit.digitalpublishing.viewmodel

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

    private val _photos =
        MutableStateFlow<List<Photo>>(emptyList())

    val photos: StateFlow<List<Photo>>
        = _photos

    private val _filters =
        MutableStateFlow(PhotoFilters())

    val filters: StateFlow<PhotoFilters>
        = _filters

    fun loadPhotos() {

        viewModelScope.launch {

            val response = repository.getPhotos()

            println("RAW RESPONSE -> ${response.body()}")

            if (response.isSuccessful) {

                val result =
                    response.body()?.results ?: emptyList()

                result.forEach {

                    println("PHOTO WIDTH -> ${it.width}")
                    println("PHOTO HEIGHT -> ${it.height}")
                    println("PHOTO COORDS -> ${it.coordinates}")
                }

                _allPhotos.value = result

                applyFilters(_filters.value)
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
}