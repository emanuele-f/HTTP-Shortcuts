package ch.rmy.android.http_shortcuts.scripting.actions.types

import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.dialogs.DialogBuilder
import ch.rmy.android.http_shortcuts.scripting.ExecutionContext
import ch.rmy.android.http_shortcuts.utils.HTMLUtil
import ch.rmy.android.http_shortcuts.variables.Variables
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers

class DialogAction(data: Map<String, String>) : BaseAction() {

    private val message: String = data[KEY_TEXT] ?: ""

    private val title: String = data[KEY_TITLE] ?: ""

    override fun execute(executionContext: ExecutionContext): Completable {
        val finalMessage = Variables.rawPlaceholdersToResolvedValues(
            message,
            executionContext.variableManager.getVariableValuesByIds()
        )
        return if (finalMessage.isNotEmpty()) {
            Completable
                .create { emitter ->
                    DialogBuilder(executionContext.context)
                        .title(title)
                        .message(HTMLUtil.format(finalMessage))
                        .positive(R.string.dialog_ok)
                        .dismissListener { emitter.onComplete() }
                        .show()
                }
                .subscribeOn(AndroidSchedulers.mainThread())
        } else {
            Completable.complete()
        }
    }

    companion object {

        const val KEY_TEXT = "text"
        const val KEY_TITLE = "title"

    }

}