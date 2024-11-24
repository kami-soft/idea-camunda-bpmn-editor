package dev.camunda.bpmn.editor.ui;

import static java.util.Objects.nonNull;

import com.intellij.openapi.options.Configurable;
import dev.camunda.bpmn.editor.settings.BpmnEditorSettings;
import dev.camunda.bpmn.editor.ui.component.BpmnEditorSettingsComponent;
import java.util.Optional;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * A configurable settings panel for the BPMN Editor. This class implements the {@link Configurable}
 * interface to provide a custom settings UI for the BPMN Editor within the IntelliJ IDEA settings dialog.
 *
 * <p>The settings panel allows users to configure the color theme of the BPMN Editor. The settings
 * are persisted using the {@link BpmnEditorSettings} class.</p>
 *
 * @author Oleksandr Havrysh
 */
public class BpmnEditorConfigurable implements Configurable {

    private static final String BPMN_EDITOR_SETTINGS = "Camunda BPMN Editor Settings";

    private BpmnEditorSettingsComponent bpmnEditorSettingsComponent;

    /**
     * Returns the display name of the settings panel.
     *
     * @return The display name of the settings panel
     */
    @Nls
    @Override
    public @NotNull String getDisplayName() {
        return BPMN_EDITOR_SETTINGS;
    }

    /**
     * Returns the preferred component to focus when the settings panel is opened.
     *
     * @return The preferred component to focus
     */
    @Override
    public @NotNull JComponent getPreferredFocusedComponent() {
        return Optional.ofNullable(bpmnEditorSettingsComponent)
                .map(BpmnEditorSettingsComponent::getPreferredFocusedComponent)
                .orElse(new JPanel());
    }

    /**
     * Creates and returns the main component of the settings panel.
     *
     * @return The main component of the settings panel
     */
    @Override
    public @NotNull JComponent createComponent() {
        return this.bpmnEditorSettingsComponent = new BpmnEditorSettingsComponent();
    }

    /**
     * Checks if the settings have been modified.
     *
     * @return true if the settings have been modified, false otherwise
     */
    @Override
    public boolean isModified() {
        var state = BpmnEditorSettings.getInstance().getState();
        return Optional.ofNullable(bpmnEditorSettingsComponent)
                .map(component -> component.getColorThemeValue() != state.getColorTheme()
                        || component.getSchemaThemeValue() != state.getSchemaTheme()
                        || component.getScriptTypeValue() != state.getScriptType()
                        || component.getEngineValue() != state.getEngine()
                        || !component.getFileSettings().equals(state.getFileSettings()))
                .orElse(false);
    }

    /**
     * Applies the modified settings.
     */
    @Override
    public void apply() {
        var state = BpmnEditorSettings.getInstance().getState();
        Optional.ofNullable(bpmnEditorSettingsComponent).ifPresent(component -> {
            state.setColorTheme(component.getColorThemeValue());
            state.setSchemaTheme(component.getSchemaThemeValue());
            state.setScriptType(component.getScriptTypeValue());
            state.setEngine(component.getEngineValue());
            state.setFileSettings(component.getFileSettings());
        });
    }

    /**
     * Resets the settings to their original values.
     */
    @Override
    public void reset() {
        var state = BpmnEditorSettings.getInstance().getState();
        Optional.ofNullable(bpmnEditorSettingsComponent).ifPresent(component -> {
            component.setColorThemeValue(state.getColorTheme());
            component.setSchemaThemeValue(state.getSchemaTheme());
            component.setScriptTypeValue(state.getScriptType());
            component.setEngineValue(state.getEngine());
            component.setFileSettings(state.getFileSettings());
        });
    }

    /**
     * Disposes of the UI resources used by the settings panel.
     */
    @Override
    public void disposeUIResources() {
        if (nonNull(bpmnEditorSettingsComponent)) {
            bpmnEditorSettingsComponent.dispose();
            bpmnEditorSettingsComponent = null;
        }
    }
}