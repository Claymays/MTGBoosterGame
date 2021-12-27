package com.mays.mtgboostergame.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MagicIMG {
    private String png;

    public MagicIMG() {}

    public MagicIMG(String png) {
        this.png = png;
    }

    public String getPng() {
        return png;
    }

    public void setPng(String png) {
        this.png = png;
    }
}
