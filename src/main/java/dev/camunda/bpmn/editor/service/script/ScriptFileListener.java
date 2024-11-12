package dev.camunda.bpmn.editor.service.script;

import static java.util.Objects.isNull;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Alarm;
import dev.camunda.bpmn.editor.service.jsquery.JSQueryService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Listener class for managing script file changes and updates in the BPMN Editor.
 * This class handles file opening and closing events, as well as document changes.
 * It updates the browser accordingly and notifies registered consumers about file closure.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class ScriptFileListener implements FileEditorManagerListener, DocumentListener, Disposable.Default {

    private final ScriptFile scriptFile;
    private final JSQueryService jsQueryService;
    private final Consumer<String> closeFileConsumer;
    private final Alarm alarm = new Alarm(this);
    private final AtomicBoolean isFileClosed = new AtomicBoolean(false);
    private final AtomicBoolean isDocumentListenerRegistered = new AtomicBoolean(false);

    /**
     * Handles the file opened event.
     * Adds a document listener to the opened file if it matches the script file.
     *
     * @param source The FileEditorManager source
     * @param file   The opened VirtualFile
     */
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (scriptFile.isNotEquals(file) || isDocumentListenerRegistered.get()) {
            return;
        }

        var document = scriptFile.getDocument();
        if (isNull(document)) {
            return;
        }

        try {
            document.addDocumentListener(this);
            isDocumentListenerRegistered.set(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the file closed event.
     * Deletes the script file, updates the browser, and notifies close file consumers.
     *
     * @param source The FileEditorManager source
     * @param file   The closed VirtualFile
     */
    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (scriptFile.isNotEquals(file) || isFileClosed.get()) {
            return;
        }

        isFileClosed.set(true);
        jsQueryService.executeQueryDeleteVirtualFileId(scriptFile.getVirtualFileId());
        closeFileConsumer.accept(scriptFile.getVirtualFileId());
    }

    /**
     * Handles document change events.
     * Schedules an update of the script content with debouncing to avoid excessive updates.
     *
     * @param event The DocumentEvent containing change information
     */
    @Override
    public void documentChanged(DocumentEvent event) {
        alarm.cancelAllRequests();
        var text = event.getDocument().getText();
        alarm.addRequest(() ->
                jsQueryService.executeQueryUpdateScript(scriptFile.getVirtualFileId(), text), 500);
    }
}