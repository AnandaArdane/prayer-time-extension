import type { PageServerLoad } from './$types';
import { getLandingPageData } from '$lib/server/services/prayerService';

export const load: PageServerLoad = async ({ url }) => {
    const latParam = url.searchParams.get('lat');
    const longParam = url.searchParams.get('long');

    const lat = latParam ? parseFloat(latParam) : undefined;
    const long = longParam ? parseFloat(longParam) : undefined;

    const data = await getLandingPageData(lat, long);
    return {
        ...data
    };
};
