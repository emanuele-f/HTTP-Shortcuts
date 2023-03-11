package ch.rmy.android.http_shortcuts.tiles

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import android.view.WindowManager
import androidx.annotation.RequiresApi
import ch.rmy.android.framework.extensions.context
import ch.rmy.android.framework.extensions.logException
import ch.rmy.android.framework.extensions.runFor
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.activities.ExecuteActivity
import ch.rmy.android.http_shortcuts.dagger.getApplicationComponent
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutRepository
import ch.rmy.android.http_shortcuts.data.enums.ShortcutTriggerType
import ch.rmy.android.http_shortcuts.data.models.Shortcut
import ch.rmy.android.http_shortcuts.utils.DialogBuilder
import ch.rmy.android.http_shortcuts.utils.ThemeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
class QuickTileService : TileService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var shortcutRepository: ShortcutRepository

    override fun onCreate() {
        super.onCreate()
        context.getApplicationComponent().inject(this)
    }

    override fun onClick() {
        if (!scope.isActive) {
            logException(IllegalStateException("QuickTileService coroutine scope was inactive"))
            val shortcuts = runBlocking {
                getShortcuts()
            }
            handleShortcuts(shortcuts)
            return
        }
        scope.launch {
            handleShortcuts(getShortcuts())
        }
    }

    private suspend fun getShortcuts() =
        shortcutRepository.getShortcuts()
            .sortedBy { it.name }
            .filter { it.quickSettingsTileShortcut }

    private fun handleShortcuts(shortcuts: List<Shortcut>) {
        when (shortcuts.size) {
            0 -> showInstructions()
            1 -> executeShortcut(shortcuts[0].id)
            else -> showPickerDialog(shortcuts)
        }
    }

    private fun showInstructions() {
        applyTheme()
        DialogBuilder(context)
            .message(
                getString(
                    R.string.instructions_quick_settings_tile,
                    getString(R.string.label_quick_tile_shortcut),
                    getString(R.string.label_execution_settings),
                )
            )
            .positive(R.string.dialog_ok)
            .build()
            .showInService()
    }

    private fun showPickerDialog(shortcuts: List<Shortcut>) {
        applyTheme()
        DialogBuilder(context)
            .runFor(shortcuts) { shortcut ->
                item(name = shortcut.name, shortcutIcon = shortcut.icon) {
                    executeShortcut(shortcut.id)
                }
            }
            .build()
            .showInService()
    }

    private fun Dialog.showInService() {
        try {
            showDialog(this)
        } catch (e: WindowManager.BadTokenException) {
            // Ignore
        } catch (e: Throwable) {
            logException(e)
        }
    }

    private fun applyTheme() {
        setTheme(ThemeHelper(context).theme)
    }

    private fun executeShortcut(shortcutId: ShortcutId) {
        ExecuteActivity.IntentBuilder(shortcutId)
            .trigger(ShortcutTriggerType.QUICK_SETTINGS_TILE)
            .build(context)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .let { intent ->
                startActivityAndCollapse(intent)
            }
    }

    override fun onStartListening() {
        super.onStartListening()
        scope.launch {
            val shortcuts = getShortcuts()
            qsTile?.label = when (shortcuts.size) {
                1 -> shortcuts.first().name
                else -> getString(R.string.action_quick_settings_tile_trigger)
            }
            qsTile?.updateTile()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
