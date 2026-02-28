<script lang="ts">
	import { page } from '$app/stores';
	import { goto } from '$app/navigation';
	import { onMount } from 'svelte';

	let { simulatedTime }: { simulatedTime: string | null } = $props();

	let isOpen = $state(false);
	let selectedDate = $state('');
	let selectedTime = $state('');

	onMount(() => {
		const now = simulatedTime ? new Date(simulatedTime) : new Date();
		selectedDate = now.toISOString().split('T')[0];
		selectedTime = now.toTimeString().split(' ')[0].substring(0, 5);
	});

	function applyTime() {
		const newTime = new Date(`${selectedDate}T${selectedTime}`);
		const url = new URL($page.url);
		url.searchParams.set('time', newTime.toISOString());
		goto(url.toString(), { keepFocus: true, noScroll: true, replaceState: true });
		isOpen = false;
	}

	function resetTime() {
		const url = new URL($page.url);
		url.searchParams.delete('time');
		goto(url.toString(), { keepFocus: true, noScroll: true, replaceState: true });
		isOpen = false;
	}
</script>

<div class="fixed bottom-6 right-6 z-[100]">
	{#if isOpen}
		<div
			class="bg-white rounded-2xl shadow-2xl border border-gray-100 p-6 mb-4 w-72 animate-in fade-in slide-in-from-bottom-4 duration-300"
		>
			<div class="flex justify-between items-center mb-4">
				<h3 class="text-sm font-black text-theme-primary-700 uppercase tracking-widest">
					Time Machine
				</h3>
				<button
					onclick={() => (isOpen = false)}
					class="text-gray-400 hover:text-gray-600"
					aria-label="Close Time Machine"
				>
					<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M6 18L18 6M6 6l12 12"
						/>
					</svg>
				</button>
			</div>

			<div class="space-y-4">
				<div>
					<label
						for="tm-date"
						class="block text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1"
						>Tanggal</label
					>
					<input
						id="tm-date"
						type="date"
						bind:value={selectedDate}
						class="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-theme-primary-500/20 focus:border-theme-primary-500"
					/>
				</div>
				<div>
					<label
						for="tm-time"
						class="block text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1"
						>Waktu</label
					>
					<input
						id="tm-time"
						type="time"
						bind:value={selectedTime}
						class="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-theme-primary-500/20 focus:border-theme-primary-500"
					/>
				</div>

				<div class="flex gap-2 pt-2">
					<button
						onclick={resetTime}
						class="flex-1 px-4 py-2 text-[10px] font-bold text-gray-500 uppercase tracking-widest hover:bg-gray-100 rounded-lg transition-colors"
					>
						Reset
					</button>
					<button
						onclick={applyTime}
						class="flex-1 px-4 py-2 text-[10px] font-bold text-white bg-theme-primary-600 uppercase tracking-widest rounded-lg shadow-md hover:bg-theme-primary-700 transition-colors"
					>
						Apply
					</button>
				</div>
			</div>
		</div>
	{/if}

	<button
		onclick={() => (isOpen = !isOpen)}
		class="w-12 h-12 bg-white rounded-full shadow-lg border border-gray-100 flex items-center justify-center hover:scale-110 active:scale-95 transition-all group"
		class:text-theme-primary-600={simulatedTime}
		class:text-gray-400={!simulatedTime}
		aria-label="Toggle Time Machine"
	>
		<svg
			class="w-6 h-6 group-hover:rotate-12 transition-transform"
			fill="none"
			stroke="currentColor"
			viewBox="0 0 24 24"
		>
			<path
				stroke-linecap="round"
				stroke-linejoin="round"
				stroke-width="2"
				d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
			/>
		</svg>
	</button>
</div>

<style>
	@keyframes -global-fade-in {
		from {
			opacity: 0;
		}
		to {
			opacity: 1;
		}
	}

	@keyframes -global-slide-in-from-bottom {
		from {
			transform: translateY(1rem);
		}
		to {
			transform: translateY(0);
		}
	}

	.animate-in {
		animation-fill-mode: forwards;
	}

	.fade-in {
		animation-name: -global-fade-in;
	}

	.slide-in-from-bottom-4 {
		animation-name: -global-slide-in-from-bottom;
	}
</style>
