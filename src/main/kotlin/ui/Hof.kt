package ui

import java.text.ParseException
import javax.swing.*

fun window(block: Frame.() -> Unit): Frame =
    Frame(JFrame()).let {
        it.block()
        it.build()
        it
    }

fun dialog(window: Frame? = null, block: Dialog.() -> Unit): Dialog =
    Dialog(JDialog(window?.frame)).let {
        it.block()
        it.build()
        it
    }
fun pane(block: Pane.() -> Unit): Pane =
    Pane(JPanel()).let {
        it.block()
        it
    }

fun horizontalSplitPane(block: SplitPane.() -> Unit): SplitPane =
    SplitPane(JSplitPane(JSplitPane.HORIZONTAL_SPLIT)).let {
        it.block()
        it
    }

fun verticalSplitPane(block: SplitPane.() -> Unit): SplitPane =
    SplitPane(JSplitPane(JSplitPane.VERTICAL_SPLIT)).let {
        it.block()
        it
    }

fun menuBar(block: MenuBar.() -> Unit): MenuBar =
    MenuBar(JMenuBar()).let {
        it.block()
        it.setListener()
        it
    }

fun toolBar(block: ToolBar.() -> Unit): ToolBar =
    ToolBar(JToolBar()).let {
        it.block()
        it.setListener()
        it
    }

fun confirm(title: String, message: String, buttonCancel: Boolean = false) = confirm(null, title, message, buttonCancel)

fun Frame.confirm(title: String, message: String, buttonCancel: Boolean = false) = confirm(this.frame, title, message, buttonCancel)

fun message(title: String, message: String) = message(null, title, message)

fun Frame.message(title: String, message: String) = message(this.frame, title, message)

fun error(title: String, message: String) = error(null, title, message)

fun Frame.error(title: String, message: String) = error(this.frame, title, message)

fun option(title: String, message: String, options: List<String>) = option(null, title, message, options)

fun Frame.option(title: String, message: String, options: List<String>) = option(this.frame, title, message, options)

fun select(title: String, message: String, options: List<String>) = select(null, title, message, options)

fun Frame.select(title: String, message: String, options: List<String>) = select(this.frame, title, message, options)

internal fun confirm(parent: java.awt.Component?, title: String, message: String, buttonCancel: Boolean = false)
    = JOptionPane.showConfirmDialog(parent,
    message,
    title,
    if (buttonCancel) JOptionPane.YES_NO_CANCEL_OPTION else JOptionPane.YES_NO_OPTION)

internal fun message(parent: java.awt.Component?, title: String, message: String)
    = JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE)

internal fun error(parent: java.awt.Component?, title: String, message: String)
        = JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE)

internal fun option(parent: java.awt.Component?, title: String, message: String, options: List<String>)
    = JOptionPane.showOptionDialog(parent,
    message,
    title,
    JOptionPane.DEFAULT_OPTION,
    JOptionPane.QUESTION_MESSAGE,
    null,
    options.toTypedArray(),
    options[0])

internal fun select(parent: java.awt.Component?, title: String, message: String, options: List<String>)
    = JOptionPane.showInputDialog(parent,
    message,
    title,
    JOptionPane.QUESTION_MESSAGE,
    null,
    options.toTypedArray(),
    options[0])

val ButtonGroup.selectedItem: AbstractButton?
    get() = this.elements.asSequence().firstOrNull { it.isSelected }

val JFormattedTextField.unformattedText: String?
    get() {
        val formatter = this.formatter
        return try {
            formatter.stringToValue(this.text).toString()
        } catch (_: ParseException) {
            null
        }
    }


