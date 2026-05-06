package com.ljdit.digitalpublishing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ljdit.digitalpublishing.data.repository.PhotoRepository
import com.ljdit.digitalpublishing.model.Distributor
import com.ljdit.digitalpublishing.model.FusionPreviewRequest
import com.ljdit.digitalpublishing.model.FusionPreviewResponse
import com.ljdit.digitalpublishing.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoViewerViewModel : ViewModel() {

    private val repository = PhotoRepository()

    private lateinit var currentPhoto: Photo

    // 🔹 Distribuidores
    private val _distributors =
        MutableStateFlow<List<Distributor>>(emptyList())

    val distributors: StateFlow<List<Distributor>> =
        _distributors

    // 🔹 Distribuidor seleccionado
    private val _selectedDistributorId =
        MutableStateFlow<Int?>(null)

    val selectedDistributorId: StateFlow<Int?> =
        _selectedDistributorId

    // 🔹 Coordenada seleccionada
    private val _selectedCoordinate =
        MutableStateFlow<Int?>(null)

    val selectedCoordinate: StateFlow<Int?> =
        _selectedCoordinate

    // 🔹 Preview
    private val _preview =
        MutableStateFlow<FusionPreviewResponse?>(null)

    val preview: StateFlow<FusionPreviewResponse?> =
        _preview

    // 🔹 Loading
    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading

    // 🔹 Error
    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage

    fun setPhoto(photo: Photo) {
        this.currentPhoto = photo
    }

    // Cargar distribuidores
    fun loadDistributors() {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val response =
                    repository.getDistributors()

                if (response.isSuccessful) {

                    _distributors.value =
                        response.body() ?: emptyList()
                }

            } catch (e: Exception) {

                _errorMessage.value =
                    "No se pudieron cargar distribuidores"
            }

            _isLoading.value = false
        }
    }

    // Seleccionar distribuidor
    fun selectDistributor(distributorId: Int) {

        _selectedDistributorId.value =
            distributorId
    }

    // Seleccionar coordenada
    fun selectCoordinate(coordinateId: Int) {

        _selectedCoordinate.value =
            coordinateId
    }

    // Aplicar cambios
    fun applyFusion() {

        val distributorId =
            _selectedDistributorId.value ?: return

        val coordinateId =
            _selectedCoordinate.value ?: return

        viewModelScope.launch {

            _isLoading.value = true

            _preview.value = null

            try {

                val response =
                    repository.createFusionPreview(
                        currentPhoto.id,
                        FusionPreviewRequest(
                            logo_id = distributorId,
                            coordenada = coordinateId
                        )
                    )

                if (
                    response.isSuccessful &&
                    response.body()?.ok == true
                ) {

                    _preview.value = response.body()
                }

            } catch (e: Exception) {

                _errorMessage.value =
                    "Error generando preview"
            }

            _isLoading.value = false
        }
    }
}