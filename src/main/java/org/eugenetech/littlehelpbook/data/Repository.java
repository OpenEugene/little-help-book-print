package org.eugenetech.littlehelpbook.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class Repository {
    private Map<String, Category> categories = new HashMap<>();
    private Map<String, City> cities = new HashMap<>();
    private Map<String, Resource> resources = new HashMap<>();
    private Map<String, Subcategory> subcategories = new HashMap<>();
    private Map<String, Schedule> schedules = new HashMap<>();

    // CITY

    public City addCity(final AirtableRecord record) {
        final City city = new City();
        city.id = record.getId();
        city.name_en = fieldAsText(record.jsonNode, "Name");
        city.name_es = fieldAsText(record.jsonNode, "Name");

        this.cities.put(city.id, city);

        return city;
    }

    public City getCity(final String id) {
        return this.cities.get(id);
    }

    public List<City> getCities_en() {
        final List<City> cities = new Vector<>();
        cities.addAll(this.cities.values());
        Collections.sort(cities, Comparator.comparing(City::getName_en));
        return cities;
    }

    public List<City> getCities_es() {
        final List<City> cities = new Vector<>();
        cities.addAll(this.cities.values());
        Collections.sort(cities, Comparator.comparing(City::getName_es));
        return cities;
    }

    // CATEGORY

    public Category addCategory(final AirtableRecord record) {
        final Category category = new Category();
        category.id = record.getId();
        category.name_en = fieldAsText(record.jsonNode, "Name");
        category.name_es = fieldAsText(record.jsonNode, "Name-ES");

        this.categories.put(category.id, category);

        return category;
    }

    public Category getCategory(final String id) {
        return this.categories.get(id);
    }

    public List<Category> getCategories_en() {
        final List<Category> categories = new Vector<>();
        categories.addAll(this.categories.values());
        Collections.sort(categories, Comparator.comparing(Category::getName_en));
        return categories;
    }

    public List<Category> getCategories_es() {
        final List<Category> categories = new Vector<>();
        categories.addAll(this.categories.values());
        Collections.sort(categories, Comparator.comparing(Category::getName_es));
        return categories;
    }

    // SUBCATEGORY

    public Subcategory addSubcategory(final AirtableRecord record) {
        final Subcategory subcategory = new Subcategory();
        subcategory.id = record.getId();
        subcategory.name_en = fieldAsText(record.jsonNode, "Name");
        subcategory.name_es = fieldAsText(record.jsonNode, "Name-ES");

        for (final String idCategory : fieldAsIDList(record.jsonNode, "Category")) {
            final Category category = this.getCategory(idCategory);
            subcategory.setCategory(category);
            category.getSubcategories().add(subcategory);
        }

        this.subcategories.put(subcategory.id, subcategory);

        return subcategory;
    }

    public Subcategory getSubcategory(final String id) {
        return this.subcategories.get(id);
    }

    public List<Subcategory> getSubcategories_en() {
        final List<Subcategory> _subcategories = new Vector<>();
        _subcategories.addAll(this.subcategories.values());
        Collections.sort(_subcategories, Comparator.comparing(Subcategory::getName_en));
        return _subcategories;
    }

    public List<Subcategory> getSubcategories_es() {
        final List<Subcategory> _subcategories = new Vector<>();
        _subcategories.addAll(this.subcategories.values());
        Collections.sort(_subcategories, Comparator.comparing(Subcategory::getName_es));
        return _subcategories;
    }

    // RESOURCE

    public Resource addResource(final AirtableRecord record) {
        final Resource resource = new Resource();
        resource.id = record.getId();

        resource.name_en = fieldAsText(record.jsonNode, "Name");
        resource.name_es = fieldAsText(record.jsonNode, "Name-ES");

        resource.description_en = fieldAsText(record.jsonNode, "Description");
        resource.description_es = fieldAsText(record.jsonNode, "Description-ES");

        resource.address = fieldAsText(record.jsonNode, "Physical Address");
        resource.phone = fieldAsText(record.jsonNode, "Phone Number");
        resource.url = fieldAsText(record.jsonNode, "Web address");

        resource.wheelchair = "y".equals(fieldAsText(record.jsonNode, "Wheelchair access (y)"));
        resource.languageHelp = "y".equals(fieldAsText(record.jsonNode, "Language Help (y)"));

        resource.hours_en = fieldAsText(record.jsonNode, "Hours of operation");
        resource.hours_es = fieldAsText(record.jsonNode, "Hours of operation-ES");

        for (final String idCity : fieldAsIDList(record.jsonNode, "City")) {
            final City city = this.getCity(idCity);
            city.getResources().add(resource);
            resource.cities.add(city);
        }

        for (final String idSubcategory : fieldAsIDList(record.jsonNode, "Subcategory")) {
            final Subcategory subcategory = this.getSubcategory(idSubcategory);
            subcategory.getResources().add(resource);
            resource.subcategories.add(subcategory);
        }

        if (resource.subcategories.size() == 0) {
            for (final String idCategory : fieldAsIDList(record.jsonNode, "Category")) {
                final Category category = this.getCategory(idCategory);
                category.getResources().add(resource);
                resource.categories.add(category);
            }
        }

        this.resources.put(resource.id, resource);

        return resource;
    }

    public Resource getResource(final String id) {
        return this.resources.get(id);
    }

    public List<Resource> getResources_en() {
        final List<Resource> _resources = new Vector<>();
        _resources.addAll(this.resources.values());
        Collections.sort(_resources, Comparator.comparing(Resource::getName_en));
        return _resources;
    }

    public List<Resource> getResources_es() {
        final List<Resource> _resources = new Vector<>();
        _resources.addAll(this.resources.values());
        Collections.sort(_resources, Comparator.comparing(Resource::getName_es));
        return _resources;
    }

    public Schedule getSchedule(final String id) {
        return this.schedules.get(id);
    }

    public List<Schedule> getSchedules_en() {
        final List<Schedule> _schedules = new Vector<>();
        _schedules.addAll(this.schedules.values());
        Collections.sort(_schedules, Comparator.comparing(Schedule::getName_en));
        return _schedules;
    }

    public List<Schedule> getSchedules_es() {
        final List<Schedule> _schedules = new Vector<>();
        _schedules.addAll(this.schedules.values());
        Collections.sort(_schedules, Comparator.comparing(Schedule::getName_es));
        return _schedules;
    }

    public void writeInfo() {
        log.info(categories.size() + " Categories");
        for (final Category category : this.categories.values()) {
            category.writeInfo();
        }

        log.info(subcategories.size() + " Subcategories");
        for (final Subcategory subcategory : this.subcategories.values()) {
            subcategory.writeInfo();
        }

        log.info(cities.size() + " Cities");

        log.info(resources.size() + " Resources");
    }

    /**
     * Static Methods
     **/

    private static String fieldAsText(final JsonNode node, final String field) {
        if (node.has(field)) {
            return node.get(field).asText();
        }
        return null;
    }

    private static Set<String> fieldAsIDList(final JsonNode node, final String field) {
        final Set<String> IDs = new HashSet<String>();

        if (node.has(field)) {
            for (final JsonNode idNode : (ArrayNode) node.get(field)) {
                IDs.add(idNode.asText());
            }
        }

        return IDs;
    }
}
