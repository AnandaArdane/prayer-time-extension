import { Router } from "express";
import { getTimingsByCoordinate, getTimingsByCity } from "../services/aladhan";
import { calculateHijriDate } from "../services/hijri";

const router = Router();

const formatTime = (t: string) => t.substring(0, 5);

router.get("/by-city", async (req, res) => {
    try {
        const { name, date } = req.query;

        if (!name || !date) {
            return res.status(400).json({ message: "City name and date (YYYY-MM-DD) are required" });
        }

        const timings = await getTimingsByCity(name as string, date as string);

        const maghrib = formatTime(timings.Maghrib);
        const hijriDate = calculateHijriDate(date as string, maghrib);

        return res.json({
            fajr: formatTime(timings.Fajr),
            dhuhr: formatTime(timings.Dhuhr),
            asr: formatTime(timings.Asr),
            maghrib: maghrib,
            isha: formatTime(timings.Isha),
            hijriDate: hijriDate
        });
    } catch (error: any) {
        console.error("Error fetching prayer times by city:", error);
        return res.status(500).json({ message: error.message || "Failed to fetch prayer times" });
    }
});

router.get("/by-coordinate", async (req, res) => {
    try {
        const { latitude, longitude, date } = req.query;

        if (!latitude || !longitude || !date) {
            return res.status(400).json({ message: "Latitude, longitude, and date (YYYY-MM-DD) are required" });
        }

        const lat = parseFloat(latitude as string);
        const lon = parseFloat(longitude as string);

        const timings = await getTimingsByCoordinate(lat, lon, date as string);

        const maghrib = formatTime(timings.Maghrib);
        const hijriDate = calculateHijriDate(date as string, maghrib);

        return res.json({
            fajr: formatTime(timings.Fajr),
            dhuhr: formatTime(timings.Dhuhr),
            asr: formatTime(timings.Asr),
            maghrib: maghrib,
            isha: formatTime(timings.Isha),
            hijriDate: hijriDate
        });
    } catch (error: any) {
        console.error("Error fetching prayer times by coordinate:", error);
        return res.status(500).json({ message: error.message || "Failed to fetch prayer times" });
    }
});

export default router;
