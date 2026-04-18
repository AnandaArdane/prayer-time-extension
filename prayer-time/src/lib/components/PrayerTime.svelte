<script lang="ts">
	import type { PrayerTime } from '$lib/server/services/prayerService';

	interface Props {
		prayerTimes: PrayerTime[];
		currentTime: Date;
		error?: string;
	}

	let { prayerTimes, currentTime, error }: Props = $props();

	// Derived state for formatting
	let timeString = $derived(currentTime.toLocaleTimeString('id-ID', { hour12: false }));

	// Prayer Logic State
	let nextPrayerIndex = $state(-1);
	let nextPrayerName = $state('...');
	let nextPrayerTime = $state('--:--');
	let countdownString = $state('--:--:--');
	let isExactTime = $state(false);

	$effect(() => {
		if (!prayerTimes.length) {
			nextPrayerName = '-';
			nextPrayerTime = '--:--';
			countdownString = '--:--:--';
			return;
		}

		const now = currentTime;

		let nextIndex = -1;
		let minDiff = Infinity;
		let foundExact = false;

		// Tolerance 10 minutes
		const tolerance = 10 * 60 * 1000;

		for (let i = 0; i < prayerTimes.length; i++) {
			const [h, m] = prayerTimes[i].time.split(':').map(Number);
			const pDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), h, m, 0);
			const diff = pDate.getTime() - now.getTime();

			// Check if now is prayer time (within tolerance)
			if (diff <= 0 && diff > -tolerance) {
				nextIndex = i;
				minDiff = 0;
				foundExact = true;
				break;
			}

			if (diff > 0 && diff < minDiff) {
				minDiff = diff;
				nextIndex = i;
			}
		}

		// If no next prayer today, it's Fajr tomorrow
		if (nextIndex === -1 && !foundExact) {
			const tom = new Date(now);
			tom.setDate(now.getDate() + 1);
			const [h, m] = prayerTimes[0].time.split(':').map(Number);
			const pDate = new Date(tom.getFullYear(), tom.getMonth(), tom.getDate(), h, m, 0);
			minDiff = pDate.getTime() - now.getTime();
			nextIndex = 0;
		}

		// Update State
		nextPrayerIndex = nextIndex;
		isExactTime = foundExact;

		if (nextIndex !== -1) {
			nextPrayerName = prayerTimes[nextIndex].name;
			nextPrayerTime = prayerTimes[nextIndex].time;
		}

		if (foundExact) {
			countdownString = 'WAKTUNYA!';
		} else {
			let seconds = Math.floor(minDiff / 1000);
			const hh = Math.floor(seconds / 3600);
			seconds %= 3600;
			const mm = Math.floor(seconds / 60);
			const ss = seconds % 60;
			countdownString = `${String(hh).padStart(2, '0')}:${String(mm).padStart(2, '0')}:${String(ss).padStart(2, '0')}`;
		}
	});

	function isPrayerActive(index: number) {
		return index === nextPrayerIndex;
	}
</script>

<div class="lg:col-span-3 flex flex-col space-y-4">
	{#if error}
		<div
			class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-2xl relative text-sm"
			role="alert"
		>
			<strong class="font-bold">Error!</strong>
			<span class="block sm:inline">{error}</span>
		</div>
	{/if}

	<div
		class="bg-[#4A4643] p-6 rounded-[2.5rem] shadow-2xl relative overflow-hidden group text-white flex-shrink-0"
	>
		<div
			class="absolute -right-4 -top-4 opacity-5 transform rotate-12 group-hover:rotate-0 transition-transform duration-500"
		>
			<svg
				xmlns="http://www.w3.org/2000/svg"
				class="w-24 h-24 text-white"
				viewBox="0 0 24 24"
				fill="none"
				stroke="currentColor"
				stroke-width="2"
				stroke-linecap="round"
				stroke-linejoin="round"
				><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"
				></polyline></svg
			>
		</div>
		<div class="relative z-10">
			<div class="text-[9px] text-[#DED9D1] font-bold uppercase tracking-[0.2em] mb-1">
				{isExactTime ? 'SEDANG BERLANGSUNG' : 'Sholat Berikutnya'}
			</div>
			<div class="text-3xl font-black mb-0.5 tracking-tight">{nextPrayerName}</div>
			<div class="text-[#DED9D1] text-xl font-black mb-4 tracking-tighter">
				{nextPrayerTime} WIB
			</div>
			<div class="pt-4 border-t border-white/10">
				<div class="text-[9px] text-[#A8A29A] uppercase font-bold mb-1 tracking-widest">
					{isExactTime ? 'STATUS' : 'Sisa Waktu'}
				</div>
				<div
					class="text-2xl font-black tabular-nums tracking-tighter {isExactTime
						? 'text-green-400 animate-bounce'
						: 'text-red-400 animate-pulse'}"
				>
					{countdownString}
				</div>
			</div>
		</div>
	</div>

	<div class="bg-white rounded-[2.5rem] p-6 card-shadow border border-gray-100/50 flex-grow">
		<h3 class="font-black text-[#4A4643] uppercase tracking-widest text-[10px] mb-4 px-2">
			Jadwal Lengkap
		</h3>
		<div class="space-y-2">
			{#if prayerTimes.length > 0}
				{#each prayerTimes as p, i}
					{@const active = isPrayerActive(i)}
					<div
						class="flex justify-between items-baseline p-4 rounded-2xl transition-all border {active
							? 'bg-[#DED9D1] border-[#4A4643]/10'
							: 'bg-[#F7F6F3]/50 border-transparent'}"
					>
						<span
							class="text-base font-black uppercase tracking-tight {active
								? 'text-[#4A4643]'
								: 'text-[#4A4643]/80'}">{p.name}</span
						>
						<span class="text-lg font-black tracking-tighter text-[#4A4643]">{p.time}</span>
					</div>
				{/each}
			{:else}
				{#each Array(5) as _, i}
					<div
						class="flex justify-between items-baseline p-4 rounded-2xl transition-all border bg-[#F7F6F3]/50 border-transparent opacity-50"
					>
						<span class="text-base font-black uppercase tracking-tight text-[#4A4643]/80">...</span>
						<span class="text-lg font-black tracking-tighter text-[#4A4643]">--:--</span>
					</div>
				{/each}
			{/if}
		</div>
	</div>
</div>
