package dev.camunda.bpmn.editor.service.browser;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import dev.camunda.bpmn.editor.settings.BpmnEditorSettings;
import dev.camunda.bpmn.editor.service.jsquery.InitJSQueryManager;
import dev.camunda.bpmn.editor.service.server.ServerService;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.JComponent;
import lombok.RequiredArgsConstructor;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;

/**
 * Represents the browser component of the BPMN Editor.
 * This class manages the JCEF browser, JavaScript queries, UI server, and script file handling.
 * It initializes the browser with the necessary settings and handles the loading of BPMN files.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class JBCefBrowserService {

    private static final String BPMN_EDITOR_URL = "http://localhost:%s/bpmn-editor.html?colorTheme=%s&engine=%s&scriptFormat=%s";

    private final VirtualFile file;
    private final JBCefBrowser browser;
    private final ServerService serverService;
    private final InitJSQueryManager initJsQueryManager;

    /**
     * Initializes the JBCefBrowserService.
     * This method is called after the bean is constructed and sets up the load handler for the browser.
     */
    @PostConstruct
    public void init() {
        browser.getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                initJsQueryManager.executeInitQueries();
            }
        }, browser.getCefBrowser());
    }

    /**
     * Loads the BPMN editor URL with the appropriate settings.
     * This method retrieves the settings for the current file and loads the URL in the browser.
     *
     * @return The JComponent of the browser
     */
    public JComponent loadBpmn() {
        var state = BpmnEditorSettings.getInstance().getState();
        var path = file.getPath();
        var engine = state.getEngine(path);
        var colorTheme = state.getColorTheme(path);
        var scriptType = state.getScriptType(path);

        browser.loadURL(BPMN_EDITOR_URL.formatted(serverService.getPort(), colorTheme, engine, scriptType));

        return browser.getComponent();
    }

    /**
     * Disposes of resources held by this JBCefBrowserService.
     * This method ensures that all managed resources are properly released.
     */
    @PreDestroy
    public void destroy() {
        browser.dispose();
    }
}