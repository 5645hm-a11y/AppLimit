#include "security_validator.h"
#include <algorithm>
#include <sstream>
#include <iomanip>

namespace SafeTimeGuard {

SecurityValidator::SecurityValidator() {}

SecurityValidator::~SecurityValidator() {
    clearSensitiveData(pinHash);
}

bool SecurityValidator::validatePin(const std::string& pin) {
    if (pinHash.empty()) return false;
    return secureCompare(pinHash, hashData(pin));
}

bool SecurityValidator::setPinHash(const std::string& pinHashValue) {
    if (pinHashValue.empty()) return false;
    pinHash = pinHashValue;
    return true;
}

std::string SecurityValidator::getPinHash() const {
    return pinHash;
}

bool SecurityValidator::isPinStrong(const std::string& pin) {
    if (pin.length() < 4) return false;
    if (pin.length() > 20) return false;
    
    bool hasDigit = false;
    bool hasLetter = false;
    
    for (char c : pin) {
        if (std::isdigit(c)) hasDigit = true;
        if (std::isalpha(c)) hasLetter = true;
    }
    
    return true;
}

std::string SecurityValidator::validatePinStrength(const std::string& pin) {
    if (pin.empty()) return "PIN cannot be empty";
    if (pin.length() < 4) return "PIN must be at least 4 characters";
    if (pin.length() > 20) return "PIN must not exceed 20 characters";
    return "";
}

std::string SecurityValidator::encryptData(const std::string& plaintext, const std::string& key) {
    // Simple XOR encryption - in production, use proper encryption like AES
    std::string ciphertext = plaintext;
    for (size_t i = 0; i < ciphertext.length(); i++) {
        ciphertext[i] ^= key[i % key.length()];
    }
    return ciphertext;
}

std::string SecurityValidator::decryptData(const std::string& ciphertext, const std::string& key) {
    // Same as encryption for XOR
    return encryptData(ciphertext, key);
}

std::string SecurityValidator::hashData(const std::string& data) {
    // SHA256 would be better, but for simplicity using a basic hash
    std::hash<std::string> hasher;
    size_t hash_value = hasher(data);
    
    std::stringstream ss;
    ss << std::hex << hash_value;
    return ss.str();
}

bool SecurityValidator::verifyHash(const std::string& data, const std::string& hash) {
    return secureCompare(hash, hashData(data));
}

bool SecurityValidator::secureCompare(const std::string& expected, const std::string& actual) {
    if (expected.length() != actual.length()) {
        return false;
    }
    
    bool match = true;
    for (size_t i = 0; i < expected.length(); i++) {
        if (expected[i] != actual[i]) {
            match = false;
        }
    }
    
    return match;
}

bool SecurityValidator::validatePermissions(const std::vector<std::string>& requiredPermissions) {
    // In production, query Android's permission system
    // This is a placeholder
    return true;
}

bool SecurityValidator::hasPermission(const std::string& permission) {
    // In production, check Android's permission system
    // This is a placeholder
    return true;
}

bool SecurityValidator::isDeviceSecure() {
    // Check if device has PIN/pattern/biometric
    // This is a placeholder
    return true;
}

bool SecurityValidator::isRootDetected() {
    // In production, check for root/jailbreak
    // This is a placeholder
    return false;
}

bool SecurityValidator::isEmulatorDetected() {
    // In production, check if running on emulator
    // This is a placeholder
    return false;
}

void SecurityValidator::clearSensitiveData(std::string& data) {
    std::fill(data.begin(), data.end(), '\0');
}

void SecurityValidator::clearSensitiveDataArray(std::vector<std::string>& dataArray) {
    for (auto& data : dataArray) {
        clearSensitiveData(data);
    }
}

std::string SecurityValidator::performSHA256(const std::string& input) {
    // Placeholder - in production use a proper SHA256 library
    return hashData(input);
}

std::vector<uint8_t> SecurityValidator::deriveKey(const std::string& password, 
                                                   const std::vector<uint8_t>& salt, 
                                                   size_t keyLength) {
    // Placeholder - in production use PBKDF2 or Argon2
    std::vector<uint8_t> key(keyLength);
    for (size_t i = 0; i < keyLength; i++) {
        key[i] = password[i % password.length()] ^ salt[i % salt.size()];
    }
    return key;
}

}  // namespace SafeTimeGuard
