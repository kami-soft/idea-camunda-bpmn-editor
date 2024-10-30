package dev.camunda.bpmn.editor.browser;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import dev.camunda.bpmn.editor.browser.jsquery.InitJSQueryManager;
import dev.camunda.bpmn.editor.browser.jsquery.impl.CloseScriptFileJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.impl.GetClipboardJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.impl.InitBpmnJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.impl.OpenScriptFileJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.impl.SaveBpmnJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.impl.SetClipboardJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.impl.SetFocusScriptFileJSQuery;
import dev.camunda.bpmn.editor.scratch.ScriptFileManager;
import dev.camunda.bpmn.editor.server.BpmnEditorUIServer;
import dev.camunda.bpmn.editor.settings.BpmnEditorSettings;
import dev.camunda.bpmn.editor.util.HashComparator;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the browser component of the BPMN Editor.
 * This class manages the JCEF browser, JavaScript queries, UI server, and script file handling.
 *
 * @author Oleksandr Havrysh
 */
public class BpmnEditorBrowser implements Disposable {

    private static final String BPMN_EDITOR_URL = "http://localhost:%s/bpmn-editor.html?colorTheme=%s";

    private final JBCefBrowser browser;
    private final InitJSQueryManager initJsQueryManager;
    private final BpmnEditorUIServer bpmnEditorUIServer;
    private final ScriptFileManager scriptFileManager;

    /**
     * Constructs a new BpmnEditorBrowser.
     *
     * @param project        The current project
     * @param file           The virtual file associated with the BPMN diagram
     * @param bpmnXml        The initial BPMN XML content (can be null)
     * @param hashComparator The hash comparator for tracking changes
     */
    public BpmnEditorBrowser(@NotNull Project project,
                             @NotNull VirtualFile file,
                             @Nullable String bpmnXml,
                             @NotNull HashComparator hashComparator) {
        var state = BpmnEditorSettings.getInstance().getState();

        this.browser = JBCefBrowser.createBuilder()
                .setOffScreenRendering(false)
                .setMouseWheelEventEnable(true)
                .setEnableOpenDevToolsMenuItem(true)
                .build();
        this.bpmnEditorUIServer = new BpmnEditorUIServer();

        browser.loadURL(BPMN_EDITOR_URL.formatted(bpmnEditorUIServer.getPort(), state.getColorTheme().name()));

        this.scriptFileManager = new ScriptFileManager();

        initJsQueryManager = new InitJSQueryManager()
                .addInitQuery(new InitBpmnJSQuery(browser, bpmnXml))
                .addInitQuery(new OpenScriptFileJSQuery(browser, scriptFileManager, project))
                .addInitQuery(new CloseScriptFileJSQuery(browser, scriptFileManager))
                .addInitQuery(new SetFocusScriptFileJSQuery(browser, scriptFileManager))
                .addInitQuery(new SaveBpmnJSQuery(browser, hashComparator, file))
                .addInitQuery(new GetClipboardJSQuery(browser))
                .addInitQuery(new SetClipboardJSQuery(browser));

        browser.getJBCefClient().addLoadHandler(new BpmnEditorLoadHandler(initJsQueryManager), browser.getCefBrowser());
    }

    /**
     * Returns the Swing component representing the browser.
     *
     * @return The JComponent of the browser
     */
    public @NotNull JComponent getComponent() {
        return browser.getComponent();
    }

    /**
     * Disposes of resources held by this BpmnEditorBrowser.
     * This method ensures that all managed resources are properly released.
     */
    @Override
    public void dispose() {
        browser.dispose();
        initJsQueryManager.dispose();
        bpmnEditorUIServer.dispose();
        scriptFileManager.dispose();
    }
}