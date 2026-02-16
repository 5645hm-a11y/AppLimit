cmake_minimum_required(VERSION 3.22.1)
project(SafeTimeGuard)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find required packages
find_package(log REQUIRED)

# Include directories
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

# Source files for the native library
set(SOURCE_FILES
    native-lib.cpp
    block_rule_engine.cpp
    time_manager.cpp
    security_validator.cpp
    pin_manager.cpp
    grayscale_controller.cpp
)

# Create the native library
add_library(safetimeguard SHARED ${SOURCE_FILES})

# Link libraries
target_link_libraries(safetimeguard
    log
)
