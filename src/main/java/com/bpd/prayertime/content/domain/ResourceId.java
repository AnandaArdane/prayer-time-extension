package com.bpd.prayertime.content.domain;

import java.util.Objects;

public record ResourceId(Long id) {

    public static final long NEW_ID_STATE = 0L;

    public ResourceId {
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }
    }

    public boolean isNew() {
        return id == NEW_ID_STATE;
    }

    public static ResourceId setAsNew() {
        return new ResourceId(NEW_ID_STATE);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ResourceId that = (ResourceId) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
