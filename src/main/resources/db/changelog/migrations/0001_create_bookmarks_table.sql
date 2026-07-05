-- liquibase formatted sql

-- changeset minakdan:0001-create-bookmarks-table
CREATE TABLE bookmarks
(
    id          UUID                     NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    url         VARCHAR(2000)            NOT NULL,
    title       VARCHAR(300)             NOT NULL,
    description TEXT,
    tags        TEXT[]                   NOT NULL DEFAULT '{}',
    is_favorite BOOLEAN                  NOT NULL DEFAULT false,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
-- rollback DROP TABLE bookmarks;
