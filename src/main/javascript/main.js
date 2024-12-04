import {enableResizing} from './panel-properties/resizer';
import {setupObserver} from './script/observers';
import './styles';
import {isDarkMode, isDraculaMode} from './utils/utils';
import {handlePasteAsync} from "./modeler/copy-paste";

if (isDarkMode) {
    await import("../resources/ui/themes/dark/style.css");
} else if (isDraculaMode) {
    await import("../resources/ui/themes/dracula/style.css");
} else {
    document.body.classList.add('light');
}

window.initApp = async function () {
    const {initModeler, setLinting} = await import('./modeler/modeler');
    const useLintModule = window.serverBaseUrl && window.bpmnlintrc;
    const bpmnModeler = await initModeler(useLintModule);
    if (useLintModule) {
        await setLinting(bpmnModeler, window.serverBaseUrl, window.bpmnlintrc);
    }

    if (window.bpmnXml && window.bpmnXml.length > 0) {
        const {openDiagram} = await import('./modeler/diagram-handlers');
        await openDiagram(atob(window.bpmnXml), bpmnModeler);
    } else {
        const {createNewDiagram} = await import('./modeler/diagram-handlers');
        await createNewDiagram(bpmnModeler);
    }

    enableResizing();
    setupObserver();

    bpmnModeler.on('commandStack.changed', async () => {
        try {
            const {xml} = await bpmnModeler.saveXML({format: true});
            window.updateBpmnXml(btoa(xml));
        } catch (err) {
            console.error('Error while saving XML:', err);
        }
    });

    bpmnModeler.on('copyPaste.elementsCopied', event => {
        const {tree} = event;
        try {
            window.copyBpmnClipboard(JSON.stringify(tree));
        } catch (err) {
            console.error('Error while copyBpmnClipboard:', err);
        }
    });

    bpmnModeler.get('keyboard').addListener(3000, event => {
        handlePasteAsync(event, bpmnModeler, window.serverBaseUrl);
    });
}