import { prisma } from '$lib/server/prisma';
import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

function toDto(item: { id: bigint; title: string; active: boolean }) {
	return {
		id: Number(item.id),
		title: item.title,
		active: item.active,
		url: `/api/contents/${item.id}/file`
	};
}

export const GET: RequestHandler = async () => {
	try {
		const items = await prisma.content.findMany({
			where: { active: true },
			orderBy: { id: 'desc' }
		});
		return json(items.map(toDto));
	} catch (err) {
		console.error('Error fetching active contents:', err);
		return json({ message: 'Internal server error' }, { status: 500 });
	}
};
