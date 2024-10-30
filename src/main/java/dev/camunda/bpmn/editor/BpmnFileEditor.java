package dev.camunda.bpmn.editor;

import static com.intellij.openapi.vfs.VirtualFileUtil.readText;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import dev.camunda.bpmn.editor.browser.BpmnEditorBrowser;
import dev.camunda.bpmn.editor.util.HashComparator;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom file editor for BPMN files. This editor integrates with the BPMN Editor Browser
 * to provide a specialized interface for editing BPMN files within the IntelliJ IDEA environment.
 *
 * @author Oleksandr Havrysh
 */
public class BpmnFileEditor implements FileEditor {

    private static final String BPMN_EDITOR = "BPMN Editor";

    private final VirtualFile file;
    private final HashComparator hashComparator;
    private final BpmnEditorBrowser bpmnEditorBrowser;

    /**
     * Constructs a new BPMN file editor.
     *
     * @param project The current project
     * @param file    The BPMN file to be edited
     */
    public BpmnFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        this.file = file;

        var bpmnXml = readText(file);
        this.hashComparator = new HashComparator(bpmnXml);
        this.bpmnEditorBrowser = new BpmnEditorBrowser(project, file, bpmnXml, hashComparator);
    }

    /**
     * Returns the main component of the BPMN editor.
     *
     * @return The main component of the BPMN editor
     */
    @Override
    public @NotNull JComponent getComponent() {
        return bpmnEditorBrowser.getComponent();
    }

    /**
     * Returns the preferred component to focus when the editor is opened.
     *
     * @return The preferred component to focus
     */
    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return bpmnEditorBrowser.getComponent();
    }

    /**
     * Returns the name of the editor.
     *
     * @return The name of the editor
     */
    @Override
    public @NotNull String getName() {
        return BPMN_EDITOR;
    }

    /**
     * Sets the state of the editor.
     *
     * @param state The new state of the editor
     */
    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    /**
     * Checks if the editor content has been modified.
     *
     * @return true if the content has been modified, false otherwise
     */
    @Override
    public boolean isModified() {
        return hashComparator.isModified();
    }

    /**
     * Checks if the editor is valid.
     *
     * @return true if the editor is valid, false otherwise
     */
    @Override
    public boolean isValid() {
        return file.isValid();
    }

    /**
     * Adds a property change listener to the editor.
     *
     * @param listener The property change listener to be added
     */
    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    /**
     * Removes a property change listener from the editor.
     *
     * @param listener The property change listener to be removed
     */
    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    /**
     * Disposes of the editor and its resources.
     */
    @Override
    public void dispose() {
        bpmnEditorBrowser.dispose();
    }

    /**
     * Returns the file being edited.
     *
     * @return The file being edited
     */
    @Override
    public @NotNull VirtualFile getFile() {
        return file;
    }

    /**
     * Gets the user data associated with the given key.
     *
     * @param key The key to retrieve the user data
     * @param <T> The type of the user data
     * @return The user data associated with the key, or null if no data is associated
     */
    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    /**
     * Associates the given user data with the given key.
     *
     * @param key   The key to associate the user data
     * @param value The user data to be associated, or null to remove the association
     * @param <T>   The type of the user data
     */
    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
    }
}