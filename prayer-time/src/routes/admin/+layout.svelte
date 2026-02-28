<script lang="ts">
	import { page } from '$app/state';
	import { fade } from 'svelte/transition';

	let { children } = $props();

	function handleLogout() {
		document.cookie = 'jwt=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
		window.location.href = '/login';
	}

	const icons = {
		MapPin: `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>`,
		SettingsIcon: `<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#D1C7BD" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/><circle cx="12" cy="12" r="3"/></svg>`,
		LogOut: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" x2="9" y1="12" y2="12"/></svg>`
	};
</script>

<div class="min-h-screen bg-[#F1F3F4] text-[#333] p-4 md:p-6 pb-20">
	<!-- Header Bar -->
	<header
		class="max-w-5xl mx-auto mb-8 flex flex-col md:flex-row md:items-center justify-between gap-4"
	>
		<div class="flex items-center gap-3">
			<div class="bg-[#4D4D4D] p-2.5 rounded-xl shadow-md">
				{@html icons.MapPin}
			</div>
			<h1 class="text-lg font-black tracking-tight text-[#2D2D2D] italic uppercase">
				PRAYERTIME CMS
			</h1>
		</div>
		<div class="flex items-center gap-3 mt-1">
			<button
				onclick={handleLogout}
				class="flex items-center gap-1.5 px-3 py-1.5 bg-red-50 text-red-500 rounded-lg text-[9px] font-black uppercase tracking-wider hover:bg-red-100 transition-colors border border-red-100 cursor-pointer"
			>
				{@html icons.LogOut}
				KELUAR
			</button>
		</div>
	</header>

	<!-- Navigation Tabs -->
	<nav class="max-w-5xl mx-auto mb-6 flex gap-2">
		<a
			href="/admin/contents"
			class="px-5 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all {page
				.url.pathname === '/admin/contents'
				? 'bg-[#D1C7BD] text-[#4A433D]'
				: 'bg-white text-gray-400 hover:text-gray-600'} shadow-sm"
		>
			Manajemen Konten
		</a>
		<a
			href="/admin/study-sessions"
			class="px-5 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all {page
				.url.pathname === '/admin/study-sessions'
				? 'bg-[#D1C7BD] text-[#4A433D]'
				: 'bg-white text-gray-400 hover:text-gray-600'} shadow-sm"
		>
			Jadwal Kajian
		</a>
	</nav>

	<main class="max-w-5xl mx-auto">
		{@render children()}
	</main>

	<!-- Footer -->
	<footer class="max-w-5xl mx-auto mt-16 pb-8 text-center">
		<div
			class="inline-flex items-center gap-2 bg-white px-5 py-2.5 rounded-full shadow-sm border border-gray-100"
		>
			{@html icons.SettingsIcon}
			<span class="text-[8px] font-black text-gray-300 uppercase tracking-[0.2em]"
				>Prayertime CMS</span
			>
		</div>
	</footer>
</div>

<style>
	:global(body) {
		background-color: #f1f3f4;
	}
</style>
