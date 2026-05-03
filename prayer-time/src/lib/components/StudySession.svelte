<script lang="ts">
	import type { SidebarEvent } from '$lib/server/services/prayerService';

	interface Props {
		events: SidebarEvent[];
	}

	let { events }: Props = $props();

	let scrollContainer: HTMLElement;
	let selectedEvent = $state<SidebarEvent | null>(null);

	function openModal(event: SidebarEvent) {
		selectedEvent = event;
		document.body.style.overflow = 'hidden';
	}

	function closeModal() {
		selectedEvent = null;
		document.body.style.overflow = '';
	}

	function handleImgError(e: Event) {
		const target = e.target as HTMLImageElement;
		target.onerror = null;
		target.src = 'https://placehold.co/200x140/e2dbd1/797166?text=Image';
	}

	function handleModalImgError(e: Event) {
		const target = e.target as HTMLImageElement;
		target.onerror = null;
		target.src = 'https://placehold.co/400x400/e2dbd1/797166?text=Kajian';
	}

	function scrollLeft() {
		if (scrollContainer) scrollContainer.scrollBy({ left: -300, behavior: 'smooth' });
	}

	function scrollRight() {
		if (scrollContainer) scrollContainer.scrollBy({ left: 300, behavior: 'smooth' });
	}
</script>

<div class="space-y-3">
	<div class="flex justify-between items-center px-6">
		<div class="flex items-center space-x-3">
			<h3 class="font-black text-[#4A4643] uppercase tracking-widest text-[11px]">
				Jadwal Kajian Terdekat
			</h3>
			<span class="relative flex h-2 w-2 ml-2">
				<span
					class="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"
				></span>
				<span class="relative inline-flex rounded-full h-2 w-2 bg-red-500"></span>
			</span>
		</div>
		<div class="flex space-x-2">
			<button
				onclick={scrollLeft}
				class="p-2 bg-white rounded-full border border-gray-100 shadow-sm hover:bg-[#4A4643] hover:text-white transition-all"
			>
				<svg
					xmlns="http://www.w3.org/2000/svg"
					class="w-4 h-4"
					viewBox="0 0 24 24"
					fill="none"
					stroke="currentColor"
					stroke-width="3"
					stroke-linecap="round"
					stroke-linejoin="round"><polyline points="15 18 9 12 15 6"></polyline></svg
				>
			</button>
			<button
				onclick={scrollRight}
				class="p-2 bg-white rounded-full border border-gray-100 shadow-sm hover:bg-[#4A4643] hover:text-white transition-all"
			>
				<svg
					xmlns="http://www.w3.org/2000/svg"
					class="w-4 h-4"
					viewBox="0 0 24 24"
					fill="none"
					stroke="currentColor"
					stroke-width="3"
					stroke-linecap="round"
					stroke-linejoin="round"><polyline points="9 18 15 12 9 6"></polyline></svg
				>
			</button>
		</div>
	</div>

	<div
		bind:this={scrollContainer}
		class="flex overflow-x-auto pb-4 gap-4 hide-scrollbar snap-x snap-mandatory px-4 items-start"
	>
		{#each events as event}
			<div
				class="flex-none w-[280px] snap-start bg-white rounded-[2rem] card-shadow border border-gray-100/50 hover:-translate-y-1 transition-all duration-300 group cursor-pointer flex items-stretch overflow-hidden"
				onclick={() => openModal(event)}
			>
				<div class="flex-1 min-w-0 p-5 pr-4 flex flex-col justify-center">
					<div class="flex items-center justify-between mb-2">
						<div class="text-[9px] font-bold text-red-400 uppercase tracking-widest">
							{event.dayTime}
						</div>
					</div>
					<h4
						class="font-black text-[#4A4643] text-sm line-clamp-2 group-hover:text-[#6B655E]"
						title={event.title}
					>
						{event.title}
					</h4>
				</div>
				{#if event.imageUrl}
					<div class="shrink-0 w-24 bg-gray-100 relative">
						<img
							src={event.imageUrl}
							alt={event.title}
							class="absolute inset-0 w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
							onerror={handleImgError}
						/>
					</div>
				{/if}
			</div>
		{/each}
		{#if events.length === 0}
			<div class="flex-none w-full text-center py-6 text-gray-400 text-sm font-medium">
				Belum ada jadwal kajian.
			</div>
		{/if}
	</div>
</div>

{#if selectedEvent}
    <div id="modalOverlay" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6">
        <div id="backdrop" class="absolute inset-0 bg-black/40 backdrop-blur-sm transition-opacity" onclick={closeModal}></div>

        <div class="modal-animate relative bg-[#F8F7F5] w-full max-w-md rounded-[2.5rem] shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
            
            <div class="absolute top-4 right-4 z-20">
                <button onclick={closeModal} class="bg-black/10 hover:bg-black/20 text-white p-2 rounded-full transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </div>

            <div class="overflow-y-auto hide-scrollbar">
                <div class="relative w-full aspect-square bg-gray-100">
                    <img src={selectedEvent.imageUrl || 'https://placehold.co/400x400/e2dbd1/797166?text=Kajian'} alt={selectedEvent.title} class="w-full h-full object-cover" onerror={handleModalImgError}>
                    <div class="absolute inset-0 bg-gradient-to-t from-[#F8F7F5] via-transparent to-transparent opacity-40"></div>
                </div>

                <div class="px-6 pb-6 -mt-10 relative z-10">
                    <div class="bg-white rounded-[2rem] p-6 shadow-sm border border-stone-100">
                        <h2 class="text-xl font-bold leading-tight mb-1 text-[#2D2A29]">
                            {selectedEvent.title}
                        </h2>
                        
                        <div class="space-y-6 mt-6">
                            <div class="flex items-center gap-4">
                                <div class="w-10 h-10 bg-stone-50 rounded-xl flex items-center justify-center text-[#4A4543] shrink-0 border border-stone-100">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
                                </div>
                                <div class="flex-1">
                                    <p class="text-[9px] text-stone-400 uppercase font-bold tracking-[0.15em] mb-0.5">Tanggal</p>
                                    <p class="font-semibold text-sm">{selectedEvent.dateStr || '-'}</p>
                                </div>
                            </div>

                            <div class="flex items-center gap-4">
                                <div class="w-10 h-10 bg-stone-50 rounded-xl flex items-center justify-center text-[#4A4543] shrink-0 border border-stone-100">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                                </div>
                                <div class="flex-1">
                                    <p class="text-[9px] text-stone-400 uppercase font-bold tracking-[0.15em] mb-0.5">Waktu</p>
                                    <p class="font-semibold text-sm">{selectedEvent.timeStr || '-'}</p>
                                </div>
                            </div>

                            <div class="flex items-start gap-4">
                                <div class="w-10 h-10 bg-stone-50 rounded-xl flex items-center justify-center text-[#4A4543] shrink-0 border border-stone-100">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                                </div>
                                <div class="flex-1">
                                    <p class="text-[9px] text-stone-400 uppercase font-bold tracking-[0.15em] mb-0.5">Lokasi</p>
                                    <p class="font-semibold text-sm leading-snug">{selectedEvent.place || '-'}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
{/if}

<style>
	.modal-animate {
		animation: zoomIn 0.3s ease-out forwards;
	}
	@keyframes zoomIn {
		from {
			opacity: 0;
			transform: scale(0.95);
		}
		to {
			opacity: 1;
			transform: scale(1);
		}
	}
</style>
