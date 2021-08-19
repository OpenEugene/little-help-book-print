package org.eugenetech.littlehelpbook.data;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Data
@Slf4j
public class Category extends AirtableBase {
    public Set<Subcategory> subcategories = new HashSet<>();
    public Set<CatSubcat> catsubcats = new HashSet<>();
    public Set<Resource> resources = new HashSet<>();

    /*
    public List<Subcategory> getSubcategories_en() {
        final List<Subcategory> _subcategories = new Vector<>();
        _subcategories.addAll(this.subcategories);
        Collections.sort(_subcategories, Comparator.comparing(Subcategory::getName_en));
        return _subcategories;
    }

    public List<Subcategory> getSubcategories_es() {
        final List<Subcategory> _subcategories = new Vector<>();
        _subcategories.addAll(this.subcategories);
        Collections.sort(_subcategories, Comparator.comparing(Subcategory::getName_es));
        return _subcategories;
    }
    */

    public List<Subcategory> getSubcategories_en() {
        final List<Subcategory> _subcategories = new Vector<>();

        for (final CatSubcat catsubcat : this.catsubcats) {
            if (catsubcat.subcategory != null)
                _subcategories.add(catsubcat.subcategory);
        }
        Collections.sort(_subcategories, Comparator.comparing(Subcategory::getName_en));

        return _subcategories;
    }

    public List<Subcategory> getSubcategories_es() {
        final List<Subcategory> _subcategories = new Vector<>();

        for (final CatSubcat catsubcat : this.catsubcats) {
            if (catsubcat.subcategory != null)
                _subcategories.add(catsubcat.subcategory);
        }
        Collections.sort(_subcategories, Comparator.comparing(Subcategory::getName_es));

        return _subcategories;
    }


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
        log.info(this.subcategories.size() + " Subcategories in Category " + this.getName_en());
        log.info(this.resources.size() + " Resources in Category " + this.getName_en());
    }

    public boolean hasResources() {
        if (this.resources.size() > 0)
            return true;

        for (final Subcategory subcategory : this.subcategories)
            if (subcategory.hasResources())
                return true;

        return false;
    }

    @Override
    public final int hashCode() {
        return this.id.hashCode();
    }
}
