export function calculateHijriDate(dateStr: string, maghribTimeStr: string): string {
    const [year, month, day] = dateStr.split('-').map(Number);
    const date = new Date(year, month - 1, day);

    // Get today's date in local timezone
    const today = new Date();
    const isToday = 
        date.getFullYear() === today.getFullYear() &&
        date.getMonth() === today.getMonth() &&
        date.getDate() === today.getDate();

    if (isToday) {
        const [mHour, mMinute] = maghribTimeStr.split(':').map(Number);
        const nowHour = today.getHours();
        const nowMinute = today.getMinutes();

        // If current time is after Maghrib, shift Hijri date by 1 day
        if (nowHour > mHour || (nowHour === mHour && nowMinute >= mMinute)) {
            date.setDate(date.getDate() + 1);
        }
    }

    // Format to Hijri (Islamic Umm al-Qura) calendar in Indonesian (id-ID)
    const formatter = new Intl.DateTimeFormat('id-ID-u-ca-islamic-umalqura', {
        weekday: 'long',
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    });

    let formatted = formatter.format(date);
    // Replace "Minggu" with "Ahad" to match the original Java implementation
    formatted = formatted.replace('Minggu', 'Ahad');

    return formatted;
}
