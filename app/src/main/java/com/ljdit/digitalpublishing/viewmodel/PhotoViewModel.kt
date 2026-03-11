package com.ljdit.digitalpublishing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ljdit.digitalpublishing.data.repository.PhotoRepository
import com.ljdit.digitalpublishing.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {

    private val repository = PhotoRepository()

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos

    init {
        loadPhotos()
    }

    private fun loadPhotos() {

        viewModelScope.launch {

            val response = repository.getPhotos()

            if (response.isSuccessful) {
                _photos.value = response.body() ?: emptyList()
            }

        }
    }
}