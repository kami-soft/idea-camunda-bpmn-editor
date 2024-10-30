import {initModeler} from './modeler';
import {createNewDiagram, openDiagram} from './diagramHandlers';
import {enableResizing} from './resizer';
import {setupObserver} from './observers';
import './styles';
import {isDraculaMode} from './utils';
import {handlePasteAsync} from "./copyPaste";
import {isPaste} from "diagram-js/lib/features/keyboard/KeyboardUtil";

if (isDraculaMode) {
    document.body.classList.add('dracula');
}

const bpmnModeler = initModeler();

window.initApp = async function () {
    if (window.bpmnXml && window.bpmnXml.length > 0) {
        await openDiagram(atob(window.bpmnXml), bpmnModeler);
    } else {
        await createNewDiagram(bpmnModeler);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    enableResizing();
    createNewDiagram(bpmnModeler).then(r => setupObserver());
});

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
})

bpmnModeler.get('keyboard').addListener(3000, event => {
    const {keyEvent} = event;
    if (!isPaste(keyEvent)) {
        return;
    }

    handlePasteAsync(bpmnModeler);
});