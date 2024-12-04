package dev.camunda.bpmn.editor.jcef.jsquery;

import static dev.camunda.bpmn.editor.util.Base64Utils.encode;
import static lombok.AccessLevel.PRIVATE;

import com.intellij.openapi.application.WriteAction;
import dev.camunda.bpmn.editor.jcef.Browser;
import dev.camunda.bpmn.editor.project.ClipboardManager;
import dev.camunda.bpmn.editor.project.ProjectService;
import dev.camunda.bpmn.editor.server.Server;
import dev.camunda.bpmn.editor.vfs.BpmnFile;
import dev.camunda.bpmn.editor.vfs.ScriptFileManager;
import lombok.NoArgsConstructor;

/**
 * Factory class for creating various JSQuery objects used in the BPMN editor.
 * These queries facilitate communication between the Java backend and the JavaScript frontend.
 * Each method in this class creates a specific type of JSQuery for different operations
 * such as file management, clipboard interactions, and BPMN content manipulation.
 * <p>
 * This class is designed as a utility class with only static methods and should not be instantiated.
 *
 * @author Oleksandr Havrysh
 */
@NoArgsConstructor(access = PRIVATE)
public final class JSQueryFactory {

    /**
     * Creates a JSQuery for closing a script file.
     *
     * @param browser           The JBCefBrowserWrapper instance for browser interaction
     * @param scriptFileManager The ScriptFileManager instance for managing script files
     * @return A JSQuery for closing a script file
     */
    public static JSQuery createCloseScriptFileJSQuery(Browser browser, ScriptFileManager scriptFileManager) {
        return new JSQuery("closeScriptExternalFile", browser, scriptFileManager::close, 0);
    }

    /**
     * Creates a JSQuery for opening a script file.
     *
     * @param browser           The JBCefBrowserWrapper instance for browser interaction
     * @param scriptFileManager The ScriptFileManager instance for managing script files
     * @return A JSQuery for opening a script file
     */
    public static JSQuery createOpenScriptFileJSQuery(Browser browser, ScriptFileManager scriptFileManager) {
        return new JSQuery("openScriptExternalFile", browser, scriptFileManager::create);
    }

    /**
     * Creates a JSQuery for saving BPMN XML content.
     *
     * @param browser  The JBCefBrowserWrapper instance for browser interaction
     * @param bpmnFile The VirtualFileService to save the BPMN content
     * @return A JSQuery for saving BPMN XML content
     */
    public static JSQuery createSaveBpmnJSQuery(Browser browser, BpmnFile bpmnFile) {
        return new JSQuery("updateBpmnXml", browser,
                encodedXml -> WriteAction.run(() -> bpmnFile.saveEncodedContent(encodedXml)),
                500);
    }

    /**
     * Creates a JSQuery for setting clipboard content.
     *
     * @param browser          The JBCefBrowserWrapper instance for browser interaction
     * @param clipboardManager The ClipboardService instance for clipboard operations
     * @return A JSQuery for setting clipboard content
     */
    public static JSQuery createSetClipboardJSQuery(Browser browser, ClipboardManager clipboardManager) {
        return new JSQuery("copyBpmnClipboard", browser, clipboardManager::setContent, 10);
    }

    /**
     * Creates a JSQuery for setting focus on a script file.
     *
     * @param browser           The JBCefBrowserWrapper instance for browser interaction
     * @param scriptFileManager The ScriptFileManager instance for managing script files
     * @return A JSQuery for setting focus on a script file
     */
    public static JSQuery createSetFocusScriptFileJSQuery(Browser browser, ScriptFileManager scriptFileManager) {
        return new JSQuery("setFocusVirtualFile", browser, scriptFileManager::setFocus, 0);
    }

    /**
     * Creates a JSQuery for initializing the BPMN editor with XML content.
     *
     * @param browser  The JBCefBrowserWrapper instance for browser interaction
     * @param bpmnFile The VirtualFileService to initialize the BPMN editor
     * @return A JSQuery for initializing the BPMN editor
     */
    public static JSQuery createInitBpmnJSQuery(Browser browser, BpmnFile bpmnFile) {
        return new JSQuery("""
                window.bpmnXml = `%s`;
                initApp();""".formatted(bpmnFile.getEncodedContent()), browser);
    }

    /**
     * Creates a JSQuery for deleting a virtual file ID.
     *
     * @param browser       The JBCefBrowserWrapper instance for browser interaction
     * @param virtualFileId The ID of the virtual file to be deleted
     * @return A JSQuery for deleting a virtual file ID
     */
    public static JSQuery createDeleteVirtualFileIdJSQuery(Browser browser, String virtualFileId) {
        return new JSQuery("deleteVirtualFileId('%s');".formatted(virtualFileId), browser);
    }

    /**
     * Creates a JSQuery for updating a script in the editor.
     *
     * @param browser       The JBCefBrowserWrapper instance for browser interaction
     * @param virtualFileId The ID of the virtual file associated with the script
     * @param script        The updated script content
     * @return A JSQuery for updating a script in the editor
     */
    public static JSQuery createUpdateScriptJSQuery(Browser browser, String virtualFileId, String script) {
        return new JSQuery("updateScript('%s', `%s`);".formatted(virtualFileId, encode(script)), browser);
    }

    /**
     * Creates a JSQuery for setting the base URL for the HTTP server in the BPMN editor.
     *
     * @param browser The JBCefBrowserWrapper instance for browser interaction
     * @param server  The HttpServerWrapper instance containing the server port
     * @return A JSQuery object that, when executed, will set the base URL for the HTTP server in the editor
     */
    public static JSQuery createSetBaseUrlJSQuery(Browser browser, Server server) {
        return new JSQuery("window.serverBaseUrl = `http://localhost:%s`;".formatted(server.getPort()), browser);
    }

    /**
     * Creates a JSQuery for setting the BPMN lint configuration in the editor.
     * This method retrieves the content of the .bpmnlintrc file from the project
     * and creates a JSQuery to set this configuration in the JavaScript environment.
     *
     * @param browser        The JBCefBrowserWrapper instance for browser interaction
     * @param projectService The ProjectService instance used to find the .bpmnlintrc file
     * @return A JSQuery object that, when executed, will set the BPMN lint configuration in the editor
     */
    public static JSQuery createSetBpmnLintrcJSQuery(Browser browser, ProjectService projectService) {
        var bpmnlintrc = projectService.findContentByFileName(".bpmnlintrc").orElse("");
        return new JSQuery("window.bpmnlintrc = `%s`;".formatted(bpmnlintrc), browser);
    }

    /**
     * Creates a JSQuery for showing error notifications in the browser.
     * <p>
     * This method sets up a communication channel between the JavaScript running in the browser
     * and the Java-side ProjectService.
     * It allows JavaScript code to trigger error notifications
     * that will be displayed in the IntelliJ IDEA environment.
     *
     * @param browser        The Browser instance where the JavaScript will run.
     * @param projectService The ProjectService instance that will handle showing the notifications.
     * @return A JSQuery instance configured to show error notifications.
     */
    public static JSQuery createShowErrorNotifictionJSQuery(Browser browser, ProjectService projectService) {
        return new JSQuery("showErrorNotification", browser, projectService::showErrorNotification, 10);
    }
}