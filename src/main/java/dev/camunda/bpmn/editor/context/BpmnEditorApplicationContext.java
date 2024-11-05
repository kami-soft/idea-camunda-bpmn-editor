package dev.camunda.bpmn.editor.context;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import dev.camunda.bpmn.editor.context.config.AppConfig;
import dev.camunda.bpmn.editor.context.config.BrowserConfig;
import dev.camunda.bpmn.editor.context.config.JSQueryConfig;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserService;
import dev.camunda.bpmn.editor.util.HashComparator;
import java.io.Closeable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * A class responsible for managing the Spring application context for the BPMN Editor.
 * This class initializes the Spring context with the necessary configurations and provides access to beans.
 * It also implements {@link Closeable} to ensure proper cleanup of resources.
 *
 * @author Oleksandr Havrysh
 */
public class BpmnEditorApplicationContext implements Closeable {

    private final AnnotationConfigApplicationContext context;

    /**
     * Creates a new BpmnEditorApplicationContext instance.
     * Initializes the Spring context with the given project and file, and registers the necessary configuration classes.
     *
     * @param project The current project
     * @param file    The BPMN file
     */
    public BpmnEditorApplicationContext(Project project, VirtualFile file) {
        context = new AnnotationConfigApplicationContext();
        context.registerBean(Project.class, () -> project);
        context.registerBean(VirtualFile.class, () -> file);
        context.register(AppConfig.class, BrowserConfig.class, JSQueryConfig.class);
        context.refresh();
    }

    /**
     * Gets the HashComparator bean from the Spring context.
     *
     * @return The HashComparator instance
     */
    public HashComparator getHashComparator() {
        return context.getBean(HashComparator.class);
    }

    /**
     * Gets the JBCefBrowserService bean from the Spring context.
     *
     * @return The JBCefBrowserService instance
     */
    public JBCefBrowserService getJBCefBrowserService() {
        return context.getBean(JBCefBrowserService.class);
    }

    /**
     * Closes the Spring context.
     * This method is called to ensure proper cleanup of resources.
     */
    @Override
    public void close() {
        context.close();
    }
}