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

<section class="w-full px-4 lg:px-12 mb-4">
	{#if error}
		<div
			class="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
			role="alert"
		>
			<strong class="font-bold">Error!</strong>
			<span class="block sm:inline">{error}</span>
		</div>
	{/if}

	<div
		class="bg-white p-4 lg:p-5 rounded-[1.5rem] lg:rounded-[2rem] shadow-sm border border-gray-100"
	>
		<div class="flex flex-col lg:flex-row gap-6 items-center">
			<!-- Time Info Center -->
			<div class="w-full lg:w-1/3 lg:border-r border-gray-100 lg:pr-8">
				<div class="flex flex-col gap-4">
					<div
						class="transition-colors duration-500 p-3 lg:p-4 rounded-2xl border"
						class:bg-green-600={isExactTime}
						class:border-green-400={isExactTime}
						class:shadow-lg={isExactTime}
						class:animate-pulse-slow={isExactTime}
						class:bg-theme-secondary={!isExactTime}
						class:border-theme-primary-100={!isExactTime}
						class:border-opacity-50={!isExactTime}
					>
						<span
							class="text-[8px] lg:text-[10px] font-black uppercase tracking-[0.2em] block mb-1"
							class:text-white={isExactTime}
							class:text-theme-primary-500={!isExactTime}
						>
							{isExactTime ? 'SEDANG BERLANGSUNG' : 'Sholat Berikutnya'}
						</span>
						<div class="flex items-baseline justify-between">
							<h2
								class="text-2xl lg:text-3xl font-black leading-none"
								class:text-white={isExactTime}
								class:text-theme-primary-700={!isExactTime}
							>
								{nextPrayerName}
							</h2>
							<p
								class="text-xs lg:text-sm font-bold"
								class:text-green-100={isExactTime}
								class:text-theme-primary-500={!isExactTime}
							>
								{nextPrayerTime}
							</p>
						</div>
					</div>

					<div class="flex justify-between items-center px-1">
						<div class="flex flex-col">
							<span
								class="text-[7px] lg:text-[8px] font-bold text-gray-400 uppercase tracking-widest mb-0.5"
								>Waktu Sekarang</span
							>
							<h3 class="text-xs lg:text-sm font-black text-gray-700 tracking-wider">
								{timeString}
							</h3>
						</div>
						<div class="h-6 w-px bg-gray-100"></div>
						<div class="flex flex-col text-right">
							<span
								class="text-[7px] lg:text-[8px] font-bold uppercase tracking-widest mb-0.5"
								class:text-green-400={isExactTime}
								class:text-red-400={!isExactTime}
							>
								{isExactTime ? 'STATUS' : 'Sisa Waktu'}
							</span>
							<h3
								class="text-xs lg:text-sm font-black tracking-wider"
								class:text-green-600={isExactTime}
								class:animate-bounce={isExactTime}
								class:text-red-600={!isExactTime}
							>
								{countdownString}
							</h3>
						</div>
					</div>
				</div>
			</div>

			<!-- Grid Sholat Utama -->
			<div class="w-full lg:w-2/3">
				<div class="grid grid-cols-5 gap-2 lg:gap-3">
					{#if prayerTimes.length > 0}
						{#each prayerTimes as prayer, i}
							{@const active = isPrayerActive(i)}
							<div
								class="sholat-card p-2 lg:p-4 rounded-xl lg:rounded-2xl flex flex-col items-center transition-all relative overflow-hidden"
								class:border-2={active}
								class:border-green-500={active && isExactTime}
								class:bg-green-50={active && isExactTime}
								class:shadow-xl={active && isExactTime}
								class:lg:scale-110={active && isExactTime}
								class:is-active-now={active && isExactTime}
								class:z-20={active && isExactTime}
								class:border-theme-primary-500={active && !isExactTime}
								class:bg-theme-primary-100={active && !isExactTime}
								class:shadow-md={active && !isExactTime}
								class:lg:scale-105={active && !isExactTime}
								class:z-10={active && !isExactTime}
								class:bg-gray-50={!active}
								class:border-none={!active}
								class:opacity-50={!active}
							>
								<span class="text-[7px] lg:text-[9px] font-bold text-gray-400 uppercase mb-1"
									>{prayer.name}</span
								>
								<span class="text-xs lg:text-xl font-black text-theme-primary-600"
									>{prayer.time}</span
								>

								{#if active && isExactTime}
									<div
										class="absolute top-0 right-0 bg-green-500 text-white text-[6px] lg:text-[8px] font-bold px-2 py-0.5 rounded-bl-lg uppercase"
									>
										SEKARANG
									</div>
								{/if}
							</div>
						{/each}
					{:else}
						<!-- Placeholder when data is missing/error -->
						{#each Array(5) as _, i}
							<div
								class="sholat-card p-2 lg:p-4 rounded-xl lg:rounded-2xl bg-gray-50 flex flex-col items-center opacity-30 border border-gray-200"
							>
								<span class="text-[7px] lg:text-[9px] font-bold text-gray-400 uppercase mb-1"
									>...</span
								>
								<span class="text-xs lg:text-xl font-black text-gray-400">--:--</span>
							</div>
						{/each}
					{/if}
				</div>
			</div>
		</div>
	</div>
</section>
