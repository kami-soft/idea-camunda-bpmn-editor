package dev.camunda.bpmn.editor;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.intellij.openapi.project.ProjectManager.TOPIC;
import static com.intellij.openapi.vfs.VirtualFileUtil.readText;
import static dev.camunda.bpmn.editor.util.Constants.ORIGIN_BPMN;
import static java.util.Optional.ofNullable;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
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
import dev.camunda.bpmn.editor.service.server.HttpServerWrapper;
import dev.camunda.bpmn.editor.service.server.ServerHandler;
import dev.camunda.bpmn.editor.util.HashComparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
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

    private static final Map<String, MutablePicoContainer> PROJECT_CONTAINER_MAP = new ConcurrentHashMap<>(1);

    private final MutablePicoContainer container;

    @Getter
    private final HashComparator hashComparator;

    @Getter
    private final JBCefBrowserService jbCefBrowserService;

    static {
        registerProjectCloseListener();
    }

    /**
     * Registers a listener to handle project close events.
     * When a project is closed, its corresponding PicoContainer is stopped and disposed of.
     */
    private static void registerProjectCloseListener() {
        getApplication().getMessageBus().connect().subscribe(TOPIC, new ProjectManagerListener() {

            @Override
            public void projectClosed(@NotNull Project project) {
                ofNullable(PROJECT_CONTAINER_MAP.remove(project.getLocationHash())).ifPresent(container -> {
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
        var projectContainer = PROJECT_CONTAINER_MAP.computeIfAbsent(project.getLocationHash(),
                key -> createProjectContainer(project));
        this.container = createVirtualFileContainer(file, projectContainer);
        this.hashComparator = container.getComponent(HashComparator.class);
        this.jbCefBrowserService = container.getComponent(JBCefBrowserService.class);
    }

    /**
     * Creates and configures a new PicoContainer instance for the given VirtualFile.
     *
     * @param file             The VirtualFile instance
     * @param projectContainer The parent PicoContainer for the project
     * @return A configured PicoContainer instance for the VirtualFile
     */
    private static MutablePicoContainer createVirtualFileContainer(VirtualFile file,
                                                                   MutablePicoContainer projectContainer) {
        var virtualFileContainer = createContainer(projectContainer)
                .addComponent(file)
                .addComponent(ORIGIN_BPMN, readText(file))
                .addComponent(HashComparator.class)
                .addComponent(JBCefBrowserWrapper.class)
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
                .addComponent(JBCefBrowserService.class);
        virtualFileContainer.start();

        return virtualFileContainer;
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
                .addComponent(FileDocumentManager.getInstance())
                .addComponent(FileTypeManager.getInstance())
                .addComponent(getApplication().getMessageBus().connect())
                .addComponent(ServerHandler.class)
                .addComponent(HttpServerWrapper.class);
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
     * Closes the PicoContainer context.
     * This method is called to ensure proper cleanup of resources.
     */
    @Override
    public void dispose() {
        container.stop();
        container.dispose();
    }
}