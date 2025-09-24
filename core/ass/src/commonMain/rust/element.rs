use crate::setting::Setting;

#[derive(Debug, Clone, Copy, PartialEq, Eq, Default)]
pub enum ElementType {
    #[default]
    Float,
    Top,
    Bottom,
    Reverse,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Default)]
pub struct Color {
    pub r: u8,
    pub g: u8,
    pub b: u8,
}

impl Color {
    pub fn new(r: u8, g: u8, b: u8) -> Self {
        Self { r, g, b }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct Element {
    pub timeline_s: f64,
    pub content: String,
    pub r#type: ElementType,
    pub color: Color,
}

impl Element {
    pub fn new(timeline_s: f64, content: String, r#type: ElementType, color: Color) -> Self {
        Self {
            timeline_s,
            content,
            r#type,
            color,
        }
    }
    pub fn estimated_width(&self, setting: &Setting) -> f64 {
        let weighted_char_count = self.content.chars().map(|ch| {
            if ch.is_ascii() {
                2.0 / 3.0
            } else {
                1.0
            }
        }).sum::<f64>();

        let base_width = weighted_char_count * setting.font_size as f64;

        base_width * setting.width_ratio
    }
}