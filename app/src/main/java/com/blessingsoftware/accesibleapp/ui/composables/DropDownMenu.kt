package com.blessingsoftware.accesibleapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DropDownMenu(placeType: String, modifier: Modifier, onPlaceTypeChanged: (String) -> Unit) {
    val typeList = listOf(
        "Estacionamiento",
        "Comercio",
        "Entidad Pública",
        "Restaurante",
        "Hotel",
        "Zona de Entretenimiento"
    )
    //var placeType: String by remember { mutableStateOf(typeList[0]) }
    var expanded by remember { mutableStateOf(false) }


    Row(
        modifier
            .fillMaxSize()
            .height(50.dp)
            .clickable { // Anchor view
                expanded = !expanded
            },
        verticalAlignment = Alignment.CenterVertically
    ) { // Anchor view
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = placeType,
            modifier = Modifier.fillMaxWidth(0.9f),
            MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.body1
        ) //label
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            "ArrowDropDown",
            tint = MaterialTheme.colors.secondary
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
        ) {
            typeList.forEach { type ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onPlaceTypeChanged(type)
                }) {
                    val isSelected = type == placeType
                    val style = if (isSelected) {
                        MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primaryVariant
                        )
                    } else {
                        MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                    Text(type, style = style)
                }
            }

        }
    }

}


/*
*  var expanded by remember { mutableStateOf(false) }
    val placeType = listOf("Estacionamiento", "Comercio", "Entidad Pública", "Restaurante", "Hotel", "Zona de Entretenimiento")
    var selectedType by remember { mutableStateOf("") }

    var textfieldSize by remember { mutableStateOf(Size.Zero)}

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown


    Column() {
        Text(
            text = selectedType,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textfieldSize = coordinates.size.toSize()
                }
        )
        Icon(icon,"contentDescription",
            Modifier.clickable { expanded = !expanded })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){textfieldSize.width.toDp()})
        ) {
            placeType.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedType = label
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }*/
