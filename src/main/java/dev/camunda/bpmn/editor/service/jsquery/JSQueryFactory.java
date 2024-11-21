package dev.camunda.bpmn.editor.service.jsquery;

import static com.intellij.openapi.vfs.VirtualFileUtil.writeBytes;
import static dev.camunda.bpmn.editor.util.Base64Utils.decodeBytes;
import static dev.camunda.bpmn.editor.util.Base64Utils.encode;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.VirtualFile;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserWrapper;
import dev.camunda.bpmn.editor.service.clipboard.ClipboardService;
import dev.camunda.bpmn.editor.service.script.ScriptFileManager;
import dev.camunda.bpmn.editor.util.HashComparator;

/**
 * Factory class for creating various JSQuery objects used in the BPMN editor.
 * These queries facilitate communication between the Java backend and the JavaScript frontend.
 * Each method in this class creates a specific type of JSQuery for different operations
 * such as file management, clipboard interactions, and BPMN content manipulation.
 */
public class JSQueryFactory {

    /**
     * Creates a JSQuery for closing a script file.
     *
     * @param browser           The JBCefBrowserWrapper instance for browser interaction
     * @param scriptFileManager The ScriptFileManager instance for managing script files
     * @return A JSQuery for closing a script file
     */
    public static JSQuery createCloseScriptFileJSQuery(JBCefBrowserWrapper browser,
                                                       ScriptFileManager scriptFileManager) {
        return new JSQuery(browser, "closeScriptExternalFile", scriptFileManager::close, 0);
    }

    /**
     * Creates a JSQuery for retrieving clipboard content.
     *
     * @param browser          The JBCefBrowserWrapper instance for browser interaction
     * @param clipboardService The ClipboardService instance for clipboard operations
     * @return A JSQuery for getting clipboard content
     */
    public static JSQuery createGetClipboardJSQuery(JBCefBrowserWrapper browser,
                                                    ClipboardService clipboardService) {
        return new JSQuery(browser, "getBpmnClipboard", text -> clipboardService.getContent());
    }

    /**
     * Creates a JSQuery for opening a script file.
     *
     * @param browser           The JBCefBrowserWrapper instance for browser interaction
     * @param scriptFileManager The ScriptFileManager instance for managing script files
     * @return A JSQuery for opening a script file
     */
    public static JSQuery createOpenScriptFileJSQuery(JBCefBrowserWrapper browser,
                                                      ScriptFileManager scriptFileManager) {
        return new JSQuery(browser, "openScriptExternalFile", scriptFileManager::create);
    }

    /**
     * Creates a JSQuery for saving BPMN XML content.
     *
     * @param browser        The JBCefBrowserWrapper instance for browser interaction
     * @param file           The VirtualFile to save the BPMN content to
     * @param hashComparator The HashComparator instance for comparing content hashes
     * @return A JSQuery for saving BPMN XML content
     */
    public static JSQuery createSaveBpmnJSQuery(JBCefBrowserWrapper browser,
                                                VirtualFile file,
                                                HashComparator hashComparator) {
        return new JSQuery(browser, "updateBpmnXml",
                encodedXml -> saveBpmn(file, hashComparator, encodedXml), 500);
    }

    /**
     * Creates a JSQuery for setting clipboard content.
     *
     * @param browser          The JBCefBrowserWrapper instance for browser interaction
     * @param clipboardService The ClipboardService instance for clipboard operations
     * @return A JSQuery for setting clipboard content
     */
    public static JSQuery createSetClipboardJSQuery(JBCefBrowserWrapper browser,
                                                    ClipboardService clipboardService) {
        return new JSQuery(browser, "copyBpmnClipboard", clipboardService::setContent, 10);
    }

    /**
     * Creates a JSQuery for setting focus on a script file.
     *
     * @param browser           The JBCefBrowserWrapper instance for browser interaction
     * @param scriptFileManager The ScriptFileManager instance for managing script files
     * @return A JSQuery for setting focus on a script file
     */
    public static JSQuery createSetFocusScriptFileJSQuery(JBCefBrowserWrapper browser,
                                                          ScriptFileManager scriptFileManager) {
        return new JSQuery(browser, "setFocusVirtualFile", scriptFileManager::setFocus, 0);
    }

    /**
     * Creates a JSQuery for initializing the BPMN editor with XML content.
     *
     * @param browser    The JBCefBrowserWrapper instance for browser interaction
     * @param originBpmn The original BPMN XML content to initialize the editor with
     * @return A JSQuery for initializing the BPMN editor
     */
    public static JSQuery createInitBpmnJSQuery(JBCefBrowserWrapper browser, String originBpmn) {
        return new JSQuery("window.bpmnXml = `%s`;\ninitApp();".formatted(encode(originBpmn)), browser);
    }

    /**
     * Creates a JSQuery for deleting a virtual file ID.
     *
     * @param browser       The JBCefBrowserWrapper instance for browser interaction
     * @param virtualFileId The ID of the virtual file to be deleted
     * @return A JSQuery for deleting a virtual file ID
     */
    public static JSQuery createDeleteVirtualFileIdJSQuery(JBCefBrowserWrapper browser, String virtualFileId) {
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
    public static JSQuery createUpdateScriptJSQuery(JBCefBrowserWrapper browser, String virtualFileId, String script) {
        return new JSQuery("updateScript('%s', `%s`);".formatted(virtualFileId, encode(script)), browser);
    }

    /**
     * Saves the BPMN XML content to a file.
     *
     * @param file           The VirtualFile to save the content to
     * @param hashComparator The HashComparator instance for updating content hash
     * @param encodedXml     The Base64 encoded XML content
     */
    private static void saveBpmn(VirtualFile file, HashComparator hashComparator, String encodedXml) {
        WriteAction.run(() -> {
            var xml = decodeBytes(encodedXml);
            writeBytes(file, xml);
            hashComparator.updateHash(xml);
        });
    }
}