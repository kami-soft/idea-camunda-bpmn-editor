package dev.camunda.bpmn.editor.ui.component;

import static java.awt.FlowLayout.LEFT;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.panels.VerticalLayout;
import dev.camunda.bpmn.editor.config.BpmnEditorSettings;
import java.awt.FlowLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

/**
 * A UI component for configuring the BPMN Editor settings. This class provides a form with a combo box
 * to select the color theme of the BPMN Editor.
 *
 * <p>The component is built using IntelliJ's UI components and is intended to be used within the settings
 * panel of the BPMN Editor.</p>
 *
 * @author Oleksandr Havrysh
 */
public class JBpmnEditorComponent extends JPanel {

    private final JComboBox<BpmnEditorSettings.ScriptType> scriptTypeComboBox;
    private final JComboBox<BpmnEditorSettings.ColorTheme> colorThemeComboBox;

    /**
     * Constructs a new JBpmnEditorComponent.
     * Initializes the combo box with the available color themes and builds the form.
     */
    public JBpmnEditorComponent() {
        super(new VerticalLayout(2));

        colorThemeComboBox = new ComboBox<>(BpmnEditorSettings.ColorTheme.values());
        var colorThemePanel = new JPanel(new FlowLayout(LEFT));
        colorThemePanel.add(new JBLabel("   Color theme:"));
        colorThemePanel.add(colorThemeComboBox);

        scriptTypeComboBox = new ComboBox<>(BpmnEditorSettings.ScriptType.values());
        var scriptTypePanel = new JPanel(new FlowLayout(LEFT));
        scriptTypePanel.add(new JBLabel("   Default script type:"));
        scriptTypePanel.add(scriptTypeComboBox);

        add(new TitledSeparator("Theme Settings", colorThemePanel));
        add(colorThemePanel);
        add(new TitledSeparator("Script Settings", colorThemePanel));
        add(scriptTypePanel);
    }

    /**
     * Returns the preferred component to focus when the settings panel is opened.
     *
     * @return The preferred component to focus
     */
    public @NotNull JComponent getPreferredFocusedComponent() {
        return colorThemeComboBox;
    }

    /**
     * Returns the currently selected color theme.
     *
     * @return The currently selected color theme
     */
    public BpmnEditorSettings.ColorTheme getColorThemeValue() {
        return (BpmnEditorSettings.ColorTheme) colorThemeComboBox.getSelectedItem();
    }

    /**
     * Sets the selected color theme in the combo box.
     *
     * @param colorTheme The color theme to be selected
     */
    public void setColorThemeValue(BpmnEditorSettings.ColorTheme colorTheme) {
        colorThemeComboBox.setSelectedItem(colorTheme);
    }

    /**
     * Returns the currently selected script type.
     *
     * @return The currently selected script type
     */
    public BpmnEditorSettings.ScriptType getScriptTypeValue() {
        return (BpmnEditorSettings.ScriptType) scriptTypeComboBox.getSelectedItem();
    }

    /**
     * Sets the selected script type in the combo box.
     *
     * @param scriptType The script type to be selected
     */
    public void setScriptTypeValue(BpmnEditorSettings.ScriptType scriptType) {
        scriptTypeComboBox.setSelectedItem(scriptType);
    }
}