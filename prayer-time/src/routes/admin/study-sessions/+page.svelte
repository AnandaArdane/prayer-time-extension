<script lang="ts">
	import { onMount } from 'svelte';
	import { fade, fly } from 'svelte/transition';

	interface StudySession {
		id: number;
		title: string;
		place: string;
		date: string;
		time: string;
	}

	let studySessions = $state<StudySession[]>([]);
	let isLoading = $state(true);
	let isModalOpen = $state(false);
	let isDeleteModalOpen = $state(false);
	let idToDelete = $state<number | null>(null);
	let isEditing = $state(false);
	let isSubmitting = $state(false);
	let searchTerm = $state('');

	function getCookie(name: string) {
		const value = `; ${document.cookie}`;
		const parts = value.split(`; ${name}=`);
		if (parts.length === 2) return parts.pop()?.split(';').shift();
	}

	function handleLogout() {
		document.cookie = 'jwt=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
		window.location.href = '/login';
	}

	// Form state
	let currentItem = $state<Partial<StudySession>>({
		id: undefined,
		title: '',
		place: '',
		date: '',
		time: ''
	});

	const filteredSessions = $derived(
		studySessions.filter((item) => item.title.toLowerCase().includes(searchTerm.toLowerCase()))
	);

	async function fetchSessions() {
		isLoading = true;
		try {
			const res = await fetch('http://localhost:8080/api/study-sessions');
			if (res.ok) {
				studySessions = await res.json();
			}
		} catch (e) {
			console.error('Failed to fetch study sessions', e);
		} finally {
			isLoading = false;
		}
	}

	function openAddModal() {
		isEditing = false;
		currentItem = { title: '', place: '', date: '', time: '' };
		isModalOpen = true;
	}

	function openEditModal(item: StudySession) {
		isEditing = true;
		currentItem = { ...item };
		isModalOpen = true;
	}

	async function handleSave() {
		if (!currentItem.title?.trim()) return;
		isSubmitting = true;

		const url = isEditing
			? `http://localhost:8080/api/study-sessions/${currentItem.id}`
			: 'http://localhost:8080/api/study-sessions';

		const method = isEditing ? 'PUT' : 'POST';

		try {
			const res = await fetch(url, {
				method,
				headers: {
					'Content-Type': 'application/json',
					Authorization: `Bearer ${getCookie('jwt')}`
				},
				body: JSON.stringify({
					title: currentItem.title,
					place: currentItem.place,
					date: currentItem.date,
					time: currentItem.time
						? currentItem.time.length === 5
							? currentItem.time + ':00'
							: currentItem.time
						: ''
				})
			});

			if (res.ok) {
				await fetchSessions();
				isModalOpen = false;
			} else {
				const err = await res.text();
				alert('Gagal menyimpan: ' + err);
			}
		} catch (e) {
			console.error('Submit error', e);
			alert('Terjadi kesalahan saat menyimpan data.');
		} finally {
			isSubmitting = false;
		}
	}

	function askToDelete(id: number) {
		idToDelete = id;
		isDeleteModalOpen = true;
	}

	function closeDeleteModal() {
		idToDelete = null;
		isDeleteModalOpen = false;
	}

	async function confirmDelete() {
		if (idToDelete === null) return;

		try {
			const res = await fetch(`http://localhost:8080/api/study-sessions/${idToDelete}`, {
				method: 'DELETE',
				headers: {
					Authorization: `Bearer ${getCookie('jwt')}`
				}
			});
			if (res.ok) {
				studySessions = studySessions.filter((c) => c.id !== idToDelete);
				closeDeleteModal();
			} else {
				const err = await res.text();
				alert('Gagal menghapus jadwal: ' + err);
			}
		} catch (e) {
			console.error('Delete error', e);
			alert('Terjadi kesalahan saat menghapus data.');
		}
	}

	onMount(fetchSessions);

	const icons = {
		MapPin: `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>`,
		Search: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#9ca3af" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>`,
		Plus: `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="M12 5v14"/></svg>`,
		XCircle: `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="m15 9-6 6"/><path d="m9 9 6 6"/></svg>`,
		Edit2: `<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>`,
		Trash2: `<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/><line x1="10" x2="10" y1="11" y2="17"/><line x1="14" x2="14" y1="11" y2="17"/></svg>`,
		SettingsIcon: `<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#D1C7BD" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/><circle cx="12" cy="12" r="3"/></svg>`,
		EmptyStateImage: `<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#e5e7eb" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="mx-auto mb-3"><rect width="18" height="18" x="3" y="3" rx="2" ry="2"/><circle cx="9" cy="9" r="2"/><path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"/></svg>`,
		Trash2Large: `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/><line x1="10" x2="10" y1="11" y2="17"/><line x1="14" x2="14" y1="11" y2="17"/></svg>`,
		LogOut: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" x2="9" y1="12" y2="12"/></svg>`,
		Calendar: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="18" x="3" y="4" rx="2" ry="2"/><line x1="16" x2="16" y1="2" y2="6"/><line x1="8" x2="8" y1="2" y2="6"/><line x1="3" x2="21" y1="10" y2="10"/></svg>`,
		Clock: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>`,
		MapPinSmall: `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>`
	};

	function formatDate(dateStr: string) {
		const options: Intl.DateTimeFormatOptions = {
			weekday: 'long',
			day: 'numeric',
			month: 'long',
			year: 'numeric'
		};
		return new Date(dateStr).toLocaleDateString('id-ID', options);
	}
</script>

<svelte:head>
	<title>Prayertime CMS - Manajemen Jadwal Kajian</title>
</svelte:head>

<div class="min-h-screen bg-[#F1F3F4] text-[#333] p-4 md:p-6 pb-20">
	<!-- Header Bar -->
	<header
		class="max-w-5xl mx-auto mb-8 flex flex-col md:flex-row md:items-center justify-between gap-4"
	>
		<div class="flex items-center gap-3">
			<div class="bg-[#4D4D4D] p-2.5 rounded-xl shadow-md">
				{@html icons.MapPin}
			</div>
			<div>
				<div class="flex items-center gap-1.5">
					<span class="w-1.5 h-1.5 bg-green-500 rounded-full animate-pulse"></span>
					<p class="text-[9px] text-gray-500 font-bold uppercase tracking-widest">
						LOKASI SAAT INI
					</p>
				</div>
				<h1 class="text-lg font-black tracking-tight text-[#2D2D2D] italic uppercase">
					TANGERANG SELATAN, BANTEN
				</h1>
			</div>
		</div>
		<div class="text-right flex flex-col items-end">
			<p class="text-[10px] font-black text-gray-400 uppercase tracking-widest">PENGELOLAAN DATA</p>
			<div class="flex items-center gap-3 mt-1">
				<p class="text-xs font-bold text-[#666]">JADWAL KAJIAN RUTIN</p>
				<button
					onclick={handleLogout}
					class="flex items-center gap-1.5 px-3 py-1.5 bg-red-50 text-red-500 rounded-lg text-[9px] font-black uppercase tracking-wider hover:bg-red-100 transition-colors border border-red-100"
				>
					{@html icons.LogOut}
					KELUAR
				</button>
			</div>
		</div>
	</header>

	<!-- Navigation Tabs -->
	<section class="max-w-5xl mx-auto mb-6 flex gap-2">
		<a
			href="/admin/contents"
			class="px-5 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all bg-white text-gray-400 hover:text-gray-600 shadow-sm"
		>
			Manajemen Konten
		</a>
		<a
			href="/admin/study-sessions"
			class="px-5 py-2.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all bg-[#D1C7BD] text-[#4A433D] shadow-sm"
		>
			Jadwal Kajian
		</a>
	</section>

	<main class="max-w-5xl mx-auto">
		<!-- Search & Add Action -->
		<section class="flex flex-col md:flex-row gap-3 mb-8">
			<div
				class="flex-1 bg-white p-0.5 rounded-2xl shadow-sm border border-gray-100 flex items-center px-4 focus-within:ring-2 ring-[#D1C7BD]/30 transition-all"
			>
				{@html icons.Search}
				<input
					type="text"
					placeholder="Cari judul kajian..."
					class="w-full p-3 outline-none bg-transparent text-sm font-medium"
					bind:value={searchTerm}
				/>
			</div>
			<button
				onclick={openAddModal}
				class="bg-[#D1C7BD] hover:bg-[#C4B9AF] text-[#4A433D] px-6 py-3 rounded-2xl font-black text-[10px] uppercase tracking-wider transition-all shadow-sm active:scale-95 flex items-center justify-center gap-2"
			>
				{@html icons.Plus}
				TAMBAH JADWAL
			</button>
		</section>

		<!-- List -->
		{#if isLoading}
			<div class="flex flex-col items-center justify-center py-20">
				<div
					class="w-10 h-10 border-4 border-[#D1C7BD]/20 border-t-[#D1C7BD] rounded-full animate-spin"
				></div>
				<p class="mt-4 text-[9px] font-black text-gray-400 uppercase tracking-widest">
					Memuat Data...
				</p>
			</div>
		{:else if filteredSessions.length === 0}
			<div
				class="text-center py-16 bg-white/50 rounded-[2rem] border-2 border-dashed border-gray-200"
			>
				{@html icons.EmptyStateImage}
				<p class="text-gray-400 font-black uppercase tracking-[0.2em] text-[10px]">
					Belum ada jadwal kajian tersedia
				</p>
			</div>
		{:else}
			<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
				{#each filteredSessions as item (item.id)}
					<div
						class="bg-white rounded-[1.5rem] p-5 shadow-sm border border-gray-50 flex flex-col gap-4 hover:shadow-md transition-all group relative"
						in:fade
					>
						<div class="flex justify-between items-start">
							<div class="flex-1 pr-10">
								<div class="flex items-center gap-2 mb-1">
									<span class="text-[9px] text-gray-300 font-bold tracking-widest uppercase"
										>ID: {item.id}</span
									>
								</div>
								<h3 class="font-bold text-[#2D2D2D] text-lg leading-tight mb-2">
									{item.title}
								</h3>
							</div>
							<div class="flex gap-1 absolute top-4 right-4">
								<button
									onclick={() => openEditModal(item)}
									class="p-2 rounded-lg text-gray-300 hover:bg-[#D1C7BD]/20 hover:text-[#8E8378]"
								>
									{@html icons.Edit2}
								</button>
								<button
									onclick={() => askToDelete(item.id)}
									class="p-2 rounded-lg text-gray-300 hover:bg-red-50 hover:text-red-400"
								>
									{@html icons.Trash2}
								</button>
							</div>
						</div>

						<div class="space-y-2 pt-2 border-t border-gray-50">
							<div class="flex items-center gap-2 text-gray-500">
								{@html icons.Calendar}
								<span class="text-xs font-medium">{formatDate(item.date)}</span>
							</div>
							<div class="flex items-center gap-2 text-gray-500">
								{@html icons.Clock}
								<span class="text-xs font-medium">{item.time.substring(0, 5)} WIB</span>
							</div>
							<div class="flex items-center gap-2 text-gray-500">
								{@html icons.MapPinSmall}
								<span class="text-xs font-medium">{item.place || 'Masjid Al-Barkah'}</span>
							</div>
						</div>
					</div>
				{/each}
			</div>
		{/if}
	</main>

	<!-- Modal -->
	{#if isModalOpen}
		<div
			class="fixed inset-0 bg-[#2D2D2D]/60 backdrop-blur-sm z-50 flex items-center justify-center p-4 transition-all"
			transition:fade
		>
			<div
				class="bg-white w-full max-w-md rounded-[2rem] p-8 shadow-2xl overflow-hidden"
				in:fly={{ y: 20 }}
			>
				<div class="flex justify-between items-center mb-6">
					<div>
						<h2 class="text-xl font-black text-[#2D2D2D] uppercase italic tracking-tighter">
							{isEditing ? 'Edit Jadwal' : 'Tambah Jadwal'}
						</h2>
						<p class="text-[9px] text-gray-400 font-bold uppercase tracking-widest">
							Informasi Kajian Masjid
						</p>
					</div>
					<button
						onclick={() => (isModalOpen = false)}
						class="bg-gray-50 p-1.5 rounded-full text-gray-400 hover:text-black"
					>
						{@html icons.XCircle}
					</button>
				</div>

				<div class="space-y-4">
					<!-- Judul Input -->
					<div>
						<label
							for="titleInput"
							class="block text-[10px] font-black text-gray-400 uppercase tracking-widest mb-1.5"
							>Judul Kajian</label
						>
						<input
							type="text"
							id="titleInput"
							class="w-full bg-[#F8F9FA] p-3.5 rounded-xl outline-none border-2 border-transparent focus:border-[#D1C7BD] transition-all font-bold text-sm"
							placeholder="Contoh: Kitab Bulughul Maram..."
							bind:value={currentItem.title}
						/>
					</div>

					<!-- Tempat Input -->
					<div>
						<label
							for="placeInput"
							class="block text-[10px] font-black text-gray-400 uppercase tracking-widest mb-1.5"
							>Tempat / Lokasi</label
						>
						<input
							type="text"
							id="placeInput"
							class="w-full bg-[#F8F9FA] p-3.5 rounded-xl outline-none border-2 border-transparent focus:border-[#D1C7BD] transition-all font-bold text-sm"
							placeholder="Contoh: Ruang Utama Masjid..."
							bind:value={currentItem.place}
						/>
					</div>

					<div class="grid grid-cols-2 gap-4">
						<!-- Tanggal Input -->
						<div>
							<label
								for="dateInput"
								class="block text-[10px] font-black text-gray-400 uppercase tracking-widest mb-1.5"
								>Tanggal</label
							>
							<input
								type="date"
								id="dateInput"
								class="w-full bg-[#F8F9FA] p-3.5 rounded-xl outline-none border-2 border-transparent focus:border-[#D1C7BD] transition-all font-bold text-sm"
								bind:value={currentItem.date}
							/>
						</div>

						<!-- Waktu Input -->
						<div>
							<label
								for="timeInput"
								class="block text-[10px] font-black text-gray-400 uppercase tracking-widest mb-1.5"
								>Waktu (WIB)</label
							>
							<input
								type="time"
								id="timeInput"
								class="w-full bg-[#F8F9FA] p-3.5 rounded-xl outline-none border-2 border-transparent focus:border-[#D1C7BD] transition-all font-bold text-sm"
								bind:value={currentItem.time}
							/>
						</div>
					</div>

					<!-- Buttons -->
					<div class="flex gap-3 pt-4">
						<button
							onclick={() => (isModalOpen = false)}
							class="flex-1 p-4 rounded-xl font-black text-gray-400 hover:bg-gray-100 uppercase text-[9px] tracking-widest"
						>
							Batal
						</button>
						<button
							onclick={handleSave}
							disabled={isSubmitting}
							class="flex-1 p-4 rounded-xl font-black bg-[#D1C7BD] text-[#4A433D] hover:bg-[#C4B9AF] uppercase text-[9px] tracking-widest shadow-lg shadow-[#D1C7BD]/10 disabled:opacity-50"
						>
							{isSubmitting ? 'Proses...' : isEditing ? 'Update' : 'Simpan'}
						</button>
					</div>
				</div>
			</div>
		</div>
	{/if}

	<!-- Delete Confirmation Modal -->
	{#if isDeleteModalOpen}
		<div
			class="fixed inset-0 bg-[#2D2D2D]/60 backdrop-blur-sm z-[60] flex items-center justify-center p-4 transition-all"
			transition:fade
		>
			<div
				class="bg-white w-full max-w-xs rounded-[2rem] p-8 shadow-2xl text-center"
				in:fly={{ y: 20 }}
			>
				<div
					class="w-16 h-16 bg-red-50 text-red-500 rounded-full flex items-center justify-center mx-auto mb-4"
				>
					{@html icons.Trash2Large}
				</div>
				<h3
					class="text-lg font-black text-[#2D2D2D] uppercase italic tracking-tighter leading-none mb-1"
				>
					Hapus Jadwal?
				</h3>
				<p class="text-[10px] text-gray-400 font-bold uppercase tracking-widest mb-6">
					Tindakan ini tidak dapat dibatalkan
				</p>

				<div class="flex flex-col gap-2">
					<button
						onclick={confirmDelete}
						class="w-full p-4 rounded-xl font-black bg-red-500 text-white hover:bg-red-600 uppercase text-[9px] tracking-widest shadow-lg shadow-red-500/20 transition-all active:scale-95"
					>
						Ya, Hapus Sekarang
					</button>
					<button
						onclick={closeDeleteModal}
						class="w-full p-4 rounded-xl font-black text-gray-400 hover:bg-gray-100 uppercase text-[9px] tracking-widest transition-all"
					>
						Kembali
					</button>
				</div>
			</div>
		</div>
	{/if}

	<!-- Footer -->
	<footer class="max-w-5xl mx-auto mt-16 pb-8 text-center">
		<div
			class="inline-flex items-center gap-2 bg-white px-5 py-2.5 rounded-full shadow-sm border border-gray-100"
		>
			{@html icons.SettingsIcon}
			<span class="text-[8px] font-black text-gray-300 uppercase tracking-[0.2em]">
				Prayertime CMS • Study Session v1.0.0
			</span>
		</div>
	</footer>
</div>

<style>
	:global(body) {
		background-color: #f1f3f4;
	}
</style>
