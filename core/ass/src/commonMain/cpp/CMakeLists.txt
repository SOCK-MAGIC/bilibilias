cmake_minimum_required(VERSION 4.1.0)

project(danmakufactory)

include(FetchContent)

FetchContent_Declare(
        pcre2
        GIT_REPOSITORY https://github.com/molihuan/pcre2.git
        GIT_TAG master
)

# 设置PCRE2的编译选项，这些选项会在pcre2配置时被应用。
# 将其编译为静态库，并禁用不需要的特性。
set(PCRE2_BUILD_TESTS OFF CACHE BOOL "Disable PCRE2 tests")
set(PCRE2_SUPPORT_JIT OFF CACHE BOOL "Disable JIT support for mobile")
set(PCRE2_BUILD_PCRE2GREP OFF CACHE BOOL "Disable pcre2grep utility")
set(BUILD_SHARED_LIBS OFF CACHE BOOL "Build PCRE2 as a static library")


FetchContent_MakeAvailable(pcre2)

FetchContent_Declare(
        danmakufactory_dep
        GIT_REPOSITORY https://github.com/molihuan/DanmakuFactory.git
        GIT_TAG master
)

FetchContent_MakeAvailable(danmakufactory_dep)

# 使用file(GLOB)查找所有需要的源文件。
# 注意：当添加或删除源文件时，需要重新运行CMake配置。
file(GLOB DANMAKU_FACTORY_SOURCES
        "${danmakufactory_dep_SOURCE_DIR}/src/*.c"
        "${danmakufactory_dep_SOURCE_DIR}/src/AssFile/*.c"
        "${danmakufactory_dep_SOURCE_DIR}/src/Config/*.c"
        "${danmakufactory_dep_SOURCE_DIR}/src/Define/*.c"
        "${danmakufactory_dep_SOURCE_DIR}/src/List/*.c"
        "${danmakufactory_dep_SOURCE_DIR}/src/String/*.c"
        "${danmakufactory_dep_SOURCE_DIR}/src/TemplateFile/*.c"
)

# 排除不需要的main.c文件。
list(REMOVE_ITEM DANMAKU_FACTORY_SOURCES "${danmakufactory_dep_SOURCE_DIR}/src/main.c")

# 定义 "danmaku_factory" 静态库目标。
add_library(danmaku_factory STATIC ${DANMAKU_FACTORY_SOURCES})

# 为 "danmaku_factory" 添加所需的包含目录和链接库。
# 使用PUBLIC关键字，这样任何链接到danmaku_factory的目标都会自动继承这些属性。
target_include_directories(danmaku_factory PUBLIC
        # pcre2目标会自动导出其包含目录，我们只需要链接它即可。
        ${danmakufactory_dep_SOURCE_DIR}/src
)

# 将pcre2链接到danmaku_factory。pcre2的目标名称通常是pcre2-8或pcre2-posix。
# 这会自动处理包含目录和链接。
target_link_libraries(danmaku_factory PUBLIC pcre2-8)

# 4. 构建主项目库 (native-lib)
add_library(${CMAKE_PROJECT_NAME} SHARED danmaku_factory_lib.cpp)

# 链接danmaku_factory库，以及Android平台所需的日志库。
# 由于danmaku_factory已经公开链接了pcre2-8，这里无需再次链接pcre2。
target_link_libraries(${CMAKE_PROJECT_NAME}
        PRIVATE
        danmaku_factory
        android
        log
)