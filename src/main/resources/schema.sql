-- schema.sql is the file that is automatically run when application starts.
-- This file is for schema creation.

create table if not exists providers
(
    id            bigint not null
        primary key,
    provider_name varchar(255),
    provider_tin  bigint,
    specialty     varchar(255)
);

alter table if exists providers
    owner to postgres;