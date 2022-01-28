package com.josephcoder.virustracker.controller;

import com.josephcoder.virustracker.models.Location;
import com.josephcoder.virustracker.services.VirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    VirusDataService virusDataService;

    @GetMapping("/")
    public String home(Model model){
        //Shouldn't be here
        List<Location> allStats = virusDataService.getAllStates();
        int totalCasesWorldwide = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDelta()).sum();

        model.addAttribute("locationStatistics", virusDataService.getAllStates());
        model.addAttribute("totalCasesWorldwide", totalCasesWorldwide);
        model.addAttribute("totalNewCases", totalNewCases);
        return "home";
    }

}
