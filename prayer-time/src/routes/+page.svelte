<script lang="ts">
	import { onMount } from 'svelte';
	import type { PageData } from './$types';
	import { type PrayerTime } from '$lib/server/services/prayerService';
	import PrayerTimeComponent from '$lib/components/PrayerTime.svelte';
	import Content from '$lib/components/Content.svelte';
	import StudySession from '$lib/components/StudySession.svelte';
	import { goto, invalidateAll } from '$app/navigation';
	import { page } from '$app/stores';
	import TimeMachine from '$lib/components/TimeMachine.svelte';

	let { data }: { data: PageData } = $props();

	// State for Head and general time
	let currentTime = $state(data.simulatedTime ? new Date(data.simulatedTime) : new Date());
	let userLocation = $state('Mendeteksi...');

	// Derived state for formatting
	let dateString = $derived(
		currentTime.toLocaleDateString('id-ID', {
			weekday: 'long',
			year: 'numeric',
			month: 'long',
			day: 'numeric'
		})
	);

	async function fetchUserLocation() {
		if ('geolocation' in navigator) {
			navigator.geolocation.getCurrentPosition(
				async (position) => {
					try {
						const { latitude, longitude } = position.coords;

						// Reload with coordinates if not already present or different
						const currentLat = $page.url.searchParams.get('lat');
						const currentLong = $page.url.searchParams.get('long');

						if (!currentLat || !currentLong) {
							goto(`?lat=${latitude}&long=${longitude}`, { keepFocus: true, noScroll: true });
						}

						const targetLat = currentLat ? parseFloat(currentLat) : latitude;
						const targetLong = currentLong ? parseFloat(currentLong) : longitude;

						// Menggunakan Nominatim untuk reverse geocoding
						const response = await fetch(
							`https://nominatim.openstreetmap.org/reverse?format=json&lat=${targetLat}&lon=${targetLong}&zoom=10`
						);
						const data = await response.json();

						// Prioritas pengambilan nama lokasi yang lebih spesifik ke umum
						const city =
							data.address.city ||
							data.address.town ||
							data.address.village ||
							data.address.suburb ||
							data.address.county ||
							'Lokasi Terdeteksi';
						const state = data.address.state || '';

						userLocation = `${city}${state ? ', ' + state : ''}`;
					} catch (error) {
						userLocation = 'Gagal memuat alamat';
						console.error('Geocoding error:', error);
					}
				},
				(error) => {
					if (error.code === 1) {
						userLocation = 'Akses Lokasi Ditolak';
					} else {
						userLocation = 'Lokasi Tidak Tersedia';
					}
				},
				{
					enableHighAccuracy: true,
					timeout: 5000,
					maximumAge: 0
				}
			);
		} else {
			userLocation = 'Browser Tidak Mendukung';
		}
	}

	let lastMaghribTrigger = $state('');

	$effect(() => {
		if (!data.prayerData.prayerTimes || data.prayerData.prayerTimes.length === 0) return;

		const maghrib = data.prayerData.prayerTimes.find((p: PrayerTime) => p.name === 'Maghrib');
		if (!maghrib) return;

		const [h, m] = maghrib.time.split(':').map(Number);
		const maghribTime = new Date(currentTime);
		maghribTime.setHours(h, m, 0, 0);

		const todayStr = currentTime.toDateString();

		// Trigger re-fetch if we just passed Maghrib today and haven't triggered it yet
		if (currentTime >= maghribTime && lastMaghribTrigger !== todayStr) {
			lastMaghribTrigger = todayStr;
			invalidateAll();
		}
	});

	$effect(() => {
		currentTime = data.simulatedTime ? new Date(data.simulatedTime) : new Date();

		const clockInterval = setInterval(() => {
			if (data.simulatedTime) {
				currentTime = new Date(currentTime.getTime() + 1000);
			} else {
				currentTime = new Date();
			}
		}, 1000);

		return () => clearInterval(clockInterval);
	});

	onMount(() => {
		// Fetch Location
		fetchUserLocation();
	});
</script>

<div class="bg-gray-50 font-sans text-gray-800 antialiased min-h-screen flex flex-col">
	<!-- Header -->
	<header class="w-full px-4 lg:px-12 pt-4 mb-2">
		<div class="flex justify-between items-center border-b border-gray-200 pb-3">
			<div class="flex items-center">
				<div
					class="bg-theme-primary-700 w-8 h-8 lg:w-10 lg:h-10 rounded-lg flex items-center justify-center shadow-md mr-3 lg:mr-4"
				>
					<svg class="w-5 h-5 lg:w-6 lg:h-6 text-white" fill="currentColor" viewBox="0 0 24 24">
						<path
							d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"
						/>
					</svg>
				</div>
				<div>
					<div class="flex items-center gap-1 mb-0.5">
						<span class="w-1.5 h-1.5 bg-green-500 rounded-full animate-pulse"></span>
						<p class="text-[7px] lg:text-[8px] font-bold text-gray-400 uppercase tracking-widest">
							Lokasi Saat Ini
						</p>
					</div>
					<p
						id="user-location"
						class="text-[10px] lg:text-[12px] text-theme-primary-700 font-black uppercase tracking-widest italic"
					>
						{userLocation}
					</p>
				</div>
			</div>

			<div class="text-right">
				<p class="text-gray-500 text-[8px] lg:text-[10px] font-bold uppercase tracking-widest">
					{dateString}
				</p>
				<p
					class="text-theme-primary-500 text-[8px] lg:text-[10px] font-black uppercase tracking-widest"
				>
					{data.prayerData.hijriDate || 'Memuat Tanggal...'}
				</p>
			</div>
		</div>
	</header>

	<!-- Jadwal Sholat Section -->
	<PrayerTimeComponent
		prayerTimes={data.prayerData.prayerTimes}
		{currentTime}
		error={data.prayerData.error}
	/>

	<!-- Main Content -->
	<main
		class="max-w-[1440px] mx-auto px-4 lg:px-12 w-full grid grid-cols-1 lg:grid-cols-12 gap-6 flex-grow pb-8"
	>
		<!-- Slider Area (Content) -->
		<Content slides={data.slides} />

		<!-- Sidebar Area (Study Session) -->
		<StudySession events={data.sidebarEvents} />
	</main>

	<!-- Footer -->
	<footer class="py-4 px-6 text-center border-t border-gray-100 bg-white mt-auto">
		<p class="text-gray-400 text-[8px] lg:text-[10px] font-bold tracking-[0.4em] uppercase">
			&copy; 2026 DIGITAL MASJID HUB | AL-BARKAH
		</p>
	</footer>
	<div class="fixed bottom-6 right-20 z-[100]">
		<a
			href="/login"
			class="w-12 h-12 bg-white rounded-full shadow-lg border border-gray-100 flex items-center justify-center hover:scale-110 active:scale-95 transition-all group text-gray-400 hover:text-theme-primary-600"
			aria-label="Login"
		>
			<svg
				class="w-6 h-6 group-hover:scale-110 transition-transform"
				fill="none"
				stroke="currentColor"
				viewBox="0 0 24 24"
			>
				<path
					stroke-linecap="round"
					stroke-linejoin="round"
					stroke-width="2"
					d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
				/>
			</svg>
		</a>
	</div>
	<TimeMachine simulatedTime={data.simulatedTime} />
</div>
