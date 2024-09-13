package com.example.retirementcalculator.controller;

import com.example.retirementcalculator.service.RetirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RetirementController {

    @Autowired
    private RetirementService retirementService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/calculate")
    public String calculateRetirement(@RequestParam("birthDate") String birthDate,
                                      @RequestParam("gender") String gender,
                                      @RequestParam("originalRetirementAge") String originalRetirementAgeStr,
                                      Model model) {
        // 输入验证
        if (birthDate == null || birthDate.isEmpty() ||
            gender == null || gender.isEmpty() ||
            originalRetirementAgeStr == null || originalRetirementAgeStr.isEmpty()) {
            model.addAttribute("error", "请填写所有字段。");
            return "index";
        }

        int originalRetirementAge;
        try {
            originalRetirementAge = Integer.parseInt(originalRetirementAgeStr);
            if (originalRetirementAge != 50 && originalRetirementAge != 55 && originalRetirementAge != 60) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            model.addAttribute("error", "原法定退休年龄必须为50、55或60。");
            return "index";
        }

        String retirementDate = retirementService.calculateRetirementDate(birthDate, gender, originalRetirementAge);
        model.addAttribute("retirementDate", retirementDate);
        return "result";
    }
}
