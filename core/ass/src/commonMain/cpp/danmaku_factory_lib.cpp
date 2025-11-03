#include <jni.h>
#include <string>
#include <android/log.h>
#include <cstring>
#include <cstdlib>

#include "Config/Config.h"
#include "Define/Status.h"
#include "CDanmakuFactory.h"

// 日志标签
#define TAG "DanmakuFactory"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

// 全局配置
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

        {500, 1080}, /* 消息框大小 */
        {20, 0},     /* 消息框位置 */
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
    while (current != nullptr) {
        DANMAKU *next = current->next;
        free(current->text);
        free(current->special);
        free(current->user);
        free(current->gift);
        free(current);
        current = next;
    }
}

struct DanmakuListDeleter {
    void operator()(DANMAKU *p) const {
        releaseDanmakuList(p);
    }
};

using DanmakuListPtr = std::unique_ptr<DANMAKU, DanmakuListDeleter>;

class JniUtfChars {
public:
    JniUtfChars(JNIEnv *env, jstring jstr) : env_(env), jstr_(jstr), cstr_(nullptr) {
        if (env && jstr) {
            cstr_ = env->GetStringUTFChars(jstr, nullptr);
        }
    }

    ~JniUtfChars() {
        if (env_ && jstr_ && cstr_) {
            env_->ReleaseStringUTFChars(jstr_, cstr_);
        }
    }

    // 禁止拷贝和赋值
    JniUtfChars(const JniUtfChars &) = delete;

    JniUtfChars &operator=(const JniUtfChars &) = delete;

    const char *get() const { return cstr_; }

    operator const char *() const { return cstr_; }

private:
    JNIEnv *env_;
    jstring jstr_;
    const char *cstr_;
};


// --- 重构后的文件转换方法 ---
extern "C" JNIEXPORT jstring JNICALL
Java_com_imcys_bilibilias_core_ass_DanmakufactoryLib_convertDanmakuFile(
        JNIEnv *env,
        jobject thiz,
        jstring inputPath_,
        jstring outputPath_) {

    JniUtfChars inputPath(env, inputPath_);
    JniUtfChars outputPath(env, outputPath_);

    if (!inputPath.get() || !outputPath.get()) {
        LOGE("无法获取输入或输出路径字符串");
        return env->NewStringUTF("无法获取路径字符串，可能内存不足。");
    }

    LOGD("转换文件: %s -> %s", inputPath.get(), outputPath.get());

    try {
        STATUS status = {FALSE, 0, 0};
        DANMAKU *rawDanmakuList = nullptr;

        if (readXml(inputPath, &rawDanmakuList, "", 0.0f, &status) != 0) {
            throw std::runtime_error("读取并解析弹幕 XML 文件失败。");
        }

        DanmakuListPtr danmakuList(rawDanmakuList);

        if (!danmakuList) {
            LOGD("输入文件为空或不包含有效弹幕数据。");
        }

        if (writeAss(outputPath, danmakuList.get(), defaultConfig, nullptr, &status) != 0) {
            throw std::runtime_error("写入目标 ASS 文件失败。");
        }

        LOGD("文件转换成功");
        return env->NewStringUTF("");
    } catch (const std::runtime_error &e) {
        LOGE("发生错误: %s", e.what());
        return env->NewStringUTF(e.what());
    }
}