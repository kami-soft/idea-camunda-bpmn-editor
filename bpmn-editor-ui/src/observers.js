import {
    closeAllVirtualFiles,
    processAllScriptEditors,
    processAllScriptFormats
} from './scriptEditor';

function isChangedFullScriptBlock(node) {
    return (node.dataset && node.dataset.groupId && node.dataset.groupId.includes('group-CamundaPlatform__Script'))
        || (node.classList && node.classList.contains('bio-properties-panel-list-item'))
        || (node.dataset && node.dataset.groupId && node.dataset.groupId.includes('group-CamundaPlatform__Condition'))
        || (node.dataset && node.dataset.groupId && node.dataset.groupId.includes('group-CamundaPlatform__Input'))
        || (node.dataset && node.dataset.groupId && node.dataset.groupId.includes('group-CamundaPlatform__Output'));
}

function isChangedScriptFormat(node) {
    return (node.dataset && node.dataset.entryId &&
        (node.dataset.entryId.includes('scriptFormat') || node.dataset.entryId.includes('ScriptLanguage')));
}

function isChangedScriptValue(node) {
    return node.dataset &&  node.dataset.entryId &&
        (node.dataset.entryId.includes('scriptValue') || node.dataset.entryId.includes('ScriptValue'));
}

export function setupObserver() {
    new MutationObserver((mutationsList) => {
                for (const mutation of mutationsList) {
                    if (mutation.type !== 'childList') {
                        continue;
                    }

                    mutation.addedNodes.forEach((node) => {
                            if (node.nodeType !== Node.ELEMENT_NODE) {
                                return;
                            }

                            if (isChangedFullScriptBlock(node)) {
                                processAllScriptFormats(node);
                                processAllScriptEditors(node);
                            } else if (isChangedScriptFormat(node)) {
                                processAllScriptFormats(node);
                            } else if (isChangedScriptValue(node)) {
                                processAllScriptEditors(node);
                            }
                        }
                    );

                    mutation.removedNodes.forEach(node => {
                        if (isChangedFullScriptBlock(node) || isChangedScriptValue(node)) {
                            closeAllVirtualFiles(node);
                        }
                    });
                }
            }
        ).observe(document.body, {
        childList: true,
        subtree: true,
    });
}