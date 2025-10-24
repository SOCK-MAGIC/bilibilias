# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Set project name. CMAKE_PROJECT_NAME will be "danmakufactory_plugin"
project("danmakufactory_plugin")

# --- Dependency Management with FetchContent ---
include(FetchContent)

# 1. Fetch and configure PCRE2 dependency
FetchContent_Declare(
        pcre2
        GIT_REPOSITORY https://github.com/molihuan/pcre2.git
        GIT_TAG master # For production, a specific commit hash or tag is safer
)

# Set PCRE2's build options BEFORE it is configured by FetchContent_MakeAvailable.
set(PCRE2_BUILD_TESTS OFF CACHE BOOL "Disable building PCRE2 tests")
set(PCRE2_SUPPORT_JIT OFF CACHE BOOL "Disable JIT support, often restricted on mobile platforms")
set(PCRE2_BUILD_PCRE2GREP OFF CACHE BOOL "Disable building the pcre2grep tool")
set(BUILD_SHARED_LIBS OFF CACHE BOOL "Force building PCRE2 as a static library")

FetchContent_MakeAvailable(pcre2)

# 2. Fetch and prepare DanmakuFactory dependency
FetchContent_Declare(
        DanmakuFactory
        GIT_REPOSITORY https://github.com/molihuan/DanmakuFactory.git
        GIT_TAG master # For production, a specific commit hash or tag is safer
)
FetchContent_MakeAvailable(DanmakuFactory)

# --- Target Definitions ---

# A. Build the danmaku_factory static library from its sources
# Since it doesn't have its own CMakeLists.txt, we define the target here.
set(DANMAKU_FACTORY_SRC_DIR ${danmakufactory_SOURCE_DIR}/src)

file(GLOB_RECURSE DANMAKU_FACTORY_SOURCES
        "${DANMAKU_FACTORY_SRC_DIR}/*.c"
)

# Exclude the original main.c to provide our own interface
list(REMOVE_ITEM DANMAKU_FACTORY_SOURCES "${DANMAKU_FACTORY_SRC_DIR}/main.c")

add_library(danmaku_factory STATIC ${DANMAKU_FACTORY_SOURCES})

# Define properties for the danmaku_factory target
target_include_directories(danmaku_factory PUBLIC
        # Public headers for consumers of this library
        "${DANMAKU_FACTORY_SRC_DIR}"
        # This seems to be a required include path for its own sources
        "${PROJECT_ROOT}"
)

# Link danmaku_factory to pcre2. This transitively provides pcre2's headers and library.
# The static target is named pcre2-8-static when BUILD_SHARED_LIBS=OFF.
target_link_libraries(danmaku_factory PUBLIC pcre2-8-static)

# B. Build the final shared library for the Android project
add_library(${CMAKE_PROJECT_NAME} SHARED
        danmaku_factory.cpp
)

# Link the final shared library against our dependencies
target_link_libraries(${CMAKE_PROJECT_NAME} PRIVATE
        danmaku_factory  # This will also link pcre2-8-static due to the transitive dependency
        android          # For Android-specific APIs
        log              # For __android_log_print
)
