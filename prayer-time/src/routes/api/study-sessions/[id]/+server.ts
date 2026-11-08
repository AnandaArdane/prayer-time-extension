import { prisma } from '$lib/server/prisma';
import { storeFile, deleteFile, BUCKET_STUDY_SESSION } from '$lib/server/s3';
import { authenticateRequest } from '$lib/server/auth';
import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

const ALLOWED_TYPES = ['image/png', 'image/jpeg', 'image/jpg', 'image/webp'];
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

function isFileUploaded(entry: FormDataEntryValue | null): entry is File {
	return entry instanceof File && entry.size > 0 && entry.name !== '';
}

function validateFile(file: File): string | null {
	if (!ALLOWED_TYPES.includes(file.type)) {
		return 'File type not allowed. Only PNG, JPEG, JPG, and WEBP are permitted.';
	}
	if (file.size > MAX_FILE_SIZE) {
		return 'File size exceeds 10MB limit';
	}
	return null;
}

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

export const PUT: RequestHandler = async ({ params, request }) => {
	try {
		await authenticateRequest(request);

		let id: bigint;
		try {
			id = BigInt(params.id);
		} catch {
			return json({ message: 'Study session not found' }, { status: 404 });
		}

		const existing = await prisma.study_session.findUnique({ where: { id } });
		if (!existing) {
			return json({ message: 'Study session not found' }, { status: 404 });
		}

		const formData = await request.formData();
		const studySessionRequestDtoRaw = formData.get('studySessionRequestDto');
		if (!studySessionRequestDtoRaw) {
			return json({ message: 'studySessionRequestDto is required' }, { status: 400 });
		}

		let dtoString = '';
		if (typeof studySessionRequestDtoRaw === 'string') {
			dtoString = studySessionRequestDtoRaw;
		} else if (
			studySessionRequestDtoRaw &&
			typeof studySessionRequestDtoRaw === 'object' &&
			'text' in studySessionRequestDtoRaw
		) {
			dtoString = await (studySessionRequestDtoRaw as Blob).text();
		}

		let requestDto: { title?: string; place?: string; date?: string; time?: string };
		try {
			requestDto = JSON.parse(dtoString);
		} catch {
			return json({ message: 'Invalid studySessionRequestDto format' }, { status: 400 });
		}

		const { title, place, date, time } = requestDto;
		const file = formData.get('file');
		const hasNewFile = isFileUploaded(file);

		if (hasNewFile && file) {
			const fileError = validateFile(file);
			if (fileError) {
				return json({ message: fileError }, { status: 400 });
			}
		}

		const hasImage = hasNewFile ? true : existing.has_image;

		const updateData: {
			title?: string;
			place?: string | null;
			date?: Date;
			time?: Date;
			has_image?: boolean;
		} = {};

		if (title !== undefined) updateData.title = title;
		if (place !== undefined) updateData.place = place || null;
		if (date !== undefined) updateData.date = new Date(`${date}T00:00:00.000Z`);
		if (time !== undefined) {
			const formattedTimeStr = time.length === 5 ? `${time}:00` : time;
			updateData.time = new Date(`1970-01-01T${formattedTimeStr}Z`);
		}
		updateData.has_image = hasImage;

		const updated = await prisma.study_session.update({
			where: { id },
			data: updateData
		});

		if (hasNewFile && isFileUploaded(file)) {
			const buffer = Buffer.from(await file.arrayBuffer());
			await storeFile(
				BUCKET_STUDY_SESSION,
				id.toString(),
				buffer,
				file.name,
				file.type
			);
		}

		return json(toDto(updated));
	} catch (err: any) {
		if (err?.status) {
			return json({ message: err.body?.message || err.message }, { status: err.status });
		}
		console.error('Error updating study session:', err);
		return json({ message: 'Internal server error' }, { status: 500 });
	}
};

export const DELETE: RequestHandler = async ({ params, request }) => {
	try {
		await authenticateRequest(request);

		let id: bigint;
		try {
			id = BigInt(params.id);
		} catch {
			return json({ message: 'Study session not found' }, { status: 404 });
		}

		const existing = await prisma.study_session.findUnique({ where: { id } });
		if (!existing) {
			return json({ message: 'Study session not found' }, { status: 404 });
		}

		await prisma.study_session.delete({ where: { id } });
		if (existing.has_image) {
			await deleteFile(BUCKET_STUDY_SESSION, id.toString());
		}

		return new Response(null, { status: 204 });
	} catch (err: any) {
		if (err?.status) {
			return json({ message: err.body?.message || err.message }, { status: err.status });
		}
		console.error('Error deleting study session:', err);
		return json({ message: 'Internal server error' }, { status: 500 });
	}
};
