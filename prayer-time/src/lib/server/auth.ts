import { error } from '@sveltejs/kit';
import jwt from 'jsonwebtoken';
import { env } from '$env/dynamic/private';

interface JwtPayload {
	email: string;
	[key: string]: unknown;
}

export async function authenticateRequest(request: Request): Promise<{ email: string }> {
	const authHeader = request.headers.get('authorization');

	if (!authHeader) {
		throw error(401, 'Unauthorized: Access token required');
	}

	const parts = authHeader.split(' ');
	const token = parts.length === 2 && parts[0].toLowerCase() === 'bearer' ? parts[1] : null;

	if (!token) {
		throw error(401, 'Unauthorized: Access token required');
	}

	const secret = env.JWT_SECRET || 'myjwtsecret12345';

	try {
		const decoded = jwt.verify(token, secret) as JwtPayload;
		if (!decoded || !decoded.email) {
			throw error(403, 'Forbidden: Invalid token payload');
		}
		return { email: decoded.email };
	} catch (err: any) {
		if (err?.status) {
			throw err;
		}
		throw error(403, 'Forbidden: Invalid or expired token');
	}
}
