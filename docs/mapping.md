# Соответствие диаграмм и кода

Пакет: `io.github.nmaksimka.bugtrack`. Номера изменений (К…, М…) — из [changes.md](changes.md).

## Диаграмма классов МПО/МПП → сущности

| Класс на диаграмме | Класс в коде | Таблица | Методы МПП → методы в коде |
|---|---|---|---|
| Пользователь | `user.User` | `users` | войти/выйти → Spring Security (`security.SecurityConfig`, `DatabaseUserDetailsService`); добавлены `block`, `unblock`, `changePassword` (К3, К4) |
| Участник проекта (`Membership`) | `project.Membership` | `memberships`, `membership_roles` | изменитьРоли → `changeRoles`, отключить → `disable` |
| Проект | `project.Project` | `projects` | добавитьУчастника → `addMember`, изменитьДедлайн → `changeDeadline` (+ запись `ProjectHistoryEntry`, К16) |
| Решение триажа | `bug.Triage` | `triage_decisions` | сохранить → `TriageRepository` (этап 2) |
| Баг | `bug.Bug` | `bugs` | назначить → `assign`, начатьРаботу → `startWork`, передатьНаПроверку → `submitForReview`, закрыть → `close` (вызывается из `VerificationResult.confirm`, К21), вернутьВРаботу → `returnToWork`; добавлены `accept`, `requestClarification`, `markDuplicate`, `reject` (К7) |
| Релиз | `release.Release` | `releases` | добавитьБаг → `addBug`, проверитьГотовность → `isReady` / `findObstacles`, выпустить → `release`; добавлен `cancel` (К10) |
| Связь багов | `bug.BugLink` | `bug_links` | связать → конструктор `BugLink(source, target, type)` |
| Элемент релиза | `release.ReleaseItem` | `release_items` | перенести → `moveTo`, исключить → `exclude` |
| Попытка исправления | `fix.FixAttempt` | `fix_attempts`, `fix_attempt_links` | передать(проверяющий) → конструктор с проверкой «проверяющий ≠ разработчик» |
| Результат проверки | `fix.VerificationResult` | `verification_results` | подтвердить(релиз) → `confirm`, вернуть(причина) → `returnToDeveloper` |
| Комментарий | `discussion.Comment` | `comments` | добавить → `Comment.of`; препятствие → `Comment.obstacle` (К22) |
| Вложение | `discussion.Attachment` | `attachments` | загрузить/скачать → сервис файлов (этап 2) |
| Запись истории | `discussion.HistoryEntry` | `history_entries` | записать → сохраняется сервисами при каждом действии |
| — (нет на диаграмме) | `notification.Notification` | `notifications` | К19 |
| — (нет на диаграмме) | `project.ProjectHistoryEntry` | `project_history` | К25 |

## Перечисления

| На диаграмме | В коде | Значения |
|---|---|---|
| Role | `project.ProjectRole` | MANAGER, TEAM_LEAD, DEVELOPER, TESTER |
| — | `user.SystemRole` | USER, ADMIN |
| BugStatus | `bug.BugStatus` | NEW, NEEDS_INFO, ASSIGNED, IN_PROGRESS, IN_REVIEW, CLOSED, REJECTED, DUPLICATE |
| Priority | `bug.Priority` | HIGH, MEDIUM, LOW |
| Severity | `bug.Severity` | CRITICAL, MAJOR, MINOR |
| TriageDecision | `bug.TriageDecision` | ACCEPTED, NEEDS_INFO, DUPLICATE, REJECTED |
| LinkType | `bug.LinkType` | DUPLICATE, RELATED, REGRESSION, BLOCKS |
| ReleaseStatus | `release.ReleaseStatus` | PLANNED, RELEASED, CANCELLED |
| Verdict | `fix.Verdict` | CONFIRMED, RETURNED |

## Диаграмма компонентов → код

| Компонент | Где в коде |
|---|---|
| Интерфейс пользователя | `templates/**` (Thymeleaf), `static/css/app.css`, контроллеры `web.*` |
| Сервер приложения | сервисы `*.…Service`, доменные классы |
| Проверка доступа | `security.*` + `ProjectService.requireMembership` |
| Работа с файлами / Хранилище файлов | папка `bugtrack.storage-dir` (этап 2) |
| База данных | PostgreSQL, миграции `db/migration` (Flyway) |
| Уведомления → почтовый сервер (К19, С1) | `notification.*`, Mailpit в `compose.yaml` (этап 3) |

## Макеты → страницы

| Макет | Адрес | Этап |
|---|---|---|
| 01 Вход | `/login` | готово |
| — Регистрация (М4) | `/register` | готово |
| 02 Проекты | `/projects` | готово |
| 11 Участники и настройки | `/projects/{id}` (просмотр) | просмотр готов, редактирование — этап 4 |
| 03 Баги, 04 Создать баг, 05 Карточка, 06 Триаж | `/projects/{id}/bugs…` | этап 2 |
| 07 Передать исправление, 08 Проверка, 09 Доска, 10 Релизы | | этап 3 |
| 12 Отчёт, админ (М5) | | этап 4 |
