# Implementation Plan: 图片移动动画

**Branch**: `001-photo-move-animation` | **Date**: 2026-02-18 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `specs/001-photo-move-animation/spec.md`

## Summary

本计划旨在为相册应用中的图片整理功能实现一项点击动画。当用户点击底部相册列表（一个RecyclerView）中的项目时，当前在ViewPager中显示的主图片将以动画形式平滑地移动并缩小到被点击的项目上。此实现将严格遵循项目已有的命令模式架构，通过实现`UiMutator`接口来封装UI动画逻辑，并由`PhotosMoveCommand`命令触发。

## Technical Context

**Language/Version**: Kotlin
**Primary Dependencies**: Android SDK, `androidx.appcompat`, `androidx.recyclerview`, `androidx.viewpager2`
**Storage**: N/A (UI-focused feature)
**Testing**: JUnit, Mockito (Assumed)
**Target Platform**: Android
**Project Type**: Mobile
**Performance Goals**: 动画必须流畅，达到 60 fps，避免卡顿。
**Constraints**: **必须**使用项目中已有的命令模式框架，特别是 `PhotosMoveCommand` 和 `UiMutator` 接口。
**Scale/Scope**: 此功能仅影响图片整理界面 (`SortingActivity`)。

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **I. 坚持使用Kotlin开发**: **PASS**. The entire implementation will be in Kotlin, adhering to the constitution.

## Project Structure

### Documentation (this feature)

```text
specs/001-photo-move-animation/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output (Not applicable for this feature)
├── contracts/           # Phase 1 output (Not applicable for this feature)
└── tasks.md             # Phase 2 output (To be created by /speckit.tasks)
```

### Source Code (repository root)

此功能将在现有的 `app` 模块中实现。关键文件变更如下:

```text
app/src/main/java/com/example/albumio/
├── logic/
│   ├── commandPattern/
│   │   ├── mutator/
│   │   │   └── PhotoMoveAnimationMutator.kt  # (New) Implements UiMutator for the animation
│   │   ├── Command.kt
│   │   └── ...
│   └── viewModel/
│       └── SortingViewModel.kt               # (Modified) To handle the new mutator
└── ui/
    ├── SortingActivity.kt                    # (Modified) To trigger the command on click
    └── adapter/
        └── ImageMovesButtonsAdapter.kt         # (Modified) To set up item click listener
```

**Structure Decision**: The changes will be integrated into the existing Android application structure, following established patterns within the `logic` and `ui` packages. A new `mutator` sub-package will be created to cleanly organize UI mutation logic, aligning with the command pattern's separation of concerns.

## Complexity Tracking

No violations of the constitution were identified. This section is not required.
