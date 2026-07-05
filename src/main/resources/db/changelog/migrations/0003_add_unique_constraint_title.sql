-- liquibase formatted sql
-- changeset minakdan:0003-add-unique-constraint-title
ALTER TABLE bookmarks ADD CONSTRAINT uq_bookmarks_title UNIQUE(title);
-- rollback ALTER TABLE bookmarks DROP CONSTRAINT uq_bookmarks_title;