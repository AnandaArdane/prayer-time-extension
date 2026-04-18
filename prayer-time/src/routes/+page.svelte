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
		currentTime
			.toLocaleDateString('id-ID', {
				weekday: 'long',
				year: 'numeric',
				month: 'long',
				day: 'numeric'
			})
			.replace('Minggu', 'Ahad')
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

<div class="text-[#4A4643] min-h-screen overflow-x-hidden flex flex-col">
	<div class="max-w-7xl mx-auto px-4 md:px-8 py-4 w-full flex-grow flex flex-col">
		<header
			class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6"
		>
			<div class="flex items-center space-x-4">
				<div class="p-2.5 bg-white rounded-2xl shadow-sm border border-gray-100">
					<svg
						xmlns="http://www.w3.org/2000/svg"
						class="w-5 h-5 text-[#6B655E]"
						viewBox="0 0 24 24"
						fill="none"
						stroke="currentColor"
						stroke-width="2.5"
						stroke-linecap="round"
						stroke-linejoin="round"
						><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle
							cx="12"
							cy="10"
							r="3"
						></circle></svg
					>
				</div>
				<div>
					<div class="text-[9px] text-[#A8A29A] uppercase tracking-[0.2em] font-bold leading-tight">
						Lokasi Saat Ini
					</div>
					<div class="font-black text-lg text-[#4A4643] tracking-tight uppercase">
						{userLocation}
					</div>
				</div>
			</div>

			<div
				class="flex items-center space-x-6 bg-white/50 backdrop-blur-sm px-5 py-2 rounded-full border border-white"
			>
				<div class="text-right">
					<div class="font-bold text-xs text-[#4A4643] uppercase">{dateString}</div>
					<div class="text-[10px] text-red-400 font-bold uppercase tracking-widest">
						{data.prayerData.hijriDate || 'Memuat Tanggal...'}
					</div>
				</div>
				<div class="h-6 w-[1px] bg-gray-200 hidden sm:block"></div>
				<div class="text-xl font-black text-[#4A4643] tracking-tight tabular-nums">
					{currentTime
						.toLocaleTimeString('id-ID', {
							hour12: false,
							hour: '2-digit',
							minute: '2-digit',
							second: '2-digit'
						})
						.replace(/\./g, ':')}
				</div>
			</div>
		</header>

		<div class="flex flex-col space-y-4 flex-grow">
			<!-- Baris Atas -->
			<div class="grid grid-cols-1 lg:grid-cols-10 gap-6 items-stretch">
				<Content slides={data.slides} />
				<PrayerTimeComponent
					prayerTimes={data.prayerData.prayerTimes}
					{currentTime}
					error={data.prayerData.error}
				/>
			</div>

			<!-- Baris Bawah (Kajian) -->
			<StudySession events={data.sidebarEvents} />
		</div>

		<!-- Tombol Floating -->
		<div class="fixed bottom-10 right-10 flex flex-col items-center space-y-3 z-50">
			<TimeMachine simulatedTime={data.simulatedTime} />
			<a
				href="/login"
				class="w-14 h-14 bg-white rounded-full btn-shadow flex items-center justify-center text-[#A8A29A] border border-gray-100 hover:bg-[#4A4643] hover:text-white transition-all duration-300 transform hover:-translate-y-1 active:scale-95 group"
				aria-label="Login"
			>
				<svg
					xmlns="http://www.w3.org/2000/svg"
					class="w-6 h-6 group-hover:scale-110 transition-transform"
					viewBox="0 0 24 24"
					fill="none"
					stroke="currentColor"
					stroke-width="2"
					stroke-linecap="round"
					stroke-linejoin="round"
					><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"
					></circle></svg
				>
			</a>
		</div>
	</div>
</div>
