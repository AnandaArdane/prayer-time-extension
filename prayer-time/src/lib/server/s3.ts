import {
	S3Client,
	HeadBucketCommand,
	CreateBucketCommand,
	PutObjectCommand,
	GetObjectCommand,
	DeleteObjectCommand
} from '@aws-sdk/client-s3';
import { env } from '$env/dynamic/private';

const s3Client = new S3Client({
	endpoint: env.CLOUD_AWS_S3_ENDPOINT || 'http://localhost:9000',
	region: env.CLOUD_AWS_REGION || 'us-east-1',
	credentials: {
		accessKeyId: env.CLOUD_AWS_ACCESSKEY || 'minioadmin',
		secretAccessKey: env.CLOUD_AWS_SECRETKEY || 'minioadmin'
	},
	forcePathStyle: env.CLOUD_AWS_PATH_STYLE_ACCESS !== 'false'
});

export const BUCKET_CONTENT = 'content';
export const BUCKET_STUDY_SESSION = 'study-session';

export async function initBuckets(): Promise<void> {
	for (const bucket of [BUCKET_CONTENT, BUCKET_STUDY_SESSION]) {
		try {
			await s3Client.send(new HeadBucketCommand({ Bucket: bucket }));
			console.log(`Bucket already exists: ${bucket}`);
		} catch (error: any) {
			console.log(`Bucket ${bucket} not found, creating...`);
			try {
				await s3Client.send(new CreateBucketCommand({ Bucket: bucket }));
				console.log(`Bucket created: ${bucket}`);
			} catch (err: any) {
				// R2 and some S3-compatible services may throw this even when headBucket fails
				if (err.name === 'BucketAlreadyExists' || err.name === 'BucketAlreadyOwnedByYou') {
					console.log(`Bucket already exists: ${bucket}`);
				} else {
					console.error(`Failed to create bucket ${bucket}:`, err);
				}
			}
		}
	}
}

export async function storeFile(
	bucket: string,
	key: string,
	body: Buffer | Uint8Array,
	filename: string,
	contentType: string
): Promise<void> {
	await s3Client.send(
		new PutObjectCommand({
			Bucket: bucket,
			Key: key,
			Body: body,
			ContentType: contentType,
			Metadata: {
				filename: filename
			}
		})
	);
}

export async function deleteFile(bucket: string, key: string): Promise<void> {
	try {
		await s3Client.send(
			new DeleteObjectCommand({
				Bucket: bucket,
				Key: key
			})
		);
	} catch (error) {
		console.error(`Error deleting object ${key} in bucket ${bucket}:`, error);
	}
}

export async function getFileStream(bucket: string, key: string) {
	const response = await s3Client.send(
		new GetObjectCommand({
			Bucket: bucket,
			Key: key
		})
	);
	return response.Body; // This is a stream
}
