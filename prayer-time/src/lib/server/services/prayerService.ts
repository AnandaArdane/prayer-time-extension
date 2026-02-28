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
    blurUrl: string;
}

export interface SidebarEvent {
    id: number;
    dayTime: string;
    title: string;
    imageUrl: string;
    isActive?: boolean;
}

export interface PrayerData {
    prayerTimes: PrayerTime[];
    hijriDate?: string;
    error?: string;
}

interface OneDayPrayerTimeDto {
    fajr: string;
    dhuhr: string;
    asr: string;
    maghrib: string;
    isha: string;
    hijriDate: string;
}

interface ContentResponseDto {
    id: number;
    title: string;
    active: boolean;
    url: string;
}

export const getPrayerData = async (lat?: number, long?: number, referenceDate?: Date): Promise<PrayerData> => {
    let prayerTimes: PrayerTime[] = [];
    let error: string | undefined;
    let hijriDate: string | undefined;

    try {
        // Default to Jakarta, Today (or simulated time)
        const latitude = lat ?? -6.1751;
        const longitude = long ?? 106.8650;
        const now = referenceDate ?? new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const dateStr = `${year}-${month}-${day}`;

        const response = await fetch(`http://localhost:8080/api/prayers/by-coordinate?latitude=${latitude}&longitude=${longitude}&date=${dateStr}`);

        if (response.ok) {
            const data: OneDayPrayerTimeDto = await response.json();
            // Map HH:mm:ss to HH:mm
            const formatTime = (t: string) => t.substring(0, 5);
            hijriDate = data.hijriDate;

            prayerTimes = [
                { name: "Subuh", time: formatTime(data.fajr) },
                { name: "Dzuhur", time: formatTime(data.dhuhr) },
                { name: "Ashar", time: formatTime(data.asr) },
                { name: "Maghrib", time: formatTime(data.maghrib) },
                { name: "Isya", time: formatTime(data.isha) }
            ];
        } else {
            console.error("Failed to fetch prayer times:", response.status);
            error = "Gagal memuat jadwal sholat. Silakan cek koneksi server.";
            prayerTimes = [];
        }
    } catch (e) {
        console.error("Error fetching prayer times:", e);
        error = "Gagal terhubung ke server.";
        prayerTimes = [];
    }

    return {
        prayerTimes,
        hijriDate,
        error
    };
};

export const getSlides = async (): Promise<Slide[]> => {
    // Default slides (fallback)
    let slides: Slide[] = [
        {
            id: 1,
            category: "-",
            title: "-",
            description: "Konten tidak tersedia",
            imageUrl: "https://images.unsplash.com/photo-1590073844006-33379778ae09?auto=format&fit=crop&w=1200&q=80",
            blurUrl: "https://images.unsplash.com/photo-1590073844006-33379778ae09?auto=format&fit=crop&w=1200&q=80"
        }
    ];

    try {
        const contentResponse = await fetch('http://localhost:8080/api/contents/active');
        if (contentResponse.ok) {
            const contents: ContentResponseDto[] = await contentResponse.json();
            if (contents.length > 0) {
                slides = contents.map(content => ({
                    id: content.id,
                    title: content.title,
                    description: content.title,
                    category: "-",
                    imageUrl: `http://localhost:8080${content.url}`,
                    blurUrl: `http://localhost:8080${content.url}`
                }));
            }
        } else {
            console.error("Failed to fetch active contents:", contentResponse.status);
        }
    } catch (e) {
        console.error("Error fetching active contents:", e);
    }

    return slides;
};

export const getSidebarEvents = (): SidebarEvent[] => {
    return [
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
    ];
};
