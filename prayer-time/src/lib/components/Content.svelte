<script lang="ts">
	import { onMount } from 'svelte';
	import type { Slide } from '$lib/server/services/prayerService';

	interface Props {
		slides: Slide[];
	}

	let { slides }: Props = $props();

	let sliderContainer: HTMLElement;
	let currentSlide = $state(0);

	onMount(() => {
		// Slider Interval
		const sliderInterval = setInterval(() => {
			if (!sliderContainer) return;
			const slideCount = slides.length;
			if (slideCount === 0) return;

			currentSlide = (currentSlide + 1) % slideCount;
			sliderContainer.scrollTo({
				left: currentSlide * sliderContainer.offsetWidth,
				behavior: 'smooth'
			});
		}, 6000);

		return () => {
			clearInterval(sliderInterval);
		};
	});

	function handleImgError(e: Event) {
		const target = e.target as HTMLImageElement;
		target.onerror = null;
		target.src = 'https://placehold.co/1200x800/797166/ffffff?text=Prayer+Time';
		// Also fix blur bg sibling if it exists
		const parent = target.parentElement;
		if (parent) {
			const blurBg = parent.querySelector('.blur-bg') as HTMLImageElement;
			if (blurBg && blurBg !== target) blurBg.src = target.src;
		}
	}
</script>

<div class="lg:col-span-7">
	<div
		class="relative w-full h-full rounded-[2.5rem] overflow-hidden shadow-xl group border border-gray-100 min-h-[350px]"
	>
		<div
			bind:this={sliderContainer}
			class="slider-container flex w-full h-full overflow-x-auto snap-x snap-mandatory hide-scrollbar"
		>
			{#each slides as slide}
				<div
					class="slide-item relative h-full w-full overflow-hidden flex items-center justify-center bg-gray-100"
				>
					<img
						src={slide.blurUrl}
						alt="{slide.title} blur"
						class="absolute inset-0 object-cover w-full h-full filter blur-xl scale-110 opacity-70"
						onerror={handleImgError}
					/>
					<img
						src={slide.imageUrl}
						alt={slide.title}
						class="absolute inset-0 w-full h-full object-contain transition-transform duration-1000 group-hover:scale-105 z-10"
						onerror={handleImgError}
					/>
					<div
						class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent z-20 flex flex-col justify-end p-6 lg:p-8 pointer-events-none"
					>
						<span
							class="glass-effect text-white text-[8px] lg:text-[10px] font-bold px-3 py-1 rounded-full mb-2 uppercase tracking-widest w-fit"
							>{slide.category}</span
						>
						<h2 class="text-xl lg:text-3xl font-black text-white leading-tight mb-1">
							{slide.title}
						</h2>
						<p class="text-gray-200 text-[10px] lg:text-sm max-w-md font-light line-clamp-2">
							{slide.description}
						</p>
					</div>
				</div>
			{/each}
		</div>
	</div>
</div>
