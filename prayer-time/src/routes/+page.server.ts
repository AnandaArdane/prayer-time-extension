import type { PageServerLoad } from './$types';
import { getPrayerData, getSlides, getSidebarEvents } from '$lib/server/services/prayerService';

export const load: PageServerLoad = async ({ url }) => {
    const latParam = url.searchParams.get('lat');
    const longParam = url.searchParams.get('long');

    const lat = latParam ? parseFloat(latParam) : undefined;
    const long = longParam ? parseFloat(longParam) : undefined;

    const [prayerData, slides] = await Promise.all([
        getPrayerData(lat, long),
        getSlides()
    ]);
    const sidebarEvents = getSidebarEvents();

    return {
        prayerData,
        slides,
        sidebarEvents
    };
};
