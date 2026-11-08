import { prisma } from '$lib/server/prisma';
import { getFileStream, BUCKET_STUDY_SESSION } from '$lib/server/s3';
import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export const GET: RequestHandler = async ({ params }) => {
	let id: bigint;
	try {
		id = BigInt(params.id);
	} catch {
		return json({ message: 'Study session not found' }, { status: 404 });
	}

	try {
		const item = await prisma.study_session.findUnique({ where: { id } });
		if (!item || !item.has_image) {
			return json({ message: 'File not found' }, { status: 404 });
		}

		const stream = await getFileStream(BUCKET_STUDY_SESSION, id.toString());
		let responseStream: any = stream;
		if (stream && typeof (stream as any).transformToWebStream === 'function') {
			responseStream = (stream as any).transformToWebStream();
		}

		return new Response(responseStream as ReadableStream, {
			headers: { 'Content-Type': 'image/jpeg' }
		});
	} catch (error) {
		console.error('Error retrieving study session file:', error);
		return json({ message: 'File not found' }, { status: 404 });
	}
};
