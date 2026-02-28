import { redirect, type Handle } from '@sveltejs/kit';

export const handle: Handle = async ({ event, resolve }) => {
    const jwt = event.cookies.get('jwt');
    const { pathname } = event.url;

    // Protect admin routes
    if (pathname.startsWith('/admin')) {
        if (!jwt) {
            throw redirect(303, '/login');
        }
    }

    // Redirect to admin if already logged in and trying to access login page
    if (pathname === '/login' && jwt) {
        throw redirect(303, '/admin/contents');
    }

    const response = await resolve(event);
    return response;
};
