#ifndef SAFETIMEGUARD_TIME_MANAGER_H
#define SAFETIMEGUARD_TIME_MANAGER_H

#include <ctime>
#include <string>
#include <vector>

namespace SafeTimeGuard {

struct TimeRange {
    int startHour;
    int startMinute;
    int endHour;
    int endMinute;
};

class TimeManager {
public:
    TimeManager();
    ~TimeManager();
    
    // Get current time
    time_t getCurrentTime() const;
    
    // Time comparison
    bool isTimeInRange(const TimeRange& range, time_t currentTime);
    bool isTimeInRange(int startHour, int startMinute, 
                       int endHour, int endMinute, 
                       time_t currentTime);
    
    // Day of week
    int getDayOfWeek(time_t timestamp);  // 1=Mon, 7=Sun
    std::string getDayName(int dayOfWeek);
    
    // Time formatting
    std::string formatTime(time_t timestamp);
    std::string formatDate(time_t timestamp);
    std::string formatDateTime(time_t timestamp);
    
    // Parse time strings
    bool parseTime(const std::string& timeStr, int& hour, int& minute);
    bool parseDate(const std::string& dateStr, struct tm& result);
    
    // Calculate time differences
    int getMinutesDifference(time_t time1, time_t time2);
    int getHoursDifference(time_t time1, time_t time2);
    int getDaysDifference(time_t time1, time_t time2);
    
    // Validate time values
    bool isValidTime(int hour, int minute);
    bool isValidHour(int hour);
    bool isValidMinute(int minute);
    bool isValidDayOfWeek(int day);
    
    // Get next scheduled time
    time_t getNextOccurrence(const TimeRange& range, const std::vector<int>& days);
    
    // Check if time is within business hours
    bool isBusinessHours(time_t timestamp);
    
private:
    // Helper methods
    struct tm* getLocalTime(time_t timestamp);
    int convertDayOfWeekFormat(int day);  // Convert system format to our format
};

}  // namespace SafeTimeGuard

#endif  // SAFETIMEGUARD_TIME_MANAGER_H
