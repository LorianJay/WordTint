# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

WordTint 是一款极简英语背单词 Android 应用，理念是"回归基本功"，将背诵控制权完全交还给用户。核心功能包括：单词标记（打标签）、变色龙模式（按颜色筛选专注背诵）、按色打乱、区间重背、单词搜索、文本分段等。

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Java 8 |
| 构建 | Gradle (Groovy DSL) + AGP 7.4.2 |
| 数据库 | Room 2.5.2 |
| Markdown 渲染 | Markwon 4.6.2 + 自定义插件 |
| 图片加载 | Glide 4.15.1 |
| JSON | Gson 2.10.1 + json-path 2.8.0 |
| UI | AndroidX, ViewBinding, ViewPager2, Navigation, Material Design, Flexbox |

## 架构

### 整体分层

```
ui (Activity/Fragment/Adapter/ViewModel)
    ↓
handler (业务逻辑处理器)
    ↓
database (Room: Entity/DAO/VO + Repository)
```

没有使用正式的 MVVM 架构（无 ViewModel 层用于 Activity/Fragment），而是采用 **Handler 模式** 处理核心业务逻辑。ViewModels（如 `BookViewModel`、`WordSearchViewModel`）仅用于 Adapter 数据绑定，不承担业务逻辑。

### 核心组件

**StaticFactory** (`context/factory/StaticFactory.java`) — 全局单例工厂，使用静态内部类 Holder 模式提供：
- `Gson` 实例
- 全局 `ExecutorService`（线程池：核心4、最大8、SynchronousQueue）
- 全局 `Markwon` 实例（已集成 ImagesPlugin、HtmlPlugin、GlobalMarkwonPlugin）
- `CssInlineStyleParser` 单例
- `WordAudioHandler` 单例

所有耗时操作（数据库查询、文件 IO）都应通过 `StaticFactory.getExecutorService()` 提交。

**APPDatabase** (`database/APPDatabase.java`) — Room 数据库，单例模式，包含 12 个 Entity、11 个 DAO。首次启动时从 `assets/sql/` 目录读取 SQL 文件执行初始化，然后解压 `assets/audio/audio.zip` 音频文件。数据库版本为 6。

**UserSettingRepository** (`database/rep/UserSettingRepository.java`) — 用户设置仓库，基于 key-value 模式（`UserSettingKeyEnums` 枚举定义所有 key），值通过反射调用 `valueOf` 反序列化为目标类型。

**WordFunctionHandler** (`handler/WordFunctionHandler.java`) — 单词背诵功能的核心接口，定义：
- 单词列表导航（上下翻页、跳转、按颜色变色龙模式）
- 按色打乱 / 区间重背 / 还原列表
- 背诵进度保存
- 实现类 `WordFunctionHandlerImpl` 包含完整的背诵状态机

**StarFunctionHandler** — 单词标记（收藏夹）功能接口，管理单词与标记颜色的关联。

### 页面结构

| Activity | 功能 |
|----------|------|
| `MainActivity` | 主页面，底部四栏导航（背诵/记录/分析/设置），ViewPager2 管理 Fragment 切换 |
| `MainReciteActivity` | 核心背诵页面，侧边栏抽屉，单词卡片翻页，变色龙/标记面板 |
| `SearchWordActivity` | 单词搜索，支持在线查询和本地收藏管理 |
| `WordReciteLaunchActivity` | 背诵启动页，选择词书和背诵偏好 |
| `TextSegmentationActivity` | 文本分段，将长文本按段落拆分为背诵单元 |
| `MockExamineActivity` | 模拟考试 |
| `WelcomeActivity` | 首次启动欢迎页，用户协议确认 |
| `AdvancedSettingActivity` | 高级设置 |

### Fragment（MainActivity 四个标签页）

- `BookListFragment` — 词书列表
- `RecordListFragment` — 背诵记录
- `AnalysisFragment` — 数据分析
- `SettingFragment` — 设置

### Markdown 渲染定制

`ui/markdown/` 包下基于 Markwon 框架做了深度定制：
- `GlobalMarkwonPlugin` — 全局插件，注册自定义 Handler、SpanFactory、LinkResolver
- `spanfactory/` — 自定义 `AppHeadingSpanFactory`、`AppLinkSpanFactory`
- `spanfactory/span/` — 自定义 Span 实现（`AppHeadingSpan`、`AppLinkSpan`、`NoUnderLineHeadingSpan`）
- `movementmethod/` — `ClickableSpanMovementMethod` 处理点击事件
- `handler/` — `SpanHandler` 处理 HTML `<span>` 标签、`APPLinkResolver` 处理内部链接跳转

### Adapter 目录结构

`ui/adapter/` 按功能分类：
- `book/` — 词书列表相关（含 `BookViewModel`）
- `star/` — 标记/收藏相关
- `wordsearch/` — 单词搜索相关（含 `WordSearchViewModel`）
- `history/` — 背诵记录相关（含 `RecordViewModel`）
- `morefeatures/segmentation/` — 文本分段相关（含 `TextSegmentationViewModel`）
- `markarea/` — 标记区域
- `customview/` — 自定义 View（`FlowingBorderView`、`RoundImageView`、`CopyTextView` 等）
- `common/` — 通用（`LoadMoreAdapter`、`SimpleItemTouchHelperCallback`）
- `listener/` — 回调接口

## 数据库表关系

核心表：
- `WordBookEntity` / `WordBookSectionEntity` / `WordBookSectionWordIdEntity` — 词书 > 章节 > 单词 ID 三层结构
- `WordOriginEntity` — 单词原始数据（词源信息）
- `WordStarEntity` / `WordStarWordIdEntity` — 收藏夹及关联
- `WordSearchEntity` — 搜索记录
- `ReciteRecordEntity` / `ReciteRecordWordEntity` / `ReciteRecordWordMarkEntity` — 背诵记录
- `WordNoteEntity` — 单词笔记
- `UserSettingEntity` — 用户设置（key-value）

## 注意事项

- 项目使用 `package com.github.lorenj.wordtint`（注意 `lorenj` 而非 `lorianjay`）
- 混淆规则在 `app/proguard-rules.pro`：保留 Room 实体和 DAO、WebView JS 接口、json-path 相关类
- Release 构建开启 `minifyEnabled` 和 `shrinkResources`
- 蓝牙权限用于音频播放设备连接
- 数据库 schema 导出到 `app/schemas/` 目录
- 新功能需求文档记录在 `Demand/` 目录下
