import { redirect } from '@sveltejs/kit';
import type { LayoutLoad } from './$types';

export const prerender = false;
export const ssr = false;

export const load: LayoutLoad = ({ url }) => {
	if (url.pathname === '/index.html') {
		throw redirect(307, '/');
	}
};
