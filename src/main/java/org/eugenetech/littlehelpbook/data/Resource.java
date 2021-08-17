package org.eugenetech.littlehelpbook.data;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Resource extends AirtableBase {
    protected Set<Subcategory> subcategories = new HashSet<>();
    protected Set<Category> categories = new HashSet<>();
    protected Set<City> cities = new HashSet<>();

    protected String phone;
    protected String address;
    protected String url;
    protected String email;

    protected String description_en;
    protected String description_es;
    public String getDescription_es() {
        if (description_es != null && description_es.length() > 0)
            return description_es;
        return description_en;
    }

    protected boolean wheelchair = false;
    protected boolean languageHelp = false;

    protected String hours_en;
    protected String hours_es;

    public String getHours_es() {
        if (hours_es != null && hours_es.length() > 0)
            return hours_es;
        return hours_en;
    }

    protected Set<Schedule> schedules = new HashSet<>();

    @Override
    public final int hashCode() {
        return this.id.hashCode();
    }
}
