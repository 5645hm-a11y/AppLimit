#ifndef SAFETIMEGUARD_GRAYSCALE_CONTROLLER_H
#define SAFETIMEGUARD_GRAYSCALE_CONTROLLER_H

#include <string>
#include <vector>
#include <ctime>

namespace SafeTimeGuard {

struct GrayscaleRule {
    std::string id;
    std::string packageName;
    std::vector<int> days;  // 1=Mon, 7=Sun
    int startHour;
    int startMinute;
    int endHour;
    int endMinute;
    float intensity;  // 0.0f to 1.0f
    bool isActive;
};

class GrayscaleController {
public:
    GrayscaleController();
    ~GrayscaleController();
    
    // Rule management
    bool addGrayscaleRule(const GrayscaleRule& rule);
    bool removeGrayscaleRule(const std::string& ruleId);
    bool updateGrayscaleRule(const GrayscaleRule& rule);
    
    // Check if grayscale should be applied
    bool shouldApplyGrayscale(const std::string& packageName, time_t currentTime);
    
    // Get grayscale intensity
    float getGrayscaleIntensity(const std::string& packageName, time_t currentTime);
    
    // Global grayscale control
    bool enableGlobalGrayscale(float intensity);
    bool disableGlobalGrayscale();
    bool isGlobalGrayscaleEnabled() const;
    float getGlobalIntensity() const;
    
    // Validate rule
    bool validateGrayscaleRule(const GrayscaleRule& rule);
    
    // Get all active rules
    std::vector<GrayscaleRule> getActiveRules(time_t currentTime);
    
private:
    std::vector<GrayscaleRule> rules;
    bool globalGrayscaleEnabled;
    float globalIntensity;
    
    // Helper methods
    bool isTimeInGrayscaleRange(const GrayscaleRule& rule, time_t currentTime);
    bool isDayInGrayscaleSet(const GrayscaleRule& rule, time_t currentTime);
};

}  // namespace SafeTimeGuard

#endif  // SAFETIMEGUARD_GRAYSCALE_CONTROLLER_H
