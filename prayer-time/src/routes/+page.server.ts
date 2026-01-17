import type { PageServerLoad } from './$types';
import { getLandingPageData } from '$lib/server/services/prayerService';

export const load: PageServerLoad = async () => {
    const data = await getLandingPageData();
    return {
        ...data
    };
};
