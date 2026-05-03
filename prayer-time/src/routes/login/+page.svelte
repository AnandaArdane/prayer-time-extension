<script lang="ts">
	import { fade, fly } from 'svelte/transition';
	import { backOut } from 'svelte/easing';
	import { goto } from '$app/navigation';

	let email = $state('');
	let password = $state('');
	let showPassword = $state(false);
	let isLoading = $state(false);

	let errorMessage = $state('');
	let successMessage = $state('');

	async function handleSubmit(e: SubmitEvent) {
		e.preventDefault();
		isLoading = true;
		errorMessage = '';
		successMessage = '';

		try {
			const response = await fetch('http://localhost:8080/api/auth/login', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ email, password })
			});

			if (response.ok) {
				const data = await response.json();
				console.log('Login success:', data);

				// Store token in cookie
				const expires = new Date();
				expires.setDate(expires.getDate() + 1);
				document.cookie = `jwt=${data.token}; expires=${expires.toUTCString()}; path=/; SameSite=Strict`;

				successMessage = 'Login Berhasil! Mengalihkan...';
				setTimeout(() => {
					goto('/admin/contents');
				}, 1000);
			} else {
				const errorText = await response.text();
				errorMessage = errorText || 'Email atau password salah';
			}
		} catch (e) {
			console.error('Login error:', e);
			errorMessage = 'Gagal terhubung ke server';
		} finally {
			isLoading = false;
		}
	}
</script>

<svelte:head>
	<title>Login</title>
</svelte:head>

<div
	class="min-h-screen bg-gray-50 flex items-center justify-center p-4 selection:bg-theme-primary-500/20 shadow-inner"
>
	<!-- Subtle Background Accents -->
	<div class="fixed inset-0 overflow-hidden pointer-events-none">
		<div
			class="absolute -top-[10%] -left-[10%] w-[50%] h-[50%] bg-theme-primary-100/30 blur-[100px] rounded-full"
		></div>
		<div
			class="absolute -bottom-[10%] -right-[10%] w-[50%] h-[50%] bg-emerald-100/20 blur-[100px] rounded-full"
		></div>
	</div>

	<div in:fly={{ y: 20, duration: 800, easing: backOut }} class="relative w-full max-w-md">
		<!-- Back Button -->
		<a
			href="/"
			class="absolute -top-16 left-0 flex items-center text-gray-400 hover:text-theme-primary-700 transition-colors group"
		>
			<div
				class="w-8 h-8 rounded-full border border-gray-200 bg-white flex items-center justify-center mr-3 group-hover:border-theme-primary-500 shadow-sm transition-all group-hover:-translate-x-1"
			>
				<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
					<path
						stroke-linecap="round"
						stroke-linejoin="round"
						stroke-width="2"
						d="M15 19l-7-7 7-7"
					/>
				</svg>
			</div>
			<span class="text-[10px] font-bold uppercase tracking-[0.2em]">Kembali Ke Utama</span>
		</a>

		<!-- Card -->
		<div
			class="bg-white rounded-3xl p-8 lg:p-12 shadow-[0_20px_50px_rgba(0,0,0,0.05)] border border-gray-100 overflow-hidden"
		>
			<!-- Logo/Header -->
			<div class="text-center mb-12">
				<div
					class="inline-flex items-center justify-center w-20 h-20 rounded-2xl bg-gradient-to-tr from-theme-primary-600 to-emerald-500 shadow-lg shadow-theme-primary-500/20 mb-6"
				>
					<svg class="w-10 h-10 text-white" fill="currentColor" viewBox="0 0 24 24">
						<path
							d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"
						/>
					</svg>
				</div>
				<h1 class="text-xs font-black text-theme-primary-700 uppercase tracking-[0.3em] mb-2">
					Selamat Datang
				</h1>
				<p class="text-gray-400 text-[10px] font-bold uppercase tracking-widest">
					Silahkan masuk ke akun Anda
				</p>
			</div>

			<!-- Form -->
			<form onsubmit={handleSubmit} class="space-y-6">
				{#if errorMessage}
					<div
						class="p-4 bg-red-50 border border-red-100 rounded-xl text-red-600 text-[10px] font-bold uppercase tracking-wider text-center"
					>
						{errorMessage}
					</div>
				{/if}

				{#if successMessage}
					<div
						class="p-4 bg-emerald-50 border border-emerald-100 rounded-xl text-emerald-600 text-[10px] font-bold uppercase tracking-wider text-center"
					>
						{successMessage}
					</div>
				{/if}

				<div class="space-y-2">
					<label
						for="email"
						class="block text-[10px] font-bold text-gray-400 uppercase tracking-widest ml-1"
						>Email</label
					>
					<div class="relative group">
						<div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
							<svg
								class="w-4 h-4 text-gray-400 group-focus-within:text-theme-primary-500 transition-colors"
								fill="none"
								stroke="currentColor"
								viewBox="0 0 24 24"
							>
								<path
									stroke-linecap="round"
									stroke-linejoin="round"
									stroke-width="2"
									d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.206"
								/>
							</svg>
						</div>
						<input
							id="email"
							type="email"
							bind:value={email}
							required
							placeholder="nama@email.com"
							class="w-full pl-11 pr-4 py-3.5 bg-gray-50 border border-gray-100 rounded-xl text-gray-700 placeholder-gray-400 focus:outline-none focus:ring-4 focus:ring-theme-primary-500/5 focus:border-theme-primary-500 transition-all text-sm"
						/>
					</div>
				</div>

				<div class="space-y-2">
					<div class="flex justify-between items-center px-1">
						<label
							for="password"
							class="block text-[10px] font-bold text-gray-400 uppercase tracking-widest"
							>Password</label
						>
					</div>
					<div class="relative group">
						<div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
							<svg
								class="w-4 h-4 text-gray-400 group-focus-within:text-theme-primary-500 transition-colors"
								fill="none"
								stroke="currentColor"
								viewBox="0 0 24 24"
							>
								<path
									stroke-linecap="round"
									stroke-linejoin="round"
									stroke-width="2"
									d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
								/>
							</svg>
						</div>
						<input
							id="password"
							type={showPassword ? 'text' : 'password'}
							bind:value={password}
							required
							placeholder="••••••••"
							class="w-full pl-11 pr-11 py-3.5 bg-gray-50 border border-gray-100 rounded-xl text-gray-700 placeholder-gray-400 focus:outline-none focus:ring-4 focus:ring-theme-primary-500/5 focus:border-theme-primary-500 transition-all text-sm"
						/>
						<button
							type="button"
							onclick={() => (showPassword = !showPassword)}
							class="absolute inset-y-0 right-0 pr-4 flex items-center text-gray-400 hover:text-theme-primary-600 transition-colors"
						>
							<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								{#if showPassword}
									<path
										stroke-linecap="round"
										stroke-linejoin="round"
										stroke-width="2"
										d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18"
									/>
								{:else}
									<path
										stroke-linecap="round"
										stroke-linejoin="round"
										stroke-width="2"
										d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
									/>
									<path
										stroke-linecap="round"
										stroke-linejoin="round"
										stroke-width="2"
										d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
									/>
								{/if}
							</svg>
						</button>
					</div>
				</div>

				<button
					type="submit"
					disabled={isLoading}
					class="relative w-full py-4 bg-theme-primary-700 hover:bg-theme-primary-600 text-white rounded-xl font-black uppercase tracking-[0.2em] text-[10px] shadow-lg shadow-theme-primary-500/20 transition-all active:scale-[0.98] disabled:opacity-70 disabled:cursor-not-allowed overflow-hidden mt-4"
				>
					{#if isLoading}
						<div class="absolute inset-0 flex items-center justify-center">
							<svg
								class="animate-spin h-5 w-5 text-white"
								xmlns="http://www.w3.org/2000/svg"
								fill="none"
								viewBox="0 0 24 24"
							>
								<circle
									class="opacity-25"
									cx="12"
									cy="12"
									r="10"
									stroke="currentColor"
									stroke-width="4"
								></circle>
								<path
									class="opacity-75"
									fill="currentColor"
									d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
								></path>
							</svg>
						</div>
					{:else}
						<span class="relative z-10">Masuk Akun</span>
					{/if}
				</button>
			</form>
		</div>

		<!-- Footer Info -->
		<div class="mt-12 text-center">
			<p class="text-[8px] font-bold text-gray-400 uppercase tracking-[0.4em]">
				&copy; 2026 | ALL RIGHTS RESERVED
			</p>
		</div>
	</div>
</div>

<style>
	:global(body) {
		background-color: #f9fafb;
	}
</style>
