package dev.camunda.bpmn.editor;

import static com.intellij.openapi.vfs.VirtualFileUtil.readText;
import static dev.camunda.bpmn.editor.service.jsquery.JSQueryFactory.createCloseScriptFileJSQuery;
import static dev.camunda.bpmn.editor.service.jsquery.JSQueryFactory.createGetClipboardJSQuery;
import static dev.camunda.bpmn.editor.service.jsquery.JSQueryFactory.createInitBpmnJSQuery;
import static dev.camunda.bpmn.editor.service.jsquery.JSQueryFactory.createOpenScriptFileJSQuery;
import static dev.camunda.bpmn.editor.service.jsquery.JSQueryFactory.createSaveBpmnJSQuery;
import static dev.camunda.bpmn.editor.service.jsquery.JSQueryFactory.createSetClipboardJSQuery;
import static dev.camunda.bpmn.editor.service.jsquery.JSQueryFactory.createSetFocusScriptFileJSQuery;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserService;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserWrapper;
import dev.camunda.bpmn.editor.service.clipboard.ClipboardService;
import dev.camunda.bpmn.editor.service.script.ScriptFileManager;
import dev.camunda.bpmn.editor.service.server.HttpServerWrapper;
import dev.camunda.bpmn.editor.util.HashComparator;
import java.util.List;
import lombok.Getter;

/**
 * Represents the context for the BPMN editor, managing various services and resources
 * required for its operation. This class serves as a central point for coordinating
 * different components of the BPMN editor, including browser integration, JavaScript
 * query management, script file handling, and clipboard operations.
 *
 * <p>The BpmnEditorContext is responsible for:
 * <ul>
 *   <li>Initializing and managing the JBCef browser instance</li>
 *   <li>Setting up and coordinating JavaScript queries for editor functionality</li>
 *   <li>Managing script files associated with the BPMN diagram</li>
 *   <li>Handling clipboard operations for the editor</li>
 *   <li>Maintaining the state of the BPMN content and detecting changes</li>
 * </ul>
 *
 * <p>This class implements {@link Disposable} to ensure proper cleanup of resources
 * when the editor is closed or the project is disposed.
 */
@Getter
public class BpmnEditorContext implements Disposable {

    private final ScriptFileManager scriptFileManager;
    private final HttpServerWrapper httpServerWrapper;
    private final JBCefBrowserService jbCefBrowserService;

    /**
     * Constructs a new BpmnEditorContext with the specified project and file.
     * This constructor initializes all necessary parts and services for the BPMN editor.
     *
     * @param project the current IntelliJ IDEA project
     * @param file    the virtual file associated with the BPMN diagram being edited
     */
    public BpmnEditorContext(Project project, VirtualFile file) {
        var originBpmn = readText(file);
        var jbCefBrowser = new JBCefBrowserWrapper();
        var clipboardService = new ClipboardService();
        var hashComparator = new HashComparator(originBpmn);
        this.httpServerWrapper = new HttpServerWrapper();
        this.scriptFileManager = new ScriptFileManager(project, jbCefBrowser);

        var initQueries = List.of(
                createCloseScriptFileJSQuery(jbCefBrowser, scriptFileManager),
                createGetClipboardJSQuery(jbCefBrowser, clipboardService),
                createInitBpmnJSQuery(jbCefBrowser, originBpmn),
                createOpenScriptFileJSQuery(jbCefBrowser, scriptFileManager),
                createSaveBpmnJSQuery(jbCefBrowser, file, hashComparator),
                createSetClipboardJSQuery(jbCefBrowser, clipboardService),
                createSetFocusScriptFileJSQuery(jbCefBrowser, scriptFileManager)
        );
        this.jbCefBrowserService = new JBCefBrowserService(file, initQueries, jbCefBrowser, httpServerWrapper);
    }

    /**
     * Disposes of the resources and services used by this context.
     * This method is called when the editor is closed or the project is disposed.
     * It ensures proper cleanup of all managed resources to prevent memory leaks.
     */
    @Override
    public void dispose() {
        scriptFileManager.dispose();
        jbCefBrowserService.dispose();
        httpServerWrapper.destroy();
    }
}