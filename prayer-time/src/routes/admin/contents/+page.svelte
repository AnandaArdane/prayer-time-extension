<script lang="ts">
	import { onMount } from 'svelte';
	import { fade, fly } from 'svelte/transition';
	import { getCookie } from '$lib/utils';

	interface Content {
		id: number;
		title: string;
		active: boolean;
		url: string;
	}

	let contents = $state<Content[]>([]);
	let isLoading = $state(true);
	let isModalOpen = $state(false);
	let isDeleteModalOpen = $state(false);
	let idToDelete = $state<number | null>(null);
	let isEditing = $state(false);
	let isSubmitting = $state(false);
	let searchTerm = $state('');

	// Form state
	let currentItem = $state<Partial<Content>>({
		id: undefined,
		title: '',
		active: true
	});
	let selectedFile = $state<File | null>(null);
	let imagePreview = $state<string | null>(null);

	const filteredContents = $derived(
		contents.filter((item) => item.title.toLowerCase().includes(searchTerm.toLowerCase()))
	);

	async function fetchContents() {
		isLoading = true;
		try {
			const res = await fetch('http://localhost:8080/api/contents');
			if (res.ok) {
				contents = await res.json();
			}
		} catch (e) {
			console.error('Failed to fetch contents', e);
		} finally {
			isLoading = false;
		}
	}

	function openAddModal() {
		isEditing = false;
		currentItem = { title: '', active: true };
		selectedFile = null;
		imagePreview = null;
		isModalOpen = true;
	}

	function openEditModal(item: Content) {
		isEditing = true;
		currentItem = { ...item };
		selectedFile = null;
		imagePreview = `http://localhost:8080${item.url}`;
		isModalOpen = true;
	}

	function handleFileChange(e: Event) {
		const target = e.target as HTMLInputElement;
		if (target.files && target.files.length > 0) {
			selectedFile = target.files[0];
			const reader = new FileReader();
			reader.onloadend = () => {
				imagePreview = reader.result as string;
			};
			reader.readAsDataURL(selectedFile);
		}
	}

	async function handleSave() {
		if (!currentItem.title?.trim()) return;
		isSubmitting = true;

		const formData = new FormData();
		formData.append(
			'contentRequestDto',
			new Blob(
				[
					JSON.stringify({
						title: currentItem.title,
						active: currentItem.active
					})
				],
				{ type: 'application/json' }
			)
		);

		if (selectedFile) {
			formData.append('file', selectedFile);
		} else if (!isEditing) {
			alert('Pilih gambar untuk konten baru');
			isSubmitting = false;
			return;
		}

		const url = isEditing
			? `http://localhost:8080/api/contents/${currentItem.id}`
			: 'http://localhost:8080/api/contents';

		const method = isEditing ? 'PUT' : 'POST';

		try {
			const res = await fetch(url, {
				method,
				headers: {
					Authorization: `Bearer ${getCookie('jwt')}`
				},
				body: formData
			});

			if (res.ok) {
				await fetchContents();
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

	async function toggleStatus(item: Content) {
		const updatedItem = { ...item, active: !item.active };

		const formData = new FormData();
		formData.append(
			'contentRequestDto',
			new Blob(
				[
					JSON.stringify({
						title: updatedItem.title,
						active: updatedItem.active
					})
				],
				{ type: 'application/json' }
			)
		);

		try {
			const res = await fetch(`http://localhost:8080/api/contents/${item.id}`, {
				method: 'PUT',
				headers: {
					Authorization: `Bearer ${getCookie('jwt')}`
				},
				body: formData
			});

			if (res.ok) {
				contents = contents.map((c) => (c.id === item.id ? updatedItem : c));
			} else {
				alert('Gagal mengubah status.');
			}
		} catch (e) {
			console.error('Toggle status error', e);
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
			const res = await fetch(`http://localhost:8080/api/contents/${idToDelete}`, {
				method: 'DELETE',
				headers: {
					Authorization: `Bearer ${getCookie('jwt')}`
				}
			});
			if (res.ok) {
				contents = contents.filter((c) => c.id !== idToDelete);
				closeDeleteModal();
			} else {
				const err = await res.text();
				alert('Gagal menghapus konten: ' + err);
			}
		} catch (e) {
			console.error('Delete error', e);
			alert('Terjadi kesalahan saat menghapus data.');
		}
	}

	onMount(fetchContents);

	const icons = {
		Search: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#9ca3af" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>`,
		Plus: `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="M12 5v14"/></svg>`,
		Image: `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="18" x="3" y="3" rx="2" ry="2"/><circle cx="9" cy="9" r="2"/><path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"/></svg>`,
		XCircle: `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="m15 9-6 6"/><path d="m9 9 6 6"/></svg>`,
		Edit2: `<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>`,
		Trash2: `<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/><line x1="10" x2="10" y1="11" y2="17"/><line x1="14" x2="14" y1="11" y2="17"/></svg>`,
		CheckCircle: `<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="m9 12 2 2 4-4"/></svg>`,
		Upload: `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" x2="12" y1="3" y2="15"/></svg>`,
		UploadGray: `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="mb-2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" x2="12" y1="3" y2="15"/></svg>`,
		EmptyStateImage: `<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#e5e7eb" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="mx-auto mb-3"><rect width="18" height="18" x="3" y="3" rx="2" ry="2"/><circle cx="9" cy="9" r="2"/><path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"/></svg>`,
		Trash2Large: `<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/><line x1="10" x2="10" y1="11" y2="17"/><line x1="14" x2="14" y1="11" y2="17"/></svg>`
	};
</script>

<svelte:head>
	<title>Prayertime CMS - Manajemen Konten</title>
</svelte:head>

<!-- Search & Add Action -->
<section class="flex flex-col md:flex-row gap-3 mb-8">
	<div
		class="flex-1 bg-white p-0.5 rounded-2xl shadow-sm border border-gray-100 flex items-center px-4 focus-within:ring-2 ring-[#D1C7BD]/30 transition-all"
	>
		{@html icons.Search}
		<input
			type="text"
			placeholder="Cari judul konten..."
			class="w-full p-3 outline-none bg-transparent text-sm font-medium"
			bind:value={searchTerm}
		/>
	</div>
	<button
		onclick={openAddModal}
		class="bg-[#D1C7BD] hover:bg-[#C4B9AF] text-[#4A433D] px-6 py-3 rounded-2xl font-black text-[10px] uppercase tracking-wider transition-all shadow-sm active:scale-95 flex items-center justify-center gap-2"
	>
		{@html icons.Plus}
		TAMBAH KONTEN
	</button>
</section>

<!-- Content List -->
{#if isLoading}
	<div class="flex flex-col items-center justify-center py-20">
		<div
			class="w-10 h-10 border-4 border-[#D1C7BD]/20 border-t-[#D1C7BD] rounded-full animate-spin"
		></div>
		<p class="mt-4 text-[9px] font-black text-gray-400 uppercase tracking-widest">Memuat Data...</p>
	</div>
{:else if filteredContents.length === 0}
	<div class="text-center py-16 bg-white/50 rounded-[2rem] border-2 border-dashed border-gray-200">
		{@html icons.EmptyStateImage}
		<p class="text-gray-400 font-black uppercase tracking-[0.2em] text-[10px]">
			Belum ada konten tersedia
		</p>
	</div>
{:else}
	<div class="space-y-3">
		{#each filteredContents as item (item.id)}
			<div
				class="bg-white rounded-[1.5rem] p-3 md:p-4 shadow-sm border border-gray-50 flex items-center gap-4 hover:shadow-md transition-all group"
				in:fade
			>
				<!-- Thumbnail Image -->
				<div
					class="w-16 h-16 md:w-20 md:h-20 bg-[#F8F9FA] rounded-xl flex flex-col items-center justify-center text-gray-300 border border-dashed border-gray-200 relative overflow-hidden shrink-0"
				>
					{#if item.url}
						<img
							src={`http://localhost:8080${item.url}`}
							alt={item.title}
							class="w-full h-full object-cover"
						/>
					{:else}
						{@html icons.Image}
						<span class="text-[7px] font-black mt-1 uppercase tracking-tighter text-gray-400"
							>IMAGE</span
						>
					{/if}
					{#if !item.active}
						<div
							class="absolute inset-0 bg-white/70 backdrop-blur-[1px] flex items-center justify-center"
						>
							{@html icons.XCircle}
						</div>
					{/if}
				</div>
				<div class="flex-1 min-w-0">
					<div class="flex items-center gap-2 mb-0.5">
						<span
							class={`text-[8px] font-black px-2 py-0.5 rounded-full uppercase tracking-widest ${item.active ? 'bg-green-100 text-green-600' : 'bg-red-50 text-red-400'}`}
						>
							{item.active ? 'AKTIF' : 'NON-AKTIF'}
						</span>
						<span class="text-[9px] text-gray-300 font-bold tracking-widest uppercase"
							>ID: {item.id}</span
						>
					</div>
					<h3 class="font-bold text-[#2D2D2D] text-lg leading-tight truncate">
						{item.title}
					</h3>
				</div>
				<div class="flex flex-col md:flex-row gap-1 md:gap-2 items-center pr-2">
					<button
						onclick={() => openEditModal(item)}
						class="p-2 rounded-lg text-gray-300 hover:bg-[#D1C7BD]/20 hover:text-[#8E8378]"
					>
						{@html icons.Edit2}
					</button>
					<button
						onclick={() => toggleStatus(item)}
						class={`p-2 rounded-lg text-gray-300 ${item.active ? 'hover:bg-red-50 hover:text-red-500' : 'hover:bg-green-50 hover:text-green-500'}`}
					>
						{@html item.active ? icons.XCircle : icons.CheckCircle}
					</button>
					<button
						onclick={() => askToDelete(item.id)}
						class="p-2 rounded-lg text-gray-300 hover:bg-red-50 hover:text-red-400"
					>
						{@html icons.Trash2}
					</button>
				</div>
			</div>
		{/each}
	</div>
{/if}

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
						{isEditing ? 'Edit Konten' : 'Buat Konten'}
					</h2>
					<p class="text-[9px] text-gray-400 font-bold uppercase tracking-widest">
						Informasi Masjid Digital
					</p>
				</div>
				<button
					onclick={() => (isModalOpen = false)}
					class="bg-gray-50 p-1.5 rounded-full text-gray-400 hover:text-black"
				>
					{@html icons.XCircle}
				</button>
			</div>

			<div class="space-y-6">
				<!-- Image Input -->
				<div>
					<label
						for="fileInput"
						class="block text-[10px] font-black text-gray-400 uppercase tracking-widest mb-2"
						>Banner / Gambar Konten</label
					>
					<div
						role="button"
						tabindex="0"
						onclick={() => document.getElementById('fileInput')?.click()}
						onkeydown={(e) => e.key === 'Enter' && document.getElementById('fileInput')?.click()}
						class="relative group cursor-pointer"
					>
						{#if imagePreview}
							<div
								class="w-full h-32 rounded-2xl overflow-hidden border-2 border-[#F8F9FA] relative"
							>
								<img src={imagePreview} class="w-full h-full object-cover" alt="Preview" />
								<div
									class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 flex items-center justify-center transition-all"
								>
									{@html icons.Upload}
								</div>
							</div>
						{:else}
							<div
								class="w-full h-32 bg-[#F8F9FA] rounded-2xl border-2 border-dashed border-gray-200 flex flex-col items-center justify-center text-gray-400 hover:bg-[#F1F3F4]"
							>
								{@html icons.UploadGray}
								<span class="text-[9px] font-black uppercase tracking-widest text-center px-4"
									>Klik untuk unggah</span
								>
							</div>
						{/if}
					</div>
					<input type="file" id="fileInput" hidden accept="image/*" onchange={handleFileChange} />
				</div>

				<!-- Judul Input -->
				<div>
					<label
						for="titleInput"
						class="block text-[10px] font-black text-gray-400 uppercase tracking-widest mb-2"
						>Judul Konten</label
					>
					<input
						type="text"
						id="titleInput"
						class="w-full bg-[#F8F9FA] p-4 rounded-xl outline-none border-2 border-transparent focus:border-[#D1C7BD] transition-all font-bold text-base"
						placeholder="Judul Kajian..."
						bind:value={currentItem.title}
					/>
				</div>

				<!-- Status Toggle -->
				<div class="flex items-center justify-between bg-[#F8F9FA] p-5 rounded-2xl">
					<div>
						<p class="text-xs font-black text-[#2D2D2D] uppercase tracking-tight">Status Aktif</p>
						<p class="text-[9px] text-gray-400 uppercase font-bold tracking-tighter">
							Tampil di layar utama
						</p>
					</div>
					<button
						onclick={() => (currentItem.active = !currentItem.active)}
						class={`w-12 h-6 rounded-full transition-all relative ${currentItem.active ? 'bg-[#D1C7BD]' : 'bg-gray-300'}`}
					>
						<div
							class={`absolute top-1 w-4 h-4 bg-white rounded-full shadow-sm transition-all ${currentItem.active ? 'left-7' : 'left-1'}`}
						></div>
					</button>
				</div>

				<!-- Buttons -->
				<div class="flex gap-3 pt-1">
					<button
						onclick={() => (isModalOpen = false)}
						class="flex-1 p-4 rounded-xl font-black text-gray-400 hover:bg-gray-100 uppercase text-[9px] tracking-widest"
						>Batal</button
					>
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
				Hapus Konten?
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
