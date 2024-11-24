package dev.camunda.bpmn.editor.ui.component;

import static com.intellij.ui.JBColor.RED;
import static dev.camunda.bpmn.editor.util.ComponentUtils.createNullableComboBox;
import static java.awt.FlowLayout.LEFT;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.VerticalLayout;
import dev.camunda.bpmn.editor.settings.BpmnEditorSettings;
import dev.camunda.bpmn.editor.ui.component.table.BpmnSettingsTable;
import java.awt.FlowLayout;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

/**
 * A UI component for configuring the BPMN Editor settings.
 * This class provides a comprehensive form with combo boxes and a table
 * to manage both global and file-specific settings of the BPMN Editor.
 *
 * <p>The component includes options for:</p>
 * <ul>
 *   <li>Global color theme selection</li>
 *   <li>Global schema theme selection</li>
 *   <li>Default script type selection</li>
 *   <li>Default engine selection</li>
 *   <li>File-specific settings management through a table</li>
 * </ul>
 *
 * <p>This component is designed to be used within the settings panel of the BPMN Editor
 * in the IntelliJ IDEA environment.</p>
 *
 * @author Oleksandr Havrysh
 */
public class BpmnEditorSettingsComponent extends JPanel implements Disposable {

    private final BpmnSettingsTable bpmnSettingsTable;
    private final JComboBox<BpmnEditorSettings.Engine> engineComboBox;
    private final JComboBox<BpmnEditorSettings.ScriptType> scriptTypeComboBox;
    private final JComboBox<BpmnEditorSettings.ColorTheme> colorThemeComboBox;
    private final JComboBox<BpmnEditorSettings.SchemaTheme> schemeThemeComboBox;

    /**
     * Constructs a new BpmnEditorSettingsComponent.
     * Initializes the combo boxes with the available options and builds the form.
     */
    public BpmnEditorSettingsComponent() {
        super(new VerticalLayout(5));

        this.bpmnSettingsTable = new BpmnSettingsTable();
        this.colorThemeComboBox = new ComboBox<>(BpmnEditorSettings.ColorTheme.values());
        this.schemeThemeComboBox = new ComboBox<>(BpmnEditorSettings.SchemaTheme.values());
        this.engineComboBox = createNullableComboBox(BpmnEditorSettings.Engine.values());
        this.scriptTypeComboBox = createNullableComboBox(BpmnEditorSettings.ScriptType.values());

        addDisclaimerComponents();
        addGlobalSettingComponents();
        addDiagramSettingsComponents();
    }

    /**
     * Adds disclaimer components to the settings panel.
     *
     * <p>This method adds three JLabel components to the panel, providing instructions
     * to the user about applying updated settings to currently opened BPMN files.
     * The disclaimer informs users that they need to close and reopen their BPMN files
     * to ensure that new configurations are properly loaded and applied to the diagrams.</p>
     *
     * <p>The disclaimer text is split into three lines for better readability in the UI.</p>
     */
    private void addDisclaimerComponents() {
        add(new JLabel("    To apply the updated settings to your currently opened BPMN files,"));
        add(new JLabel("    please close the files and reopen them. This ensures that the new"));
        add(new JLabel("    configuration is properly loaded and applied to the diagrams, reflecting the latest changes."));
    }

    /**
     * Adds components related to global settings to the panel.
     */
    private void addGlobalSettingComponents() {
        add(new TitledSeparator("Global Settings"));
        add(createColorThemePanel());
        add(createSchemaThemePanel());
        add(createScriptTypePanel());
        add(createEnginePanel());
    }

    /**
     * Adds components related to diagram settings to the panel.
     */
    private void addDiagramSettingsComponents() {
        add(new TitledSeparator("Diagram Settings"));
        add(new JLabel("    This is a list of used BPMN files."));
        add(new JLabel("    You can individually configure the engine, theme, and auto script format for inline scripts."));
        add(new JLabel("    You can also delete the selected row or clear the table."));
        add(new JLabel("    If you select an empty value from the dropdown, the default value will be used if it is set."));
        add(new JBScrollPane(bpmnSettingsTable));
        add(createTableButtonsPanel());
    }

    /**
     * Creates and returns a panel for selecting the color theme of the BPMN editor.
     *
     * <p>This panel contains:
     * <ul>
     *   <li>A label indicating the purpose of the combo box</li>
     *   <li>A combo box ({@link #colorThemeComboBox}) for selecting the color theme</li>
     * </ul>
     * </p>
     *
     * <p>The panel uses a {@link FlowLayout} with left alignment to arrange its components.
     * This ensures a consistent layout with other setting panels in the component.</p>
     *
     * @return A {@link JPanel} containing the color theme selection components
     */
    private @NotNull JPanel createColorThemePanel() {
        var colorThemePanel = new JPanel(new FlowLayout(LEFT));
        colorThemePanel.add(new JBLabel("   Color theme:"));
        colorThemePanel.add(colorThemeComboBox);

        return colorThemePanel;
    }

    /**
     * Creates and returns a panel for selecting the schema theme.
     *
     * <p>This panel contains:
     * <ul>
     *   <li>A label indicating the purpose of the combo box</li>
     *   <li>A combo box ({@link #schemeThemeComboBox}) for selecting the schema theme</li>
     * </ul>
     * </p>
     *
     * <p>The panel uses a {@link FlowLayout} with left alignment to arrange its components.</p>
     *
     * @return A {@link JPanel} containing the schema theme selection components
     */
    private @NotNull JPanel createSchemaThemePanel() {
        var schemeThemePanel = new JPanel(new FlowLayout(LEFT));
        schemeThemePanel.add(new JBLabel("   Scheme theme:"));
        schemeThemePanel.add(schemeThemeComboBox);

        return schemeThemePanel;
    }

    /**
     * Creates the panel for selecting the default script type.
     *
     * @return The panel for selecting the default script type
     */
    private @NotNull JPanel createScriptTypePanel() {
        var scriptTypePanel = new JPanel(new FlowLayout(LEFT));
        scriptTypePanel.add(new JBLabel("   Default script type:"));
        scriptTypePanel.add(scriptTypeComboBox);

        return scriptTypePanel;
    }

    /**
     * Creates the panel for selecting the default engine.
     *
     * @return The panel for selecting the default engine
     */
    private @NotNull JPanel createEnginePanel() {
        var enginePanel = new JPanel(new FlowLayout(LEFT));
        enginePanel.add(new JBLabel("   Default engine:"));
        enginePanel.add(engineComboBox);

        return enginePanel;
    }

    /**
     * Creates the panel with buttons for managing the table.
     *
     * @return The panel with buttons for managing the table
     */
    private @NotNull JPanel createTableButtonsPanel() {
        var deleteRowButton = new JButton("Delete Row");
        deleteRowButton.addActionListener(e -> bpmnSettingsTable.deleteSelectedRow());
        deleteRowButton.setEnabled(false);
        deleteRowButton.setForeground(RED);
        bpmnSettingsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteRowButton.setEnabled(bpmnSettingsTable.getSelectedRow() != -1);
            }
        });

        var clearTableButton = new JButton("Clear Table");
        clearTableButton.addActionListener(e -> bpmnSettingsTable.clearAll());
        clearTableButton.setForeground(RED);

        var tableButtonPanel = new JPanel(new FlowLayout());
        tableButtonPanel.add(deleteRowButton);
        tableButtonPanel.add(clearTableButton);

        return tableButtonPanel;
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
     * Retrieves the currently selected schema theme.
     *
     * @return The currently selected {@link BpmnEditorSettings.SchemaTheme}
     */
    public BpmnEditorSettings.SchemaTheme getSchemaThemeValue() {
        return (BpmnEditorSettings.SchemaTheme) schemeThemeComboBox.getSelectedItem();
    }

    /**
     * Sets the selected schema theme in the combo box.
     *
     * @param schemaTheme The {@link BpmnEditorSettings.SchemaTheme} to be selected
     */
    public void setSchemaThemeValue(BpmnEditorSettings.SchemaTheme schemaTheme) {
        schemeThemeComboBox.setSelectedItem(schemaTheme);
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

    /**
     * Returns the currently selected engine.
     *
     * @return The currently selected engine
     */
    public BpmnEditorSettings.Engine getEngineValue() {
        return (BpmnEditorSettings.Engine) engineComboBox.getSelectedItem();
    }

    /**
     * Sets the selected engine in the combo box.
     *
     * @param engine The engine to be selected
     */
    public void setEngineValue(BpmnEditorSettings.Engine engine) {
        engineComboBox.setSelectedItem(engine);
    }

    /**
     * Sets the file settings in the table.
     *
     * @param fileSettings The file settings to be set
     */
    public void setFileSettings(Map<String, BpmnEditorSettings.FileSettings> fileSettings) {
        bpmnSettingsTable.setFileSettings(fileSettings);
    }

    /**
     * Returns the file settings from the table.
     *
     * @return The file settings from the table
     */
    public Map<String, BpmnEditorSettings.FileSettings> getFileSettings() {
        return bpmnSettingsTable.getFileSettings();
    }

    /**
     * Disposes of the resources used by this component.
     */
    @Override
    public void dispose() {
        removeAll();
        scriptTypeComboBox.removeAll();
        colorThemeComboBox.removeAll();
        engineComboBox.removeAll();
        schemeThemeComboBox.removeAll();
    }
}