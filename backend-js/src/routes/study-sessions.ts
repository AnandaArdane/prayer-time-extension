import { Router } from "express";
import prisma from "../prisma";
import { authenticateToken } from "../middlewares/auth";
import { upload } from "../middlewares/upload";
import { 
    storeFile, 
    deleteFile, 
    getFileStream, 
    BUCKET_STUDY_SESSION 
} from "../services/s3";
import { Readable } from "stream";

const router = Router();

// Helper to format Prisma Time to HH:mm:ss
function formatTime(time: Date): string {
    const hours = String(time.getUTCHours()).padStart(2, '0');
    const minutes = String(time.getUTCMinutes()).padStart(2, '0');
    const seconds = String(time.getUTCSeconds()).padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
}

// Helper to convert Prisma entity to DTO
function toDto(item: any) {
    return {
        id: Number(item.id),
        place: item.place,
        date: item.date.toISOString().split("T")[0],
        time: formatTime(item.time),
        title: item.title,
        url: item.has_image ? `/api/study-sessions/${item.id}/file` : null
    };
}

// 1. GET /api/study-sessions (public)
router.get("/study-sessions", async (req, res) => {
    try {
        const items = await prisma.study_session.findMany({
            orderBy: [
                { date: "desc" },
                { time: "desc" }
            ]
        });
        return res.json(items.map(toDto));
    } catch (error) {
        console.error("Error fetching study sessions:", error);
        return res.status(500).json({ message: "Internal server error" });
    }
});

// 2. GET /api/study-sessions/active (public)
router.get("/study-sessions/active", async (req, res) => {
    try {
        // Today at UTC midnight
        const todayStr = new Date().toISOString().split("T")[0];
        const todayDate = new Date(`${todayStr}T00:00:00.000Z`);

        const items = await prisma.study_session.findMany({
            where: {
                date: {
                    gte: todayDate
                }
            },
            orderBy: [
                { date: "asc" },
                { time: "asc" }
            ]
        });
        return res.json(items.map(toDto));
    } catch (error) {
        console.error("Error fetching active study sessions:", error);
        return res.status(500).json({ message: "Internal server error" });
    }
});

// 3. GET /api/study-sessions/:id/file (public)
router.get("/study-sessions/:id/file", async (req, res) => {
    try {
        const id = BigInt(req.params.id);
        const item = await prisma.study_session.findUnique({ where: { id } });
        if (!item || !item.has_image) {
            return res.status(404).json({ message: "File not found" });
        }

        const stream = await getFileStream(BUCKET_STUDY_SESSION, id.toString());
        res.setHeader("Content-Type", "image/jpeg");

        if (stream instanceof Readable) {
            stream.pipe(res);
        } else if (stream && typeof (stream as any).pipe === 'function') {
            (stream as any).pipe(res);
        } else {
            const chunks = [];
            for await (const chunk of stream as any) {
                chunks.push(chunk);
            }
            res.send(Buffer.concat(chunks));
        }
    } catch (error: any) {
        console.error("Error retrieving study session file:", error);
        return res.status(404).json({ message: "File not found" });
    }
});

// 4. POST /api/study-sessions (authenticated)
router.post("/study-sessions", authenticateToken, upload.single("file"), async (req, res) => {
    try {
        if (!req.body.studySessionRequestDto) {
            return res.status(400).json({ message: "studySessionRequestDto is required" });
        }

        let requestDto;
        try {
            requestDto = JSON.parse(req.body.studySessionRequestDto);
        } catch (e) {
            return res.status(400).json({ message: "Invalid studySessionRequestDto format" });
        }

        const { title, place, date, time } = requestDto;
        if (!title || !date || !time) {
            return res.status(400).json({ message: "Title, date, and time are required" });
        }

        const hasImage = !!req.file;

        // Parse date and time into UTC midnight / UTC time base
        const dateObj = new Date(`${date}T00:00:00.000Z`);
        const timeObj = new Date(`1970-01-01T${time}Z`);

        const item = await prisma.study_session.create({
            data: {
                title,
                place: place || null,
                date: dateObj,
                time: timeObj,
                has_image: hasImage
            }
        });

        if (hasImage && req.file) {
            await storeFile(
                BUCKET_STUDY_SESSION,
                item.id.toString(),
                req.file.buffer,
                req.file.originalname,
                req.file.mimetype
            );
        }

        return res.status(200).json(toDto(item));
    } catch (error) {
        console.error("Error creating study session:", error);
        return res.status(500).json({ message: "Internal server error" });
    }
});

// 5. PUT /api/study-sessions/:id (authenticated)
router.put("/study-sessions/:id", authenticateToken, upload.single("file"), async (req, res) => {
    try {
        const id = BigInt(req.params.id);
        const existing = await prisma.study_session.findUnique({ where: { id } });
        if (!existing) {
            return res.status(404).json({ message: "Study session not found" });
        }

        if (!req.body.studySessionRequestDto) {
            return res.status(400).json({ message: "studySessionRequestDto is required" });
        }

        let requestDto;
        try {
            requestDto = JSON.parse(req.body.studySessionRequestDto);
        } catch (e) {
            return res.status(400).json({ message: "Invalid studySessionRequestDto format" });
        }

        const { title, place, date, time } = requestDto;
        const hasImage = req.file ? true : existing.has_image;

        // Prep updates
        const updateData: any = {};
        if (title !== undefined) updateData.title = title;
        if (place !== undefined) updateData.place = place || null;
        if (date !== undefined) updateData.date = new Date(`${date}T00:00:00.000Z`);
        if (time !== undefined) updateData.time = new Date(`1970-01-01T${time}Z`);
        updateData.has_image = hasImage;

        const updated = await prisma.study_session.update({
            where: { id },
            data: updateData
        });

        if (req.file) {
            await storeFile(
                BUCKET_STUDY_SESSION,
                id.toString(),
                req.file.buffer,
                req.file.originalname,
                req.file.mimetype
            );
        }

        return res.json(toDto(updated));
    } catch (error) {
        console.error("Error updating study session:", error);
        return res.status(500).json({ message: "Internal server error" });
    }
});

// 6. DELETE /api/study-sessions/:id (authenticated)
router.delete("/study-sessions/:id", authenticateToken, async (req, res) => {
    try {
        const id = BigInt(req.params.id);
        const existing = await prisma.study_session.findUnique({ where: { id } });
        if (!existing) {
            return res.status(404).json({ message: "Study session not found" });
        }

        await prisma.study_session.delete({ where: { id } });
        if (existing.has_image) {
            await deleteFile(BUCKET_STUDY_SESSION, id.toString());
        }

        return res.status(204).send();
    } catch (error) {
        console.error("Error deleting study session:", error);
        return res.status(500).json({ message: "Internal server error" });
    }
});

export default router;
