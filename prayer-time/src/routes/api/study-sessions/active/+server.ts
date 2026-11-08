import { prisma } from '$lib/server/prisma';
import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

function formatTime(time: Date): string {
	const hours = String(time.getUTCHours()).padStart(2, '0');
	const minutes = String(time.getUTCMinutes()).padStart(2, '0');
	const seconds = String(time.getUTCSeconds()).padStart(2, '0');
	return `${hours}:${minutes}:${seconds}`;
}

function toDto(item: {
	id: bigint;
	place: string | null;
	date: Date;
	time: Date;
	title: string;
	has_image: boolean;
}) {
	return {
		id: Number(item.id),
		place: item.place,
		date: item.date.toISOString().split('T')[0],
		time: formatTime(item.time),
		title: item.title,
		url: item.has_image ? `/api/study-sessions/${item.id}/file` : null
	};
}

export const GET: RequestHandler = async () => {
	try {
		const todayStr = new Date().toISOString().split('T')[0];
		const todayDate = new Date(`${todayStr}T00:00:00.000Z`);

		const items = await prisma.study_session.findMany({
			where: {
				date: {
					gte: todayDate
				}
			},
			orderBy: [{ date: 'asc' }, { time: 'asc' }]
		});

		return json(items.map(toDto));
	} catch (err) {
		console.error('Error fetching active study sessions:', err);
		return json({ message: 'Internal server error' }, { status: 500 });
	}
};
