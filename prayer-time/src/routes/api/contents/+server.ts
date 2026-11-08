import { prisma } from '$lib/server/prisma';
import { storeFile, BUCKET_CONTENT } from '$lib/server/s3';
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

export const GET: RequestHandler = async () => {
	try {
		const items = await prisma.content.findMany({
			orderBy: { id: 'desc' }
		});
		return json(items.map(toDto));
	} catch (err) {
		console.error('Error fetching contents:', err);
		return json({ message: 'Internal server error' }, { status: 500 });
	}
};

export const POST: RequestHandler = async ({ request }) => {
	try {
		await authenticateRequest(request);

		const formData = await request.formData();
		const file = formData.get('file');

		if (!isFileUploaded(file)) {
			return json({ message: 'File is required for new content' }, { status: 400 });
		}

		const fileError = validateFile(file);
		if (fileError) {
			return json({ message: fileError }, { status: 400 });
		}

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

		if (!requestDto.title) {
			return json({ message: 'Title is required' }, { status: 400 });
		}

		const item = await prisma.content.create({
			data: {
				title: requestDto.title,
				active: requestDto.active ?? false
			}
		});

		const buffer = Buffer.from(await file.arrayBuffer());
		await storeFile(
			BUCKET_CONTENT,
			item.id.toString(),
			buffer,
			file.name,
			file.type
		);

		return json(toDto(item));
	} catch (err: any) {
		if (err?.status) {
			return json({ message: err.body?.message || err.message }, { status: err.status });
		}
		console.error('Error creating content:', err);
		return json({ message: 'Internal server error' }, { status: 500 });
	}
};
