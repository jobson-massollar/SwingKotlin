package ui

import java.awt.Color
import java.awt.Dimension
import java.awt.event.ActionListener
import java.awt.event.FocusListener
import java.awt.event.ItemListener

import javax.swing.*
import javax.swing.JSpinner.DefaultEditor
import javax.swing.event.ChangeListener
import javax.swing.event.DocumentListener
import javax.swing.event.ListSelectionListener
import javax.swing.text.JTextComponent

@SwingComposerDsl
sealed class Widget(swingComponent: JComponent): Component(swingComponent)

sealed class CommandWidget(override val swingComponent: AbstractButton): Widget(swingComponent) {
    override var id: String
        get() = super.id
        set(value) {
            super.id = value
            swingComponent.actionCommand = value
        }
}

sealed class ToggleWidget(override val swingComponent: JToggleButton): CommandWidget(swingComponent) {

    internal var onClick: ItemListener? = null
        set(value) {
            field = value
            swingComponent.addItemListener(value)
        }
}

sealed class TextWidget(override val swingComponent: JTextComponent): Widget(swingComponent) {

    override var id: String
        get() = super.id
        set(value) {
            super.id = value
            if (onChange != null) {
                swingComponent.document.putProperty("name", value)
                swingComponent.document.putProperty("widget", swingComponent)
            }
        }

    internal var onFocus: FocusListener? = null
        set(value) {
            field = value
            swingComponent.addFocusListener(value)
        }

    internal var onChange: DocumentListener? = null
        set(value) {
            field = value
            swingComponent.document.addDocumentListener(value)
            if (id.isNotEmpty()) {
                swingComponent.document.putProperty("name", id)
                swingComponent.document.putProperty("widget", swingComponent)
            }
        }
}

sealed class ButtonWidget(final override val swingComponent: JButton): CommandWidget(swingComponent) {
    internal var onClick: ActionListener? = null
        set(value) {
            field = value
            swingComponent.addActionListener(value)
        }

    fun loadIcon(icon: String): Boolean {
        val url = ButtonWidget::class.java.getResource("/$icon")
        if (url != null) {
            swingComponent.icon = ImageIcon(url)
            return true
        }
        return false
    }
}

class Button(swingComponent: JButton): ButtonWidget(swingComponent) {
    internal var icon =  ""
        set(value) {
            if (loadIcon(value))
                field = value
        }
}

class TButton(swingComponent: JButton): ButtonWidget(swingComponent) {
    internal var label =  ""
        set(value) {
            field = value
            swingComponent.text = value
        }
}

class TextField(override val swingComponent: JTextField): TextWidget(swingComponent) {
    val text: String? = swingComponent.text

    init {
        swingComponent.maximumSize = Dimension(swingComponent.maximumSize.width, swingComponent.preferredSize.height)
        swingComponent.minimumSize = swingComponent.preferredSize
    }
}

class FormattedTextField(override val swingComponent: JFormattedTextField): TextWidget(swingComponent) {
    val text: String? = swingComponent.text

    init {
        swingComponent.maximumSize = Dimension(swingComponent.maximumSize.width, swingComponent.preferredSize.height)
        swingComponent.minimumSize = swingComponent.preferredSize
    }
}

class Label(override val swingComponent: JLabel): Widget(swingComponent) {
    internal var color: Color? = null
        set(value) {
            value?.let {
                field = it
                swingComponent.foreground = it
            }
        }
}

class TextArea(override val swingComponent: JTextArea): Widget(swingComponent) {
    val text: String? = swingComponent.text
}

class Checkbox(override val swingComponent: JCheckBox): ToggleWidget(swingComponent) {
    val checked = swingComponent.isSelected
}

class RadioButton(override val swingComponent: JRadioButton): ToggleWidget(swingComponent) {
    val checked = swingComponent.isSelected
}

val SelectionList<*>.SINGLE
    get() = ListSelectionModel.SINGLE_SELECTION
val SelectionList<*>.SINGLE_INTERVAL
    get() = ListSelectionModel.SINGLE_INTERVAL_SELECTION
val SelectionList<*>.MULTIPLE
    get() = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION

class SelectionList<T>(override val swingComponent: JList<T>): Widget(swingComponent) {
    internal var selectionMode: Int =  ListSelectionModel.SINGLE_SELECTION
        set(value) {
            field = value
            swingComponent.selectionMode = value
        }

    internal var visibleRows: Int = 5
        set(value) {
            field = value
            swingComponent.visibleRowCount = value
        }

    internal var onSelect: ListSelectionListener? = null
        set(value) {
            field = value
            swingComponent.selectionModel.addListSelectionListener(value)
        }

    init {
        visibleRows = 5
        selectionMode = ListSelectionModel.SINGLE_SELECTION
    }
}

val Table.SINGLE
    get() = ListSelectionModel.SINGLE_SELECTION
val Table.SINGLE_INTERVAL
    get() = ListSelectionModel.SINGLE_INTERVAL_SELECTION
val Table.MULTIPLE
    get() = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION

class Table(override val swingComponent: JTable): Widget(swingComponent) {
    internal var selectionMode: Int =  ListSelectionModel.SINGLE_SELECTION
        set(value) {
            field = value
            swingComponent.setSelectionMode(value)
        }

    internal var onSelect: ListSelectionListener? = null
        set(value) {
            field = value
            swingComponent.selectionModel.addListSelectionListener(value)
        }

    fun columnWidths(vararg widths: Int) {
        widths.forEachIndexed { index, value -> swingComponent.columnModel.getColumn(index).preferredWidth = value }
    }
}

class ComboBox<T>( override val swingComponent: JComboBox<T>): Widget(swingComponent) {
    internal var onChange: ItemListener? = null
        set(value) {
            field = value
            swingComponent.addItemListener(value)
        }
}

sealed class Spinner(final override val swingComponent: JSpinner): Widget(swingComponent) {
    protected var spinnerModel: AbstractSpinnerModel? = null
    abstract fun build()

    internal var onChange: ChangeListener? = null
        set(value) {
            field = value
            swingComponent.addChangeListener(value)
        }

    init {
        (swingComponent.editor as DefaultEditor).textField.isEditable = false
    }
}

sealed class NumberSpinner<T: Number>(swingComponent: JSpinner): Spinner(swingComponent) {
    internal open lateinit var min: T
    internal open lateinit var max: T
    internal open lateinit var step: T
    internal open lateinit var value: T
}

class IntegerSpinner(swingComponent: JSpinner): NumberSpinner<Int>(swingComponent) {

    override var min: Int = 1
    override var max: Int = 10
    override var step: Int = 1
    override var value: Int = 5

    override fun build() {
        swingComponent.model = SpinnerNumberModel(value, min, max, step)
    }
}

class DoubleSpinner(swingComponent: JSpinner): NumberSpinner<Double>(swingComponent) {

    override var min: Double = 1.0
    override var max: Double = 10.0
    override var step: Double = 1.0
    override var value: Double = 5.0

    override fun build() {
        swingComponent.model = SpinnerNumberModel(value, min, max, step)
    }
}

class Slider( override val swingComponent: JSlider): Widget(swingComponent) {

    internal var min: Int = 1
    internal var max: Int = 100
    internal var value: Int = 50
    internal var minStick: Int = 1
    internal var maxStick: Int = 10
    internal var paintTicks = true
    internal var paintLabels = true
    internal var width = -1

    internal var onChange: ChangeListener? = null
        set(value) {
            field = value
            swingComponent.addChangeListener(value)
        }

    fun build() {
        with(swingComponent) {
            minimum = min
            maximum = max
            value = this@Slider.value
            minorTickSpacing = minStick
            majorTickSpacing = maxStick
            paintTicks = this@Slider.paintTicks
            paintLabels = this@Slider.paintLabels

            if (this@Slider.width != -1) {
                preferredSize = Dimension(this@Slider.width, preferredSize.height)
                maximumSize = preferredSize
                minimumSize = preferredSize
            }
        }
    }
}
