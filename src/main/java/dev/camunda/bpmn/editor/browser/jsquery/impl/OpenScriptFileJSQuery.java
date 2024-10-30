package dev.camunda.bpmn.editor.browser.jsquery.impl;

import com.intellij.openapi.project.Project;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.HandledJSQuery;
import dev.camunda.bpmn.editor.script.ScriptFileManager;
import org.jetbrains.annotations.NotNull;

/**
 * A specific implementation of HandledJSQuery for opening script files.
 * This class creates a JavaScript function that can be called from the browser
 * to open a script file with the given text content.
 *
 * @author Oleksandr Havrysh
 */
public class OpenScriptFileJSQuery extends HandledJSQuery {

    /**
     * The JavaScript code template for creating the openScriptExternalFile function.
     * The %s placeholder will be replaced with the injected handler code.
     */
    private static final String OPEN_SCRIPT_FILE_JS = """
            window.openScriptExternalFile = function(text) {
                return new Promise((resolve, reject) => {
                    %s
                });
            }""";

    /**
     * Constructs a new OpenScriptFileJSQuery.
     *
     * @param browser           The JBCefBrowser instance on which the JavaScript query will be executed.
     * @param scriptFileManager The ScriptFileManager used to create the script file.
     * @param project           The current Project instance.
     */
    public OpenScriptFileJSQuery(@NotNull JBCefBrowser browser,
                                 @NotNull ScriptFileManager scriptFileManager,
                                 @NotNull Project project) {
        super(browser, text ->
                new JBCefJSQuery.Response(scriptFileManager.createScriptScratchFile(project, browser, "groovy", text)));
    }

    /**
     * Returns the JavaScript query string to be executed.
     * This query creates a function in the browser's window object that can be called to open a script file.
     * The function returns a Promise that resolves with the virtual file ID of the created script file.
     *
     * @return A String representing the JavaScript query to create the openScriptExternalFile function.
     */
    @Override
    public @NotNull String getQuery() {
        return OPEN_SCRIPT_FILE_JS.formatted(jbCefJSQuery.inject("text",
                """
                        function(response) {
                            resolve(response);
                        }""",
                """
                        function(error_code, error_message) {
                            reject(new Error(`Error ${error_code}: ${error_message}`));
                        }"""));
    }
}