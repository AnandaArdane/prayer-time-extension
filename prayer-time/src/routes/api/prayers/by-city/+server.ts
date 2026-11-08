import { getTimingsByCity } from '$lib/server/aladhan';
import { calculateHijriDate } from '$lib/server/hijri';
import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

const formatTime = (t: string) => t.substring(0, 5);

export const GET: RequestHandler = async ({ url }) => {
	try {
		const name = url.searchParams.get('name');
		const date = url.searchParams.get('date');

		if (!name || !date) {
			return json(
				{ message: 'City name and date (YYYY-MM-DD) are required' },
				{ status: 400 }
			);
		}

		const timings = await getTimingsByCity(name, date);

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
		console.error('Error fetching prayer times by city:', err);
		return json({ message: err?.message || 'Failed to fetch prayer times' }, { status: 500 });
	}
};
