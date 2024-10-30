package dev.camunda.bpmn.editor.scratch;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static java.util.Objects.nonNull;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.util.Alarm;
import dev.camunda.bpmn.editor.browser.jsquery.impl.DeleteVirtualFileIdJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.impl.UpdateScriptJSQuery;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * Listener class for managing script file changes and updates in the BPMN Editor.
 * This class handles file opening and closing events, as well as document changes.
 * It updates the browser accordingly and notifies registered consumers about file closure.
 *
 * @author Oleksandr Havrysh
 */
public class ScriptFileListener implements FileEditorManagerListener, DocumentListener, Disposable {

    private final Alarm updateAlarm;
    private final ScriptFile scriptFile;
    private final AtomicBoolean isFileClosed;
    private final WeakReference<JBCefBrowser> browserRef;
    private final AtomicBoolean isDocumentListenerRegistered;
    private final List<Consumer<String>> closeFileConsumers = new CopyOnWriteArrayList<>();

    /**
     * Constructs a new ScriptFileListener.
     *
     * @param browser       The JBCefBrowser instance to update
     * @param scriptFile    The ScriptFile representing the script file
     */
    public ScriptFileListener(@NotNull JBCefBrowser browser, @NotNull ScriptFile scriptFile) {
        this.scriptFile = scriptFile;
        this.browserRef = new WeakReference<>(browser);
        this.updateAlarm = new Alarm(this);
        this.isFileClosed = new AtomicBoolean(false);
        this.isDocumentListenerRegistered = new AtomicBoolean(false);

        try {
            getApplication().getMessageBus().connect().subscribe(FILE_EDITOR_MANAGER, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a consumer to be notified when the file is closed.
     *
     * @param consumer The consumer to add
     */
    public void addCloseFileConsumer(@NotNull Consumer<String> consumer) {
        closeFileConsumers.add(consumer);
    }

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
        if (nonNull(document)) {
            try {
                document.addDocumentListener(this);
                isDocumentListenerRegistered.set(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        getApplication().invokeLater(() -> getApplication().runWriteAction(() -> {
            try {
                scriptFile.delete();
                isFileClosed.set(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        Optional.ofNullable(browserRef.get())
                .ifPresent(browser -> new DeleteVirtualFileIdJSQuery(browser, scriptFile.getVirtualFileId())
                        .executeQuery());
        closeFileConsumers.forEach(closeFileConsumer -> closeFileConsumer.accept(scriptFile.getVirtualFileId()));
    }

    /**
     * Handles document change events.
     * Schedules an update of the script content with debouncing to avoid excessive updates.
     *
     * @param event The DocumentEvent containing change information
     */
    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        updateAlarm.cancelAllRequests();
        updateAlarm.addRequest(() -> updateScript(event.getDocument().getText()), 300);
    }

    /**
     * Updates the script content in the browser.
     * This method is called after the debounce period to reflect the latest changes.
     *
     * @param content The new content of the script
     */
    private void updateScript(@NotNull String content) {
        Optional.ofNullable(browserRef.get())
                .ifPresent(browser -> new UpdateScriptJSQuery(browser, scriptFile.getVirtualFileId(), content)
                        .executeQuery());
    }

    @Override
    public void dispose() {
        browserRef.clear();
        closeFileConsumers.clear();
    }
}