import {enableResizing} from './panel-properties/resizer';
import {setupObserver} from './script/observers';
import './styles';
import {isDarkMode, isDraculaMode} from './utils/utils';
import {handlePasteAsync} from "./modeler/copy-paste";
import base64js from 'base64-js';

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
        await openDiagram(Base64Decode(window.bpmnXml), bpmnModeler);
    } else {
        const {createNewDiagram} = await import('./modeler/diagram-handlers');
        await createNewDiagram(bpmnModeler);
    }

    enableResizing();
    setupObserver();

    bpmnModeler.on('commandStack.changed', async () => {
        try {
            const {xml} = await bpmnModeler.saveXML({format: true});
            window.updateBpmnXml(Base64Encode(xml));
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

function Base64Encode(str, encoding = "utf-8") {
    var bytes = new (TextEncoder || TextEncoderLite)(encoding).encode(str);
    return base64js.fromByteArray(bytes);
}

function Base64Decode(str, encoding = "utf-8") {
    var bytes = base64js.toByteArray(str);
    return new (TextDecoder || TextDecoderLite)(encoding).decode(bytes);
}