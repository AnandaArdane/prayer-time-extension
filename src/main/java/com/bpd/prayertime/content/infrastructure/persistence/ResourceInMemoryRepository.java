package com.bpd.prayertime.content.infrastructure.persistence;

import com.bpd.prayertime.content.domain.Resource;
import com.bpd.prayertime.content.domain.ResourceId;
import com.bpd.prayertime.content.domain.ResourceRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ResourceInMemoryRepository implements ResourceRepository {

    private final List<Resource> resources = new ArrayList<>();
    private long count = 0;

    @Override
    public Resource save(Resource resource) {
        boolean isNew = resource.id().isNew();
        if (isNew) {
            count++;
            Resource newResource = new Resource(new ResourceId(count), resource.file());
            resources.add(newResource);
            return newResource;
        }
        Resource matchedResource = resources.stream().filter(it -> Objects.equals(it.id(), resource.id()))
                .findFirst()
                .orElseThrow();
        resources.remove(matchedResource);
        resources.add(resource);
        return resource;
    }

    @Override
    public boolean exist(ResourceId resourceId) {
        return resources.stream().anyMatch(it -> Objects.equals(resourceId, it.id()));
    }
}
