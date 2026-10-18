import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import { initBuckets } from "./services/s3";

// Route imports
import authRouter from "./routes/auth";
import prayersRouter from "./routes/prayers";
import contentsRouter from "./routes/contents";
import studySessionsRouter from "./routes/study-sessions";

dotenv.config();

const app = express();
const PORT = process.env.PORT || 8080;

// CORS setup - configurable via CORS_ORIGINS env var (comma-separated)
const defaultOrigins = ["http://localhost:5173", "http://localhost:4173"];
const allowedOrigins = process.env.CORS_ORIGINS
    ? process.env.CORS_ORIGINS.split(",").map(o => o.trim())
    : defaultOrigins;
app.use(cors({
    origin: (origin, callback) => {
        // Allow requests with no origin (like mobile apps, curl, or server-to-server)
        if (!origin || allowedOrigins.includes(origin)) {
            callback(null, true);
        } else {
            callback(new Error("Not allowed by CORS"));
        }
    },
    credentials: true,
    methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
    allowedHeaders: ["Authorization", "Content-Type", "X-Requested-With"]
}));

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Health Check
app.get("/health", (req, res) => {
    res.json({ status: "UP", timestamp: new Date() });
});

// Map routes
app.use("/api/auth", authRouter);
app.use("/api/prayers", prayersRouter);
app.use("/api", contentsRouter);
app.use("/api", studySessionsRouter);

// Global Error Handler
app.use((err: any, req: express.Request, res: express.Response, next: express.NextFunction) => {
    console.error("Unhandled Error:", err);
    res.status(err.status || 500).json({
        message: err.message || "Internal server error"
    });
});

// Initialize storage and start server
async function startServer() {
    try {
        console.log("Initializing S3 buckets...");
        await initBuckets();
        
        app.listen(PORT, () => {
            console.log(`Backend server is running on http://localhost:${PORT}`);
        });
    } catch (error) {
        console.error("Failed to start backend server:", error);
        process.exit(1);
    }
}

startServer();
