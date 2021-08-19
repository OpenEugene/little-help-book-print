package org.eugenetech.littlehelpbook.data;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Data
@Slf4j
public class CatSubcat extends AirtableBase {
    public Category category;
    public Subcategory subcategory;

    public Set<Resource> resources = new HashSet<>();

    public List<Resource> getResources_en() {
        final List<Resource> _resources = new Vector<>();
        _resources.addAll(this.resources);
        Collections.sort(_resources, Comparator.comparing(Resource::getName_en));
        return _resources;
    }

    public List<Resource> getResources_es() {
        final List<Resource> _resources = new Vector<>();
        _resources.addAll(this.resources);
        Collections.sort(_resources, Comparator.comparing(Resource::getName_es));
        return _resources;
    }

    public void writeInfo() {
        log.info(this.resources.size() + " Resources in CatSubcat " + this.getName_en());
    }

    @Override
    public final int hashCode() {
        return this.id.hashCode();
    }
}
