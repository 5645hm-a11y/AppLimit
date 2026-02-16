#include "pin_manager.h"
#include <algorithm>
#include <cstring>
#include <cctype>

namespace SafeTimeGuard {

PinManager::PinManager() 
    : failedAttempts(0), lockoutEndTime(0) {}

PinManager::~PinManager() {
    // Clear sensitive data
    std::fill(pinHash.begin(), pinHash.end(), '\0');
}

bool PinManager::setPin(const std::string& pin) {
    if (!isPinStrong(pin)) {
        return false;
    }
    
    pinHash = hashPin(pin);
    failedAttempts = 0;
    lockoutEndTime = 0;
    return true;
}

bool PinManager::updatePin(const std::string& oldPin, const std::string& newPin) {
    if (!verifyPin(oldPin)) {
        incrementFailedAttempts();
        return false;
    }
    
    return setPin(newPin);
}

bool PinManager::verifyPin(const std::string& pin) {
    if (isLockedOut()) {
        return false;
    }
    
    std::string hash = hashPin(pin);
    
    // Secure comparison to prevent timing attacks
    if (hash.length() != pinHash.length()) {
        incrementFailedAttempts();
        return false;
    }
    
    bool match = true;
    for (size_t i = 0; i < hash.length(); i++) {
        if (hash[i] != pinHash[i]) {
            match = false;
        }
    }
    
    if (!match) {
        incrementFailedAttempts();
        return false;
    }
    
    resetFailedAttempts();
    return true;
}

bool PinManager::isPinSet() const {
    return !pinHash.empty();
}

std::string PinManager::hashPin(const std::string& pin) {
    // Simple hash function - in production, use proper cryptographic hash
    // This is a placeholder using basic hashing
    std::hash<std::string> hasher;
    size_t hash_value = hasher(pin);
    return std::to_string(hash_value);
}

int PinManager::getFailedAttempts() const {
    return failedAttempts;
}

void PinManager::incrementFailedAttempts() {
    failedAttempts++;
    if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
        applyLockout(LOCKOUT_DURATION_SECONDS);
    }
}

void PinManager::resetFailedAttempts() {
    failedAttempts = 0;
    lockoutEndTime = 0;
}

bool PinManager::isLockedOut() const {
    time_t now = std::time(nullptr);
    return now < lockoutEndTime;
}

time_t PinManager::getLockoutEndTime() const {
    return lockoutEndTime;
}

void PinManager::applyLockout(int lockoutDurationSeconds) {
    lockoutEndTime = std::time(nullptr) + lockoutDurationSeconds;
}

bool PinManager::isPinStrong(const std::string& pin) {
    if (pin.length() < 4) return false;
    if (pin.length() > 20) return false;
    
    bool hasDigit = false;
    bool hasLetter = false;
    
    for (char c : pin) {
        if (std::isdigit(c)) hasDigit = true;
        if (std::isalpha(c)) hasLetter = true;
    }
    
    return true;  // Basic validation - at least 4 chars
}

std::string PinManager::validatePinRequirements(const std::string& pin) {
    if (pin.empty()) return "PIN cannot be empty";
    if (pin.length() < 4) return "PIN must be at least 4 characters";
    if (pin.length() > 20) return "PIN must not exceed 20 characters";
    
    return "";  // Valid
}

bool PinManager::clearPin() {
    std::fill(pinHash.begin(), pinHash.end(), '\0');
    pinHash.clear();
    failedAttempts = 0;
    lockoutEndTime = 0;
    return true;
}

bool PinManager::isAlphanumeric(const std::string& str) {
    for (char c : str) {
        if (!std::isalnum(c)) return false;
    }
    return true;
}

bool PinManager::hasNumbers(const std::string& str) {
    for (char c : str) {
        if (std::isdigit(c)) return true;
    }
    return false;
}

bool PinManager::hasLetters(const std::string& str) {
    for (char c : str) {
        if (std::isalpha(c)) return true;
    }
    return false;
}

}  // namespace SafeTimeGuard
