package com.mays.mtgboostergame.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class BulkData {
    private String name;
    private String dataLocation;

    @JsonProperty("data")
    public void mapper(List<Map<String, String>> bulkJson) {
        this.name = (String) bulkJson.get(0).get("name");
        this.dataLocation = (String) bulkJson.get(0).get("download_uri");
    }
}
