package com.lordbritishix.opendnshomework.urlshortener.controller;

import java.net.URI;
import java.util.Optional;
import com.lordbritishix.opendnshomework.urlshortener.model.UrlMap;
import com.lordbritishix.opendnshomework.urlshortener.service.PhishTankService;
import com.lordbritishix.opendnshomework.urlshortener.service.ShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {
    private ShortenerService shortenerService;
    private PhishTankService phishTankService;

    @Autowired
    public HomeController(ShortenerService shortenerService, PhishTankService phishTankService) {
        this.shortenerService = shortenerService;
        this.phishTankService = phishTankService;
    }

    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping("/pagenotfound")
    public String pageNotFound() {
        return "pagenotfound";
    }

    @RequestMapping("/phish")
    public String phish() {
        return "phish";
    }

    @RequestMapping("/{id}")
    public RedirectView redirect(@PathVariable String id) {
        RedirectView redirectView = new RedirectView();

        Optional<UrlMap> longUrl = shortenerService.getShortUrl(id);

        if (!longUrl.isPresent()) {
            redirectView.setUrl("pagenotfound");
        } else {
            String url = longUrl.get().getOriginalUrl();
            if (phishTankService.isPhish(URI.create(url))) {
                redirectView.setUrl("phish");
            } else {
                redirectView.setUrl(longUrl.get().getOriginalUrl());
            }
        }

        return redirectView;
    }
}
