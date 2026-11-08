export interface AladhanTimings {
	Fajr: string;
	Sunrise: string;
	Dhuhr: string;
	Asr: string;
	Sunset: string;
	Maghrib: string;
	Isha: string;
	Imsak: string;
	Midnight: string;
	Firstthird: string;
	Lastthird: string;
}

interface AladhanResponse {
	code: number;
	status: string;
	data: {
		timings: AladhanTimings;
	};
}

const ALADHAN_BASE_URL = 'https://api.aladhan.com/v1';
const KEMENAG_METHOD = 20;

function formatToAladhanDate(dateStr: string): string {
	// Input is 'YYYY-MM-DD', convert to 'DD-MM-YYYY'
	const [year, month, day] = dateStr.split('-');
	return `${day}-${month}-${year}`;
}

export async function getTimingsByCoordinate(
	latitude: number,
	longitude: number,
	dateStr: string
): Promise<AladhanTimings> {
	const formattedDate = formatToAladhanDate(dateStr);
	const url = `${ALADHAN_BASE_URL}/timings/${formattedDate}?latitude=${latitude}&longitude=${longitude}&method=${KEMENAG_METHOD}`;

	const response = await fetch(url);
	if (!response.ok) {
		throw new Error(`Failed to fetch timings from Aladhan API: ${response.statusText}`);
	}

	const body = (await response.json()) as AladhanResponse;
	return body.data.timings;
}

export async function getTimingsByCity(
	city: string,
	dateStr: string
): Promise<AladhanTimings> {
	const formattedDate = formatToAladhanDate(dateStr);
	const url = `${ALADHAN_BASE_URL}/timingsByCity/${formattedDate}?country=ID&city=${encodeURIComponent(
		city
	)}&method=${KEMENAG_METHOD}`;

	const response = await fetch(url);
	if (!response.ok) {
		throw new Error(`Failed to fetch timings from Aladhan API by city: ${response.statusText}`);
	}

	const body = (await response.json()) as AladhanResponse;
	return body.data.timings;
}
