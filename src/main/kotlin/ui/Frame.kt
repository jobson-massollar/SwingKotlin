package ui

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.ButtonGroup
import javax.swing.JComponent
import javax.swing.JFrame

val Frame.NORTH: String
    get() = BorderLayout.NORTH
val Frame.SOUTH: String
    get() = BorderLayout.SOUTH
val Frame.WEST: String
    get() = BorderLayout.WEST
val Frame.EAST: String
    get() = BorderLayout.EAST
val Frame.CENTER: String
    get() = BorderLayout.CENTER

infix fun Int.x(other: Int) = Pair(this, other)

//infix fun RootPane<*>.position(direction: String) = Pair(this, direction)

class Frame(private val _frame: JFrame) {

    private var _currentPane: RootPane<*>? = null

    val currentPane: RootPane<*>?
        get() = _currentPane

    val frame: JFrame
        get() = _frame

    internal var title = ""
    internal var resizable = true
    internal var centered = false
    internal var onClosing: () -> Boolean = { true }
    internal var dimension: Pair<Int, Int> = 0 x 0
        set(value) {
            if (value.first <= 0 || value.second <= 0)
                throw RuntimeException("Window: width/height must be greater than zero")
            field = value
            val d = Dimension(value.first, value.second)
            _frame.preferredSize = d
            _frame.minimumSize = d
            _frame.maximumSize = d
        }
    internal var pane: RootPane<*>? = null
    internal var menuBar: MenuBar? = null
        set(value) {
            if (field != null)
                field!!.parent = null
            field = value
            if (field != null) {
                field!!.parent = this
                field!!.setListener()
            }
        }
    var toolBar: ToolBar? = null
        set(value) {
            if (_frame.isVisible) {
                if (field != null)
                    _frame.remove(field?.swingComponent)
                if (value != null)
                    _frame.add(value.swingComponent, NORTH)
                _frame.revalidate()
            }
            field = value
        }

    internal fun build() {
        _frame.title = title
        _frame.isResizable = resizable
        _frame.jMenuBar = menuBar?.swingComponent
        _frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        _frame.addWindowListener(object: WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                if (onClosing())
                    this@Frame.close()
            }
        })

        pane?.apply {
            _currentPane = this
            _currentPane?.swingComponent?.let { _frame.add(it, CENTER) }
        } ?: throw java.lang.RuntimeException("Window: panel must be defined")

        toolBar?.apply {
            _frame.add(this.swingComponent, NORTH)
        }

        if (dimension.first <= 0 || dimension.second <= 0)
            _frame.pack()
    }

    fun show() {
        if (_frame.isVisible)
            return
        if (centered)
            _frame.setLocationRelativeTo(null)
        _frame.isVisible = true
    }

    fun close() {
        if (! _frame.isVisible)
            return
        _frame.isVisible = false
        _frame.dispose()
    }

    fun switchTo(newPane: RootPane<*>) {
        _currentPane?.apply {
            this.swingComponent.isVisible = false
            _frame.remove(this.swingComponent)
            _frame.revalidate()
        }

        this.pane = newPane

        newPane.apply {
            _currentPane = this
            _currentPane?.swingComponent?.let {
                _frame.add(it, CENTER)
                it.isVisible = true
                _frame.revalidate()
            }
            if (dimension.first <= 0 || dimension.second <= 0)
                _frame.pack()
        }
    }

    fun findMenuItem(id: String) = menuBar?.findMenuItem(id)

    fun findToolBarButton(id: String) = toolBar?.findToolBarButton(id)?.swingComponent

    fun <T:JComponent> findComponent(id: String): T? = pane?.findComponent(id) as T?

    fun findGroup(id: String): ButtonGroup? = pane?.findGroup(id)

    fun findComponents(group: String) = pane?.findComponents(group)
}