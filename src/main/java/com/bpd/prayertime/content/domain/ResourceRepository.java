package com.bpd.prayertime.content.domain;

public interface ResourceRepository {
    Resource save(Resource resource);

    boolean exist(ResourceId resourceId);
}
