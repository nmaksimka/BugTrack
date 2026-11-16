create table users (
    id            uuid primary key,
    name          varchar(100) not null,
    email         varchar(255) not null unique,
    password_hash varchar(100) not null,
    system_role   varchar(20)  not null,
    active        boolean      not null,
    created_at    timestamptz  not null
);

create table projects (
    id           uuid primary key,
    name         varchar(150) not null,
    project_key  varchar(10)  not null unique,
    description  text,
    repository   varchar(500),
    deadline     date,
    bug_sequence integer      not null,
    created_at   timestamptz  not null
);

create table memberships (
    id         uuid primary key,
    user_id    uuid        not null references users (id),
    project_id uuid        not null references projects (id),
    active     boolean     not null,
    created_at timestamptz not null,
    unique (user_id, project_id)
);

create table membership_roles (
    membership_id uuid        not null references memberships (id) on delete cascade,
    role          varchar(30) not null,
    primary key (membership_id, role)
);

create table releases (
    id           uuid primary key,
    project_id   uuid         not null references projects (id),
    version      varchar(50)  not null,
    description  text,
    planned_date date,
    released_at  timestamptz,
    status       varchar(20)  not null,
    unique (project_id, version)
);

create table bugs (
    id           uuid primary key,
    project_id   uuid         not null references projects (id),
    number       integer      not null,
    title        varchar(200) not null,
    description  text,
    steps        text         not null,
    expected     text         not null,
    actual       text         not null,
    environment  varchar(500) not null,
    status       varchar(30)  not null,
    priority     varchar(20)  not null,
    severity     varchar(20)  not null,
    deadline     date,
    returned     boolean      not null,
    author_id    uuid         not null references memberships (id),
    assignee_id  uuid references memberships (id),
    created_at   timestamptz  not null,
    updated_at   timestamptz  not null,
    version      bigint       not null,
    unique (project_id, number)
);

create table triage_decisions (
    id         uuid primary key,
    bug_id     uuid        not null references bugs (id),
    decision   varchar(30) not null,
    reason     text,
    author_id  uuid        not null references memberships (id),
    decided_at timestamptz not null
);

create table bug_links (
    id        uuid primary key,
    source_id uuid        not null references bugs (id),
    target_id uuid        not null references bugs (id),
    type      varchar(20) not null,
    unique (source_id, target_id, type)
);

create table release_items (
    id               uuid primary key,
    release_id       uuid    not null references releases (id),
    bug_id           uuid    not null references bugs (id),
    blocks_release   boolean not null,
    excluded         boolean not null,
    exclusion_reason text,
    unique (release_id, bug_id)
);

create table fix_attempts (
    id                uuid primary key,
    bug_id            uuid        not null references bugs (id),
    number            integer     not null,
    description       text        not null,
    no_link_reason    text,
    build             varchar(200) not null,
    environment       varchar(500) not null,
    instruction       text        not null,
    developer_id      uuid        not null references memberships (id),
    reviewer_id       uuid        not null references memberships (id),
    submitted_at      timestamptz not null,
    unique (bug_id, number)
);

create table fix_attempt_links (
    fix_attempt_id uuid          not null references fix_attempts (id) on delete cascade,
    position       integer       not null,
    url            varchar(1000) not null,
    primary key (fix_attempt_id, position)
);

create table verification_results (
    id             uuid primary key,
    fix_attempt_id uuid        not null unique references fix_attempts (id),
    verdict        varchar(20) not null,
    actual_result  text        not null,
    return_reason  text,
    fix_release_id uuid references releases (id),
    tester_id      uuid        not null references memberships (id),
    verified_at    timestamptz not null
);

create table comments (
    id         uuid primary key,
    bug_id     uuid        not null references bugs (id),
    author_id  uuid        not null references memberships (id),
    text       text        not null,
    obstacle   boolean     not null,
    created_at timestamptz not null
);

create table attachments (
    id                     uuid primary key,
    bug_id                 uuid         not null references bugs (id),
    comment_id             uuid references comments (id),
    verification_result_id uuid references verification_results (id),
    file_name              varchar(255) not null,
    content_type           varchar(100) not null,
    size                   bigint       not null,
    storage_key            varchar(100) not null,
    uploaded_by_id         uuid         not null references memberships (id),
    uploaded_at            timestamptz  not null
);

create table history_entries (
    id         uuid primary key,
    bug_id     uuid         not null references bugs (id),
    author_id  uuid         not null references memberships (id),
    action     varchar(100) not null,
    old_value  text,
    new_value  text,
    created_at timestamptz  not null
);

create table project_history (
    id         uuid primary key,
    project_id uuid         not null references projects (id),
    author_id  uuid         not null references memberships (id),
    action     varchar(100) not null,
    old_value  text,
    new_value  text,
    reason     text,
    created_at timestamptz  not null
);

create table notifications (
    id           uuid primary key,
    recipient_id uuid         not null references users (id),
    text         varchar(500) not null,
    link         varchar(500),
    read         boolean      not null,
    created_at   timestamptz  not null
);

create index idx_bugs_project_status on bugs (project_id, status);
create index idx_bugs_assignee on bugs (assignee_id);
create index idx_history_bug on history_entries (bug_id, created_at);
create index idx_comments_bug on comments (bug_id, created_at);
create index idx_notifications_recipient on notifications (recipient_id, read);
