package ch.rmy.android.http_shortcuts.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SettingsButton(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    SettingsButton(
        title,
        subtitle,
        iconContent = { Icon(icon, contentDescription = title) },
        enabled,
        onClick,
    )
}

@Composable
fun SettingsButton(
    title: String,
    subtitle: String? = null,
    icon: Painter? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    SettingsButton(
        title,
        subtitle,
        iconContent = icon?.let { { Icon(icon, contentDescription = title) } },
        enabled,
        onClick,
    )
}

@Composable
private fun SettingsButton(
    title: String,
    subtitle: String? = null,
    iconContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    ListItem(
        leadingContent = iconContent,
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let {
            {
                Text(
                    text = it,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        colors = if (enabled) ListItemDefaults.colors() else {
            val disabledColor = ListItemDefaults.contentColor.copy(alpha = 0.38f)
            ListItemDefaults.colors(
                leadingIconColor = disabledColor,
                headlineColor = disabledColor,
                supportingColor = disabledColor,
                overlineColor = disabledColor,
                trailingIconColor = disabledColor,
            )
        },
        modifier = Modifier
            .clickable(enabled) {
                onClick()
            }
            .heightIn(min = 72.dp)
    )
}
