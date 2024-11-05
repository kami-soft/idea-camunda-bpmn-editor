package dev.camunda.bpmn.editor.ui.component.table;

import static java.util.Objects.isNull;

import dev.camunda.bpmn.editor.settings.BpmnEditorSettings;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;

/**
 * A table model for managing BPMN Editor settings.
 * This class extends AbstractTableModel and provides methods for managing
 * the settings of the BPMN Editor, such as engine, color theme, and script type.
 *
 * <p>The table model uses a concurrent map to store file settings and a list to store file paths.
 * It supports adding, updating, and removing rows, as well as clearing the entire table.</p>
 *
 * @author Oleksandr Havrysh
 */
public class BpmnSettingsTableModel extends AbstractTableModel {

    private final List<String> paths = new CopyOnWriteArrayList<>();

    @Getter
    private final Map<String, BpmnEditorSettings.FileSettings> fileSettings = new ConcurrentHashMap<>();

    private final String[] columnNames = {"Path", "Engine", "Color Theme", "Script Type"};
    private final Class<?>[] columnClasses = {String.class, BpmnEditorSettings.Engine.class,
            BpmnEditorSettings.ColorTheme.class, BpmnEditorSettings.ScriptType.class};

    /**
     * Sets the file settings in the table model.
     *
     * @param fileSettings The file settings to be set
     */
    public void setFileSettings(Map<String, BpmnEditorSettings.FileSettings> fileSettings) {
        this.fileSettings.clear();
        this.fileSettings.putAll(fileSettings);
        this.paths.clear();
        this.paths.addAll(fileSettings.keySet());
        fireTableDataChanged();
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return The number of rows in the table
     */
    @Override
    public int getRowCount() {
        return paths.size();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return The number of columns in the table
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the name of the column at the specified index.
     *
     * @param column The index of the column
     * @return The name of the column
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Returns the class of the column at the specified index.
     *
     * @param columnIndex The index of the column
     * @return The class of the column
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    /**
     * Returns the value at the specified row and column.
     *
     * @param rowIndex    The index of the row
     * @param columnIndex The index of the column
     * @return The value at the specified row and column
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var path = paths.get(rowIndex);
        var settings = fileSettings.get(path);
        return switch (columnIndex) {
            case 0 -> path;
            case 1 -> settings.getEngine();
            case 2 -> settings.getColorTheme();
            case 3 -> settings.getScriptType();
            default -> null;
        };
    }

    /**
     * Returns whether the cell at the specified row and column is editable.
     *
     * @param rowIndex    The index of the row
     * @param columnIndex The index of the column
     * @return true if the cell is editable, false otherwise
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > 0;
    }

    /**
     * Sets the value at the specified row and column.
     *
     * @param aValue      The value to set
     * @param rowIndex    The index of the row
     * @param columnIndex The index of the column
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        var path = paths.get(rowIndex);
        var settings = fileSettings.get(path);
        if (isNull(settings)) {
            return;
        }

        switch (columnIndex) {
            case 1:
                settings.setEngine((BpmnEditorSettings.Engine) aValue);
                break;
            case 2:
                settings.setColorTheme((BpmnEditorSettings.ColorTheme) aValue);
                break;
            case 3:
                settings.setScriptType((BpmnEditorSettings.ScriptType) aValue);
                break;
        }

        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Removes a row from the table model.
     *
     * @param rowIndex The index of the row to remove
     */
    public void removeRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < fileSettings.size()) {
            var path = paths.get(rowIndex);
            fileSettings.remove(path);
            paths.remove(path);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    /**
     * Clears all rows from the table model.
     */
    public void clearTable() {
        if (!fileSettings.isEmpty()) {
            fileSettings.clear();
            paths.clear();
            fireTableDataChanged();
        }
    }
}