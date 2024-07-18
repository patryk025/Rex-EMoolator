package pl.genschu.bloomooemulator.ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

// adapted from https://coderanch.com/t/333340/java/set-Line-wrap-Column-JTable
public class TextTableRenderer extends JTextArea implements TableCellRenderer {
    public TextTableRenderer() {
        setOpaque(true);
        setLineWrap(true);
        setWrapStyleWord(true);

        // set font to system default
        setFont(UIManager.getFont("Table.font"));
    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value, boolean isSelected, boolean hasFocus, int row,
                                                   int column) {

        if (isSelected) {
            setForeground(new Color(255, 255, 255));
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(new Color(0,0,0));
            setBackground(table.getBackground());
        }

        setText((value == null) ? "" : value.toString());
        return this;
    }
}
