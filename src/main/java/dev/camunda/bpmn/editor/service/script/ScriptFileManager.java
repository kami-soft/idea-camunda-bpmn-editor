package dev.camunda.bpmn.editor.service.script;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserWrapper;
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

    private final Project project;
    private final JBCefBrowserWrapper browser;
    private final Map<String, ScriptFile> scriptFiles = new ConcurrentHashMap<>(1);

    /**
     * Creates a new script virtual file.
     *
     * @param text The initial content of the script
     * @return The virtual file ID of the created script file
     */
    public String create(String text) {
        var scriptFile = new ScriptFile(text, project, browser, scriptFiles::remove);
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
        Optional.ofNullable(scriptFiles.remove(virtualFileId)).ifPresent(ScriptFile::close);
    }

    @Override
    public void dispose() {
        scriptFiles.values().forEach(ScriptFile::close);
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