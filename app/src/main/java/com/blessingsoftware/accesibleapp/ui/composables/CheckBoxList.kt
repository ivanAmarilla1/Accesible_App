package com.blessingsoftware.accesibleapp.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


data class Option( // 1
    var checked: Boolean,
    var onCheckedChange: (Boolean) -> Unit = {},
    val label: String,
    var enabled: Boolean = true
)


@Composable
fun CheckboxList(
    options: List<Option>,
    listTitle: String,
    validateCheckbox: Boolean?,
    validateCheckboxError: String,
): List<String> { // 2
    val checkedList: MutableList<String> = arrayListOf()
    Column(
        modifier = Modifier
    ) { // 3
        Text(listTitle, textAlign = TextAlign.Justify, color = MaterialTheme.colors.secondary, fontWeight = FontWeight.Bold) // 4
        Spacer(Modifier.size(16.dp)) // 5
        options.forEach { option -> // 6
            LabelledCheckbox( // 7
                checked = option.checked,
                onCheckedChange = option.onCheckedChange,
                label = option.label,
                enabled = option.enabled
            )
        }
    }
    if (validateCheckbox == false) {
        Text(
            text = validateCheckboxError,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .padding(start = 8.dp)
                .offset(y = (-8).dp)
                .fillMaxWidth(0.9f)
        )
    }
    options.forEach {
        if (it.checked) {
            checkedList.add(it.label)
        }
    }
    return checkedList
}


@Composable
fun LabelledCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,

    ) {

    val colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colors.secondary,
        uncheckedColor = MaterialTheme.colors.secondary.copy(alpha = 0.6f),
        checkmarkColor = MaterialTheme.colors.background,
        disabledColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
        disabledIndeterminateColor = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.disabled)
    )

    Row(
        modifier = modifier.height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = colors
        )
        Spacer(Modifier.width(5.dp))
        Text(label, color = MaterialTheme.colors.secondary)
    }
}






