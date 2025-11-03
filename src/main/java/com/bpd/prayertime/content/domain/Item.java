package com.bpd.prayertime.content.domain;

public record Item(ItemId id, Integer sequence, ContentId contentId, ResourceId resourceId) {
    public Item {
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }

        if (sequence == null) {
            throw new IllegalArgumentException("Sequence can't be null");
        }

        if (sequence <= 0) {
            throw new IllegalArgumentException("Sequence is invalid");
        }

        if (contentId == null) {
            throw new IllegalArgumentException("Content ID can't be null");
        }

        if (resourceId == null) {
            throw new IllegalArgumentException("Resource ID can't be null");
        }
    }

    public static Item createNew(ContentId contentId, ResourceId resourceId, Integer sequence) {
        return new Item(ItemId.setAsNew(), sequence, contentId, resourceId);
    }
}
