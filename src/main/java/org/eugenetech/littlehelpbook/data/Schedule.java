package org.eugenetech.littlehelpbook.data;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Schedule extends AirtableBase {
    protected Resource resource;

    protected String freq;
    protected String byday;
    protected String opens_at;
    protected String closes_at;
    protected String hours;

    @Override
    public final int hashCode() {
        return this.id.hashCode();
    }
}
