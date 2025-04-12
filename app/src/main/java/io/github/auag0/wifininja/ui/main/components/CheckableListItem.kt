package io.github.auag0.wifininja.ui.main.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CheckableListItem(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    supportingText: String? = null,
    supportingTextColor: Color = Color.Unspecified
) {
    ListItem(
        modifier = Modifier.clickable {
            onCheckedChange(!isChecked)
        },
        colors = ListItemDefaults.colors(
            containerColor = AlertDialogDefaults.containerColor
        ),
        leadingContent = {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        },
        headlineContent = {
            Text(text = text)
        },
        supportingContent = supportingText?.let {
            {
                Text(
                    text = it,
                    color = supportingTextColor
                )
            }
        }
    )
}