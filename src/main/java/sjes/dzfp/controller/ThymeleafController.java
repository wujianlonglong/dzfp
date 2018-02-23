package sjes.dzfp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ThymeleafController {

    @RequestMapping("/")
    public String invoiceSearch(Model model) {
        return "index";
    }

    @RequestMapping("/{searchNo}")
    public String invoiceSearch(@PathVariable("searchNo") String searchNo, Model model) {
        return "details";
    }

}
