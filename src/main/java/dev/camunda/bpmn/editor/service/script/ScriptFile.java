package dev.camunda.bpmn.editor.service.script;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static dev.camunda.bpmn.editor.util.Base64Utils.decode;
import static dev.camunda.bpmn.editor.util.Constants.DOT;
import static dev.camunda.bpmn.editor.util.Constants.EMPTY;
import static dev.camunda.bpmn.editor.util.Constants.JAVASCRIPT;
import static dev.camunda.bpmn.editor.util.Constants.JS;
import static java.lang.String.join;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import lombok.Getter;

/**
 * A class responsible for creating and managing script files in the BPMN Editor.
 * This class allows for the creation of scratch files with different languages based on the file extension.
 * It also provides methods to manage the lifecycle of the script file, such as setting focus, and deleting the file.
 * The class implements {@link Disposable} to ensure proper cleanup of resources.
 *
 * @author Oleksandr Havrysh
 */
public class ScriptFile implements Disposable {

    @Getter
    private final Document document;

    @Getter
    private final String virtualFileId;
    private final VirtualFile scriptFile;
    private final FileEditorManager fileEditorManager;

    /**
     * Creates a new ScriptFile instance.
     *
     * @param text                The initial content of the script
     * @param fileEditorManager   The FileEditorManager instance
     * @param fileDocumentManager The FileDocumentManager instance
     * @param fileTypeManager     The FileTypeManager instance
     * @throws RuntimeException if the scratch file creation fails
     */
    public ScriptFile(String text,
                      FileEditorManager fileEditorManager,
                      FileDocumentManager fileDocumentManager,
                      FileTypeManager fileTypeManager) {
        this.virtualFileId = randomUUID().toString();
        this.fileEditorManager = fileEditorManager;

        text = isBlank(text) ? EMPTY : text;
        var scriptData = text.split("@");
        var extension = formatExtension(scriptData[1]);
        var fileName = join(DOT, virtualFileId, extension);
        var fileType = fileTypeManager.getFileTypeByExtension(extension);
        this.scriptFile = new LightVirtualFile(fileName, fileType, decode(scriptData[0]));
        this.document = fileDocumentManager.getDocument(scriptFile);
    }

    /**
     * Formats the file extension to ensure it is valid.
     *
     * @param extension The original file extension
     * @return The formatted file extension
     */
    private String formatExtension(String extension) {
        return JAVASCRIPT.equalsIgnoreCase(extension) ? JS : extension;
    }

    /**
     * Sets focus to the created scratch file.
     */
    public void setFocus() {
        getApplication().invokeLater(() -> fileEditorManager.openFile(scriptFile, true));
    }

    /**
     * Checks if the given file is not equal to the current script file.
     *
     * @param file The file to compare
     * @return true if the given file is not equal to the current script file, false otherwise
     */
    public boolean isNotEquals(VirtualFile file) {
        return !scriptFile.equals(file);
    }

    /**
     * Closes the scratch file.
     * This method should be called when the file is no longer needed.
     */
    public void close() {
        getApplication().invokeLater(() -> fileEditorManager.closeFile(scriptFile));
    }

    /**
     * Disposes of the script file and its associated resources.
     * This method is called to ensure proper cleanup of resources.
     */
    @Override
    public void dispose() {
        close();
    }
}