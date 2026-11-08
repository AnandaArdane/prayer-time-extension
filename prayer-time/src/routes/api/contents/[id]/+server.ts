import { prisma } from '$lib/server/prisma';
import { storeFile, deleteFile, BUCKET_CONTENT } from '$lib/server/s3';
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

function toDto(item: { id: bigint; title: string; active: boolean }) {
	return {
		id: Number(item.id),
		title: item.title,
		active: item.active,
		url: `/api/contents/${item.id}/file`
	};
}

export const PUT: RequestHandler = async ({ params, request }) => {
	try {
		await authenticateRequest(request);

		let id: bigint;
		try {
			id = BigInt(params.id);
		} catch {
			return json({ message: 'Content not found' }, { status: 404 });
		}

		const existing = await prisma.content.findUnique({ where: { id } });
		if (!existing) {
			return json({ message: 'Content not found' }, { status: 404 });
		}

		const formData = await request.formData();
		const contentRequestDtoRaw = formData.get('contentRequestDto');
		if (!contentRequestDtoRaw) {
			return json({ message: 'contentRequestDto is required' }, { status: 400 });
		}

		let dtoString = '';
		if (typeof contentRequestDtoRaw === 'string') {
			dtoString = contentRequestDtoRaw;
		} else if (contentRequestDtoRaw && typeof contentRequestDtoRaw === 'object' && 'text' in contentRequestDtoRaw) {
			dtoString = await (contentRequestDtoRaw as Blob).text();
		}

		let requestDto: { title?: string; active?: boolean };
		try {
			requestDto = JSON.parse(dtoString);
		} catch {
			return json({ message: 'Invalid contentRequestDto format' }, { status: 400 });
		}

		const file = formData.get('file');
		if (isFileUploaded(file)) {
			const fileError = validateFile(file);
			if (fileError) {
				return json({ message: fileError }, { status: 400 });
			}
		}

		const updated = await prisma.content.update({
			where: { id },
			data: {
				title: requestDto.title ?? existing.title,
				active: requestDto.active ?? existing.active
			}
		});

		// If new file is uploaded, replace existing file in S3
		if (isFileUploaded(file)) {
			const buffer = Buffer.from(await file.arrayBuffer());
			await storeFile(
				BUCKET_CONTENT,
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
		console.error('Error updating content:', err);
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
			return json({ message: 'Content not found' }, { status: 404 });
		}

		const existing = await prisma.content.findUnique({ where: { id } });
		if (!existing) {
			return json({ message: 'Content not found' }, { status: 404 });
		}

		await prisma.content.delete({ where: { id } });
		await deleteFile(BUCKET_CONTENT, id.toString());

		return new Response(null, { status: 204 });
	} catch (err: any) {
		if (err?.status) {
			return json({ message: err.body?.message || err.message }, { status: err.status });
		}
		console.error('Error deleting content:', err);
		return json({ message: 'Internal server error' }, { status: 500 });
	}
};
