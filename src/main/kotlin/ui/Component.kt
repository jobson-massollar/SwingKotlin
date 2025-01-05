package ui

import javax.swing.JComponent

sealed class Component(open val swingComponent: JComponent) {
    private var _id: String = ""
    private var _group: String = ""

    internal open var id: String
        get() = _id
        set(value) {
            _id = value
            swingComponent.name = value
        }

    internal open var group: String
        get() = _group
        set(value) {
            _group = value
        }

    internal var toolTip: String = ""
        set(value) {
            field = value
            swingComponent.toolTipText = value
        }

    val component: JComponent
        get() = swingComponent
}