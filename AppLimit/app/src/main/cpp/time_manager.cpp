#include "time_manager.h"
#include <sstream>
#include <iomanip>
#include <cstring>

namespace SafeTimeGuard {

TimeManager::TimeManager() {}

TimeManager::~TimeManager() {}

time_t TimeManager::getCurrentTime() const {
    return std::time(nullptr);
}

bool TimeManager::isTimeInRange(const TimeRange& range, time_t currentTime) {
    return isTimeInRange(range.startHour, range.startMinute, 
                         range.endHour, range.endMinute, currentTime);
}

bool TimeManager::isTimeInRange(int startHour, int startMinute, 
                                 int endHour, int endMinute, 
                                 time_t currentTime) {
    struct tm* timeinfo = std::localtime(&currentTime);
    int currentHour = timeinfo->tm_hour;
    int currentMinute = timeinfo->tm_min;
    
    int startTotalMinutes = startHour * 60 + startMinute;
    int endTotalMinutes = endHour * 60 + endMinute;
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

int TimeManager::getDayOfWeek(time_t timestamp) {
    struct tm* timeinfo = std::localtime(&timestamp);
    int dayOfWeek = timeinfo->tm_wday;
    if (dayOfWeek == 0) dayOfWeek = 7;  // Sunday = 7
    return dayOfWeek;
}

std::string TimeManager::getDayName(int dayOfWeek) {
    static const std::string days[] = {
        "Monday", "Tuesday", "Wednesday", "Thursday", 
        "Friday", "Saturday", "Sunday"
    };
    
    if (dayOfWeek >= 1 && dayOfWeek <= 7) {
        return days[dayOfWeek - 1];
    }
    return "Unknown";
}

std::string TimeManager::formatTime(time_t timestamp) {
    struct tm* timeinfo = std::localtime(&timestamp);
    char buffer[9];
    std::strftime(buffer, sizeof(buffer), "%H:%M:%S", timeinfo);
    return std::string(buffer);
}

std::string TimeManager::formatDate(time_t timestamp) {
    struct tm* timeinfo = std::localtime(&timestamp);
    char buffer[11];
    std::strftime(buffer, sizeof(buffer), "%Y-%m-%d", timeinfo);
    return std::string(buffer);
}

std::string TimeManager::formatDateTime(time_t timestamp) {
    struct tm* timeinfo = std::localtime(&timestamp);
    char buffer[20];
    std::strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", timeinfo);
    return std::string(buffer);
}

bool TimeManager::parseTime(const std::string& timeStr, int& hour, int& minute) {
    size_t colonPos = timeStr.find(':');
    if (colonPos == std::string::npos) {
        return false;
    }
    
    try {
        hour = std::stoi(timeStr.substr(0, colonPos));
        minute = std::stoi(timeStr.substr(colonPos + 1));
        return isValidTime(hour, minute);
    } catch (...) {
        return false;
    }
}

bool TimeManager::parseDate(const std::string& dateStr, struct tm& result) {
    std::istringstream ss(dateStr);
    ss >> std::get_time(&result, "%Y-%m-%d");
    return !ss.fail();
}

int TimeManager::getMinutesDifference(time_t time1, time_t time2) {
    return static_cast<int>(std::difftime(time2, time1) / 60);
}

int TimeManager::getHoursDifference(time_t time1, time_t time2) {
    return static_cast<int>(std::difftime(time2, time1) / 3600);
}

int TimeManager::getDaysDifference(time_t time1, time_t time2) {
    return static_cast<int>(std::difftime(time2, time1) / (3600 * 24));
}

bool TimeManager::isValidTime(int hour, int minute) {
    return isValidHour(hour) && isValidMinute(minute);
}

bool TimeManager::isValidHour(int hour) {
    return hour >= 0 && hour <= 23;
}

bool TimeManager::isValidMinute(int minute) {
    return minute >= 0 && minute <= 59;
}

bool TimeManager::isValidDayOfWeek(int day) {
    return day >= 1 && day <= 7;
}

time_t TimeManager::getNextOccurrence(const TimeRange& range, const std::vector<int>& days) {
    time_t now = getCurrentTime();
    struct tm* timeinfo = std::localtime(&now);
    
    for (int i = 0; i < 7; i++) {
        int dayOfWeek = timeinfo->tm_wday;
        if (dayOfWeek == 0) dayOfWeek = 7;
        
        for (int targetDay : days) {
            if (dayOfWeek == targetDay) {
                // Found the day, now set the time
                struct tm nextTime = *timeinfo;
                nextTime.tm_hour = range.startHour;
                nextTime.tm_min = range.startMinute;
                nextTime.tm_sec = 0;
                
                time_t nextTimestamp = std::mktime(&nextTime);
                if (nextTimestamp > now) {
                    return nextTimestamp;
                }
            }
        }
        
        // Move to next day
        time_t nextDay = now + (86400 * (i + 1));
        timeinfo = std::localtime(&nextDay);
    }
    
    return 0;  // Not found
}

bool TimeManager::isBusinessHours(time_t timestamp) {
    struct tm* timeinfo = std::localtime(&timestamp);
    int hour = timeinfo->tm_hour;
    int dayOfWeek = timeinfo->tm_wday;
    
    // Monday-Friday, 9:00-17:00
    return dayOfWeek >= 1 && dayOfWeek <= 5 && hour >= 9 && hour < 17;
}

struct tm* TimeManager::getLocalTime(time_t timestamp) {
    return std::localtime(&timestamp);
}

}  // namespace SafeTimeGuard
