package ch.rmy.android.http_shortcuts.activities.globalcode

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ch.rmy.android.framework.extensions.consume
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.activities.editor.scripting.codesnippets.CodeSnippetPickerActivity
import ch.rmy.android.http_shortcuts.components.BackButton
import ch.rmy.android.http_shortcuts.components.ScreenScope
import ch.rmy.android.http_shortcuts.components.SimpleScaffold
import ch.rmy.android.http_shortcuts.components.ToolbarIcon
import ch.rmy.android.http_shortcuts.components.bindViewModel

@Composable
fun ScreenScope.GlobalScriptingScreen() {
    val (viewModel, state) = bindViewModel<GlobalScriptingViewState, GlobalScriptingViewModel>()

    val pickCodeSnippet = rememberLauncherForActivityResult(CodeSnippetPickerActivity.PickCodeSnippet) { result ->
        if (result != null) {
            viewModel.onCodeSnippetPicked(result.textBeforeCursor, result.textAfterCursor)
        }
    }

    BackHandler(enabled = state?.hasChanges == true) {
        viewModel.onBackPressed()
    }

    EventHandler { event ->
        when (event) {
            is GlobalScriptingEvent.ShowCodeSnippetPicker -> consume {
                pickCodeSnippet.launch {
                    includeResponseOptions(false)
                        .includeNetworkErrorOption(false)
                        .includeFileOptions(true)
                }
            }
            else -> false
        }
    }

    SimpleScaffold(
        viewState = state,
        title = stringResource(R.string.title_global_scripting),
        backButton = BackButton.CROSS,
        actions = { viewState ->
            ToolbarIcon(
                Icons.Filled.HelpOutline,
                contentDescription = stringResource(R.string.button_show_help),
                onClick = viewModel::onHelpButtonClicked,
            )
            ToolbarIcon(
                Icons.Filled.Check,
                contentDescription = stringResource(R.string.action_save_global_scripting),
                enabled = viewState.saveButtonEnabled,
                onClick = viewModel::onSaveButtonClicked,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onCodeSnippetButtonClicked) {
                Icon(
                    imageVector = Icons.Outlined.PostAdd,
                    contentDescription = stringResource(R.string.button_add_code_snippet),
                )
            }
        },
    ) { viewState ->
        GlobalScriptingContent(
            globalCode = viewState.globalCode,
            onGlobalCodeChanged = viewModel::onGlobalCodeChanged,
        )
    }

    GlobalScriptingDialogs(
        state?.dialogState,
        onDiscardConfirmed = viewModel::onDiscardDialogConfirmed,
        onDismissRequested = viewModel::onDialogDismissed,
    )
}
