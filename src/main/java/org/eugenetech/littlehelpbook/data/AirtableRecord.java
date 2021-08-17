package org.eugenetech.littlehelpbook.data;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Data
@Slf4j
public class AirtableRecord {
    protected String id;
    protected JsonNode jsonNode;
    protected String createdTime;
}
