package ui

import java.awt.GridBagConstraints
import javax.swing.*

sealed class ComponentCollection<T: Component>(override val swingComponent: JComponent): Component(swingComponent) {
    protected val components: MutableList<T> = mutableListOf()
    protected val groups: MutableMap<String, ButtonGroup> = mutableMapOf()

    enum class SplitPosition { FIRST, SECOND }

    fun add(component: T) {
        components.add(component)
        this.swingComponent.add(component.swingComponent)
    }

    fun addScroll(component: T) {
        components.add(component)
        this.swingComponent.add(JScrollPane(component.swingComponent))
    }

    fun addConstraint(component: T, gbc: GridBagConstraints) {
        components.add(component)
        this.swingComponent.add(component.swingComponent, gbc)
    }

    fun addGrouped(component: T) {
        if (component.group.isNotEmpty()) {
            if (! groups.containsKey(component.group))
                groups[component.group] = ButtonGroup()
            groups[component.group]!!.add(component.swingComponent as AbstractButton)
        }
        components.add(component)
        this.swingComponent.add(component.swingComponent)
    }

    fun addBorderPane(component: T, position: String) {
        components.add(component)
        this.swingComponent.add(component.swingComponent, position)
    }

    fun addSplit(component: T, position: SplitPosition) {
        components.add(component)
        if (position == SplitPosition.FIRST)
            (this as SplitPane).swingComponent.topComponent = component.swingComponent
        else
            (this as SplitPane).swingComponent.bottomComponent = component.swingComponent
    }
}

sealed class RootPane<T: Component>(swingComponent: JComponent): ComponentCollection<T>(swingComponent = swingComponent) {
    fun findComponent(id: String): JComponent? {
        components.forEach {
            if (it is Pane)
                it.findComponent(id)?.apply {
                    return this
                }
            else if (it.id == id)
                return it.swingComponent
        }
        return null
    }

    fun findGroup(group: String): ButtonGroup? {
        if (groups.containsKey(group))
            return groups[group]

        components.filterIsInstance<RootPane<*>>().forEach {
            it.findGroup(group)?.apply {
                return this
            }
        }

        return null
    }

    fun findComponents(group: String): List<JComponent> {
        val comp: MutableList<JComponent> = mutableListOf()
        findComponents(group, comp)
        return comp
    }

    private fun findComponents(group: String, comp: MutableList<JComponent>) {
        components.forEach {
            if (it is RootPane<*>)
                it.findComponents(group, comp)
            else if (it.group == group)
                comp.add(it.swingComponent)
        }
    }

    fun reset() {
        components.forEach {
            when (it) {
                is Button,
                is Label,
                is Table,
                is Menu,
                is MenuItem,
                is MenuBar,
                is SplitPane,
                is Pane -> null
                is TextArea -> it.swingComponent.text = it.text
                is TextField -> it.swingComponent.text = it.text
                is FormattedTextField -> it.swingComponent.text = it.text
                is Checkbox -> it.swingComponent.isSelected = it.checked
                is RadioButton -> it.swingComponent.isSelected = it.checked
                is ComboBox<*> -> it.swingComponent.selectedIndex = 0
                is SelectionList<*> -> it.swingComponent.selectedIndex = 0
                is DoubleSpinner -> it.swingComponent.value = it.value
                is IntegerSpinner -> it.swingComponent.value = it.value
                is Slider -> it.swingComponent.value = it.value
            }
        }
    }
}

sealed class LayoutPane<T: Component>(final override val swingComponent: JPanel): RootPane<T>(swingComponent = swingComponent) {
    abstract var layout: LayoutBuilder
}
