<script lang="ts">
	import type { SidebarEvent } from '$lib/server/services/prayerService';

	interface Props {
		events: SidebarEvent[];
	}

	let { events }: Props = $props();

	let scrollContainer: HTMLElement;

	function handleImgError(e: Event) {
		const target = e.target as HTMLImageElement;
		target.onerror = null;
		target.src = 'https://placehold.co/200x140/e2dbd1/797166?text=Image';
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
