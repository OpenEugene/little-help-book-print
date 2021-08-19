package org.eugenetech.littlehelpbook.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class AirtableBase {
    public String id;

    public String name_en;
    public String name_es;

    /**
     * Get the spanish Name, or the English name if there is no Spanish name
     *
     * @return
     */
    public String getName_es() {
        if (name_es != null)
            return name_es;
        return name_en;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AirtableBase))
            return false;
        return this.id == ((AirtableBase) o).id;
    }
}
