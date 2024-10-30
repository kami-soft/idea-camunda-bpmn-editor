import BpmnModeler7 from "camunda-bpmn-js/lib/camunda-platform/Modeler";
import {CreateAppendElementTemplatesModule} from "bpmn-js-create-append-anything";
import ElementTemplateChooserModule from "@bpmn-io/element-template-chooser";
import TokenSimulationModule from "bpmn-js-token-simulation";
import {isDraculaMode} from './utils';
import $ from "jquery";
import './styles';

export function initModeler() {
    const commonModules = [TokenSimulationModule, ElementTemplateChooserModule];

    return new BpmnModeler7({
        keyboard: {bindTo: document},
        container: $('#js-canvas'),
        propertiesPanel: {parent: $('#js-properties-panel')},
        alignToOrigin: {
            alignOnSave: false,
            offset: 150,
            tolerance: 50,
        },
        additionalModules: [
            commonModules,
            CreateAppendElementTemplatesModule
        ],
        ...(isDraculaMode ? {
            bpmnRenderer: {
                defaultFillColor: 'rgba(43,45,48,255)',
                defaultStrokeColor: '#BCBEC4'
            }
        } : {})
    });
}