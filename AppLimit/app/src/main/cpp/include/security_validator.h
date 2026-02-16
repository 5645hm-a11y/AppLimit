#ifndef SAFETIMEGUARD_SECURITY_VALIDATOR_H
#define SAFETIMEGUARD_SECURITY_VALIDATOR_H

#include <string>
#include <vector>
#include <cstdint>

namespace SafeTimeGuard {

class SecurityValidator {
public:
    SecurityValidator();
    ~SecurityValidator();
    
    // PIN validation
    bool validatePin(const std::string& pin);
    bool setPinHash(const std::string& pinHash);
    std::string getPinHash() const;
    
    // PIN strength check
    bool isPinStrong(const std::string& pin);
    std::string validatePinStrength(const std::string& pin);
    
    // Encryption/Decryption
    std::string encryptData(const std::string& plaintext, const std::string& key);
    std::string decryptData(const std::string& ciphertext, const std::string& key);
    
    // Hash function
    std::string hashData(const std::string& data);
    bool verifyHash(const std::string& data, const std::string& hash);
    
    // Secure comparison (against timing attacks)
    bool secureCompare(const std::string& expected, const std::string& actual);
    
    // Permission checks
    bool validatePermissions(const std::vector<std::string>& requiredPermissions);
    bool hasPermission(const std::string& permission);
    
    // Security state checks
    bool isDeviceSecure();
    bool isRootDetected();
    bool isEmulatorDetected();
    
    // Clear sensitive data
    void clearSensitiveData(std::string& data);
    void clearSensitiveDataArray(std::vector<std::string>& dataArray);
    
private:
    std::string pinHash;
    
    // Helper methods for cryptographic operations
    std::string performSHA256(const std::string& input);
    std::vector<uint8_t> deriveKey(const std::string& password, 
                                    const std::vector<uint8_t>& salt, 
                                    size_t keyLength);
};

}  // namespace SafeTimeGuard

#endif  // SAFETIMEGUARD_SECURITY_VALIDATOR_H
