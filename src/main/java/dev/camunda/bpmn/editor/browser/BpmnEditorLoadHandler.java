package dev.camunda.bpmn.editor.browser;

import dev.camunda.bpmn.editor.browser.jsquery.InitJSQueryManager;
import lombok.RequiredArgsConstructor;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;

/**
 * A custom load handler for the BPMN Editor browser.
 * This class extends CefLoadHandlerAdapter to handle the completion of page loading
 * and execute initialization queries.
 */
@RequiredArgsConstructor
public class BpmnEditorLoadHandler extends CefLoadHandlerAdapter {

    private final InitJSQueryManager initJsQueryManager;

    /**
     * Called when a page load ends.
     * This method is invoked when the browser finishes loading a page, including all sub-frames.
     * It triggers the execution of all initialization queries managed by the JSQueryManager.
     *
     * @param browser        The browser generating the event.
     * @param frame          The frame that has finished loading.
     * @param httpStatusCode The HTTP status code for the load. This value will be 0 for non-HTTP loads.
     */
    @Override
    public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
        initJsQueryManager.executeInitQueries();
    }
}