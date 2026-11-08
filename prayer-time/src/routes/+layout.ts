import type { LayoutLoad } from './$types';

export const ssr = false;

export const load: LayoutLoad = () => {
    // No redirects, /index.html is handled as a native route now.
};
