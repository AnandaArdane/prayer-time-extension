import type { LayoutLoad } from './$types';

export const prerender = false;
export const ssr = false;

export const load: LayoutLoad = () => {
    // No redirects, /index.html is handled as a native route now.
};
