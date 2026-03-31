-- Parkirin OLTP Cassandra Schema
-- Run manually or on startup via schema-action: CREATE_IF_NOT_EXISTS

CREATE KEYSPACE IF NOT EXISTS parkirin_oltp
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE parkirin_oltp;

-- parking_sessions
-- Partition: (zone_id, session_date), Clustering: id DESC
CREATE TABLE IF NOT EXISTS parking_sessions (
    zone_id         UUID,
    session_date    DATE,
    id              UUID,
    session_number  TEXT,
    plate_number    TEXT,
    vehicle_id      UUID,
    vehicle_type    TEXT,
    street_id       UUID,
    street_name     TEXT,
    entry_officer_id UUID,
    exit_officer_id UUID,
    entry_time      TIMESTAMP,
    exit_time       TIMESTAMP,
    entry_lat       DOUBLE,
    entry_lng       DOUBLE,
    entry_accuracy  FLOAT,
    exit_lat        DOUBLE,
    exit_lng        DOUBLE,
    exit_accuracy   FLOAT,
    entry_photo_url TEXT,
    exit_photo_url  TEXT,
    is_subscription BOOLEAN,
    status          TEXT,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    PRIMARY KEY ((zone_id, session_date), id)
) WITH CLUSTERING ORDER BY (id DESC);

CREATE INDEX IF NOT EXISTS idx_parking_sessions_plate ON parking_sessions (plate_number);
CREATE INDEX IF NOT EXISTS idx_parking_sessions_session_number ON parking_sessions (session_number);
CREATE INDEX IF NOT EXISTS idx_parking_sessions_entry_officer ON parking_sessions (entry_officer_id);

-- invoices
-- Partition: (zone_id, invoice_date), Clustering: id DESC
CREATE TABLE IF NOT EXISTS invoices (
    zone_id         UUID,
    invoice_date    DATE,
    id              UUID,
    invoice_number  TEXT,
    session_id      UUID,
    plate_number    TEXT,
    amount          BIGINT,
    duration_minutes INT,
    status          TEXT,
    expired_at      TIMESTAMP,
    qr_token        TEXT,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    PRIMARY KEY ((zone_id, invoice_date), id)
) WITH CLUSTERING ORDER BY (id DESC);

CREATE INDEX IF NOT EXISTS idx_invoices_qr_token ON invoices (qr_token);
CREATE INDEX IF NOT EXISTS idx_invoices_session_id ON invoices (session_id);

-- payments
-- Partition: (zone_id, payment_date), Clustering: id DESC
CREATE TABLE IF NOT EXISTS payments (
    zone_id         UUID,
    payment_date    DATE,
    id              UUID,
    invoice_id      UUID,
    method          TEXT,
    amount          BIGINT,
    receipt_number  TEXT,
    gateway_ref     TEXT,
    paid_at         TIMESTAMP,
    created_at      TIMESTAMP,
    PRIMARY KEY ((zone_id, payment_date), id)
) WITH CLUSTERING ORDER BY (id DESC);

-- lottery_entries
-- Partition: period, Clustering: id DESC
CREATE TABLE IF NOT EXISTS lottery_entries (
    period      TEXT,
    id          UUID,
    payment_id  UUID,
    plate_number TEXT,
    zone_id     UUID,
    code        TEXT,
    is_winner   BOOLEAN,
    created_at  TIMESTAMP,
    PRIMARY KEY ((period), id)
) WITH CLUSTERING ORDER BY (id DESC);

CREATE INDEX IF NOT EXISTS idx_lottery_entries_code ON lottery_entries (code);
