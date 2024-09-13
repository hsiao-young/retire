package com.example.retirementcalculator.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class RetirementService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final LocalDate POLICY_START_DATE = LocalDate.of(2025, 1, 1);

    public String calculateRetirementDate(String birthDateStr, String gender, int originalRetirementAge) {
        try {
            // 将输入的出生日期字符串转换为 LocalDate 对象
            LocalDate birthDate = LocalDate.parse(birthDateStr, FORMATTER);

            // 计算原法定退休日期
            LocalDate originalRetirementDate = birthDate.plusYears(originalRetirementAge);

            // 如果在2025年1月1日之前已达到退休年龄，则不受新政策影响
            if (originalRetirementDate.isBefore(POLICY_START_DATE)) {
                return originalRetirementDate.format(FORMATTER);
            }

            // 根据性别和原退休年龄，确定延迟规则
            int delayIntervalMonths;
            int delayAmountMonths;
            int maxRetirementAge;

            if ("男".equals(gender)) {
                delayIntervalMonths = 4;   // 每4个月延迟1个月
                delayAmountMonths = 1;
                maxRetirementAge = 63;
            } else if ("女".equals(gender) && originalRetirementAge == 55) {
                // 女干部
                delayIntervalMonths = 4;
                delayAmountMonths = 1;
                maxRetirementAge = 58;
            } else if ("女".equals(gender) && originalRetirementAge == 50) {
                // 女工人
                delayIntervalMonths = 2;
                delayAmountMonths = 1;
                maxRetirementAge = 55;
            } else {
                // 其他情况，按照原退休年龄计算
                return originalRetirementDate.format(FORMATTER);
            }

            // 计算从政策实施到原退休日期的总月份数
            int yearsDifference = originalRetirementDate.getYear() - POLICY_START_DATE.getYear();
            int monthsDifference = originalRetirementDate.getMonthValue() - POLICY_START_DATE.getMonthValue();
            int totalMonthsAfterPolicy = yearsDifference * 12 + monthsDifference;

            // 计算需要延迟的月份数
            int delays = (totalMonthsAfterPolicy / delayIntervalMonths) * delayAmountMonths;

            // 计算新的退休日期
            LocalDate newRetirementDate = originalRetirementDate.plusMonths(delays);

            // 计算新的退休年龄
            long newRetirementAge = ChronoUnit.YEARS.between(birthDate, newRetirementDate);

            // 确保退休年龄不超过目标退休年龄
            if (newRetirementAge > maxRetirementAge) {
                newRetirementDate = birthDate.plusYears(maxRetirementAge);
            }

            return newRetirementDate.format(FORMATTER);

        } catch (Exception e) {
            return "计算退休日期时出错，请检查输入。";
        }
    }
}
