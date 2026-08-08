import { Request, Response, NextFunction } from "express";
import jwt from "jsonwebtoken";

const JWT_SECRET = process.env.JWT_SECRET || "myjwtsecret12345";

export interface AuthenticatedRequest extends Request {
    user?: {
        email: string;
    };
}

export function authenticateToken(req: AuthenticatedRequest, res: Response, next: NextFunction) {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
        return res.status(401).json({ message: "Unauthorized: Access token required" });
    }

    jwt.verify(token, JWT_SECRET, (err: any, decoded: any) => {
        if (err) {
            return res.status(403).json({ message: "Forbidden: Invalid or expired token" });
        }
        req.user = { email: decoded.email };
        next();
    });
}
