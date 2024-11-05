package dev.camunda.bpmn.editor.context.config;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import dev.camunda.bpmn.editor.service.jsquery.InitJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.InitJSQueryManager;
import dev.camunda.bpmn.editor.service.jsquery.JSQueryService;
import dev.camunda.bpmn.editor.service.jsquery.impl.CloseScriptFileJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.impl.DeleteVirtualFileIdJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.impl.GetClipboardJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.impl.InitBpmnJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.impl.OpenScriptFileJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.impl.SaveBpmnJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.impl.SetClipboardJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.impl.SetFocusScriptFileJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.impl.UpdateScriptJSQuery;
import dev.camunda.bpmn.editor.service.script.ScriptFileService;
import dev.camunda.bpmn.editor.util.HashComparator;
import java.util.List;
import org.cef.browser.CefBrowser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for defining beans related to JSQuery services.
 * This class provides the necessary beans for managing JSQuery operations, such as opening and closing script files,
 * managing clipboard operations, and initializing BPMN content.
 *
 * @author Oleksandr Havrysh
 */
@Configuration
public class JSQueryConfig {

    @Bean
    public CloseScriptFileJSQuery closeScriptFileJSQuery(JBCefBrowser browser, ScriptFileService scriptFileService) {
        return new CloseScriptFileJSQuery(browser, scriptFileService);
    }

    @Bean
    public GetClipboardJSQuery clipboardJSQuery(JBCefBrowser browser) {
        return new GetClipboardJSQuery(browser);
    }

    @Bean
    public InitBpmnJSQuery initBpmnJSQuery(CefBrowser cefBrowser, String originBpmn) {
        return new InitBpmnJSQuery(cefBrowser, originBpmn);
    }

    @Bean
    public OpenScriptFileJSQuery openScriptFileJSQuery(JBCefBrowser browser, ScriptFileService scriptFileService) {
        return new OpenScriptFileJSQuery(browser, scriptFileService);
    }

    @Bean
    public SaveBpmnJSQuery saveBpmnJSQuery(VirtualFile file, JBCefBrowser browser, HashComparator hashComparator) {
        return new SaveBpmnJSQuery(file, browser, hashComparator);
    }

    @Bean
    public SetClipboardJSQuery setClipboardJSQuery(JBCefBrowser browser) {
        return new SetClipboardJSQuery(browser);
    }

    @Bean
    public SetFocusScriptFileJSQuery setFocusScriptFileJSQuery(JBCefBrowser browser,
                                                               ScriptFileService scriptFileService) {
        return new SetFocusScriptFileJSQuery(browser, scriptFileService);
    }

    @Bean
    public UpdateScriptJSQuery updateScriptJSQuery(CefBrowser cefBrowser) {
        return new UpdateScriptJSQuery(cefBrowser);
    }

    @Bean
    public DeleteVirtualFileIdJSQuery deleteVirtualFileIdJSQuery(CefBrowser cefBrowser) {
        return new DeleteVirtualFileIdJSQuery(cefBrowser);
    }

    @Bean
    public InitJSQueryManager initJSQueryManager(List<InitJSQuery> initQueries) {
        return new InitJSQueryManager(initQueries);
    }

    @Bean
    public JSQueryService jsQueryService(UpdateScriptJSQuery updateScriptJSQuery,
                                         DeleteVirtualFileIdJSQuery deleteVirtualFileIdJSQuery) {
        return new JSQueryService(updateScriptJSQuery, deleteVirtualFileIdJSQuery);
    }
}
