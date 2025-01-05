package ui

import java.awt.Color
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JToolBar

@SwingComposerDsl
class ToolBar(override val swingComponent: JToolBar): ComponentCollection<TButton>(swingComponent) {

    var onClick: ActionListener? = null

    init {
        swingComponent.background = Color(57, 57, 57)
    }
    fun setListener() {
        onClick?.apply {
            components.forEach { it.swingComponent.addActionListener(this) }
        }
    }

    fun button(id: String, icon: String, block: (TButton.() -> Unit)? = null) {
        val button = TButton(JButton())
        if (! button.loadIcon(icon))
            button.swingComponent.text = icon
        button.id = id
        block?.let {
            button.it()
        }
        add(button)
    }

    fun findToolBarButton(id: String) = components.firstOrNull { it.id == id}
}