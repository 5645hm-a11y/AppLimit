#ifndef SAFETIMEGUARD_BLOCK_RULE_ENGINE_H
#define SAFETIMEGUARD_BLOCK_RULE_ENGINE_H

#include <string>
#include <vector>
#include <set>
#include <memory>
#include <ctime>

namespace SafeTimeGuard {

enum class BlockType {
    APP_BLOCK,    // Block the app
    GRAYSCALE     // Apply grayscale filter
};

struct BlockRule {
    std::string id;
    std::string packageName;
    std::set<int> days;           // 1=Monday ... 7=Sunday
    int startHour;
    int startMinute;
    int endHour;
    int endMinute;
    BlockType type;
    bool isActive;
    
    BlockRule() : startHour(0), startMinute(0), endHour(0), endMinute(0), 
                  type(BlockType::APP_BLOCK), isActive(true) {}
};

class BlockRuleEngine {
public:
    BlockRuleEngine();
    ~BlockRuleEngine();
    
    // Rule management
    bool addRule(const BlockRule& rule);
    bool removeRule(const std::string& ruleId);
    bool updateRule(const BlockRule& rule);
    BlockRule* getRule(const std::string& ruleId);
    std::vector<BlockRule> getAllRules() const;
    
    // Check if app should be blocked
    bool shouldBlockApp(const std::string& packageName, time_t currentTime);
    
    // Get block type for app
    BlockType getBlockType(const std::string& packageName, time_t currentTime);
    
    // Validate rule
    bool validateRule(const BlockRule& rule);
    
    // Clear all rules
    void clearRules();
    
    // Get rules for specific package
    std::vector<BlockRule> getRulesForPackage(const std::string& packageName) const;
    
private:
    std::vector<BlockRule> rules;
    
    // Helper methods
    bool isCurrentTimeInRange(const BlockRule& rule, time_t currentTime);
    bool isCurrentDayInSet(const BlockRule& rule, time_t currentTime);
    int getCurrentDayOfWeek(time_t currentTime);
};

}  // namespace SafeTimeGuard

#endif  // SAFETIMEGUARD_BLOCK_RULE_ENGINE_H
