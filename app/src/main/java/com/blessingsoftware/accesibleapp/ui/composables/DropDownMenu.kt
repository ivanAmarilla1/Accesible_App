package com.blessingsoftware.accesibleapp.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.blessingsoftware.accesibleapp.model.domain.PlaceTypes

@Composable
fun DropDownMenu(
    selectedItem: String,
    list: List<String>,
    validateData: Boolean?,
    validateDataError: String,
    modifier: Modifier,
    onSelectedItemChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier
            .fillMaxSize()
            .height(50.dp)
            .border(if (validateData!!) BorderStroke(2.dp, MaterialTheme.colors.error) else BorderStroke(2.dp, MaterialTheme.colors.onSecondary), shape = RoundedCornerShape(10.dp))
            .clickable { // Anchor view
                expanded = !expanded
            },
        verticalAlignment = Alignment.CenterVertically
    ) { // Anchor view
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Row {
                Text(
                    text = selectedItem,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.body1
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    "ArrowDropDown",
                    tint = MaterialTheme.colors.secondary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .background(MaterialTheme.colors.background)
        ) {
            list.forEach { item ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onSelectedItemChanged(item)
                }) {
                    val isSelected = item == selectedItem
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
                    Text(item, style = style)
                }
            }

        }
    }
    Spacer(modifier = Modifier.height(10.dp))
    if (validateData) {
        Text(
            text = validateDataError,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .padding(start = 8.dp)
                .offset(y = (-8).dp)
                .fillMaxWidth(0.9f)
        )
    }

}

