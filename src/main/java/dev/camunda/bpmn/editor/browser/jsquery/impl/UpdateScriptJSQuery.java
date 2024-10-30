package dev.camunda.bpmn.editor.browser.jsquery.impl;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.intellij.ui.jcef.JBCefBrowser;
import dev.camunda.bpmn.editor.browser.jsquery.SimpleJSQuery;
import java.util.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A specific implementation of SimpleJSQuery for updating script content in the browser.
 * This class creates a JavaScript query to call a function that updates the script content
 * associated with a specific virtual file ID.
 *
 * @author Oleksandr Havrysh
 */
public class UpdateScriptJSQuery extends SimpleJSQuery {

    /**
     * Constant representing an empty script.
     */
    private static final String EMPTY_SCRIPT = "";

    /**
     * The JavaScript code template for calling the updateScript function.
     * The first %s placeholder will be replaced with the virtual file ID,
     * and the second %s placeholder will be replaced with the Base64 encoded script content.
     */
    private static final String UPDATE_SCRIPT_JS = "updateScript('%s', `%s`);";

    /**
     * The Base64 encoded script content.
     */
    private final String script;

    /**
     * The virtual file ID associated with the script.
     */
    private final String virtualFileId;

    /**
     * Constructs a new UpdateScriptJSQuery.
     *
     * @param browser       The JBCefBrowser instance on which the JavaScript query will be executed.
     * @param virtualFileId The ID of the virtual file associated with the script.
     * @param script        The script content to be updated. If null or blank, an empty script will be used.
     */
    public UpdateScriptJSQuery(@NotNull JBCefBrowser browser,
                               @NotNull String virtualFileId,
                               @Nullable String script) {
        super(browser);
        this.virtualFileId = virtualFileId;
        this.script = new String(Base64.getEncoder().encode((isBlank(script) ? EMPTY_SCRIPT : script).getBytes()));
    }

    /**
     * Returns the JavaScript query string to be executed.
     * This query calls the updateScript function in the browser with the specified virtual file ID
     * and the Base64 encoded script content.
     *
     * @return A String representing the JavaScript query to update the script content.
     */
    @Override
    public @NotNull String getQuery() {
        return UPDATE_SCRIPT_JS.formatted(virtualFileId, script);
    }
}