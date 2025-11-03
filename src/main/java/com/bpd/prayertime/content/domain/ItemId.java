package com.bpd.prayertime.content.domain;

import java.util.Objects;

public record ItemId(Long id) {
    public static final long NEW_ID_STATE = 0L;

    public ItemId {
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }
    }

    public static ItemId setAsNew() {
        return new ItemId(NEW_ID_STATE);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ItemId itemId = (ItemId) object;
        return Objects.equals(id, itemId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
