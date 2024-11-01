package dev.camunda.bpmn.editor.config;

import static com.intellij.openapi.application.ApplicationManager.getApplication;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.serviceContainer.NonInjectable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * A class that manages the settings for the BPMN Editor. This class is responsible for
 * persisting and loading the editor settings, such as the color theme.
 *
 * @author Oleksandr Havrysh
 */
@Data
@NoArgsConstructor(onConstructor_ = {@NonInjectable})
@State(name = "dev.camunda.bpmn.editor.settings.BpmnEditorSettings", storages = @Storage("BpmnEditorSettings.xml"))
public final class BpmnEditorSettings implements PersistentStateComponent<BpmnEditorSettings.State> {

    /**
     * A class representing the state of the BPMN Editor settings.
     */
    @Data
    public static class State {

        /**
         * The color theme of the BPMN Editor.
         */
        @NotNull
        private ColorTheme colorTheme = ColorTheme.DARK;

        @NotNull
        private ScriptType scriptType = ScriptType.NONE;
    }

    @NotNull
    private State state = new State();

    /**
     * Returns the instance of the BPMN Editor settings.
     *
     * @return The instance of the BPMN Editor settings
     */
    public static @NotNull BpmnEditorSettings getInstance() {
        return Optional.ofNullable(getApplication().getService(BpmnEditorSettings.class))
                .orElseGet(BpmnEditorSettings::new);
    }

    /**
     * Loads the given state into the BPMN Editor settings.
     *
     * @param state The state to be loaded
     */
    @Override
    public void loadState(@NotNull BpmnEditorSettings.State state) {
        this.state = state;
    }

    /**
     * An enum representing the color theme of the BPMN Editor.
     */
    public enum ColorTheme {LIGHT, DARK}

    @Getter
    @AllArgsConstructor
    public enum ScriptType {
        NONE(""),
        GROOVY("groovy"),
        JAVASCRIPT("javascript");

        private final String value;
    }
}