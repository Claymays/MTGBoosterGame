package com.mays.mtgboostergame.Data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ScryFallCard {
    private UUID id;
    private String name;
    @SerializedName("mana_cost")
    private String manaCost;
    @SerializedName("type_line")
    private String typeLine;
    private String rarity;
    @SerializedName("set_name")
    private String setName;
    @SerializedName("oracle_text")
    private String oracleText;

}
