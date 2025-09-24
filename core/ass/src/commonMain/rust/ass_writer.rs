use crate::drawable::{DrawEffect, Drawable};
use crate::setting::Setting;
use anyhow::Result;
use log::error;
use std::borrow::Cow;
use std::fmt;
use std::fmt::{Display, Formatter};
use std::io::{BufWriter, Write};
use crate::setting;

struct TimePoint {
    t: f64,
}
impl Display for TimePoint {
    fn fmt(&self, f: &mut Formatter) -> fmt::Result {
        let secs = self.t.floor() as u32;
        let hour = secs / 3600;
        let minutes = (secs % 3600) / 60;

        let left = self.t - (hour * 3600) as f64 - (minutes * 60) as f64;

        write!(f, "{hour}:{minutes:02}:{left:05.2}")
    }
}

struct AssEffect {
    effect: DrawEffect,
}
impl Display for AssEffect {
    fn fmt(&self, f: &mut Formatter) -> fmt::Result {
        match self.effect {
            DrawEffect::Move { start, end } => {
                let (x0, y0) = start;
                let (x1, y1) = end;
                write!(f, "\\move({x0}, {y0}, {x1}, {y1})")
            }
            DrawEffect::Fixed {} => {
                error!("应该不会出现固定弹幕的");
                Err(fmt::Error)
            }
        }
    }
}

impl Setting {
    pub fn ass_styles(&self) -> Vec<String> {
        vec![
            // Name, FontName, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, \
            // Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, \
            // Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding
            format!(
                "Style: Float,{font_name},{font_size},&H{a:02x}FFFFFF,&H00FFFFFF,&H{a:02x}000000,&H00000000,\
                {bold}, 0, 0, 0, 100, 100, 0.00, 0.00, 1, \
                {outline}, 0, 7, 0, 0, 0, 1",
                a = self.opacity as u8,
                font_name = self.font_name,
                font_size = self.font_size,
                bold = self.bold as u8,
                outline = self.outline,
            ),
            format!(
                "Style: Bottom,{font_name},{font_size},&H{a:02x}FFFFFF,&H00FFFFFF,&H{a:02x}000000,&H00000000,\
                {bold}, 0, 0, 0, 100, 100, 0.00, 0.00, 1, \
                {outline}, 0, 7, 0, 0, 0, 1",
                a = self.opacity as u8,
                font_name = self.font_name,
                font_size = self.font_size,
                bold = self.bold as u8,
                outline = self.outline,
            ),
            format!(
                "Style: Top,{font_name},{font_size},&H{a:02x}FFFFFF,&H00FFFFFF,&H{a:02x}000000,&H00000000,\
                {bold}, 0, 0, 0, 100, 100, 0.00, 0.00, 1, \
                {outline}, 0, 7, 0, 0, 0, 1",
                a = self.opacity as u8,
                font_name = self.font_name,
                font_size = self.font_size,
                bold = self.bold as u8,
                outline = self.outline,
            ),
        ]
    }

}
pub struct AssWriter {
    title: String,
    setting: Setting,
}
impl AssWriter {
    pub fn write(&mut self, drawable: Drawable) -> String {
        format!(
            // Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
            "Dialogue: 2,{start},{end},{style},,0,0,0,,{{{effect}\\c&H{b:02x}{g:02x}{r:02x}&}}{text}",
            start = TimePoint {
                t: drawable.element.timeline_s
            },
            end = TimePoint {
                t: drawable.element.timeline_s + drawable.duration
            },
            style = drawable.style_name,
            effect = AssEffect {
                effect: drawable.effect
            },
            b = drawable.element.color.b,
            g = drawable.element.color.g,
            r = drawable.element.color.r,
            text = escape_text(&drawable.element.content),
        )
    }
}
struct CanvasStyles(Vec<String>);
impl Display for CanvasStyles {
    fn fmt(&self, f: &mut Formatter) -> fmt::Result {
        for style in &self.0 {
            writeln!(f, "{}", style)?;
        }
        Ok(())
    }
}

fn escape_text(text: &str) -> Cow<str> {
    let text = text.trim();
    if memchr::memchr(b'\n', text.as_bytes()).is_some() {
        Cow::from(text.replace('\n', "\\N"))
    } else {
        Cow::from(text)
    }
}