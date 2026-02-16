#ifndef SAFETIMEGUARD_PIN_MANAGER_H
#define SAFETIMEGUARD_PIN_MANAGER_H

#include <string>
#include <cstdint>
#include <ctime>

namespace SafeTimeGuard {

class PinManager {
public:
    PinManager();
    ~PinManager();
    
    // PIN setup
    bool setPin(const std::string& pin);
    bool updatePin(const std::string& oldPin, const std::string& newPin);
    
    // PIN validation
    bool verifyPin(const std::string& pin);
    bool isPinSet() const;
    
    // PIN security
    std::string hashPin(const std::string& pin);
    
    // Failed attempts
    int getFailedAttempts() const;
    void incrementFailedAttempts();
    void resetFailedAttempts();
    
    // Lockout management
    bool isLockedOut() const;
    time_t getLockoutEndTime() const;
    void applyLockout(int lockoutDurationSeconds);
    
    // PIN strength validation
    bool isPinStrong(const std::string& pin);
    std::string validatePinRequirements(const std::string& pin);
    
    // Clear PIN
    bool clearPin();
    
private:
    std::string pinHash;
    int failedAttempts;
    time_t lockoutEndTime;
    
    static const int MAX_FAILED_ATTEMPTS = 5;
    static const int LOCKOUT_DURATION_SECONDS = 300;  // 5 minutes
    
    // Helper methods
    bool isAlphanumeric(const std::string& str);
    bool hasNumbers(const std::string& str);
    bool hasLetters(const std::string& str);
};

}  // namespace SafeTimeGuard

#endif  // SAFETIMEGUARD_PIN_MANAGER_H
