package com.bpd.prayertime.prayertime.domain;

public record Coordinate(Latitude latitude, Longitude longitude) {
    public Coordinate {
        if (latitude == null) {
            throw new IllegalArgumentException("Latitude should not be null");
        }

        if (longitude == null) {
            throw new IllegalArgumentException("Longitude should not be null");
        }
    }

    public record Latitude(Double value) {
        public Latitude {
            if (value == null) {
                throw new IllegalArgumentException("Latitude value should not be null");
            }

            if (value < -90 || value > 90) {
                throw new IllegalArgumentException("Invalid latitude");
            }
        }
    }

    public record Longitude(Double value) {
        public Longitude {
            if (value == null) {
                throw new IllegalArgumentException("Longitude value should not be null");
            }

            if (value < -180 || value > 180) {
                throw new IllegalArgumentException("Invalid latitude");
            }
        }
    }
}
