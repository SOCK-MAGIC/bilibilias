use crate::danmaku_elem::{DanmuElement, DanmuMode};
use crate::drawable::{DrawEffect, Drawable};
use crate::lane::{Collision, Lane};
use float_ord::FloatOrd;
use log::debug;
use std::sync::atomic::Ordering;
use std::sync::{Arc, Mutex, MutexGuard};

#[derive(uniffi::Record)]
pub struct DanmuSetting {
    pub duration: f64,
    pub width: u32,
    pub height: u32,
    pub font_name: String,
    pub font_size: u32,
    pub width_ratio: f64,
    /// 两条弹幕之间最小的水平距离
    pub horizontal_gap: f64,
    /// lane 大小
    pub lane_size: u32,
    /// 屏幕上滚动弹幕最多高度百分比
    pub float_percentage: f64,
    /// 屏幕上底部弹幕最多高度百分比
    pub bottom_percentage: f64,
    /// 透明度
    pub opacity: u8,
    /// 是否加粗，1代表是，0代表否
    pub bold: bool,
    /// 描边
    pub outline: f64,
    /// 时间轴偏移
    pub time_offset: f64,
}

#[derive(uniffi::Object)]
pub struct Canvas {
    pub setting: DanmuSetting,
    pub float_lanes: Mutex<Vec<Option<Lane>>>,
    pub bottom_lanes: Mutex<Vec<Option<Lane>>>,
}

#[uniffi::export]
impl Canvas {
    #[uniffi::constructor]
    pub fn new(setting: DanmuSetting) -> Self {
        let float_lanes_cnt =
            (setting.float_percentage * setting.height as f64 / setting.lane_size as f64) as usize;
        let bottom_lanes_cnt =
            (setting.bottom_percentage * setting.height as f64 / setting.lane_size as f64) as usize;

        Self {
            setting,
            float_lanes: Mutex::new(vec![None; float_lanes_cnt]),
            bottom_lanes: Mutex::new(vec![None; bottom_lanes_cnt]),
        }
    }
    pub fn draw(&self, element: Arc<DanmuElement>) -> Option<Drawable> {
        element
            .timeline
            .fetch_add(self.setting.time_offset as u32, Ordering::SeqCst);
        if element.timeline.load(Ordering::SeqCst) < 0 {
            return None;
        }
        match element.r#type.as_ref() {
            DanmuMode::Float => self.draw_float(element),
            DanmuMode::Bottom | DanmuMode::Top | DanmuMode::Reverse => {
                // 不喜欢底部弹幕，直接转成 Float
                // 这是 feature 不是 bug
                // element.r#type =Arc::new(DanmuMode::Float);
                self.draw_float(element)
            }
        }
    }

    fn draw_float(&self, element: Arc<DanmuElement>) -> Option<Drawable> {
        let mut float_lanes_guard = self.float_lanes.lock().unwrap();

        let mut collisions = Vec::with_capacity(float_lanes_guard.len());
        for (idx, lane) in float_lanes_guard.iter_mut().enumerate() {
            // Now you can call iter_mut()
            match lane {
                // 优先画不存在的槽位
                None => {
                    return Some(self.draw_float_in_lane(element, idx as u32));
                }
                Some(l) => {
                    let col = l.available_for(&element, &self.setting);
                    match col {
                        Collision::Separate { .. } | Collision::NotEnoughTime { .. } => {
                            return Some(self.draw_float_in_lane(element, idx as u32));
                        }
                        Collision::Collide { time_needed } => {
                            collisions.push((FloatOrd(time_needed), idx));
                        }
                    }
                }
            }
        }

        // // 允许部分弹幕在延迟后填充
        if !collisions.is_empty() {
            collisions.sort_unstable();
            let (FloatOrd(time_need), lane_idx) = collisions[0];
            if time_need < 1.0 {
                debug!("延迟弹幕 {} 秒", time_need);
                // 只允许延迟 1s
                element.timeline.fetch_add(1000, Ordering::SeqCst); // 间隔也不要太小了
                return Some(self.draw_float_in_lane(element, lane_idx as u32));
            }
        }
        debug!("skipping danmu: {}", element.content);
        None
    }

    fn draw_float_in_lane(&self, element: Arc<DanmuElement>, lane_idx: u32) -> Drawable {
        let lane_idx = lane_idx as usize;
        self.float_lanes.lock().unwrap()[lane_idx] = Some(Lane::draw(&element, &self.setting));
        let y = lane_idx as i32 * self.setting.lane_size as i32;
        let l = element.length(&self.setting);
        Drawable {
            element,
            duration: self.setting.duration,
            style_name: "Float".to_string(),
            effect: DrawEffect::Move {
                x1: self.setting.width as i32,
                y1: y,
                x2: -(l as i32),
                y2: y,
            },
        }
    }
}
