package dev.camunda.bpmn.editor.service.script;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.intellij.openapi.fileEditor.FileEditorManagerListener.FILE_EDITOR_MANAGER;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import dev.camunda.bpmn.editor.service.jsquery.JSQueryService;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

/**
 * Manages the creation, focus, and disposal of script virtual files.
 * This class is responsible for keeping track of all created script files
 * and provides methods to interact with them.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class ScriptFileService {

    private final JSQueryService jsQueryService;
    private final FileEditorManager fileEditorManager;
    private final Map<String, ScriptFile> scriptFiles = new ConcurrentHashMap<>(1);
    private final MessageBusConnection messageBusConnection = getApplication().getMessageBus().connect();

    /**
     * Creates a new script virtual file.
     *
     * @param text The initial content of the script
     * @return The virtual file ID of the created script file
     */
    public String create(String text) {
        var scriptFile = new ScriptFile(text, fileEditorManager);
        var virtualFileId = scriptFile.getVirtualFileId();
        scriptFiles.put(virtualFileId, scriptFile);

        var scriptFileListener = new ScriptFileListener(scriptFile, jsQueryService, scriptFiles::remove);
        Disposer.register(scriptFile, scriptFileListener);
        messageBusConnection.subscribe(FILE_EDITOR_MANAGER, scriptFileListener);

        scriptFile.setFocus();

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

    /**
     * Disposes of all managed script files.
     * This method is called when the ScriptFileService is no longer needed.
     */
    @PreDestroy
    public void destroy() {
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