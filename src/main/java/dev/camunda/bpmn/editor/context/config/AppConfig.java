package dev.camunda.bpmn.editor.context.config;

import static com.intellij.openapi.vfs.VirtualFileUtil.readText;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import dev.camunda.bpmn.editor.service.jsquery.JSQueryService;
import dev.camunda.bpmn.editor.service.script.ScriptFileService;
import dev.camunda.bpmn.editor.util.HashComparator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for defining beans used in the BPMN Editor.
 * This class provides the necessary beans for managing file editors, script files, and hash comparison.
 *
 * @author Oleksandr Havrysh<
 */
@Configuration
public class AppConfig {

    @Bean
    public FileEditorManager fileEditorManager(Project project) {
        return FileEditorManager.getInstance(project);
    }

    @Bean
    public String originBpmn(VirtualFile file) {
        return readText(file);
    }

    @Bean
    public HashComparator hashComparator(String originBpmn) {
        return new HashComparator(originBpmn);
    }

    @Bean
    public ScriptFileService scriptFileManager(JSQueryService jsQueryService, FileEditorManager fileEditorManager) {
        return new ScriptFileService(jsQueryService, fileEditorManager);
    }
}