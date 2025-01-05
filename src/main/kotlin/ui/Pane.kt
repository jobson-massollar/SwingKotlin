package ui

import java.awt.FlowLayout
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.border.Border

@SwingComposerDsl
class Pane(swingComponent: JPanel): LayoutPane<Component>(swingComponent) {

//    init {
//        swingComponent.background = Color.RED
//    }

//    var dimension: Pair<Int, Int> = 0 to 0
//        set(value) {
//            field = value
//            val d = Dimension(value.first, value.second)
//            swingComponent.preferredSize = d
//            swingComponent.minimumSize = d
//            swingComponent.maximumSize = d
//        }

    internal var border: Border? = null
        set(value) {
            field = value
            swingComponent.border = border
        }

    override var layout: LayoutBuilder = FlowLayoutBuilder(this, FlowLayout())
        set(value) {
            field = value
            swingComponent.layout = value.layoutManager
        }

    fun flowLayout(block: FlowLayoutBuilder.() -> Unit) {
        val builder = FlowLayoutBuilder(this)
        layout = builder
        builder.block()
    }

    fun horizontalBoxLayout(block: BoxLayoutBuilder.() -> Unit)  = boxLayout(BoxLayout.X_AXIS, block)

    fun verticalBoxLayout(block: BoxLayoutBuilder.() -> Unit)  = boxLayout(BoxLayout.Y_AXIS, block)

    fun borderLayout(block: BorderLayoutBuilder.() -> Unit) {
        val builder = BorderLayoutBuilder(this)
        layout = builder
        builder.block()
    }

    fun gridBagLayout(block: GridBagLayoutBuilder.() -> Unit) {
        val builder = GridBagLayoutBuilder(this)
        layout = builder
        builder.block()
    }

//    override fun toString(): String {
//        val str = StringBuilder()
//        str.append("[id=$id group=$group length=${components.size} components=[")
//
//        str.apply {
//            components.forEach { append("${it.id}, ") }
//            append("]]")
//        }
//
//        return str.toString()
//    }

    private fun boxLayout(axis: Int, block: BoxLayoutBuilder.() -> Unit) {
        val box = BoxLayout(swingComponent, axis)
        when(axis) {
            BoxLayout.X_AXIS -> HorizontalBoxLayoutBuilder(this, box)
            BoxLayout.Y_AXIS -> VerticalBoxLayoutBuilder(this, box)
            else -> throw RuntimeException("BoxLayout: invalid axis value")
        }.apply {
            this@Pane.layout = this
            this.block()
        }
    }
}

@SwingComposerDsl
class SplitPane(override val swingComponent: JSplitPane): RootPane<Pane>(swingComponent) {

    private var hasFirstPane = false
    private var hasSecondPane = false

    internal var oneTouchExpandable = false
        set(value) {
            field = value
            swingComponent.isOneTouchExpandable = value
        }

    internal var dividerSize = 0
        set(value) {
            field = value
            swingComponent.dividerSize = value
        }

    internal var splitLocation = 0
        set(value) {
            field = value
            swingComponent.setDividerLocation(value)
        }

    internal var border: Border? = null
        set(value) {
            field = value
            swingComponent.border = border
        }

    init {
        swingComponent.isOneTouchExpandable = oneTouchExpandable
    }

    internal fun first(block: Pane.() -> Unit) {
        if (hasFirstPane)
            throw RuntimeException("SplitPane: First pane already defined")

        Pane(JPanel()).let {
            it.block()
            addSplit(it, SplitPosition.FIRST)
            //it.swingComponent.border = EmptyBorder(1,1,1,1)
        }

        hasFirstPane = true
    }

    internal fun second(block: Pane.() -> Unit) {
        if (hasSecondPane)
            throw RuntimeException("SplitPane: Second pane already defined")

        Pane(JPanel()).let {
            it.block()
            addSplit(it, SplitPosition.SECOND)
        }

        hasSecondPane = true
    }
}
