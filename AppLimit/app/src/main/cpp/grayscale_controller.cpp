#include "grayscale_controller.h"
#include <algorithm>
#include <ctime>

namespace SafeTimeGuard {

GrayscaleController::GrayscaleController() 
    : globalGrayscaleEnabled(false), globalIntensity(0.0f) {}

GrayscaleController::~GrayscaleController() {}

bool GrayscaleController::addGrayscaleRule(const GrayscaleRule& rule) {
    if (!validateGrayscaleRule(rule)) {
        return false;
    }
    
    // Check for duplicate ID
    for (const auto& existing : rules) {
        if (existing.id == rule.id) {
            return false;
        }
    }
    
    rules.push_back(rule);
    return true;
}

bool GrayscaleController::removeGrayscaleRule(const std::string& ruleId) {
    auto it = std::find_if(rules.begin(), rules.end(),
        [&ruleId](const GrayscaleRule& rule) { return rule.id == ruleId; });
    
    if (it != rules.end()) {
        rules.erase(it);
        return true;
    }
    return false;
}

bool GrayscaleController::updateGrayscaleRule(const GrayscaleRule& rule) {
    if (!validateGrayscaleRule(rule)) {
        return false;
    }
    
    for (auto& existing : rules) {
        if (existing.id == rule.id) {
            existing = rule;
            return true;
        }
    }
    return false;
}

bool GrayscaleController::shouldApplyGrayscale(const std::string& packageName, time_t currentTime) {
    if (globalGrayscaleEnabled) {
        return true;
    }
    
    for (const auto& rule : rules) {
        if (!rule.isActive) continue;
        if (rule.packageName != packageName) continue;
        
        if (isTimeInGrayscaleRange(rule, currentTime) && 
            isDayInGrayscaleSet(rule, currentTime)) {
            return true;
        }
    }
    return false;
}

float GrayscaleController::getGrayscaleIntensity(const std::string& packageName, time_t currentTime) {
    if (globalGrayscaleEnabled) {
        return globalIntensity;
    }
    
    for (const auto& rule : rules) {
        if (!rule.isActive) continue;
        if (rule.packageName != packageName) continue;
        
        if (isTimeInGrayscaleRange(rule, currentTime) && 
            isDayInGrayscaleSet(rule, currentTime)) {
            return rule.intensity;
        }
    }
    return 0.0f;
}

bool GrayscaleController::enableGlobalGrayscale(float intensity) {
    if (intensity < 0.0f || intensity > 1.0f) {
        return false;
    }
    
    globalGrayscaleEnabled = true;
    globalIntensity = intensity;
    return true;
}

bool GrayscaleController::disableGlobalGrayscale() {
    globalGrayscaleEnabled = false;
    globalIntensity = 0.0f;
    return true;
}

bool GrayscaleController::isGlobalGrayscaleEnabled() const {
    return globalGrayscaleEnabled;
}

float GrayscaleController::getGlobalIntensity() const {
    return globalIntensity;
}

bool GrayscaleController::validateGrayscaleRule(const GrayscaleRule& rule) {
    // Validate hour and minute ranges
    if (rule.startHour < 0 || rule.startHour > 23) return false;
    if (rule.startMinute < 0 || rule.startMinute > 59) return false;
    if (rule.endHour < 0 || rule.endHour > 23) return false;
    if (rule.endMinute < 0 || rule.endMinute > 59) return false;
    
    // Validate intensity
    if (rule.intensity < 0.0f || rule.intensity > 1.0f) return false;
    
    // Validate package name
    if (rule.packageName.empty()) return false;
    
    // Validate days
    if (rule.days.empty()) return false;
    for (int day : rule.days) {
        if (day < 1 || day > 7) return false;
    }
    
    return true;
}

std::vector<GrayscaleRule> GrayscaleController::getActiveRules(time_t currentTime) {
    std::vector<GrayscaleRule> activeRules;
    
    for (const auto& rule : rules) {
        if (rule.isActive && isTimeInGrayscaleRange(rule, currentTime) && 
            isDayInGrayscaleSet(rule, currentTime)) {
            activeRules.push_back(rule);
        }
    }
    
    return activeRules;
}

bool GrayscaleController::isTimeInGrayscaleRange(const GrayscaleRule& rule, time_t currentTime) {
    struct tm* timeinfo = std::localtime(&currentTime);
    int currentHour = timeinfo->tm_hour;
    int currentMinute = timeinfo->tm_min;
    
    int startTotalMinutes = rule.startHour * 60 + rule.startMinute;
    int endTotalMinutes = rule.endHour * 60 + rule.endMinute;
    int currentTotalMinutes = currentHour * 60 + currentMinute;
    
    // Handle ranges that cross midnight
    if (endTotalMinutes <= startTotalMinutes) {
        return (currentTotalMinutes >= startTotalMinutes) || 
               (currentTotalMinutes < endTotalMinutes);
    } else {
        return (currentTotalMinutes >= startTotalMinutes) && 
               (currentTotalMinutes < endTotalMinutes);
    }
}

bool GrayscaleController::isDayInGrayscaleSet(const GrayscaleRule& rule, time_t currentTime) {
    struct tm* timeinfo = std::localtime(&currentTime);
    int dayOfWeek = timeinfo->tm_wday;
    if (dayOfWeek == 0) dayOfWeek = 7;  // Sunday = 7
    
    return std::find(rule.days.begin(), rule.days.end(), dayOfWeek) != rule.days.end();
}

}  // namespace SafeTimeGuard
