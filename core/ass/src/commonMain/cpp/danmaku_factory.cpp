#include <jni.h>
#include <string>
#include <android/log.h>
#include <cstring>
#include <cstdlib>
#include <utility> // For std::move

// 导入DanmakuFactory的头文件
#include "CDanmakuFactory.h"
#include "Define/DanmakuDef.h"
#include "Config/Config.h"

// 日志标签
#define TAG "DanmakuFactory"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

// --- RAII 包装类定义 ---

/**
 * @brief JNI 字符串的 RAII 包装器.
 * 自动管理 GetStringUTFChars 和 ReleaseStringUTFChars.
 */
class JniStringChars {
public:
    // 构造函数：获取资源
    JniStringChars(JNIEnv *env, jstring jstr) : env_(env), jstring_(jstr), chars_(nullptr) {
        if (env_ && jstring_) {
            chars_ = env_->GetStringUTFChars(jstring_, nullptr);
        }
    }

    // 析构函数：释放资源
    ~JniStringChars() {
        if (env_ && jstring_ && chars_) {
            env_->ReleaseStringUTFChars(jstring_, chars_);
        }
    }

    // 删除拷贝构造和拷贝赋值，防止资源被多次释放
    JniStringChars(const JniStringChars &) = delete;

    JniStringChars &operator=(const JniStringChars &) = delete;

    // 允许移动构造和移动赋值（可选，但在复杂场景中有用）
    JniStringChars(JniStringChars &&other) noexcept: env_(other.env_), jstring_(other.jstring_),
                                                     chars_(other.chars_) {
        other.env_ = nullptr;
        other.jstring_ = nullptr;
        other.chars_ = nullptr;
    }

    JniStringChars &operator=(JniStringChars &&other) noexcept {
        if (this != &other) {
            if (env_ && jstring_ && chars_) {
                env_->ReleaseStringUTFChars(jstring_, chars_);
            }
            env_ = other.env_;
            jstring_ = other.jstring_;
            chars_ = other.chars_;
            other.env_ = nullptr;
            other.jstring_ = nullptr;
            other.chars_ = nullptr;
        }
        return *this;
    }

    // 提供访问原始 C 字符串的接口
    const char *get() const {
        return chars_;
    }

    // 隐式转换为 const char*，方便直接使用
    explicit operator const char *() const {
        return chars_;
    }

private:
    JNIEnv *env_;
    jstring jstring_;
    const char *chars_;
};


// 声明释放函数，以便在 RAII 类中使用
void releaseDanmakuList(DANMAKU *danmakuList);

/**
 * @brief DANMAKU 链表的 RAII 包装器.
 * 自动管理链表内存.
 */
class DanmakuListManager {
public:
    // 构造函数：获取资源所有权
    explicit DanmakuListManager(DANMAKU *&danmakuList) : danmakuList_(danmakuList) {}

    // 析构函数：释放资源
    ~DanmakuListManager() {
        if (danmakuList_ != nullptr) {
            releaseDanmakuList(danmakuList_);
            danmakuList_ = nullptr; // 防止悬挂指针
        }
    }

    // 删除拷贝构造和拷贝赋值
    DanmakuListManager(const DanmakuListManager &) = delete;

    DanmakuListManager &operator=(const DanmakuListManager &) = delete;

private:
    DANMAKU *&danmakuList_;
};

static CONFIG defaultConfig = {
        {1920, 1080}, /* 分辨率 */
        1.00,         /* 显示区域 */
        1.00,         /* 滚动区域 */
        12.0,         /* 滚动时间 */
        5.0,          /* 固定时间 */

        0,     /* 弹幕密度 */
        0,     /* 行间距 */
        38,    /* 字号 */
        FALSE, /* 是否严格保持指定的字号大小 */
        FALSE, /* 是否修正字号 */
        /* 字体 */
        "Microsoft YaHei",
        180,   /* 不透明度 */
        0,     /* 描边 */
        0,     /* 描边模糊半径 */
        255,   /* 描边不透明度 */
        1,     /* 阴影 */
        FALSE, /* 是否加粗 */

        TRUE,  /* 是否保存屏蔽部分 */
        FALSE, /* 是否显示用户名 */
        TRUE,  /* 是否显示消息框 */

        {500, 1080}, /* 消息框大小 (已修正) */
        {20, 0},     /* 消息框位置 (已修正) */
        38,          /* 消息框内文字大小 */
        0.0f,        /* 消息框持续时长 */
        0.0f,        /* 消息框礼物最低价格限制 */

        0,     /* 屏蔽模式 */
        0,     /* 统计模式 */
        nullptr,  /* 弹幕黑名单 */
        FALSE, /* 弹幕黑名单是否启用正则表达式匹配 */
};

void releaseDanmakuList(DANMAKU *danmakuList) {
    DANMAKU *current = danmakuList;
    DANMAKU *next;

    while (current != nullptr) {
        next = current->next;
        if (current->text) free(current->text);
        if (current->special) free(current->special);
        if (current->user) free(current->user);
        if (current->gift) free(current->gift);
        free(current);
        current = next;
    }
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_imcys_bilibilias_core_ass_Danmakufactory_convertDanmakuFile(
        JNIEnv *env,
        jobject thiz,
        jstring inputPath_,
        jstring outputPath_) {

    // 使用 RAII 包装器自动管理 C 字符串的生命周期
    JniStringChars inputPath(env, inputPath_);
    JniStringChars outputPath(env, outputPath_);

    // 检查字符串是否成功获取
    if (inputPath.get() == nullptr || outputPath.get() == nullptr) {
        LOGE("无法获取输入或输出文件路径");
        return env->NewStringUTF("无法获取文件路径");
    }

    LOGD("转换文件: %s -> %s", inputPath.get(), outputPath.get());

    STATUS status = {FALSE, 0, 0};
    DANMAKU *danmakuList = nullptr;

    // 使用 RAII 包装器确保 danmakuList 在任何情况下都会被释放
    DanmakuListManager danmakuGuard(danmakuList);

    // 读取弹幕文件
    const char *ext = strrchr(inputPath, '.');
    if (ext != nullptr && strcmp(ext, ".xml") == 0) {
        if (readXml(inputPath, &danmakuList, "", 0.0f, &status) != 0) {
            LOGE("读取XML文件失败");
            return env->NewStringUTF("读取XML文件失败");
        }
    } else if (ext != nullptr && strcmp(ext, ".json") == 0) {
        if (readJson(inputPath, &danmakuList, "", 0.0f, &status) != 0) {
            LOGE("读取JSON文件失败");
            return env->NewStringUTF("读取JSON文件失败");
        }
    } else {
        LOGE("不支持的输入文件格式");
        return env->NewStringUTF("不支持的输入文件格式");
    }

    // 写入弹幕文件
    const char *outExt = strrchr(outputPath, '.');
    if (outExt != nullptr && strcmp(outExt, ".ass") == 0) {
        if (writeAss(outputPath, danmakuList, defaultConfig, nullptr, &status) != 0) {
            LOGE("写入ASS文件失败");
            return env->NewStringUTF("写入ASS文件失败");
        }
    } else if (outExt != nullptr && strcmp(outExt, ".xml") == 0) {
        if (writeXml(outputPath, danmakuList, &status) != 0) {
            LOGE("写入XML文件失败");
            return env->NewStringUTF("写入XML文件失败");
        }
    } else if (outExt != nullptr && strcmp(outExt, ".json") == 0) {
        if (writeJson(outputPath, danmakuList, &status) != 0) {
            LOGE("写入JSON文件失败");
            return env->NewStringUTF("写入JSON文件失败");
        }
    } else {
        LOGE("不支持的输出文件格式");
        return env->NewStringUTF("不支持的输出文件格式");
    }

    LOGD("文件转换成功");
    // 成功时返回空字符串
    // 此处函数返回时，danmakuGuard 和 JniStringChars 的析构函数会被自动调用，从而释放资源
    return env->NewStringUTF("");
}