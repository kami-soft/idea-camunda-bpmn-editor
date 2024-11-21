import {isPaste} from "diagram-js/lib/features/keyboard/KeyboardUtil";

export async function handlePasteAsync(event, bpmnModeler) {
    const {keyEvent} = event;
    if (!isPaste(keyEvent)) {
        return;
    }

    const serializedCopy = await window.getBpmnClipboard('bpmnClipboard');
    if (!serializedCopy) {
        return;
    }

    const reviver = createReviver(bpmnModeler.get('moddle'));
    const parsedCopy = JSON.parse(serializedCopy, reviver);

    const clipboard = bpmnModeler.get('clipboard');
    clipboard.set(parsedCopy);
}

export function createReviver(moddle) {
    return function(key, object) {
        if (typeof object === 'object' && typeof object.$type === 'string') {
            const type = object.$type;
            const attrs = Object.assign({}, object);

            delete attrs.$type;

            return moddle.create(type, attrs);
        }

        return object;
    };
}
