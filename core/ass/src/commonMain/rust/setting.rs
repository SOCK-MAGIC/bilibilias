use derive_builder::Builder;
#[derive(Default, Builder, Debug)]
#[builder(setter(into))]
pub struct Setting {
    #[builder(default = "1920")]
    /// 屏幕宽度
    pub width: u32,

    #[builder(default = "1080")]
    /// 屏幕高度
    pub height: u32,

    #[builder(default = "\"黑体\".to_string()")]
    /// 弹幕使用字体
    pub font_name: String,

    #[builder(default = "25")]
    /// 弹幕字体大小
    pub font_size: u32,

    #[builder(default = "1.2")]
    /// 计算弹幕宽度的比例
    pub width_ratio: f64,

    #[builder(default = "20.0")]
    /// 每条弹幕之间的最小水平间距
    pub horizontal_gap: f64,

    #[builder(default = "15.0")]
    /// 弹幕在屏幕上的持续时间
    pub duration: f64,

    #[builder(default = "32")]
    /// 弹幕所占据的高度，即“行高度/行间距”
    pub lane_size: u32,

    #[builder(default = "0.5")]
    /// 屏幕上滚动弹幕最多高度百分比
    pub float_percentage: f64,

    #[builder(default = "0.7")]
    /// 弹幕透明度 (Opacity 更准确，1.0 为不透明)
    pub opacity: f64,

    #[builder(default = "0.8")]
    /// 描边宽度
    pub outline: f64,

    #[builder(default = "false")]
    /// 加粗
    pub bold: bool,

    #[builder(default = "0.0")]
    /// 时间轴偏移
    pub time_offset: f64,
}
