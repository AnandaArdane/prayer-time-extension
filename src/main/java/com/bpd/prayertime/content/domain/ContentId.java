package com.bpd.prayertime.content.domain;

import java.util.Objects;

public record ContentId(Long id) {

    public static final long NEW_ID_STATE = 0L;

    public ContentId {
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }
    }

    public boolean isNew() {
        return id == NEW_ID_STATE;
    }

    public static ContentId setAsNew() {
        return new ContentId(NEW_ID_STATE);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ContentId contentId = (ContentId) object;
        return Objects.equals(id, contentId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
