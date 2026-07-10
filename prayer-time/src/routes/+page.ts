import type { PageLoad } from './$types';
import { getPrayerData, getSlides, getSidebarEvents } from '$lib/services/prayerService';

export const load: PageLoad = async ({ url }) => {
    const latParam = url.searchParams.get('lat');
    const longParam = url.searchParams.get('long');
    const timeParam = url.searchParams.get('time');

    const lat = latParam ? parseFloat(latParam) : undefined;
    const long = longParam ? parseFloat(longParam) : undefined;

    let referenceDate: Date | undefined;
    if (timeParam) {
        const parsed = new Date(timeParam);
        if (!isNaN(parsed.getTime())) {
            referenceDate = parsed;
        }
    }

    const [prayerData, slides, sidebarEvents] = await Promise.all([
        getPrayerData(lat, long, referenceDate),
        getSlides(),
        getSidebarEvents()
    ]);

    return {
        prayerData,
        slides,
        sidebarEvents,
        simulatedTime: referenceDate ? referenceDate.toISOString() : null
    };
};
