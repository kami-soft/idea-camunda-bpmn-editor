package dev.camunda.bpmn.editor.vfs;

import com.intellij.openapi.Disposable;
import dev.camunda.bpmn.editor.jcef.Browser;
import dev.camunda.bpmn.editor.project.ProjectService;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;

/**
 * Manages the creation, focus, and disposal of script virtual files.
 * This class is responsible for keeping track of all created script files
 * and provides methods to interact with them.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class ScriptFileManager implements Disposable {

    private final ProjectService projectService;
    private final Browser browser;
    private final Map<String, ScriptFile> scriptFiles = new ConcurrentHashMap<>(1);

    /**
     * Creates a new script virtual file.
     *
     * @param text The initial content of the script
     * @return The virtual file ID of the created script file
     */
    public String create(String text) {
        var scriptFile = new ScriptFile(text, projectService, browser, scriptFiles::remove);
        var virtualFileId = scriptFile.getVirtualFileId();
        scriptFiles.put(virtualFileId, scriptFile);
        return virtualFileId;
    }

    /**
     * Closes a script virtual file with the given virtual file ID.
     *
     * @param virtualFileId The virtual file ID of the script to close
     */
    public void close(String virtualFileId) {
        Optional.ofNullable(scriptFiles.remove(virtualFileId)).ifPresent(ScriptFile::dispose);
    }

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