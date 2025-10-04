mod danmaku_elem;
mod lane;
mod danmu_setting;
mod drawable;
#[derive(Debug, thiserror::Error)]
#[error("{e:?}")]
pub struct ErrorInterface {
    e: anyhow::Error,
}
impl ErrorInterface {
    fn chain(&self) -> Vec<String> {
        self.e.chain().map(ToString::to_string).collect()
    }
    fn link(&self, ndx: u64) -> Option<String> {
        self.e.chain().nth(ndx as usize).map(ToString::to_string)
    }
}

// A conversion into our ErrorInterface from anyhow::Error.
// We can't use this implicitly yet, but it still helps.
impl From<anyhow::Error> for ErrorInterface {
    fn from(e: anyhow::Error) -> Self {
        Self { e }
    }
}
// This generates extra Rust code required by UniFFI.
uniffi::setup_scaffolding!();