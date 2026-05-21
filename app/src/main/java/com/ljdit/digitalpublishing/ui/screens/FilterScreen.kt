package com.ljdit.digitalpublishing.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.model.PhotoFilters
import com.ljdit.digitalpublishing.viewmodel.PhotoViewModel

@Composable
fun FilterScreen(
    navController: NavController,
    viewModel: PhotoViewModel = viewModel()
) {

    val currentFilters by viewModel.filters.collectAsState()

    val formatos =
        remember {
            mutableStateListOf<String>().apply {
                addAll(currentFilters.formatos)
            }
        }

    val origenes =
        remember {
            mutableStateListOf<String>().apply {
                addAll(currentFilters.origenes)
            }
        }

    val contenidos =
        remember {
            mutableStateListOf<String>().apply {
                addAll(currentFilters.contenidos)
            }
        }

    var orden by remember {
        mutableStateOf(currentFilters.orden)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Filtros",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // FORMATOS
        Text(
            text = "Formato",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            "horizontal",
            "cuadrado",
            "semivertical",
            "vertical"
        ).forEach { value ->

            FilterCheckbox(
                label = value,
                checked = formatos.contains(value),
                onCheckedChange = { checked ->

                    if (checked) {

                        formatos.add(value)

                    } else {

                        formatos.remove(value)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ORIGEN
        Text(
            text = "Origen",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            "mexico",
            "colombia",
            "ecuador",
            "argentina",
            "chile",
            "paraguay",
            "uruguay"
        ).forEach { value ->

            FilterCheckbox(
                label = value,
                checked = origenes.contains(value),
                onCheckedChange = { checked ->

                    if (checked) {

                        origenes.add(value)

                    } else {

                        origenes.remove(value)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CONTENIDO
        Text(
            text = "Contenido",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            "solo",
            "en_uso"
        ).forEach { value ->

            FilterCheckbox(
                label = value,
                checked = contenidos.contains(value),
                onCheckedChange = { checked ->

                    if (checked) {

                        contenidos.add(value)

                    } else {

                        contenidos.remove(value)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ORDEN
        Text(
            text = "Orden",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {

            RadioButton(
                selected = orden == "recientes",
                onClick = {
                    orden = "recientes"
                }
            )

            Text(
                text = "Más recientes",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Row {

            RadioButton(
                selected = orden == "antiguas",
                onClick = {
                    orden = "antiguas"
                }
            )

            Text(
                text = "Más antiguas",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // BOTÓN APLICAR
        Button(
            onClick = {

                viewModel.applyFilters(

                    PhotoFilters(

                        formatos =
                        formatos.toSet(),

                        origenes =
                        origenes.toSet(),

                        contenidos =
                        contenidos.toSet(),

                        orden = orden
                    )
                )

                navController.popBackStack()
            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Aplicar filtros")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // LIMPIAR
        OutlinedButton(
            onClick = {

                viewModel.applyFilters(
                    PhotoFilters()
                )

                navController.popBackStack()
            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Limpiar filtros")
        }
    }
}

@Composable
private fun FilterCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row {

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = label,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}