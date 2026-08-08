import { 
    S3Client, 
    HeadBucketCommand, 
    CreateBucketCommand, 
    PutObjectCommand, 
    GetObjectCommand, 
    DeleteObjectCommand 
} from "@aws-sdk/client-s3";
import dotenv from "dotenv";

dotenv.config();

const s3Client = new S3Client({
    endpoint: process.env.CLOUD_AWS_S3_ENDPOINT || "http://localhost:9000",
    region: process.env.CLOUD_AWS_REGION || "us-east-1",
    credentials: {
        accessKeyId: process.env.CLOUD_AWS_ACCESSKEY || "minioadmin",
        secretAccessKey: process.env.CLOUD_AWS_SECRETKEY || "minioadmin",
    },
    forcePathStyle: process.env.CLOUD_AWS_PATH_STYLE_ACCESS !== "false",
});

export const BUCKET_CONTENT = "content";
export const BUCKET_STUDY_SESSION = "study-session";

export async function initBuckets() {
    for (const bucket of [BUCKET_CONTENT, BUCKET_STUDY_SESSION]) {
        try {
            await s3Client.send(new HeadBucketCommand({ Bucket: bucket }));
        } catch (error: any) {
            console.log(`Bucket ${bucket} not found, creating...`);
            try {
                await s3Client.send(new CreateBucketCommand({ Bucket: bucket }));
                console.log(`Bucket ${bucket} created.`);
            } catch (err) {
                console.error(`Failed to create bucket ${bucket}:`, err);
            }
        }
    }
}

export async function storeFile(
    bucket: string, 
    key: string, 
    body: Buffer, 
    filename: string, 
    contentType: string
): Promise<void> {
    await s3Client.send(new PutObjectCommand({
        Bucket: bucket,
        Key: key,
        Body: body,
        ContentType: contentType,
        Metadata: {
            filename: filename
        }
    }));
}

export async function deleteFile(bucket: string, key: string): Promise<void> {
    try {
        await s3Client.send(new DeleteObjectCommand({
            Bucket: bucket,
            Key: key
        }));
    } catch (error) {
        console.error(`Error deleting object ${key} in bucket ${bucket}:`, error);
    }
}

export async function getFileStream(bucket: string, key: string) {
    const response = await s3Client.send(new GetObjectCommand({
        Bucket: bucket,
        Key: key
    }));
    return response.Body; // This is a stream
}
