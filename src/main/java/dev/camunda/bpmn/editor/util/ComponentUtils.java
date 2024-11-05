package dev.camunda.bpmn.editor.util;

import com.intellij.openapi.ui.ComboBox;
import javax.swing.JComboBox;

/**
 * Utility class for creating UI components.
 *
 * <p>This class provides utility methods for creating and configuring UI components
 * used in the BPMN Editor.</p>
 *
 * @author Oleksandr Havrysh
 */
public class ComponentUtils {

    /**
     * Creates a JComboBox that allows null selection.
     *
     * @param values The array of values to populate the combo box
     * @param <T>    The type of the values
     * @return A JComboBox populated with the given values and allowing null selection
     */
    public static <T> JComboBox<T> createNullableComboBox(T[] values) {
        var comboBox = new ComboBox<>(values);
        comboBox.insertItemAt(null, 0);
        comboBox.setSelectedIndex(0);

        return comboBox;
    }
}