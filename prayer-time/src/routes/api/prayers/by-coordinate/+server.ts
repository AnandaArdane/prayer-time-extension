import { getTimingsByCoordinate } from '$lib/server/aladhan';
import { calculateHijriDate } from '$lib/server/hijri';
import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

const formatTime = (t: string) => t.substring(0, 5);

export const GET: RequestHandler = async ({ url }) => {
	try {
		const latitude = url.searchParams.get('latitude');
		const longitude = url.searchParams.get('longitude');
		const date = url.searchParams.get('date');

		if (!latitude || !longitude || !date) {
			return json(
				{ message: 'Latitude, longitude, and date (YYYY-MM-DD) are required' },
				{ status: 400 }
			);
		}

		const lat = parseFloat(latitude);
		const lon = parseFloat(longitude);

		if (isNaN(lat) || isNaN(lon)) {
			return json({ message: 'Invalid latitude or longitude' }, { status: 400 });
		}

		const timings = await getTimingsByCoordinate(lat, lon, date);

		const maghrib = formatTime(timings.Maghrib);
		const hijriDate = calculateHijriDate(date, maghrib);

		return json({
			fajr: formatTime(timings.Fajr),
			dhuhr: formatTime(timings.Dhuhr),
			asr: formatTime(timings.Asr),
			maghrib,
			isha: formatTime(timings.Isha),
			hijriDate
		});
	} catch (err: any) {
		console.error('Error fetching prayer times by coordinate:', err);
		return json({ message: err?.message || 'Failed to fetch prayer times' }, { status: 500 });
	}
};
