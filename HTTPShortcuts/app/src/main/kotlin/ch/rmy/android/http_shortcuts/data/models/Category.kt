package ch.rmy.android.http_shortcuts.data.models

import ch.rmy.android.framework.extensions.isInt
import ch.rmy.android.framework.extensions.isUUID
import ch.rmy.android.framework.extensions.takeUnlessEmpty
import ch.rmy.android.http_shortcuts.data.domains.categories.CategoryId
import ch.rmy.android.http_shortcuts.data.enums.CategoryBackgroundType
import ch.rmy.android.http_shortcuts.data.enums.CategoryLayoutType
import ch.rmy.android.http_shortcuts.data.enums.ShortcutClickBehavior
import ch.rmy.android.http_shortcuts.icons.ShortcutIcon
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
open class Category(
    @Required
    var name: String = "",
    categoryLayoutType: CategoryLayoutType = CategoryLayoutType.LINEAR_LIST,
    categoryBackgroundType: CategoryBackgroundType = CategoryBackgroundType.Default,
    clickBehavior: ShortcutClickBehavior? = null,
) : RealmModel {

    @PrimaryKey
    var id: CategoryId = ""
    var shortcuts: RealmList<Shortcut> = RealmList()
    private var iconName: String? = null

    var icon: ShortcutIcon?
        get() = iconName?.let(ShortcutIcon::fromName)
        set(value) {
            iconName = value?.toString()?.takeUnlessEmpty()
        }

    @Required
    private var layoutType: String = CategoryLayoutType.LINEAR_LIST.type

    @Required
    private var background: String = CategoryBackgroundType.Default.serialize()
    var hidden: Boolean = false

    private var shortcutClickBehavior: String? = null

    var categoryLayoutType
        get() = CategoryLayoutType.parse(layoutType)
        set(value) {
            layoutType = value.type
        }

    var categoryBackgroundType
        get() = CategoryBackgroundType.parse(background)
        set(value) {
            background = value.serialize()
        }

    var clickBehavior: ShortcutClickBehavior?
        get() = shortcutClickBehavior?.let(ShortcutClickBehavior::parse)
        set(value) {
            shortcutClickBehavior = value?.type
        }

    init {
        layoutType = categoryLayoutType.type
        background = categoryBackgroundType.serialize()
        shortcutClickBehavior = clickBehavior?.type
    }

    fun validate() {
        require(id.isUUID() || id.isInt()) {
            "Invalid category ID found, must be UUID: $id"
        }
        require(CategoryLayoutType.values().any { it.type == layoutType || it.legacyAlias == layoutType }) {
            "Invalid layout type: $layoutType"
        }
        require(name.isNotBlank()) {
            "Category without a name found"
        }
        require(name.length <= NAME_MAX_LENGTH) {
            "Category name too long: $name"
        }
        shortcuts.forEach(Shortcut::validate)
    }

    companion object {
        const val FIELD_ID = "id"

        const val NAME_MAX_LENGTH = 50
    }
}
