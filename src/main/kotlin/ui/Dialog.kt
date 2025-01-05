package ui

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.ButtonGroup
import javax.swing.JComponent
import javax.swing.JDialog

class Dialog(private val dialog: JDialog) {

    internal var title = ""
    internal var centered = true

    internal var resizable = false
    internal var pane: RootPane<*>? = null
    internal var onClosing: () -> Boolean = { true }
    internal var dimension: Pair<Int, Int> = 0 x 0
        set(value) {
            if (value.first <= 0 || value.second <= 0)
                throw RuntimeException("Dialog: width/height must be greater than zero")
            field = value
            val d = Dimension(value.first, value.second)
            dialog.preferredSize = d
            dialog.minimumSize = d
            dialog.maximumSize = d
        }

    internal fun build() {
        dialog.title = title
        dialog.isResizable = resizable
        dialog.modalityType = JDialog.DEFAULT_MODALITY_TYPE
        dialog.defaultCloseOperation = JDialog.DO_NOTHING_ON_CLOSE
        dialog.addWindowListener(object: WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                if (onClosing())
                    this@Dialog.close()
            }
        })

        pane?.apply {
            dialog.add(this.swingComponent, BorderLayout.CENTER)
        } ?: throw java.lang.RuntimeException("Dialog: panel must be defined")

        if (dimension.first <= 0 || dimension.second <= 0)
            dialog.pack()
    }

    fun show() {
        if (dialog.isVisible)
            return
        if (centered)
            dialog.setLocationRelativeTo(dialog.parent)
        dialog.isVisible = true
    }

    fun close() {
        if (! dialog.isVisible)
            return
        dialog.isVisible = false
        dialog.dispose()
    }


    fun <T: JComponent> findComponent(id: String): T? = pane?.findComponent(id) as T?

    fun findGroup(id: String): ButtonGroup? = pane?.findGroup(id)

    fun findComponents(group: String) = pane?.findComponents(group)
}