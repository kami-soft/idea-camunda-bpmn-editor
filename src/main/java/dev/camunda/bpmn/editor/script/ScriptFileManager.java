package dev.camunda.bpmn.editor.script;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.jcef.JBCefBrowser;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the creation, focus, and disposal of script scratch files.
 * This class is responsible for keeping track of all created script files
 * and provides methods to interact with them.
 *
 * @author Oleksandr Havrysh
 */
public class ScriptFileManager implements Disposable {

    private final ConcurrentHashMap<String, ScriptFile> scriptFiles = new ConcurrentHashMap<>();

    /**
     * Creates a new script scratch file.
     *
     * @param project   The current project
     * @param browser   The JBCefBrowser instance
     * @param extension The file extension for the script
     * @param text      The initial content of the script
     * @return The virtual file ID of the created script file
     */
    public @NotNull String createScriptScratchFile(@NotNull Project project,
                                                   @NotNull JBCefBrowser browser,
                                                   @NotNull String extension,
                                                   String text) {
        var scratchFileCreator = new ScriptFile(project, browser, extension, text);
        var virtualFileId = scratchFileCreator.getVirtualFileId();
        scratchFileCreator.addCloseFileConsumer(scriptFiles::remove);
        scriptFiles.put(virtualFileId, scratchFileCreator);

        return virtualFileId;
    }

    /**
     * Closes a script scratch file with the given virtual file ID.
     *
     * @param virtualFileId The virtual file ID of the script to close
     */
    public void closeScriptScratchFile(String virtualFileId) {
        Optional.ofNullable(scriptFiles.remove(virtualFileId)).ifPresent(ScriptFile::close);
    }

    /**
     * Disposes of all managed script files.
     * This method is called when the ScriptFileManager is no longer needed.
     */
    @Override
    public void dispose() {
        scriptFiles.values().forEach(ScriptFile::dispose);
    }

    /**
     * Sets focus to the script file with the given virtual file ID.
     *
     * @param virtualFileId The virtual file ID of the script to focus
     */
    public void setFocus(String virtualFileId) {
        Optional.ofNullable(scriptFiles.get(virtualFileId)).ifPresent(ScriptFile::setFocus);
    }
}