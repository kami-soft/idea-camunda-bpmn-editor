package dev.camunda.bpmn.editor.script;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static java.util.Objects.isNull;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.GroovyLanguage;

/**
 * A class responsible for creating and managing script files in the BPMN Editor.
 * This class allows for the creation of scratch files with different languages based on the file extension.
 * It also provides methods to manage the lifecycle of the script file, such as setting focus, adding close file consumers, and deleting the file.
 * The class implements {@link Disposable} to ensure proper cleanup of resources.
 *
 * @author Oleksandr Havrysh
 */
public class ScriptFile implements Disposable {

    private static final String DOT = ".";
    private static final String EMPTY_SCRIPT = "";
    private static final Map<Extension, Language> LANGUAGE_MAP = new EnumMap<>(Extension.class);

    private final Project project;

    @Getter
    private final String virtualFileId;
    private final VirtualFile scriptFile;
    private final ScriptFileListener scriptFileListener;

    /**
     * Enum representing supported file extensions.
     */
    private enum Extension {
        GROOVY, JAVA, OTHER, TXT
    }

    static {
        LANGUAGE_MAP.put(Extension.GROOVY, GroovyLanguage.INSTANCE);
        LANGUAGE_MAP.put(Extension.JAVA, JavaLanguage.INSTANCE);
        LANGUAGE_MAP.put(Extension.TXT, PlainTextLanguage.INSTANCE);
    }

    /**
     * Creates a new ScriptFile instance.
     *
     * @param project   The current project
     * @param browser   The JBCefBrowser instance
     * @param extension The file extension for the script
     * @param text      The initial content of the script
     * @throws RuntimeException if the scratch file creation fails
     */
    public ScriptFile(@NotNull Project project,
                      @NotNull JBCefBrowser browser,
                      @NotNull String extension,
                      @Nullable String text) {
        this.project = project;
        this.virtualFileId = randomUUID().toString();

        var language = determineLanguage(extension);
        var fileName = String.join(DOT, virtualFileId, extension);
        text = isBlank(text) ? EMPTY_SCRIPT : text;
        this.scriptFile = ScratchRootType.getInstance().createScratchFile(project, fileName, language, text);

        if (isNull(scriptFile)) {
            throw new RuntimeException("Failed to create scratch file");
        }

        setFocus();
        this.scriptFileListener = new ScriptFileListener(browser, this);
    }

    /**
     * Determines the language based on the file extension.
     *
     * @param extension The file extension
     * @return The corresponding Language instance
     */
    private static @NotNull Language determineLanguage(@NotNull String extension) {
        var ext = Extension.valueOf(extension.toUpperCase());
        return LANGUAGE_MAP.getOrDefault(ext, PlainTextLanguage.INSTANCE);
    }

    /**
     * Adds a consumer to be notified when the file is closed.
     *
     * @param consumer The consumer to be added
     */
    public void addCloseFileConsumer(Consumer<String> consumer) {
        scriptFileListener.addCloseFileConsumer(consumer);
    }

    /**
     * Sets focus to the created scratch file.
     */
    public void setFocus() {
        getApplication().invokeLater(() -> FileEditorManager.getInstance(project).openFile(scriptFile, true));
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
     * Deletes the script file.
     *
     * @throws IOException if an I/O error occurs
     */
    public void delete() throws IOException {
        scriptFile.delete(this);
    }

    /**
     * Gets the document associated with the script file.
     *
     * @return The document associated with the script file
     */
    public Document getDocument() {
        return FileDocumentManager.getInstance().getDocument(scriptFile);
    }

    /**
     * Closes the scratch file.
     * This method should be called when the file is no longer needed.
     */
    public void close() {
        getApplication().invokeLater(() -> FileEditorManager.getInstance(project).closeFile(scriptFile));
    }

    /**
     * Disposes of the script file and its associated resources.
     * This method is called to ensure proper cleanup of resources.
     */
    @Override
    public void dispose() {
        close();
        scriptFileListener.dispose();
    }
}