export const isDarkMode = new URLSearchParams(window.location.search).get('colorTheme') === 'DARK';
export const isDraculaMode = new URLSearchParams(window.location.search).get('colorTheme') === 'DRACULA';
export const isSketchyMode = new URLSearchParams(window.location.search).get('schemaTheme') === 'SKETCHY';
export const engine = new URLSearchParams(window.location.search).get('engine');
export const scriptFormat = new URLSearchParams(window.location.search).get('scriptFormat');