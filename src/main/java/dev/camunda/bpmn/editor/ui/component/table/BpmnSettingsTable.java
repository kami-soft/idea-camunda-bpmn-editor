package dev.camunda.bpmn.editor.ui.component.table;

import static dev.camunda.bpmn.editor.util.ComponentUtils.createNullableComboBox;
import static dev.camunda.bpmn.editor.util.Constants.EMPTY;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.table.JBTable;
import dev.camunda.bpmn.editor.settings.BpmnEditorSettings;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A table component for displaying and editing BPMN Editor settings.
 * This class extends JBTable and provides a custom table model for managing
 * the settings of the BPMN Editor, such as engine, color theme, and script type.
 *
 * <p>The table uses combo boxes for editing specific columns and automatically
 * resizes columns to fit the content.</p>
 *
 * @author Oleksandr Havrysh
 */
public class BpmnSettingsTable extends JBTable {

    private final BpmnSettingsTableModel bpmnSettingsTableModel;

    /**
     * Constructs a new BpmnSettingsTable.
     * Initializes the table model and sets up the table with combo box editors for specific columns.
     */
    public BpmnSettingsTable() {
        bpmnSettingsTableModel = new BpmnSettingsTableModel();

        setModel(bpmnSettingsTableModel);
        setPreferredScrollableViewportSize(new Dimension(600, 200));
        setFillsViewportHeight(true);

        setComboBoxEditor(1, new ComboBox<>(BpmnEditorSettings.Engine.values()));
        setComboBoxEditor(2, createNullableComboBox(BpmnEditorSettings.ColorTheme.values()));
        setComboBoxEditor(3, createNullableComboBox(BpmnEditorSettings.ScriptType.values()));

        getModel().addTableModelListener(e -> ColumnsAutoSizer.sizeColumnsToFit(this));
    }

    /**
     * Sets a combo box editor for a specific column.
     *
     * @param columnIndex The index of the column to set the editor for
     * @param comboBox    The combo box to use as the editor
     */
    private void setComboBoxEditor(int columnIndex, JComboBox<?> comboBox) {
        var column = getColumnModel().getColumn(columnIndex);
        column.setCellEditor(new DefaultCellEditor(comboBox));
        column.setCellRenderer(new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {
                setText(value instanceof Enum ? value.toString() : EMPTY);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
    }

    /**
     * Sets the file settings in the table model.
     *
     * @param fileSettings The file settings to be set
     */
    public void setFileSettings(Map<String, BpmnEditorSettings.FileSettings> fileSettings) {
        bpmnSettingsTableModel.setFileSettings(fileSettings);
    }

    /**
     * Returns the file settings from the table model.
     *
     * @return The file settings from the table model
     */
    public Map<String, BpmnEditorSettings.FileSettings> getFileSettings() {
        return bpmnSettingsTableModel.getFileSettings();
    }

    /**
     * Deletes the selected row from the table.
     */
    public void deleteSelectedRow() {
        bpmnSettingsTableModel.removeRow(getSelectedRow());
    }

    /**
     * Clears all rows from the table.
     */
    public void clearAll() {
        bpmnSettingsTableModel.clearTable();
    }
}