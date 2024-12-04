import request from 'sync-request';

class HttpResolver {

    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }

    fetchAndCompileSync(url) {
        try {
            const response = request('GET', url);
            if (response.statusCode !== 200) {
                throw new Error(`Failed to fetch: ${url} (status code: ${response.statusCode})`);
            }

            const content = response.getBody('utf8');
            const module = {};
            const fn = new Function('module', content);
            fn(module);

            return module.exports;
        } catch (err) {
            throw new Error(`Error fetching and compiling ${url}: ${err.message}`);
        }
    }

    resolveRule(pkg, ruleName) {
        return this.fetchAndCompileSync(`${this.baseUrl}/lint/${pkg}/rules/${ruleName}.js`);
    }

    resolveConfig(pkg, configName) {
        const url = `${this.baseUrl}/lint/${pkg}/index.js`;
        const moduleExports = this.fetchAndCompileSync(url);
        if (!moduleExports.configs || !(configName in moduleExports.configs)) {
            throw new Error(`Config ${configName} not found in ${url}`);
        }

        return moduleExports.configs[configName];
    }
}

export default HttpResolver;