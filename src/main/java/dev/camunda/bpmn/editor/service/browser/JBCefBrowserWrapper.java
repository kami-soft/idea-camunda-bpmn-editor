package dev.camunda.bpmn.editor.service.browser;

import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import java.util.function.Function;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;

public class JBCefBrowserWrapper extends JBCefBrowser {

    public JBCefBrowserWrapper() {
        super(JBCefBrowser.createBuilder()
                .setOffScreenRendering(false)
                .setMouseWheelEventEnable(true)
                .setEnableOpenDevToolsMenuItem(true));
    }

    public void onLoadEnd(Runnable runnable) {
        getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                runnable.run();
            }
        }, getCefBrowser());
    }

    public JBCefJSQuery createJBCefJSQuery(Function<? super String, ? extends JBCefJSQuery.Response> handler) {
        var jbCefJSQuery = JBCefJSQuery.create((JBCefBrowserBase) this);
        jbCefJSQuery.addHandler(handler);
        return jbCefJSQuery;
    }

    public void executeQuery(String query) {
        getCefBrowser().executeJavaScript(query, getCefBrowser().getURL(), 0);
    }
}
