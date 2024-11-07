package dev.camunda.bpmn.editor;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.intellij.openapi.vfs.VirtualFileUtil.readText;
import static java.util.Optional.ofNullable;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserService;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserWrapper;
import dev.camunda.bpmn.editor.service.jsquery.InitJSQueryManager;
import dev.camunda.bpmn.editor.service.jsquery.JSQueryService;
import dev.camunda.bpmn.editor.service.jsquery.init.CloseScriptFileJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.init.GetClipboardJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.init.InitBpmnJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.init.OpenScriptFileJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.init.SaveBpmnJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.init.SetClipboardJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.init.SetFocusScriptFileJSQuery;
import dev.camunda.bpmn.editor.service.script.ScriptFileService;
import dev.camunda.bpmn.editor.service.server.ServerHandler;
import dev.camunda.bpmn.editor.service.server.ServerService;
import dev.camunda.bpmn.editor.util.HashComparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.containers.EmptyPicoContainer;

/**
 * A class responsible for managing the PicoContainer application context for the BPMN Editor.
 * This class initializes the PicoContainer context with the necessary configurations and provides access to components.
 * It also implements {@link Disposable} to ensure proper cleanup of resources.
 *
 * @author Oleksandr Havrysh
 */
public class BpmnFileEditorContext implements Disposable {

    private static final Map<String, MutablePicoContainer> projectContainerMap = new ConcurrentHashMap<>(1);

    private final MutablePicoContainer container;

    static {
        registerProjectCloseListener();
    }

    /**
     * Registers a listener to handle project close events.
     * When a project is closed, its corresponding PicoContainer is stopped and disposed of.
     */
    private static void registerProjectCloseListener() {
        getApplication().getMessageBus().connect().subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {

            @Override
            public void projectClosed(@NotNull Project project) {
                ofNullable(projectContainerMap.remove(project.getLocationHash())).ifPresent(container -> {
                    container.stop();
                    container.dispose();
                });
            }
        });
    }

    /**
     * Creates a new BpmnFileEditorContext instance.
     * Initializes the PicoContainer context with the given project and file, and registers the necessary components.
     *
     * @param project The current project
     * @param file    The BPMN file
     */
    public BpmnFileEditorContext(Project project, VirtualFile file) {
        var projectContainer = projectContainerMap.computeIfAbsent(project.getLocationHash(),
                key -> createProjectContainer(project));

        this.container = createContainer(projectContainer);
        // Register default components
        container.addComponent(file)
                .addComponent("originBpmn", readText(file))
                .addComponent(HashComparator.class)
                .addComponent(new JBCefBrowserWrapper())
                // Register JS query components
                .addComponent(JSQueryService.class)
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
     * Creates and configures a new PicoContainer instance for the given project.
     *
     * @param project The current project
     * @return A configured PicoContainer instance for the project
     */
    private static MutablePicoContainer createProjectContainer(Project project) {
        var projectContainer = createContainer(new EmptyPicoContainer())
                .addComponent(project)
                .addComponent(FileEditorManager.getInstance(project))
                .addComponent(ServerHandler.class)
                .addComponent(ServerService.class);
        projectContainer.start();

        return projectContainer;
    }

    /**
     * Creates and configures a new PicoContainer instance with the given parent container.
     *
     * @param parentContainer The parent PicoContainer
     * @return A configured PicoContainer instance
     */
    private static MutablePicoContainer createContainer(PicoContainer parentContainer) {
        return new PicoBuilder(parentContainer)
                .withCaching()
                .withJavaEE5Lifecycle()
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