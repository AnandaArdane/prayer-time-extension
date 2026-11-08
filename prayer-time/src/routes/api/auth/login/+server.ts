import { prisma } from '$lib/server/prisma';
import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { env } from '$env/dynamic/private';

export const POST: RequestHandler = async ({ request }) => {
	try {
		let body: { email?: string; password?: string };
		try {
			body = await request.json();
		} catch {
			return json({ message: 'Email and password are required' }, { status: 400 });
		}

		const { email, password } = body;

		if (!email || !password) {
			return json({ message: 'Email and password are required' }, { status: 400 });
		}

		const user = await prisma.admin_user.findUnique({
			where: { email }
		});

		if (!user) {
			return json({ message: 'Invalid email or password' }, { status: 401 });
		}

		const isMatch = await bcrypt.compare(password, user.password);
		if (!isMatch) {
			return json({ message: 'Invalid email or password' }, { status: 401 });
		}

		const secret = env.JWT_SECRET || 'myjwtsecret12345';
		const token = jwt.sign({ email: user.email }, secret, { expiresIn: '24h' });

		return json({
			message: 'Login successful',
			email: user.email,
			token
		});
	} catch (err) {
		console.error('Login error:', err);
		return json({ message: 'Internal server error' }, { status: 500 });
	}
};
