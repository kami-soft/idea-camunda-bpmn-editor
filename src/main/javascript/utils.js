export const isDraculaMode = new URLSearchParams(window.location.search).get('colorTheme') === 'DARK';
export const engine = new URLSearchParams(window.location.search).get('engine');
export const scriptFormat = new URLSearchParams(window.location.search).get('scriptFormat');