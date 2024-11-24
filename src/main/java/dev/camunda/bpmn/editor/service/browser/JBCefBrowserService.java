package dev.camunda.bpmn.editor.service.browser;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.vfs.VirtualFile;
import dev.camunda.bpmn.editor.service.jsquery.JSQuery;
import dev.camunda.bpmn.editor.service.server.HttpServerWrapper;
import dev.camunda.bpmn.editor.settings.BpmnEditorSettings;
import java.util.List;
import javax.swing.JComponent;

/**
 * Represents the browser component of the BPMN Editor.
 * This class manages the JCEF (Java Chromium Embedded Framework) browser, JavaScript queries,
 * and handles the loading of BPMN files.
 * <p>
 * It initializes the browser with the necessary settings and provides functionality to:
 * <ul>
 *   <li>Load BPMN editor URL with appropriate settings</li>
 *   <li>Execute initialization queries when the browser finishes loading</li>
 *   <li>Manage resources and ensure proper disposal</li>
 * </ul>
 *
 * @author Oleksandr Havrysh
 */
public class JBCefBrowserService implements Disposable {

    /**
     * The URL template for the BPMN editor, including placeholders for settings.
     */
    private static final String BPMN_EDITOR_URL = "http://localhost:%s/index.html?colorTheme=%s&engine=%s&scriptFormat=%s&schemaTheme=%s";

    /**
     * The path of the BPMN file being edited.
     */
    private final String path;

    /**
     * List of JavaScript queries to be executed upon browser load completion.
     */
    private final List<JSQuery> initQueries;

    /**
     * The wrapper for the JCEF browser component.
     */
    private final JBCefBrowserWrapper browser;

    /**
     * The port number of the HTTP server.
     */
    private final int port;

    /**
     * Constructs a new JBCefBrowserService.
     *
     * @param file              The VirtualFile representing the BPMN file being edited
     * @param initQueries       A list of JSQuery objects to be executed upon browser load completion
     * @param browser           The JBCefBrowserWrapper instance for browser interactions
     * @param httpServerWrapper The HttpServerWrapper instance for managing the HTTP server
     */
    public JBCefBrowserService(VirtualFile file,
                               List<JSQuery> initQueries,
                               JBCefBrowserWrapper browser,
                               HttpServerWrapper httpServerWrapper) {
        this.browser = browser;
        this.path = file.getPath();
        this.initQueries = initQueries;
        this.port = httpServerWrapper.getPort();

        browser.onLoadEnd(() -> initQueries.forEach(JSQuery::executeQuery));
    }

    /**
     * Loads the BPMN editor URL with the appropriate settings.
     * This method retrieves the settings for the current file (color theme, engine, and script type)
     * and loads the URL in the browser.
     *
     * @return The JComponent of the browser, which can be used for UI integration
     */
    public JComponent loadBpmn() {
        var state = BpmnEditorSettings.getInstance().getState();
        var engine = state.getEngine(path);
        var colorTheme = state.getColorTheme(path);
        var scriptType = state.getScriptType(path);
        var schemaTheme = state.getSchemaTheme(path);

        browser.loadURL(BPMN_EDITOR_URL.formatted(port, colorTheme, engine, scriptType, schemaTheme));
        return browser.getComponent();
    }

    /**
     * Disposes of resources held by this JBCefBrowserService.
     * This method ensures that all managed resources are properly released, including:
     * <ul>
     *   <li>The JCEF browser wrapper</li>
     *   <li>All initialization JavaScript queries</li>
     * </ul>
     * It's automatically called when the object is no longer needed if managed by a disposer.
     */
    @Override
    public void dispose() {
        browser.dispose();
        initQueries.forEach(JSQuery::dispose);
    }
}