package dev.camunda.bpmn.editor.fileEditor;

import static com.intellij.openapi.fileEditor.FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
import static java.util.Objects.nonNull;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * A provider for BPMN file editors. This class is responsible for determining whether a given file
 * can be opened with a BPMN editor and for creating instances of the BPMN editor.
 *
 * @author Oleksandr Havrysh
 */
public class BpmnFileEditorProvider implements FileEditorProvider {

    private static final String BPMN_EDITOR = "bpmn-editor";
    private static final List<String> SUPPORTED_EXTENSIONS = List.of(".bpmn", ".bpmn20.xml");

    /**
     * Determines whether the given file can be opened with a BPMN editor.
     *
     * @param project The current project
     * @param file    The file to be checked
     * @return true if the file has a "bpmn" extension, false otherwise
     */
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return nonNull(file.getExtension())
                && SUPPORTED_EXTENSIONS.stream().anyMatch(extension -> file.getName().endsWith(extension));
    }

    /**
     * Creates a new instance of the BPMN editor for the given file.
     *
     * @param project The current project
     * @param file    The file to be opened with the BPMN editor
     * @return A new instance of {@link BpmnFileEditor}
     */
    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new BpmnFileEditor(project, file);
    }

    /**
     * Returns the editor type ID for the BPMN editor.
     *
     * @return The editor type ID
     */
    @Override
    public @NotNull String getEditorTypeId() {
        return BPMN_EDITOR;
    }

    /**
     * Returns the policy for placing the BPMN editor in the editor tab.
     *
     * @return The policy for placing the BPMN editor
     */
    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return PLACE_BEFORE_DEFAULT_EDITOR;
    }
}