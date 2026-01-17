<script lang="ts">
    import type { SidebarEvent } from '$lib/server/services/prayerService';

    interface Props {
        events: SidebarEvent[];
    }

    let { events }: Props = $props();

    function handleImgError(e: Event) {
        const target = e.target as HTMLImageElement;
        target.onerror = null;
        target.src = 'https://placehold.co/200x140/e2dbd1/797166?text=Image';
    }
</script>

<aside class="lg:col-span-4 flex flex-col bg-white p-6 rounded-[1.5rem] lg:rounded-[2.5rem] shadow-lg border border-gray-50 self-stretch">
    <div class="flex items-center justify-between mb-5">
        <h3 class="text-[11px] font-black text-theme-primary-700 uppercase tracking-widest">Jadwal Kajian Terdekat</h3>
        <div class="flex items-center gap-2">
            <span class="relative flex h-2 w-2">
                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"></span>
                <span class="relative inline-flex rounded-full h-2 w-2 bg-red-500"></span>
            </span>
            <span class="text-[9px] font-black text-red-600 uppercase tracking-tighter">Aktif</span>
        </div>
    </div>
    
    <div class="scrollable-sidebar space-y-4 flex-grow pr-1">
        {#each events as event, i}
            <!-- Item -->
            <div class="group cursor-pointer flex gap-4 items-center {i > 0 ? 'pt-4 border-t border-gray-50' : ''}">
                <div class="relative w-20 h-14 shrink-0 rounded-xl overflow-hidden shadow-sm bg-gray-100">
                    <img src={event.imageUrl} alt={event.title} class="w-full h-full object-cover" onerror={handleImgError}>
                </div>
                <div>
                    <p class="text-[9px] font-bold text-theme-primary-600 uppercase mb-0.5">{event.dayTime}</p>
                    <h4 class="font-bold text-xs text-gray-800 leading-tight group-hover:text-theme-primary-700 transition">{event.title}</h4>
                </div>
            </div>
        {/each}
    </div>
</aside>
