package dev.camunda.bpmn.editor.ui.component.table;

import static dev.camunda.bpmn.editor.util.ComponentUtils.createNullableComboBox;

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
 * A custom table component for displaying and editing BPMN Editor settings.
 * This class extends JBTable and provides a specialized interface for managing
 * file-specific settings of the BPMN Editor, such as engine, color theme, and script type.
 *
 * <p>Features of this table include:</p>
 * <ul>
 *   <li>Custom table model ({@link BpmnSettingsTableModel}) for BPMN settings</li>
 *   <li>Combo box editors for engine, color theme, and script type columns</li>
 *   <li>Automatic column resizing to fit content</li>
 *   <li>Methods for managing file settings data</li>
 * </ul>
 *
 * <p>The table is designed to be used within the BPMN Editor's settings UI,
 * allowing users to view and modify settings for individual BPMN files.</p>
 *
 * @author Oleksandr Havrysh
 */
public class BpmnSettingsTable extends JBTable {

    private final BpmnSettingsTableModel bpmnSettingsTableModel;

    /**
     * Constructs a new BpmnSettingsTable.
     * Initializes the table with a custom model, sets up combo box editors for specific columns,
     * and configures automatic column resizing.
     */
    public BpmnSettingsTable() {
        bpmnSettingsTableModel = new BpmnSettingsTableModel();

        setModel(bpmnSettingsTableModel);
        setPreferredScrollableViewportSize(new Dimension(600, 200));
        setFillsViewportHeight(true);

        setComboBoxEditor(1, new ComboBox<>(BpmnEditorSettings.Engine.values()));
        setComboBoxEditor(2, createNullableComboBox(BpmnEditorSettings.ColorTheme.values()));
        setComboBoxEditor(3, createNullableComboBox(BpmnEditorSettings.SchemaTheme.values()));
        setComboBoxEditor(4, createNullableComboBox(BpmnEditorSettings.ScriptType.values()));

        getModel().addTableModelListener(e -> ColumnsAutoSizer.sizeColumnsToFit(this));
    }

    /**
     * Sets a combo box editor for a specific column.
     *
     * @param columnIndex The index of the column to set the editor for (0-based)
     * @param comboBox    The combo box to use as the editor
     */
    private void setComboBoxEditor(int columnIndex, JComboBox<?> comboBox) {
        var column = getColumnModel().getColumn(columnIndex);
        column.setCellEditor(new DefaultCellEditor(comboBox));
        column.setCellRenderer(new EnumTableCellRenderer());
    }

    /**
     * Custom table cell renderer for enum values.
     * This renderer ensures that enum values are displayed as strings in the table cells.
     */
    public static class EnumTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            setText(value instanceof Enum ? value.toString() : "");
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    /**
     * Sets the file settings in the table model.
     * This method updates the table with the provided file settings data.
     *
     * @param fileSettings A map of file paths to their corresponding settings
     */
    public void setFileSettings(Map<String, BpmnEditorSettings.FileSettings> fileSettings) {
        bpmnSettingsTableModel.setFileSettings(fileSettings);
    }

    /**
     * Retrieves the current file settings from the table model.
     *
     * @return A map of file paths to their corresponding settings as represented in the table
     */
    public Map<String, BpmnEditorSettings.FileSettings> getFileSettings() {
        return bpmnSettingsTableModel.getFileSettings();
    }

    /**
     * Deletes the currently selected row from the table.
     * If no row is selected, this method has no effect.
     */
    public void deleteSelectedRow() {
        bpmnSettingsTableModel.removeRow(getSelectedRow());
    }

    /**
     * Removes all rows from the table, effectively clearing all file settings.
     */
    public void clearAll() {
        bpmnSettingsTableModel.clearTable();
    }
}