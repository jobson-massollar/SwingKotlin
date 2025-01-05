package main

import ui.*
import java.awt.Color
import java.awt.event.*
import javax.swing.*
import javax.swing.event.*
import javax.swing.table.AbstractTableModel
import javax.swing.text.JTextComponent

object FListener: FocusListener {
    override fun focusGained(e: FocusEvent?) {
        val tf = e?.source as JTextField?

        tf?.apply {
            println("Focus (${this.name})")
        }
    }

    override fun focusLost(e: FocusEvent?) {
        val tf = e?.source as JTextComponent?

        tf?.apply {
            when (tf) {
                is JFormattedTextField -> println("Blur ${tf.name} =  Text ${tf.text} / Unformatted ${tf.unformattedText}")
                is JTextField -> println("Blur ${tf.name} = ${tf.text}")
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object TextListener: DocumentListener {

    var w: Frame? = null

    override fun insertUpdate(e: DocumentEvent?) {
        when (val tf = e?.document?.getProperty("widget") as JTextComponent?) {
            is JFormattedTextField -> println("Insert Formatted ${tf.name} =  Text ${tf.text} / Unformatted ${tf.unformattedText}")
            is JTextField -> println("Insert ${tf.name} = ${tf.text}")
        }
    }

    override fun removeUpdate(e: DocumentEvent?) {
        when (val tf = e?.document?.getProperty("widget") as JTextComponent?) {
            is JFormattedTextField -> println("Remove Formatted ${tf.name} = Text ${tf.text} / Unformatted ${tf.unformattedText}")
            is JTextField -> println("Remove ${tf.name} = ${tf.text}")
        }
    }

    override fun changedUpdate(e: DocumentEvent?) {
        when (val tf = e?.document?.getProperty("widget") as JTextComponent?) {
            is JFormattedTextField -> println("Changed Formatted ${tf.name} = Text ${tf.text} / Unformatted ${tf.unformattedText}")
            is JTextField -> println("Changed ${tf.name} = ${tf.text}")
        }
    }

}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object CheckListener: ItemListener {
    override fun itemStateChanged(e: ItemEvent?) {
        val chk = e?.source as JToggleButton?

        chk?.apply {
            println("${this.name} ${if (e.stateChange == ItemEvent.SELECTED) "(X)" else "( )"}")
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object ComboListener: ItemListener {
    override fun itemStateChanged(e: ItemEvent?) {
        val cb = e?.source as JComboBox<*>?

        cb?.apply {
            println("${this.name} ${if (e.stateChange == ItemEvent.SELECTED) "(X) "+this.selectedItem else "( )"}")
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object ListListener: ListSelectionListener {
    override fun valueChanged(e: ListSelectionEvent?) {
        if (e?.valueIsAdjusting == true)
            return

        val lsm = e?.source as ListSelectionModel?

        lsm?.apply {
            println("${this.minSelectionIndex} ${this.maxSelectionIndex}")
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object SpinnerListener: ChangeListener {
    override fun stateChanged(e: ChangeEvent?) {
        val spin = e?.source as JSpinner?

        spin?.apply {
            println("Spinner (${this.name}): ${this.model.value}")
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object SliderListener: ChangeListener {
    override fun stateChanged(e: ChangeEvent?) {
        val slider = e?.source as JSlider?

        slider?.apply {
            println("Slider (${this.name}): ${this.value}")
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object ToolBarListener: ActionListener {
    override fun actionPerformed(e: ActionEvent?) {
        val source = e?.source as JButton?
        println("ToolBar (${source?.name})")
    }

}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object MenuListener: ActionListener {

    var w: Frame? = null

    override fun actionPerformed(e: ActionEvent?) {
        val source = e?.source as JMenuItem?

        println("item clicado ${source?.name}")

        if (source?.name == "open") {
            val d = dialog(Listener.w) {
                title = "Dialogo modal"
                dimension = 200 x 100
                pane = Panel5.create()
            }

            d.show()
        } else if (source?.name == "about") {
            w?.toolBar = toolBar {
                onClick = ToolBarListener
                button("sad",       "sad32.png")
                button("folder",    "folder32.png")
                button("glasses",   "glasses32.png")
                button("grid",      "grid32.png")
                button("headphones", "headphones32.png")
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object Listener: ActionListener {

    var w: Frame? = null

    override fun actionPerformed(e: ActionEvent?) {
        when(val source = e?.source) {
            is JButton -> {
                when (source.name) {
                    "btnOk" -> {
                        w?.switchTo(Panel2.create())
                    }
                    "voltar" -> {
                        w?.switchTo(Panel1.create())
                    }
                    "cancelar" -> {
                        w?.switchTo(Panel3.create())
                    }
                    "ok" -> {
                        w?.switchTo(Panel1.create())
                    }
                    "painel4" -> {
                        w?.switchTo(Panel4.create())
                    }
                    "painel1" -> {
                        w?.switchTo(Panel1.create())
                    }
                    "mostra3" -> {
                        val cpf = w?.findComponent<JTextField>("cpf")
                        val nome = w?.findComponent<JTextField>("nome")
                        println("${cpf?.text?:"Not found"} ${nome?.text?:"Not found"}")
                    }
                    "btnCancelar" -> {
                        val futebol = w?.findComponent<JCheckBox>("futebol")
                        val volei = w?.findComponent<JCheckBox>("volei")
                        val nome = w?.findComponent<JTextField>("nome")
                        println("${nome?.text?:"Not found"} Futebol ${if (futebol?.isSelected == true) "X" else " "} Volei ${if (volei?.isSelected == true) "X" else " "}")
                    }
                    "mostra2" -> {
                        val p = w?.findComponent<JList<Pessoa>>("people")
                        val cb = w?.findComponent<JComboBox<Pessoa>>("cbPeople")
                        val ec = w?.findGroup("ec")?.selectedItem
                        val casado = w?.findComponent<JRadioButton>("C")

                        println("${p?.selectedIndex} - ${p?.selectedValue}")
                        println("${cb?.selectedIndex} - ${cb?.selectedItem}")
                        println("${ec?.name} - ${ec?.actionCommand} - ${ec?.isSelected}")
                        println("${casado?.name} - ${casado?.actionCommand} - ${casado?.isSelected}")
                    }
                }
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object Panel1 {
    fun create() = pane {
        id = "panel1"
        border = BorderFactory.createEmptyBorder(10, 5, 10, 5)
        flowLayout {
            align = LEFT
            filler(10)
            label("Nome:")
            textField(20, "Valor inicial") {
                id="nome"
            }
            filler(30)
            button("Painel 2") {
                id = "btnOk"
                toolTip = "Botão OK"
                onClick = Listener
                icon = "battery.png"
            }
            filler(10)
            button("Cancelar") {
                id="btnCancelar"
                toolTip = "Botão Cancelar"
                onClick = Listener
            }
            checkbox("Futebol", true) {
                id = "futebol"
                onClick = CheckListener
            }
            checkbox("Vôlei") {
                id = "volei"
                onClick = CheckListener
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object Panel2 {

    val pessoas = listOf(Pessoa(82372250744, "Ana"), Pessoa(93916990730, "Maria"), Pessoa(18430070702, "Pedro"), Pessoa(76544312343, "Carlos"))

    private val adm = object: AbstractTableModel() {
        override fun getRowCount() = pessoas.size

        override fun getColumnCount() = 2

        override fun getValueAt(rowIndex: Int, columnIndex: Int) = if (columnIndex == 0) pessoas[rowIndex].cpf else pessoas[rowIndex].name

        override fun getColumnClass(columnIndex: Int): Class<*> {
            return if (columnIndex == 0) Int.Companion::class.java else String.Companion::class.java
        }

        override fun getColumnName(column: Int) = if (column == 0) "CPF" else "Nome"
    }

    fun create() = pane {
        id = "panel2"
        border = BorderFactory.createEmptyBorder(10, 5, 10, 5)
        horizontalBoxLayout {
            pane {
                verticalBoxLayout {
                    alignX = LEFT
                    //filler(10)
                    label("Coluna 1") {
                        color = Color.RED
                        id="coluna1"
                    }
                    //filler(10)
                    textField(20)
                    //filler(10)
                    //radioGroup {
                    radio("Casado") {
                        id="C"
                        group="ec"
                        onClick = CheckListener
                    }
                    radio("Solteiro"){
                        id="S"
                        group="ec"
                        onClick = CheckListener
                    }
                    radio("Viúvo"){
                        id="V"
                        group="ec"
                        onClick = CheckListener
                    }
                    radio("Divorciado") {
                        id="D"
                        group="ec"
                        onClick = CheckListener
                    }
                    //}
                    list(pessoas.toTypedArray()) {
                        id = "people"
                        visibleRows = 3
                        selectionMode = MULTIPLE
                        onSelect = ListListener
                    }
                    button("Voltar Painel 1") {
                        id = "voltar"
                        onClick = Listener
                    }
                }
            }
            filler(10)
            pane {
                verticalBoxLayout {
                    filler(10)
                    label("Coluna 2")
                    filler(10)
                    table(adm) {
                        columnWidths(30, 180)
                        selectionMode = MULTIPLE
                        onSelect = ListListener
                    }
                    filler(10)
                    comboBox(pessoas.toTypedArray()) {
                        id="cbPeople"
                        onChange = ComboListener
                    }
                    button("Cancelar") {
                        id = "cancelar"
                        onClick = Listener
                    }
                    button("Mostrar") {
                        id = "mostra2"
                        onClick = Listener
                    }
                }
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object Panel3 {
    fun create() = pane {
        id = "panel3"
        border = BorderFactory.createEmptyBorder(10, 5, 10, 5)
        gridBagLayout {
            padding(2, 5, 2, 5)
            grid {
                +"A B B B"
                +"C D D D"
                +"X Y Y Y"
                +"W K K K"
                +"W Z Z Z"
                +"I J J J"
                +"E F G H"
            }
            cell("A") {
                //anchor = NORTHWEST
                //fill = NONE
                label("CPF:")
            }
            cell("B") {
                //fill = HORIZONTAL
                //anchor = NORTHWEST
                //fill = NONE
                textField(15) {
                    id = "cpf"
                    onFocus = FListener
                    onChange = TextListener
                }
            }
            cell("C") {
                label("Nome:")
            }
            cell("D") {
                textField(40) {
                    id = "nome"
                    onFocus = FListener
                    onChange = TextListener
                }
            }
            cell("X") {
                label("CEP:")
            }
            cell("Y") {
                formattedTextField("#####-###", "20540006") {
                    id = "cep"
                    onFocus = FListener
                    onChange = TextListener
                }
            }
            cell("W") {
                anchor = NORTH
                label("Spinners:")
            }
            cell("K") {
                integerSpinner {
                    id = "spin1"
                    onChange = SpinnerListener
                }
            }
            cell("Z") {
                doubleSpinner {
                    id = "spin2"
                    min = 0.0
                    max = 100.0
                    step = 0.5
                    value = 10.0
                    onChange = SpinnerListener
                }
            }
            cell("I") {
                label("Slider:")
            }
            cell("J") {
                horizontalSlider {
                    id = "slider1"
                    min = 0
                    max = 100
                    value = 50
                    minStick = 1
                    maxStick = 10
                    onChange = SliderListener
                    width = 600
                }
            }
            cell("F") {
                button("Ok") {
                    id = "ok"
                    onClick = Listener
                }
            }
            cell("G") {
                button("Mostra") {
                    id = "mostra3"
                    onClick = Listener
                }
            }
            cell("H") {
                button("Painel 4") {
                    id = "painel4"
                    onClick = Listener
                }
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object Panel4 {
    fun create() = pane {
        border = BorderFactory.createEmptyBorder(10, 5, 10, 5)
        borderLayout {
            north {
                border = BorderFactory.createTitledBorder(" Norte ")
                verticalBoxLayout {
                    alignX = JComponent.CENTER_ALIGNMENT
                    label("Norte")
                    button("Painel 1") {
                        id = "painel1"
                        onClick = Listener
                    }
                }
            }
            south {
                border = BorderFactory.createTitledBorder(" Sul ")
                flowLayout {
                    label("Sul")
                }
            }
            east {
                border = BorderFactory.createTitledBorder(" Leste ")
                flowLayout {
                    label("Leste")
                }
            }
            west {
                border = BorderFactory.createTitledBorder(" Oeste ")
                flowLayout {
                    label("Oeste")
                }
            }
            center {
                border = BorderFactory.createTitledBorder(" Centro ")
                flowLayout {
                    align = LEFT
                    label("Centro")
                }
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object Panel5 {

    fun create() = pane {
            flowLayout {
                label("Dialog")
            }
        }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

object Panel6 {

    fun create() = horizontalSplitPane {
        border = BorderFactory.createEmptyBorder(10, 5, 10, 5)
        splitLocation = 380
        //dividerSize = 20
        oneTouchExpandable = true
        first {
            border = BorderFactory.createTitledBorder(" Esquerda ")
            flowLayout {
                label("Esquerda")
            }
        }
        second {
            verticalBoxLayout {
                verticalSplitPane {
                    splitLocation = 280
                    oneTouchExpandable = true
                    first {
                        border = BorderFactory.createTitledBorder(" Direita topo ")
                        flowLayout {
                            label("Direita Topo")
                        }
                    }
                    second {
                        border = BorderFactory.createTitledBorder(" Direita base ")
                        flowLayout {
                            label("Direita Base")
                        }
                    }
                }
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

class Pessoa(val cpf: Long, val name: String) {
    override fun toString() = "CPF: $cpf / Nome: $name"
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun main() {

    //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel")
    //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
    //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel") // DEFAULT
    //UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf")
    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf")
    //UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf")
    //UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacLightLaf")
    //UIManager.setLookAndFeel("RadianceBusinessLookAndFeel")

//    JFrame.setDefaultLookAndFeelDecorated(true)
//    SwingUtilities.invokeLater {
//        try {
//            UIManager.setLookAndFeel(RadianceBusinessLookAndFeel())
//        } catch (e: Exception) {
//            println("Radiance Graphite failed to initialize " + e.message)
//            exitProcess(1)
//        }
//        // TODO O CODIGO DA MAIN DEVE FICAR AQUI DENTRO
//    }

    val menu = menuBar {
        onClick = MenuListener
        menu("File") {
            mnemonic = KeyEvent.VK_F
            item("open", "Open") {
                shortcut = KeyEvent.VK_O with KeyEvent.CTRL_DOWN_MASK
                mnemonic = KeyEvent.VK_O
            }
            item("save", "Save") {
                shortcut = KeyEvent.VK_S with KeyEvent.CTRL_DOWN_MASK
                mnemonic = KeyEvent.VK_S
                switchTo = { Panel6.create() }
            }
            item("saveas", "Save As") {
                shortcut = KeyEvent.VK_A with KeyEvent.CTRL_DOWN_MASK
                mnemonic = KeyEvent.VK_A
                switchTo = { Panel1.create() }
            }
            item("exit", "Exit") {
                shortcut = KeyEvent.VK_X with KeyEvent.CTRL_DOWN_MASK
                mnemonic = KeyEvent.VK_X
                switchTo = { Panel2.create() }
            }
        }
        menu("Help") {
            mnemonic = KeyEvent.VK_H
            item("about", "About")
        }
    }

    val bar = toolBar {
        onClick = ToolBarListener
        button("air",       "air32.png")
        button("window",    "window32.png")
        button("left",      "left32.png")
        button("settings",  "settings32.png")
        button("edit",      "edit32.png")
//        button("sad",       "sad32.png")
//        button("folder",    "folder32.png")
//        button("glasses",   "glasses32.png")
//        button("grid",      "grid32.png")
//        button("headphones", "headphones32.png")
    }

    val mainWindow = window {
        title = "Titulo de teste"
        dimension = 800 x 600
        // resizable = false
        centered = true
        // onClosing = { println("fechando..."); true }
        menuBar = menu
        pane = Panel1.create()
        toolBar = bar
    }

    Listener.w = mainWindow
    TextListener.w = mainWindow
    MenuListener.w = mainWindow

    mainWindow.show()
}