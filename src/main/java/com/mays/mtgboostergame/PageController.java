package com.mays.mtgboostergame;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@CrossOrigin(origins = "*")
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
