-- liquibase formatted sql

-- changeset minakdan:0002-add-bookmarks-tags-gin-index
CREATE INDEX idx_bookmarks_tags ON bookmarks USING gin (tags);
-- rollback DROP INDEX idx_bookmarks_tags;
