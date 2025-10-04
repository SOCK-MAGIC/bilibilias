use std::sync::Arc;
use crate::danmaku_elem::DanmuElement;

/// 弹幕开始绘制的时间就是 DanmuElement 的时间
#[derive(uniffi::Record)]
pub struct Drawable {
    pub element:  Arc<DanmuElement>,
    /// 弹幕一共绘制的时间  
    pub duration: f64,
    /// 弹幕的绘制 style
    pub style_name: String,
    /// 绘制的“特效”
    pub effect: DrawEffect,
}

#[derive(uniffi::Enum)]
pub enum DrawEffect {
    Move { x1:i32, y1:i32, x2:i32, y2:i32 },
    Fixed {},
}