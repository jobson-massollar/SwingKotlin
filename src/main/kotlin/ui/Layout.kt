package ui

import java.awt.*
import javax.swing.*
import javax.swing.table.TableModel
import javax.swing.text.MaskFormatter

@DslMarker
annotation class SwingComposerDsl

@SwingComposerDsl
sealed class LayoutBuilder(open val layoutPane: LayoutPane<Component>,
                           open val layoutManager: LayoutManager) {
    abstract fun <T: Component> decorate(c:T): T

    protected fun <T: Component> addComponent(c: T, block: (T.() -> Unit)? = null) {
        block?.let {
            c.it()
        }
        this.layoutPane.add(this.decorate(c))
    }

    protected fun <T: Component> addScrollComponent(c: T, block: (T.() -> Unit)? = null) {
        block?.let {
            c.it()
        }
        this.layoutPane.addScroll(this.decorate(c))
    }

    protected fun <T: Component> addGroupedComponent(c: T, block: (T.() -> Unit)? = null) {
        block?.let {
            c.it()
        }
        this.layoutPane.addGrouped(this.decorate(c))
    }

    protected fun <T: Component> addBorderPane(c: T, position: String, block: (T.() -> Unit)? = null) {
        block?.let {
            c.it()
        }
        this.layoutPane.addBorderPane(this.decorate(c), position)
    }
}

sealed class ComponentLayoutBuilder(layoutPane: LayoutPane<Component>,
                                    layoutManager: LayoutManager): LayoutBuilder(layoutPane, layoutManager) {

    fun button(label: String, block: (Button.() -> Unit)? = null) =
        addComponent(Button(JButton(label)), block)

    fun textField(size: Int, value: String = "", block: (TextField.() -> Unit)? = null) =
        addComponent(TextField(JTextField(value, size)), block)

    fun formattedTextField(mask: String, value: String = "", block: (FormattedTextField.() -> Unit)? = null) {
        val formatter = MaskFormatter(mask)
        formatter.placeholderCharacter = '_'
        formatter.valueContainsLiteralCharacters = false
        val tf = JFormattedTextField(formatter)
        tf.focusLostBehavior = JFormattedTextField.PERSIST
        tf.text = value
        addComponent(FormattedTextField(tf), block)
    }

    fun label(label: String, block: (Label.() -> Unit)? = null) =
        addComponent(Label(JLabel(label)), block)

    fun textarea(rows: Int, columns: Int, value: String = "", block: (TextArea.() -> Unit)? = null) =
        addComponent(TextArea(JTextArea(value, rows, columns)), block)

    fun <T: Any> list(items: Array<T>, block: (SelectionList<T>.() -> Unit)? = null) =
        addScrollComponent(SelectionList(JList<T>(items)), block)

    fun table(model: TableModel, block: (Table.() -> Unit)? = null) =
        addScrollComponent(Table(JTable(model)), block)

    fun <T: Any> comboBox(items: Array<T>, block: (ComboBox<T>.() -> Unit)? = null) =
        addComponent(ComboBox(JComboBox<T>(items)), block)

    fun checkbox(label: String, checked: Boolean = false, block: (Checkbox.() -> Unit)? = null)  =
        addComponent(Checkbox(JCheckBox(label, checked)), block)

    fun radio(label: String, checked: Boolean = false, block: (RadioButton.() -> Unit)? = null) =
        addGroupedComponent(RadioButton(JRadioButton(label, checked)), block)
}

sealed class FlowBuilder(layoutPane: LayoutPane<Component>,
                         override val layoutManager: LayoutManager): ComponentLayoutBuilder(layoutPane, layoutManager) {

    internal abstract fun filler(size: Int)

    internal fun pane(block: Pane.() -> Unit) = addComponent(Pane(JPanel()), block)

    internal fun horizontalSplitPane(block: SplitPane.() -> Unit) = addComponent(SplitPane(JSplitPane(JSplitPane.HORIZONTAL_SPLIT)), block)

    internal fun  verticalSplitPane(block: SplitPane.() -> Unit) = addComponent(SplitPane(JSplitPane(JSplitPane.VERTICAL_SPLIT)), block)
}

class FlowLayoutBuilder(layoutPane: LayoutPane<Component>,
                        override val layoutManager: FlowLayout = FlowLayout()): FlowBuilder(layoutPane, layoutManager) {

    enum class FlowAlignment(val alignment: Int) {
        LEFT(FlowLayout.LEFT),
        RIGHT(FlowLayout.RIGHT),
        CENTER(FlowLayout.CENTER)
    }

    val left = FlowLayoutBuilder.FlowAlignment.LEFT
    val right = FlowLayoutBuilder.FlowAlignment.RIGHT
    val center = FlowLayoutBuilder.FlowAlignment.CENTER

    internal var align = FlowAlignment.CENTER
        set(value) {
            field = value
            layoutManager.alignment = value.alignment
        }

    init {
        layoutManager.alignment = align.alignment
    }

    override fun filler(size: Int) {
        layoutPane.swingComponent.add(Box.createRigidArea(Dimension(size, 1)))
    }

    override fun <T : Component> decorate(c: T): T = c
}

sealed class BoxLayoutBuilder(layoutPane: LayoutPane<Component>,
                              override val layoutManager: BoxLayout): FlowBuilder(layoutPane, layoutManager) {

    enum class Alignment(val value: Float) {
        NONE(-1f),
        LEFT(JComponent.LEFT_ALIGNMENT),
        CENTER(JComponent.CENTER_ALIGNMENT),
        RIGHT(JComponent.RIGHT_ALIGNMENT),
        TOP(JComponent.TOP_ALIGNMENT),
        BOTTOM(JComponent.BOTTOM_ALIGNMENT)
    }

    val none = Alignment.NONE
    val left = Alignment.LEFT
    val right = Alignment.RIGHT
    val center = Alignment.CENTER
    val top = Alignment.TOP
    val bottom = Alignment.BOTTOM

    internal var alignX: Alignment = none
    internal var alignY: Alignment = none

    override fun <T : Component> decorate(c: T): T {
        if (alignX != none)
            c.swingComponent.alignmentX = alignX.value
        if (alignY != none)
            c.swingComponent.alignmentY = alignY.value
        return c
    }
}

class VerticalBoxLayoutBuilder(layoutPane: LayoutPane<Component>,
                               layoutManager: BoxLayout): BoxLayoutBuilder(layoutPane, layoutManager) {

    override fun filler(size: Int) {
        layoutPane.swingComponent.add(Box.createRigidArea(Dimension(1, size)))
    }
}

class HorizontalBoxLayoutBuilder(layoutPane: LayoutPane<Component>,
                                 layoutManager: BoxLayout): BoxLayoutBuilder(layoutPane, layoutManager) {

    override fun filler(size: Int) {
        layoutPane.swingComponent.add(Box.createRigidArea(Dimension(size, 1)))
    }
}

class BorderLayoutBuilder(layoutPane: LayoutPane<Component>,
                          override val layoutManager: BorderLayout = BorderLayout()): LayoutBuilder(layoutPane, layoutManager) {

    private var hasNorthPane = false
    private var hasSouthPane = false
    private var hasEastPane = false
    private var hasWestPane = false
    private var hasCenterPane =  false

    override fun <T : Component> decorate(c: T) = c

    internal fun north(block: Pane.() -> Unit) {
        if (hasNorthPane)
            throw RuntimeException("BorderLayout: North pane already defined")

        addBorderPane(Pane(JPanel()), BorderLayout.NORTH, block)
        hasNorthPane = true
    }

    internal fun south(block: Pane.() -> Unit) {
        if (hasSouthPane)
            throw RuntimeException("BorderLayout: South pane already defined")

        addBorderPane(Pane(JPanel()), BorderLayout.SOUTH, block)
        hasSouthPane = true
    }
    internal fun east(block: Pane.() -> Unit) {
        if (hasEastPane)
            throw RuntimeException("BorderLayout: East pane already defined")

        addBorderPane(Pane(JPanel()), BorderLayout.EAST, block)
        hasEastPane = true
    }
    internal fun west(block: Pane.() -> Unit) {
        if (hasWestPane)
            throw RuntimeException("BorderLayout: West pane already defined")

        addBorderPane(Pane(JPanel()), BorderLayout.WEST, block)
        hasWestPane = true
    }

    internal fun center(block: Pane.() -> Unit) {
        if (hasCenterPane)
            throw RuntimeException("BorderLayout: Center pane already defined")

        addBorderPane(Pane(JPanel()), BorderLayout.CENTER, block)
        hasCenterPane = true
    }
}

data class GridArea(val area: String, val startRow: Int, val startCol: Int, val endRow: Int, val endCol: Int)

class GridBagLayoutBuilder(layoutPane: LayoutPane<Component>,
                           override val layoutManager: GridBagLayout = GridBagLayout()): LayoutBuilder(layoutPane, layoutManager) {
    private val areas: MutableSet<String> = mutableSetOf()
    private val grid: MutableList<List<String>> = mutableListOf()
    private val gridAreas: MutableList<GridArea> = mutableListOf()
    private val cells: MutableList<Cell> = mutableListOf()
    private val gbc = GridBagConstraints()
    private var lastColumn = -1
    private var lastRow = -1

    class Grid(private val parent: GridBagLayoutBuilder) {

        operator fun String.unaryPlus() {
            val s = this.uppercase().split(Regex("\\s+"))
            s.forEach { parent.areas.add(it) }
            parent.grid.add(s)
        }
    }

    class Cell(val gridArea: GridArea, private val parent: GridBagLayoutBuilder) {

        enum class Anchor(val value: Int) {
            CENTER(GridBagConstraints.CENTER),
            NORTH(GridBagConstraints.NONE),
            SOUTH(GridBagConstraints.SOUTH),
            EAST(GridBagConstraints.EAST),
            WEST(GridBagConstraints.WEST),
            NORTHEAST(GridBagConstraints.NORTHEAST),
            NORTHWEST(GridBagConstraints.NORTHWEST),
            SOUTHEAST(GridBagConstraints.SOUTHEAST),
            SOUTHWEST(GridBagConstraints.SOUTHWEST)
        }

        enum class Fill(val value: Int) {
            NONE(GridBagConstraints.NONE),
            VERTICAL(GridBagConstraints.VERTICAL),
            HORIZONTAL(GridBagConstraints.HORIZONTAL),
            BOTH(GridBagConstraints.BOTH)
        }

        val none = Fill.NONE
        val vertical = Fill.VERTICAL
        val horizontal = Fill.HORIZONTAL
        val both = Fill.BOTH
        val center = Anchor.CENTER
        val north = Anchor.NORTHEAST
        val south = Anchor.SOUTH
        val east = Anchor.EAST
        val west = Anchor.WEST
        val northeast = Anchor.NORTHEAST
        val northwest = Anchor.NORTHWEST
        val southeast = Anchor.SOUTHEAST
        val southwest = Anchor.SOUTHWEST

        internal var anchor: Anchor = northwest

        internal var fill: Fill = none

        fun button(label: String, block: (Button.() -> Unit)? = null) = addComponent(Button(JButton(label)), block)

        fun textField(size: Int, value: String = "", block: (TextField.() -> Unit)? = null) = addComponent(TextField(JTextField(value, size)), block)

        fun label(label: String, block: (Label.() -> Unit)? = null) = addComponent(Label(JLabel(label)), block)

        fun integerSpinner(block: (IntegerSpinner.() -> Unit)? = null) = addComponentBuild(IntegerSpinner(JSpinner()), block)

        fun doubleSpinner(block: (DoubleSpinner.() -> Unit)? = null) = addComponentBuild(DoubleSpinner(JSpinner()), block)

        fun verticalSlider(block: (Slider.() -> Unit)? = null) = addComponentBuild(Slider(JSlider(JSlider.VERTICAL)), block)

        fun horizontalSlider(block: (Slider.() -> Unit)? = null) = addComponentBuild(Slider(JSlider(JSlider.HORIZONTAL)), block)

        fun formattedTextField(mask: String, value: String = "", block: (FormattedTextField.() -> Unit)? = null) {
            val formatter = MaskFormatter(mask)
            formatter.placeholderCharacter = '_'
            formatter.valueContainsLiteralCharacters = false
            val tf = JFormattedTextField(formatter)
            tf.focusLostBehavior = JFormattedTextField.PERSIST
            tf.text = value
            addComponent(FormattedTextField(tf), block)
        }

        private fun <T: Component> addComponent(c: T, block: (T.() -> Unit)? = null) {
            block?.let {
                c.it()
            }

            addWithConstraint(c)
        }

        private fun <T: Spinner>addComponentBuild(c: T, block: (T.() -> Unit)? = null) {
            block?.let {
                c.it()
            }

            c.build()
            addWithConstraint(c)
        }

        private fun addComponentBuild(c: Slider, block: (Slider.() -> Unit)? = null) {
            block?.let {
                c.it()
            }

            c.build()
            addWithConstraint(c)
        }

        private fun <T: Component> addWithConstraint(c: T) {
            parent.gbc.gridx = gridArea.startCol
            parent.gbc.gridy = gridArea.startRow
            parent.gbc.gridwidth = gridArea.endCol - gridArea.startCol + 1
            parent.gbc.gridheight = gridArea.endRow - gridArea.startRow + 1
            parent.gbc.anchor = anchor.value
            parent.gbc.fill = fill.value
            parent.gbc.weightx = if (gridArea.endCol == parent.lastColumn) 1.0 else 0.0
            parent.gbc.weighty = if (gridArea.endRow == parent.lastRow) 1.0 else 0.0
            parent.layoutPane.addConstraint(c, parent.gbc)
        }
    }

    internal fun padding(top: Int, right: Int, bottom: Int, left: Int) {
        gbc.insets = Insets(top, left, bottom, right)
    }

    internal fun grid(block: Grid.() -> Unit) {
        if (gridAreas.isNotEmpty())
            throw RuntimeException("GridBagLayout: grid must be defined just once")
        Grid(this).let { it ->
            it.block()
            if (grid.any { it.size != grid[0].size })
                throw RuntimeException("GridBagLayout: all grid lines must have the same number of columns")
            lastColumn = grid[0].size - 1
            lastRow = grid.size - 1
            calculateAreas()
        }
    }

    internal fun cell(area: String, block: Cell.() -> Unit) {
        if (areas.none { it == area })
            throw RuntimeException("GridBagLayout: cell '$area' not found in the grid. Grid must be defined before cells")
        if (cells.any { it.gridArea.area == area })
            throw RuntimeException("GridBagLayout: cell '$area' already defined")
        Cell(gridAreas.first { it.area == area }, this).let {
            cells.add(it)
            it.block()
        }
    }

    private fun calculateAreas() {
        var startRow: Int
        var startCol: Int
        var endRow: Int
        var endCol: Int

        areas.forEach { area ->
            startRow = -1
            startCol = -1
            endRow = -1
            endCol = -1
            grid.forEachIndexed { row, line ->
                line.forEachIndexed { col, name ->
                    if (name == area && startRow == -1) {
                        startRow = row
                        startCol = col
                        endRow = row
                        endCol = col
                    }
                    else if (name == area && row == startRow) {
                        endCol++
                    }
                    else if (name == area && col == startCol) {
                        endRow++
                    }
                }
            }
            gridAreas.add(GridArea(area, startRow, startCol, endRow, endCol))
        }
    }

    override fun <T : Component> decorate(c: T): T = c
}