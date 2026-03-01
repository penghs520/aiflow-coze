-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_workflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ai_workflow;

-- 插入示例工作流数据
INSERT INTO workflows (id, name, description, category, tags, cover_url, base_points, coze_workflow_id, status, sort_order, usage_count, average_rating, created_at, updated_at)
VALUES
('wf001', '视频口播剪辑', '自动识别口误并生成剪辑任务，支持多种视频格式', 'video', '["视频处理", "口播", "剪辑"]', 'https://example.com/cover1.jpg', 1000, 'coze_wf_001', 1, 100, 0, 0.0, NOW(), NOW()),
('wf002', '图文创作助手', '根据主题生成图文内容，支持多种风格', 'image', '["图文", "创作", "AI生成"]', 'https://example.com/cover2.jpg', 500, 'coze_wf_002', 1, 90, 0, 0.0, NOW(), NOW()),
('wf003', '文案生成器', '智能生成营销文案、产品描述等', 'text', '["文案", "营销", "AI写作"]', 'https://example.com/cover3.jpg', 300, 'coze_wf_003', 1, 80, 0, 0.0, NOW(), NOW()),
('wf004', '音频转文字', '高精度语音识别，支持多语言', 'audio', '["语音识别", "转录", "字幕"]', 'https://example.com/cover4.jpg', 800, 'coze_wf_004', 1, 70, 0, 0.0, NOW(), NOW()),
('wf005', '智能配音', '文字转语音，多种音色可选', 'audio', '["配音", "TTS", "语音合成"]', 'https://example.com/cover5.jpg', 600, 'coze_wf_005', 1, 60, 0, 0.0, NOW(), NOW());
