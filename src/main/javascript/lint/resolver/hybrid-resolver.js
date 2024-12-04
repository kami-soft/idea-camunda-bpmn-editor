import HttpResolver from './http-resolver';

class HybridResolver {

    constructor(baseUrl) {
        this.httpResolver = new HttpResolver(baseUrl);
    }

    resolveRule(pkg, ruleName) {
        if (pkg === 'bpmnlint') {
            try {
                return require(`bpmnlint/rules/${ruleName}`);
            } catch (err) {
                throw new Error(`Failed to resolve rule '${ruleName}' in '${pkg}': ${err.message}`);
            }
        } else {
            return this.httpResolver.resolveRule(pkg, ruleName);
        }
    }

    resolveConfig(pkg, configName) {
        if (pkg === 'bpmnlint') {
            try {
                return require(`bpmnlint/config/${configName}`);
            } catch (err) {
                throw new Error(`Failed to resolve config '${configName}' in '${pkg}': ${err.message}`);
            }
        } else {
            return this.httpResolver.resolveConfig(pkg, configName);
        }
    }
}

export default HybridResolver;
