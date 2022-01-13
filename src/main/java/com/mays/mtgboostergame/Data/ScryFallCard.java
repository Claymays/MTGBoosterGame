package com.mays.mtgboostergame.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ScryFallCard {
    private UUID id;
    private String name;
    @JsonProperty("mana_cost")
    private String manaCost;
    @JsonProperty("type_line")
    private String typeLine;
    private String rarity;
    @JsonProperty("set_name")
    private String setName;
    @JsonProperty("oracle_text")
    private String oracleText;
    private String png;

    @JsonProperty("image_uris")
    public void unpackImg(Map<String, Object> images) {
        this.png = (String) images.get("png");
    }


}
