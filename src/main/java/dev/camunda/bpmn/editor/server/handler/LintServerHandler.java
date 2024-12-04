package dev.camunda.bpmn.editor.server.handler;

import static java.util.regex.Pattern.compile;
import static org.apache.groovy.util.Arrays.concat;

import com.intellij.openapi.util.JDOMUtil;
import dev.camunda.bpmn.editor.project.ProjectService;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.jdom.JDOMException;

/**
 * Handler for BPMN Editor UI HTTP requests related to linting plugins.
 * This class is responsible for serving and processing JavaScript files for linting plugins.
 * It handles file retrieval, module inlining, and serves the processed content to the BPMN Editor UI.
 *
 * <p>The LintPluginHandler extends AbstractServerHandler and works in conjunction
 * with the ProjectService to provide linting plugin functionality over HTTP.</p>
 *
 * <p>This handler dynamically processes JavaScript files, inlining required modules
 * to create self-contained linting plugins that can be executed in the BPMN Editor UI.</p>
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class LintServerHandler extends AbstractServerHandler {

    private static final String SLASH = "/";
    private static final String RESOURCE_PATH = "/lint/";
    private static final String[] INDEX_JS = {"index.js"};
    private static final String[] NODE_MODULES = {"node_modules"};
    private static final String[] PACKAGE_JSON = {"package.json"};
    private static final String EXPORTS_PATTERN = "(exports.[a-zA-Z0-9_]+\\s*=\\s*[^;]+;)|(module.exports\\s*=\\s*.[a-zA-Z0-9_]+;)";
    private static final Pattern REQUIRE_PATTERN = compile("require\\(['\"](.*?)['\"]\\)|import\\s*[{},a-zA-Z0-9_\\s]+\\s*from\\s*['\"](.*?)['\"];*");

    private final ProjectService projectService;

    /**
     * Retrieves and processes the content of a linting plugin file.
     *
     * <p>This method overrides the abstract method from AbstractServerHandler.
     * It locates the requested file using the ProjectService, then processes
     * the file content by inlining required modules.</p>
     *
     * @param path The path of the requested linting plugin file.
     * @return A byte array containing the processed JavaScript content, or null if not found.
     */
    @Override
    protected byte[] getContent(String path) {
        var folderPath = path.replace(RESOURCE_PATH, "").split(SLASH);
        var fileContent = projectService.findContentByPath(folderPath);
        return fileContent.map(s -> inlineModules(s, new HashSet<>()).getBytes()).orElse(null);
    }

    /**
     * Processes JavaScript code by inlining required modules.
     * This method recursively processes require and import statements, replacing them with the actual module content.
     *
     * <p>It handles circular dependencies by keeping track of processed modules.</p>
     *
     * @param jsCode           The original JavaScript code to process.
     * @param processedModules A set of module names that have already been processed to avoid circular dependencies.
     * @return The processed JavaScript code with modules inlined.
     */
    private String inlineModules(String jsCode, Set<String> processedModules) {
        var result = new StringBuilder();
        var matcher = REQUIRE_PATTERN.matcher(jsCode);
        var lastIndex = 0;
        while (matcher.find()) {
            var moduleName = matcher.group(1);
            if (!processedModules.contains(moduleName)) {
                processedModules.add(moduleName);
                var moduleFile = findModuleFile(moduleName);
                if (moduleFile.isPresent()) {
                    result.append(inlineModules(moduleFile.get(), processedModules));
                } else {
                    result.append("/* Missing module: ").append(moduleName).append(" */");
                }
            }

            lastIndex = matcher.end();
        }

        result.append(jsCode.substring(lastIndex));
        return result.toString().replaceAll(EXPORTS_PATTERN, "");
    }

    /**
     * Locates the file for a given module name in the project's node_modules directory.
     *
     * <p>This method attempts to find the main file for the module by checking the package.json file.
     * If package.json is not found or cannot be parsed, it falls back to looking for an index.js file.</p>
     *
     * @param moduleName The name of the module to find.
     * @return An Optional containing the module file content if found, or empty if not found.
     */
    private Optional<String> findModuleFile(String moduleName) {
        var nodeModulePath = concat(NODE_MODULES, moduleName.split(SLASH));
        var packageJson = projectService.findContentByPath(concat(nodeModulePath, PACKAGE_JSON));
        if (packageJson.isEmpty()) {
            return projectService.findContentByPath(concat(nodeModulePath, INDEX_JS));
        }

        try {
            var mainFile = JDOMUtil.load(packageJson.get()).getChild("main").getValue();
            return projectService.findContentByPath(concat(nodeModulePath, mainFile.split(SLASH)));
        } catch (IOException | JDOMException e) {
            return Optional.empty();
        }
    }
}