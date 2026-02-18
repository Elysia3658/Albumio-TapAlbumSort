# Tasks: 图片移动动画与浏览

**Input**: Design documents from `/specs/001-photo-move-animation/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Tests**: Tests are not included as they were not explicitly requested.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

## Path Conventions

- Paths are based on the structure defined in `plan.md`.

---

## Phase 1: Setup

**Purpose**: Prepare the project structure for the new animation logic.

- [ ] T001 Create new package `app/src/main/java/com/example/albumio/logic/commandPattern/mutator/` for UI mutators.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented.

- Not applicable for this feature as it builds upon existing architecture.

---

## Phase 3: User Story 1 - 动画化图片到相册 (Priority: P1) 🎯 MVP

**Goal**: When a user clicks a destination album in the bottom recycler view, the main photo animates smoothly towards the clicked item, providing clear visual feedback for the move operation.

**Independent Test**: Navigate to the `SortingActivity`. With a photo displayed, click on any album icon in the bottom `RecyclerView`. The animation should play correctly, and the underlying photo move logic should be triggered.

### Implementation for User Story 1

- [ ] T002 [US1] Create the `PhotoMoveAnimationMutator.kt` file in `app/src/main/java/com/example/albumio/logic/commandPattern/mutator/`. This class must implement the `UiMutator` interface and contain the core animation logic.
- [ ] T003 [US1] Modify `ImageMovesButtonsAdapter.kt` in `app/src/main/java/com/example/albumio/ui/adapter/` to set an `onClickListener` on the item view that passes the clicked view back to the `SortingActivity`.
- [ ] T004 [US1] Modify `SortingActivity.kt` in `app/src/main/java/com/example/albumio/ui/` to handle the click from the adapter, calculate coordinates, and trigger the `PhotosMoveCommand`.
- [ ] T005 [US1] Modify `SortingViewModel.kt` in `app/src/main/java/com/example/albumio/logic/viewModel/` to integrate the `PhotoMoveAnimationMutator` with the `photosMove` command logic.
- [ ] T006 [US1] In `SortingActivity.kt`, implement logic to prevent new clicks on the album list while the animation is running (to cover requirement FR-005).

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently.

---

## Phase 4: User Story 2 - 查看下一张图片 (Priority: P2)

**Goal**: Allows the user to navigate to the next image in the sequence from the sorting screen.

**Independent Test**: Navigate to the `SortingActivity`. Click the new 'Next Image' button. The main view should show the next image in the album.

### Implementation for User Story 2

- [ ] T007 [P] [US2] Add a "Next Image" button or another suitable UI element to the `res/layout/activity_sorting.xml` layout file.
- [ ] T008 [US2] In `SortingActivity.kt`, implement a click listener for the new "Next Image" button. This listener must trigger the existing command for viewing the next image, presumably via the `SortingViewModel`.

**Checkpoint**: At this point, User Stories 1 and 2 should both be functional.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements and cleanup for all new functionality.

- [ ] T009 [P] Review and add KDoc comments to all new or modified classes and methods for both User Story 1 and 2.
- [ ] T010 Code cleanup and refactoring of the new animation and button logic to ensure it is readable and maintainable.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Can start immediately.
- **User Stories (Phase 3 & 4)**: Depend on Setup completion. US1 and US2 can be worked on in parallel by different developers if desired.
- **Polish (Phase 5)**: Depends on all user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)** and **User Story 2 (P2)** are independent of each other and can be implemented in any order after Phase 1, or in parallel.

---

## Implementation Strategy

### Incremental Delivery

1. Complete Phase 1: Setup.
2. Complete Phase 3: User Story 1 → Test independently → MVP is ready.
3. Complete Phase 4: User Story 2 → Test independently.
4. Complete Phase 5: Polish.
