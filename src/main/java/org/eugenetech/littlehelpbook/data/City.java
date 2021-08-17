package org.eugenetech.littlehelpbook.data;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class City extends AirtableBase {
    protected Set<Resource> resources = new HashSet<>();

    @Override
    public final int hashCode() {
        return this.id.hashCode();
    }
}
