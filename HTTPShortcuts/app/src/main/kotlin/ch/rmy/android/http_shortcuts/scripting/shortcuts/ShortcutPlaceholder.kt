package ch.rmy.android.http_shortcuts.scripting.shortcuts

import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.models.Shortcut
import ch.rmy.android.http_shortcuts.icons.ShortcutIcon

data class ShortcutPlaceholder(
    val id: ShortcutId,
    val name: String,
    val icon: ShortcutIcon,
) {
    companion object {

        fun fromShortcut(shortcut: Shortcut) =
            ShortcutPlaceholder(
                id = shortcut.id,
                name = shortcut.name,
                icon = shortcut.icon,
            )
    }
}
