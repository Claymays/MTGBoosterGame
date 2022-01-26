package com.mays.mtgboostergame.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class PageController {

    @GetMapping("card")
    public String cardSearch() {
        return "card";
    }

    @GetMapping("login")
    public String homePage() {
        return "login";
    }

    @GetMapping("userPage")
    public String userPage() {
        return "userPage";
    }

    @GetMapping("deck")
    public String deckPage() {
        return "deckPage";
    }

}
