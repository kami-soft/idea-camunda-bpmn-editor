package dev.camunda.bpmn.editor.context.config;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserService;
import dev.camunda.bpmn.editor.service.jsquery.InitJSQueryManager;
import dev.camunda.bpmn.editor.service.server.ServerHandler;
import dev.camunda.bpmn.editor.service.server.ServerService;
import org.cef.browser.CefBrowser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for defining beans related to the JBCefBrowser and server services.
 * This class provides the necessary beans for managing the browser, server handlers, and related services.
 *
 * @author Oleksandr Havrysh
 */
@Configuration
public class BrowserConfig {

    @Bean
    public JBCefBrowser jbCefBrowser() {
        return JBCefBrowser.createBuilder()
                .setOffScreenRendering(false)
                .setMouseWheelEventEnable(true)
                .setEnableOpenDevToolsMenuItem(true)
                .build();
    }

    @Bean
    public CefBrowser cefBrowser(JBCefBrowser jbCefBrowser) {
        return jbCefBrowser.getCefBrowser();
    }

    @Bean
    public ServerHandler serverHandler() {
        return new ServerHandler();
    }

    @Bean
    public ServerService serverService(ServerHandler serverHandler) {
        return new ServerService(serverHandler);
    }

    @Bean
    public JBCefBrowserService jbCefBrowserService(VirtualFile file,
                                                   JBCefBrowser browser,
                                                   ServerService serverService,
                                                   InitJSQueryManager initJsQueryManager) {
        return new JBCefBrowserService(file, browser, serverService, initJsQueryManager);
    }
}