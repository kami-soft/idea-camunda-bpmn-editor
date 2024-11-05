import BpmnModeler7 from "camunda-bpmn-js/lib/camunda-platform/Modeler";
import BpmnModeler8 from "camunda-bpmn-js/lib/camunda-cloud/Modeler";
import BpmnModeler from 'bpmn-js/lib/Modeler';
import {CreateAppendElementTemplatesModule} from "bpmn-js-create-append-anything";
import ElementTemplateChooserModule from "@bpmn-io/element-template-chooser";
import TokenSimulationModule from "bpmn-js-token-simulation";
import {isDraculaMode, engine} from './utils';
import $ from "jquery";
import './styles';
import { BpmnPropertiesPanelModule, BpmnPropertiesProviderModule } from 'bpmn-js-properties-panel';

export function initModeler() {
    const commonModules = [TokenSimulationModule, ElementTemplateChooserModule];
    let modeler;
    switch (engine) {
        case 'c7': {
            modeler = new BpmnModeler7({
                keyboard: {bindTo: document},
                container: $('#js-canvas'),
                propertiesPanel: {parent: $('#js-properties-panel')},
                alignToOrigin: {
                    alignOnSave: false,
                    offset: 150,
                    tolerance: 50,
                },
                additionalModules: [
                    ...commonModules,
                    CreateAppendElementTemplatesModule
                ],
                ...(isDraculaMode ? {
                    bpmnRenderer: {
                        defaultFillColor: 'rgba(43,45,48,255)',
                        defaultStrokeColor: '#BCBEC4'
                    }
                } : {})
            });
            break;
        }
        case 'c8': {
            modeler = new BpmnModeler8({
                keyboard: {
                    bindTo: document,
                },
                container: $('#js-canvas'),
                propertiesPanel: {
                    parent: $('#js-properties-panel'),
                },
                alignToOrigin: {
                    alignOnSave: false,
                    offset: 150,
                    tolerance: 50,
                },
                additionalModules: [...commonModules],
                ...(isDraculaMode ? {
                    bpmnRenderer: {
                        defaultFillColor: 'rgba(43,45,48,255)',
                        defaultStrokeColor: '#BCBEC4'
                    }
                } : {})
            });
            break;
        }
        case 'cb': {
            modeler = new BpmnModeler({
                keyboard: {
                    bindTo: document,
                },
                container: $('#js-canvas'),
                propertiesPanel: {
                    parent: $('#js-properties-panel'),
                },
                alignToOrigin: {
                    alignOnSave: false,
                    offset: 150,
                    tolerance: 50,
                },
                additionalModules: [
                    BpmnPropertiesPanelModule,
                    BpmnPropertiesProviderModule
                ],
                ...(isDraculaMode ? {
                    bpmnRenderer: {
                        defaultFillColor: 'rgba(43,45,48,255)',
                        defaultStrokeColor: '#BCBEC4'
                    }
                } : {})
            });
            break;
        }
        default: {
            throw new UnsupportedEngineError(engine);
        }
    }

    return modeler;
}

export class UnsupportedEngineError extends Error {
    constructor(engine) {
        super(`Unsupported engine: ${engine}`);
    }
}