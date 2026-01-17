export interface PrayerTime {
    name: string;
    time: string;
}

export interface Slide {
    id: number;
    title: string;
    description: string;
    category: string;
    imageUrl: string;
    blurUrl: string; // Using same or different image for blur effect
}

export interface SidebarEvent {
    id: number;
    dayTime: string; // e.g., "Selasa | Ba'da Isya"
    title: string;
    imageUrl: string;
    isActive?: boolean;
}

export interface LandingPageData {
    prayerTimes: PrayerTime[];
    slides: Slide[];
    sidebarEvents: SidebarEvent[];
}

export const getLandingPageData = async (): Promise<LandingPageData> => {
    // Helper to simulate network delay if needed, but for now just return direct
    return {
        prayerTimes: [
            { name: "Subuh", time: "04:45" },
            { name: "Dzuhur", time: "11:50" },
            { name: "Ashar", time: "15:10" },
            { name: "Maghrib", time: "18:05" },
            { name: "Isya", time: "19:20" }
        ],
        slides: [
            {
                id: 1,
                category: "Program Masjid",
                title: "Program Tahfidz Akhir Pekan",
                description: "Pendaftaran santri baru untuk kelas hafalan intensif setiap Sabtu dan Ahad.",
                imageUrl: "https://images.unsplash.com/photo-1590073844006-33379778ae09?auto=format&fit=crop&w=1200&q=80",
                blurUrl: "https://images.unsplash.com/photo-1590073844006-33379778ae09?auto=format&fit=crop&w=1200&q=80"
            },
            {
                id: 2,
                category: "Program Masjid",
                title: "Program Tahfidz Akhir Pekan",
                description: "Pendaftaran santri baru untuk kelas hafalan intensif setiap Sabtu dan Ahad.",
                imageUrl: "https://images.unsplash.com/photo-1590073844006-33379778ae09?auto=format&fit=crop&w=1200&q=80",
                blurUrl: "https://images.unsplash.com/photo-1590073844006-33379778ae09?auto=format&fit=crop&w=1200&q=80"
            },
            {
                id: 3,
                category: "Program Masjid",
                title: "Program Tahfidz Akhir Pekan",
                description: "Pendaftaran santri baru untuk kelas hafalan intensif setiap Sabtu dan Ahad.",
                imageUrl: "https://images.unsplash.com/photo-1590073844006-33379778ae09?auto=format&fit=crop&w=1200&q=80",
                blurUrl: "https://images.unsplash.com/photo-1590073844006-33379778ae09?auto=format&fit=crop&w=1200&q=80"
            }
        ],
        sidebarEvents: [
            {
                id: 1,
                dayTime: "Selasa | 13:00 WIB",
                title: "Kitab Bulughul Maram",
                imageUrl: "https://images.unsplash.com/photo-1518991033282-3e28d488f723?auto=format&fit=crop&w=200&q=80",
                isActive: true
            },
            {
                id: 2,
                dayTime: "Rabu | 09:00 WIB",
                title: "Kajian Muslimah: Fiqih Wanita",
                imageUrl: "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=200&q=80"
            },
            {
                id: 3,
                dayTime: "Jumat | 16:00 WIB",
                title: "Tahsin Al-Qur'an Dewasa",
                imageUrl: "https://images.unsplash.com/photo-1551041777-ed0764a0028d?auto=format&fit=crop&w=200&q=80"
            }
        ]
    };
};
