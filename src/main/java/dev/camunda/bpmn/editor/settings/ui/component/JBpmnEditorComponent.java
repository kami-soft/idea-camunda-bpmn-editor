package dev.camunda.bpmn.editor.settings.ui.component;

import static java.awt.FlowLayout.LEFT;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import dev.camunda.bpmn.editor.settings.BpmnEditorSettings;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
public class JBpmnEditorComponent {

    private final JPanel mainPanel;
    private final JComboBox<BpmnEditorSettings.ColorTheme> colorThemeComboBox;

    /**
     * Constructs a new JBpmnEditorComponent.
     * Initializes the combo box with the available color themes and builds the form.
     */
    public JBpmnEditorComponent() {
        colorThemeComboBox = new ComboBox<>(BpmnEditorSettings.ColorTheme.values());

        var colorThemePanel = new JPanel(new FlowLayout(LEFT));
        colorThemePanel.add(new JBLabel("Color theme:"));
        colorThemePanel.add(colorThemeComboBox);

        mainPanel = new JPanel(new GridLayout(1, 1));
        mainPanel.add(colorThemePanel);
    }

    /**
     * Returns the main panel of the component.
     *
     * @return The main panel of the component
     */
    public @NotNull JPanel getPanel() {
        return mainPanel;
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
}