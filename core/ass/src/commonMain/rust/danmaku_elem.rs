use crate::danmu_setting::DanmuSetting;
use std::sync::atomic::AtomicU32;
use std::sync::Arc;

#[derive(uniffi::Object)]
pub struct DanmuElement {
    pub timeline: AtomicU32,
    pub content: String,
    pub r#type: Arc<DanmuMode>,
    /// 虽然这里有 fontsize，但是我们实际上使用 canvas config 的 font size，
    /// 否在在调节分辨率的时候字体会发生变化。
    pub fontsize: u32,
    pub rgb: (u8, u8, u8),
}

#[uniffi::export]
impl DanmuElement {
    #[uniffi::constructor]
    pub fn new(
        timeline_s: i32,
        content: String,
        r#type: DanmuMode,
        fontsize: u32,
        color: u32,
    ) -> Self {
        Self {
            timeline: AtomicU32::new(timeline_s as u32),
            content,
            r#type: Arc::new(r#type),
            fontsize,
            rgb: (
                ((color >> 16) & 0xFF) as u8,
                ((color >> 8) & 0xFF) as u8,
                (color & 0xFF) as u8,
            ),
        }
    }

    /// 计算弹幕的“像素长度”，会乘上一个缩放因子
    ///
    /// 汉字算一个全宽，英文算2/3宽
    pub fn length(&self, setting: &DanmuSetting) -> f64 {
        let pts = setting.font_size
            * self
                .content
                .chars()
                .map(|ch| if ch.is_ascii() { 2 } else { 3 })
                .sum::<u32>()
            / 3;

        pts as f64 * setting.width_ratio
    }
}

#[derive(uniffi::Enum)]
pub enum DanmuMode {
    Float,
    Top,
    Bottom,
    Reverse,
}
