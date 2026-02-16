#include "block_rule_engine.h"
#include <algorithm>
#include <ctime>

namespace SafeTimeGuard {

BlockRuleEngine::BlockRuleEngine() {}

BlockRuleEngine::~BlockRuleEngine() {}

bool BlockRuleEngine::addRule(const BlockRule& rule) {
    if (!validateRule(rule)) {
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

bool BlockRuleEngine::removeRule(const std::string& ruleId) {
    auto it = std::find_if(rules.begin(), rules.end(),
        [&ruleId](const BlockRule& rule) { return rule.id == ruleId; });
    
    if (it != rules.end()) {
        rules.erase(it);
        return true;
    }
    return false;
}

bool BlockRuleEngine::updateRule(const BlockRule& rule) {
    if (!validateRule(rule)) {
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

BlockRule* BlockRuleEngine::getRule(const std::string& ruleId) {
    for (auto& rule : rules) {
        if (rule.id == ruleId) {
            return &rule;
        }
    }
    return nullptr;
}

std::vector<BlockRule> BlockRuleEngine::getAllRules() const {
    return rules;
}

bool BlockRuleEngine::shouldBlockApp(const std::string& packageName, time_t currentTime) {
    for (const auto& rule : rules) {
        if (!rule.isActive) continue;
        if (rule.packageName != packageName) continue;
        
        if (isCurrentTimeInRange(rule, currentTime) && 
            isCurrentDayInSet(rule, currentTime)) {
            return true;
        }
    }
    return false;
}

BlockType BlockRuleEngine::getBlockType(const std::string& packageName, time_t currentTime) {
    // Default to APP_BLOCK if multiple rules exist
    BlockType blockType = BlockType::APP_BLOCK;
    
    for (const auto& rule : rules) {
        if (!rule.isActive) continue;
        if (rule.packageName != packageName) continue;
        
        if (isCurrentTimeInRange(rule, currentTime) && 
            isCurrentDayInSet(rule, currentTime)) {
            if (rule.type == BlockType::GRAYSCALE) {
                blockType = BlockType::GRAYSCALE;
            }
            // APP_BLOCK takes precedence
            if (rule.type == BlockType::APP_BLOCK) {
                return BlockType::APP_BLOCK;
            }
        }
    }
    return blockType;
}

bool BlockRuleEngine::validateRule(const BlockRule& rule) {
    // Validate hour and minute ranges
    if (rule.startHour < 0 || rule.startHour > 23) return false;
    if (rule.startMinute < 0 || rule.startMinute > 59) return false;
    if (rule.endHour < 0 || rule.endHour > 23) return false;
    if (rule.endMinute < 0 || rule.endMinute > 59) return false;
    
    // Validate package name is not empty
    if (rule.packageName.empty()) return false;
    
    // Validate days set
    if (rule.days.empty()) return false;
    for (int day : rule.days) {
        if (day < 1 || day > 7) return false;
    }
    
    return true;
}

void BlockRuleEngine::clearRules() {
    rules.clear();
}

std::vector<BlockRule> BlockRuleEngine::getRulesForPackage(const std::string& packageName) const {
    std::vector<BlockRule> result;
    for (const auto& rule : rules) {
        if (rule.packageName == packageName) {
            result.push_back(rule);
        }
    }
    return result;
}

bool BlockRuleEngine::isCurrentTimeInRange(const BlockRule& rule, time_t currentTime) {
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

bool BlockRuleEngine::isCurrentDayInSet(const BlockRule& rule, time_t currentTime) {
    int dayOfWeek = getCurrentDayOfWeek(currentTime);
    return rule.days.count(dayOfWeek) > 0;
}

int BlockRuleEngine::getCurrentDayOfWeek(time_t currentTime) {
    struct tm* timeinfo = std::localtime(&currentTime);
    // Convert from system format (0=Sunday) to our format (1=Monday)
    int dayOfWeek = timeinfo->tm_wday;
    if (dayOfWeek == 0) dayOfWeek = 7;  // Sunday = 7
    return dayOfWeek;
}

}  // namespace SafeTimeGuard
