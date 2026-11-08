import { Router } from "express";
import prisma from "../prisma";
import { authenticateToken } from "../middlewares/auth";
import { upload } from "../middlewares/upload";
import { 
    storeFile, 
    deleteFile, 
    getFileStream, 
    BUCKET_CONTENT 
} from "../services/s3";
import { Readable } from "stream";

const router = Router();

// Helper to convert Prisma entity to DTO
function toDto(item: any) {
    return {
        id: Number(item.id),
        title: item.title,
        active: item.active,
        url: `/api/contents/${item.id}/file`
    };
}

// 1. GET /api/contents (public)
router.get("/contents", async (req, res) => {
    try {
        const items = await prisma.content.findMany({
            orderBy: { id: "desc" }
        });
        return res.json(items.map(toDto));
    } catch (error) {
        console.error("Error fetching contents:", error);
        return res.status(500).json({ message: "Internal server error" });
    }
});

// 2. GET /api/contents/active (public)
router.get("/contents/active", async (req, res) => {
    try {
        const items = await prisma.content.findMany({
            where: { active: true },
            orderBy: { id: "desc" }
        });
        return res.json(items.map(toDto));
    } catch (error) {
        console.error("Error fetching active contents:", error);
        return res.status(500).json({ message: "Internal server error" });
    }
});

// 3. GET /api/contents/:id/file (public) - Stream file from S3/MinIO
router.get("/contents/:id/file", async (req, res) => {
    try {
        const id = BigInt(req.params.id);
        const item = await prisma.content.findUnique({ where: { id } });
        if (!item) {
            return res.status(404).json({ message: "Content not found" });
        }

        const stream = await getFileStream(BUCKET_CONTENT, id.toString());
        res.setHeader("Content-Type", "image/jpeg");
        
        if (stream instanceof Readable) {
            stream.pipe(res);
        } else if (stream && typeof (stream as any).pipe === 'function') {
            (stream as any).pipe(res);
        } else {
            // Web stream or SDK v3 structure
            const chunks = [];
            for await (const chunk of stream as any) {
                chunks.push(chunk);
            }
            res.send(Buffer.concat(chunks));
        }
    } catch (error: any) {
        console.error("Error retrieving file:", error);
        return res.status(404).json({ message: "File not found" });
    }
});

// 4. POST /api/contents (authenticated)
router.post("/contents", authenticateToken, upload.single("file"), async (req, res) => {
    try {
        if (!req.file) {
            return res.status(400).json({ message: "File is required for new content" });
        }

        // Parse contentRequestDto (sent as Blob/string)
        if (!req.body.contentRequestDto) {
            return res.status(400).json({ message: "contentRequestDto is required" });
        }

        let requestDto;
        try {
            requestDto = JSON.parse(req.body.contentRequestDto);
        } catch (e) {
            return res.status(400).json({ message: "Invalid contentRequestDto format" });
        }

        if (!requestDto.title) {
            return res.status(400).json({ message: "Title is required" });
        }

        // Create db record
        const item = await prisma.content.create({
            data: {
                title: requestDto.title,
                active: requestDto.active ?? false
            }
        });

        // Store file in S3/MinIO using content ID as the key
        await storeFile(
            BUCKET_CONTENT,
            item.id.toString(),
            req.file.buffer,
            req.file.originalname,
            req.file.mimetype
        );

        return res.status(200).json(toDto(item));
    } catch (error) {
        console.error("Error creating content:", error);
        return res.status(500).json({ message: "Internal server error" });
    }
});

// 5. PUT /api/contents/:id (authenticated)
router.put("/contents/:id", authenticateToken, upload.single("file"), async (req, res) => {
    try {
        const id = BigInt(req.params.id);
        const existing = await prisma.content.findUnique({ where: { id } });
        if (!existing) {
            return res.status(404).json({ message: "Content not found" });
        }

        if (!req.body.contentRequestDto) {
            return res.status(400).json({ message: "contentRequestDto is required" });
        }

        let requestDto;
        try {
            requestDto = JSON.parse(req.body.contentRequestDto);
        } catch (e) {
            return res.status(400).json({ message: "Invalid contentRequestDto format" });
        }

        const updated = await prisma.content.update({
            where: { id },
            data: {
                title: requestDto.title ?? existing.title,
                active: requestDto.active ?? existing.active
            }
        });

        // If new file is uploaded, replace existing file in S3
        if (req.file) {
            await storeFile(
                BUCKET_CONTENT,
                id.toString(),
                req.file.buffer,
                req.file.originalname,
                req.file.mimetype
            );
        }

        return res.json(toDto(updated));
    } catch (error) {
        console.error("Error updating content:", error);
        return res.status(500).json({ message: "Internal server error" });
    }
});

// 6. DELETE /api/contents/:id (authenticated)
router.delete("/contents/:id", authenticateToken, async (req, res) => {
    try {
        const id = BigInt(req.params.id);
        const existing = await prisma.content.findUnique({ where: { id } });
        if (!existing) {
            return res.status(404).json({ message: "Content not found" });
        }

        await prisma.content.delete({ where: { id } });
        await deleteFile(BUCKET_CONTENT, id.toString());

        return res.status(204).send();
    } catch (error) {
        console.error("Error deleting content:", error);
        return res.status(500).json({ message: "Internal server error" });
    }
});

export default router;
