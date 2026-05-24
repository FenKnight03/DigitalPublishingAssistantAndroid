package com.ljdit.digitalpublishing.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ljdit.digitalpublishing.core.session.SessionManager
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

    // usuario actual
    private var currentUserDistributorId: Int? = null
    private var isAdmin: Boolean = false

    fun setCurrentUser(
        distributorId: Int?,
        admin: Boolean
    ) {
        currentUserDistributorId = distributorId
        isAdmin = admin
    }

    // 🔹 Distribuidores
    private val _distributors =
        MutableStateFlow<List<Distributor>>(emptyList())

    val distributors: StateFlow<List<Distributor>> =
        _distributors

    // 🔹 Distribuidor seleccionado
    private val _selectedLogoId =
        MutableStateFlow<Int?>(null)

    val selectedLogoId: StateFlow<Int?> =
        _selectedLogoId

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

                    val allDistributors =
                        response.body() ?: emptyList()

                    _distributors.value =
                        if (SessionManager.isAdmin) {

                            allDistributors

                        } else {

                            allDistributors.filter {
                                it.id == SessionManager.distributorId
                            }
                        }
                }

            } catch (e: Exception) {

                _errorMessage.value =
                    "No se pudieron cargar distribuidores"
            }

            _isLoading.value = false
        }
    }

    // Seleccionar logo
    fun selectLogo(logoId: Int) {

        _selectedLogoId.value =
            logoId

        _preview.value = null
    }

    // Seleccionar coordenada
    fun selectCoordinate(coordinateId: Int) {

        _selectedCoordinate.value =
            coordinateId

        _preview.value = null
    }

    // Aplicar cambios
    fun applyFusion() {

        val logoId =
            _selectedLogoId.value ?: return

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
                            logo_id = logoId,
                            coordenada = coordinateId
                        )
                    )

                Log.d(
                    "PhotoViewer",
                    "Fusion preview code=${response.code()} body=${response.body()}"
                )

                if (
                    response.isSuccessful &&
                    response.body()?.ok == true
                ) {

                    _preview.value = response.body()
                } else {
                    val errorText =
                        try {
                            response.errorBody()?.string()
                        } catch (e: Exception) {
                            "No se pudo leer error: ${e.message}"
                        }

                    Log.e(
                        "PhotoViewer",
                        "Fusion preview error=$errorText"
                    )

                    _errorMessage.value =
                        errorText ?: "Error generando preview"
                }

            } catch (e: Exception) {

                Log.e("PhotoViewer", "Error generando preview", e)

                _errorMessage.value =
                    "Error generando preview"
            }

            _isLoading.value = false
        }
    }
}
