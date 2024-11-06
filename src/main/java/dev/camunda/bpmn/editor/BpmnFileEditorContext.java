package dev.camunda.bpmn.editor;

import static com.intellij.openapi.vfs.VirtualFileUtil.readText;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserService;
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
import dev.camunda.bpmn.editor.service.server.ServerHandler;
import dev.camunda.bpmn.editor.service.server.ServerService;
import dev.camunda.bpmn.editor.util.HashComparator;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.lifecycle.JavaEE5LifecycleStrategy;

/**
 * A class responsible for managing the PicoContainer application context for the BPMN Editor.
 * This class initializes the PicoContainer context with the necessary configurations and provides access to components.
 * It also implements {@link Disposable} to ensure proper cleanup of resources.
 *
 * @author Oleksandr Havrysh
 */
public class BpmnFileEditorContext implements Disposable {

    private final MutablePicoContainer container;

    /**
     * Creates a new BpmnFileEditorContext instance.
     * Initializes the PicoContainer context with the given project and file, and registers the necessary components.
     *
     * @param project The current project
     * @param file    The BPMN file
     */
    public BpmnFileEditorContext(Project project, VirtualFile file) {
        container = createContainer();
        // Register default components
        container.addComponent(project)
                .addComponent(file)
                .addComponent(FileEditorManager.getInstance(project))
                .addComponent("originBpmn", readText(file))
                .addComponent(HashComparator.class)
                .addComponent(createJBCefBrowser())
                .addComponent("cefBrowser", container.getComponent(JBCefBrowser.class).getCefBrowser())
                // Register server components
                .addComponent(ServerHandler.class)
                .addComponent(ServerService.class)
                // Register JS query components
                .addComponent(UpdateScriptJSQuery.class)
                .addComponent(DeleteVirtualFileIdJSQuery.class)
                .addComponent(JSQueryService.class)
                // Register init JS query components
                .addComponent(ScriptFileService.class)
                .addComponent(CloseScriptFileJSQuery.class)
                .addComponent(GetClipboardJSQuery.class)
                .addComponent(InitBpmnJSQuery.class)
                .addComponent(OpenScriptFileJSQuery.class)
                .addComponent(SaveBpmnJSQuery.class)
                .addComponent(SetClipboardJSQuery.class)
                .addComponent(SetFocusScriptFileJSQuery.class)
                .addComponent(InitJSQueryManager.class)
                // Register browser components
                .addComponent(JBCefBrowserService.class);
        container.start();
    }

    /**
     * Creates and configures a new PicoContainer instance.
     *
     * @return A configured PicoContainer instance
     */
    private static MutablePicoContainer createContainer() {
        return new PicoBuilder()
                .withCaching()
                .withLifecycle(JavaEE5LifecycleStrategy.class)
                .build();
    }

    /**
     * Creates and configures a new JBCefBrowser instance.
     *
     * @return A configured JBCefBrowser instance
     */
    private static JBCefBrowser createJBCefBrowser() {
        return JBCefBrowser.createBuilder()
                .setOffScreenRendering(false)
                .setMouseWheelEventEnable(true)
                .setEnableOpenDevToolsMenuItem(true)
                .build();
    }

    /**
     * Gets the HashComparator component from the PicoContainer context.
     *
     * @return The HashComparator instance
     */
    public HashComparator getHashComparator() {
        return container.getComponent(HashComparator.class);
    }

    /**
     * Gets the JBCefBrowserService component from the PicoContainer context.
     *
     * @return The JBCefBrowserService instance
     */
    public JBCefBrowserService getJBCefBrowserService() {
        return container.getComponent(JBCefBrowserService.class);
    }

    /**
     * Closes the PicoContainer context.
     * This method is called to ensure proper cleanup of resources.
     */
    @Override
    public void dispose() {
        container.stop();
        container.dispose();
    }
}