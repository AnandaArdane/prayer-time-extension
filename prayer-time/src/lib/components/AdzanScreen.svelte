<script lang="ts">
	import { onMount, onDestroy } from 'svelte';

	interface Props {
		prayerName: string;
		prayerTime: string;
		currentTime: Date;
		dateString: string;
		hijriDate: string;
		userLocation: string;
		nextPrayerName: string;
		nextPrayerTime: string;
	}

	let {
		prayerName,
		prayerTime,
		currentTime,
		dateString,
		hijriDate,
		userLocation,
		nextPrayerName,
		nextPrayerTime
	}: Props = $props();

	let audioRef: HTMLAudioElement;

	let timeString = $derived(
		currentTime.toLocaleTimeString('id-ID', {
			hour12: false,
			hour: '2-digit',
			minute: '2-digit',
			second: '2-digit'
		}).replace(/\./g, ':')
	);

	onMount(() => {
		if (audioRef) {
			audioRef.play().catch(() => {
				console.log('Menunggu interaksi pengguna untuk memutar audio.');
			});
		}
	});

    onDestroy(() => {
        if (audioRef) {
            audioRef.pause();
            audioRef.currentTime = 0;
        }
    })
</script>

<svelte:head>
	<link rel="preconnect" href="https://fonts.googleapis.com" />
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="anonymous" />
	<link
		href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap"
		rel="stylesheet"
	/>
</svelte:head>

<div class="adzan-wrapper fixed inset-0 z-[100] flex items-center justify-center overflow-hidden w-full h-full">
	<div id="app" class="relative w-full max-w-lg p-6 mx-4">
		<!-- Decoration Elements -->
		<div class="absolute -top-16 -left-16 w-64 h-64 bg-red-200/20 blur-[100px] rounded-full"></div>
		<div
			class="absolute -bottom-16 -right-16 w-64 h-64 bg-stone-300/30 blur-[100px] rounded-full"
		></div>

		<div
			class="main-card rounded-[48px] p-10 text-center relative overflow-hidden shadow-soft border border-stone-100"
		>
			<!-- Location Header -->
			<div class="reveal-item delay-1 flex items-center justify-center space-x-3 mb-12">
				<div
					class="bg-stone-50 p-2.5 rounded-2xl border border-stone-100 shadow-sm text-stone-400"
				>
					<svg class="w-4 h-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512" fill="currentColor"><!--!Font Awesome Free 6.x.x by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d="M215.7 499.2C267 435 384 279.4 384 192C384 86 298 0 192 0S0 86 0 192c0 87.4 117 243 168.3 307.2c12.3 15.3 35.1 15.3 47.4 0zM192 128a64 64 0 1 1 0 128 64 64 0 1 1 0-128z"/></svg>
				</div>
				<div class="text-left">
					<p
						class="text-[9px] uppercase tracking-[0.25em] text-stone-400 font-extrabold leading-none mb-1"
					>
						Lokasi Saat Ini
					</p>
					<p class="text-[13px] font-bold text-stone-700">{userLocation}</p>
				</div>
			</div>

			<!-- Icon Section -->
			<div class="reveal-item delay-2 relative flex justify-center mb-14 mt-6">
				<div class="pulse-ring w-32 h-32"></div>
				<div class="pulse-ring w-32 h-32" style="animation-delay: 1s"></div>
				<div
					class="w-24 h-24 bg-[#4A4543] rounded-[32px] flex items-center justify-center shadow-2xl relative z-10 rotate-[4deg] border-b-4 border-black/20"
				>
					<svg class="w-10 h-10 text-stone-100" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" fill="currentColor"><!--!Font Awesome Free 6.x.x by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d="M256 0a256 256 0 1 1 0 512A256 256 0 1 1 256 0zM232 120V256c0 8 4 15.5 10.7 20l96 64c11 7.4 25.9 4.4 33.3-6.7s4.4-25.9-6.7-33.3L280 243.2V120c0-13.3-10.7-24-24-24s-24 10.7-24 24z"/></svg>
				</div>
			</div>

			<!-- Text Content -->
			<div class="space-y-3 mb-12">
				<h2 class="reveal-item delay-3 text-[#B33D3D] font-extrabold tracking-[0.3em] uppercase text-[11px]">
					Panggilan Sholat
				</h2>
				<h1 id="sholat-name" class="reveal-item delay-3 text-6xl font-[800] tracking-tight text-[#3A3533]">
					{prayerName}
				</h1>
				<p
					id="current-time"
					class="reveal-item delay-4 text-xl text-stone-300 font-light tracking-wider"
				>
					{timeString}
				</p>

				<div class="reveal-item delay-4 flex justify-center pt-5">
					<div
						class="bg-stone-50/80 backdrop-blur-sm px-5 py-2.5 rounded-full border border-stone-100 shadow-sm flex items-center"
					>
						<span class="text-[11px] font-bold text-[#B33D3D]/80 uppercase tracking-[0.15em]">{hijriDate}</span>
					</div>
				</div>
			</div>

			<!-- Message Box -->
			<div
				class="reveal-item delay-5 bg-stone-50/50 border border-stone-100 rounded-[32px] p-8 text-stone-500 text-[14px] leading-relaxed italic font-medium"
			>
				"Sesungguhnya sholat itu mencegah dari perbuatan keji dan mungkar."
			</div>

			<!-- Information Text -->
			<div
				class="reveal-item delay-5 mt-10 text-[10px] uppercase tracking-[0.3em] text-stone-300 font-black"
			>
				Panggilan Menuju Kemenangan
			</div>
		</div>

		<!-- Progress Footer -->
		<div class="reveal-item delay-5 mt-10 flex justify-between items-center px-6">
			<div class="text-left group cursor-pointer">
				<p class="text-[9px] uppercase font-black text-stone-300 tracking-[0.2em] mb-1">
					Jadwal Selanjutnya
				</p>
				<div class="flex items-center space-x-2">
					<p class="text-[15px] font-bold text-stone-600 uppercase">{nextPrayerName}</p>
					<span class="w-1 h-1 rounded-full bg-stone-300"></span>
					<p class="text-[15px] font-medium text-stone-400">{nextPrayerTime} WIB</p>
				</div>
			</div>
			<div
				class="w-10 h-10 rounded-full border border-stone-200 flex items-center justify-center text-stone-300 hover:text-[#B33D3D] hover:border-[#B33D3D]/30 transition-colors"
			>
				<svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512" fill="currentColor"><!--!Font Awesome Free 6.x.x by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d="M278.6 233.4c12.5 12.5 12.5 32.8 0 45.3l-160 160c-12.5 12.5-32.8 12.5-45.3 0s-12.5-32.8 0-45.3L210.7 256 73.4 118.6c-12.5-12.5-12.5-32.8 0-45.3s32.8-12.5 45.3 0l160 160z"/></svg>
			</div>
		</div>
	</div>

	<!-- Audio Element -->
	<audio bind:this={audioRef} id="adzan-audio" loop>
		<source src="https://www.islamcan.com/audio/adhan/makkah.mp3" type="audio/mpeg" />
	</audio>
</div>

<style>
	.adzan-wrapper {
		background-color: #f4f2ee;
		font-family: 'Plus Jakarta Sans', sans-serif;
		color: #3a3533;
		-webkit-font-smoothing: antialiased;
	}

	/* Animasi Reveal Utama */
	#app {
		opacity: 0;
		animation: pageReveal 1.2s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
	}

	@keyframes pageReveal {
		from {
			opacity: 0;
			transform: translateY(40px) scale(0.98);
		}
		to {
			opacity: 1;
			transform: translateY(0) scale(1);
		}
	}

	/* Staggered Entrance untuk elemen internal */
	.reveal-item {
		opacity: 0;
		transform: translateY(20px);
		animation: itemFadeUp 0.8s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
	}

	@keyframes itemFadeUp {
		to {
			opacity: 1;
			transform: translateY(0);
		}
	}

	.delay-1 {
		animation-delay: 0.2s;
	}
	.delay-2 {
		animation-delay: 0.3s;
	}
	.delay-3 {
		animation-delay: 0.4s;
	}
	.delay-4 {
		animation-delay: 0.5s;
	}
	.delay-5 {
		animation-delay: 0.6s;
	}

	.pulse-ring {
		position: absolute;
		border-radius: 50%;
		border: 1px solid rgba(179, 61, 61, 0.15);
		animation: pulse 3s cubic-bezier(0.455, 0.03, 0.515, 0.955) infinite;
	}

	@keyframes pulse {
		0% {
			transform: scale(0.8);
			opacity: 0.8;
		}
		100% {
			transform: scale(1.8);
			opacity: 0;
		}
	}

	.shadow-soft {
		box-shadow:
			0 20px 50px -15px rgba(74, 69, 67, 0.08),
			0 10px 20px -10px rgba(74, 69, 67, 0.04);
	}

	.main-card {
		transition: all 0.5s ease;
		background: #ffffff;
	}

	.main-card:hover {
		transform: translateY(-5px);
		box-shadow: 0 30px 60px -12px rgba(74, 69, 67, 0.1);
	}
</style>
