package ui

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.KeyStroke

infix fun Int.with(mask: Int): KeyStroke = KeyStroke.getKeyStroke(this, mask)

@SwingComposerDsl
class MenuBar(override val swingComponent: JMenuBar): ComponentCollection<Menu>(swingComponent) {

    internal var onClick: ActionListener? = null
    internal var parent: Frame? = null

    internal fun menu(label: String, block: Menu.() -> Unit) {
        Menu(this, JMenu(label)).let {
            it.block()
            add(it)
        }
    }

    internal fun setListener() {
        components.forEach { it.setListener(onClick) }
    }

    fun findMenuItem(id: String): JMenuItem? {
        components.forEach {
            val item = it.findMenuItem(id)
            if (item != null)
                return item.swingComponent
        }
        return null
    }
}

@SwingComposerDsl
class Menu(private var parent: MenuBar, override val swingComponent: JMenu): ComponentCollection<MenuItem>(swingComponent = swingComponent) {

    internal var mnemonic: Int = 0
        set(value) {
            field = value
            swingComponent.setMnemonic(value)
        }

    internal fun item(id: String, label: String, block: (MenuItem.() -> Unit)? = null) {
        MenuItem(JMenuItem(label)).let {
            it.id = id
            if (block != null) {
                it.block()
            }
            add(it)
        }
    }

    internal fun setListener(onClick: ActionListener?) {
        components.forEach {
            val comp = it.swingComponent
            comp.actionListeners.forEach {  comp.removeActionListener(it) }
            if (it.switchTo != null && this.parent.parent != null) {
                comp.addActionListener(object: ActionListener {
                    override fun actionPerformed(e: ActionEvent?) {
                        this@Menu.parent.parent?.switchTo(it.switchTo?.invoke()!!)
                    }
                })
            }
            else {
                onClick?.apply { comp.addActionListener(this) }
            }
        }
    }

    fun findMenuItem(id: String) = components.firstOrNull { it.id == id }
}

@SwingComposerDsl
class MenuItem(override val swingComponent: JMenuItem): Component(swingComponent) {
    internal var mnemonic: Int = 0
        set(value) {
            field = value
            swingComponent.mnemonic = value
        }

    internal var shortcut: KeyStroke? = null
        set(value) {
            field = value
            swingComponent.accelerator = value
        }

    internal var switchTo: (() -> RootPane<*>)? = null
}
