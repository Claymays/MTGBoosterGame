package com.mays.mtgboostergame.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.jayway.jsonpath.JsonPath;
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
    private String alternateFacePng;

    @JsonProperty("image_uris")
    public void unpackImg(Map<String, String> images) {
        this.png = images.get("png");
    }

    @JsonProperty("card_faces")
    public void unpackDualFace(Object faces) {
        this.png = JsonPath.read(faces, "$[0].image_uris.png");
        this.alternateFacePng = JsonPath.read(faces, "$[1].image_uris.png");
    }


}
