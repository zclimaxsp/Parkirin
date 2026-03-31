CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE tariff_zones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    description TEXT,
    boundary GEOMETRY(POLYGON, 4326),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_tariff_zones_boundary ON tariff_zones USING GIST (boundary);

CREATE TABLE tariff_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_tariff_zone UUID NOT NULL REFERENCES tariff_zones(id),
    vehicle_type TEXT NOT NULL CHECK (vehicle_type IN ('MOTORCYCLE','CAR','TRUCK')),
    first_duration_hours INT NOT NULL DEFAULT 2,
    first_rate BIGINT NOT NULL,
    per_hour_rate BIGINT NOT NULL,
    max_daily_rate BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

CREATE TABLE streets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    segment TEXT,
    city TEXT,
    id_tariff_zone UUID REFERENCES tariff_zones(id),
    path GEOMETRY(LINESTRING, 4326),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_streets_path ON streets USING GIST (path);

CREATE TABLE officers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name TEXT NOT NULL,
    nip TEXT,
    phone TEXT,
    assigned_street_id UUID REFERENCES streets(id),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_officers_user_id ON officers (user_id);

CREATE TABLE vehicles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plate_number TEXT NOT NULL UNIQUE,
    vehicle_type TEXT NOT NULL CHECK (vehicle_type IN ('MOTORCYCLE','CAR','TRUCK')),
    owner_name TEXT,
    owner_phone TEXT,
    owner_user_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_vehicles_plate ON vehicles (plate_number);

CREATE TABLE subscription_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    vehicle_type TEXT NOT NULL CHECK (vehicle_type IN ('MOTORCYCLE','CAR','TRUCK')),
    price BIGINT NOT NULL,
    duration_days INT NOT NULL DEFAULT 30,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_vehicle UUID NOT NULL REFERENCES vehicles(id),
    id_plan UUID NOT NULL REFERENCES subscription_plans(id),
    id_tariff_zone UUID NOT NULL REFERENCES tariff_zones(id),
    user_id UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','EXPIRED','CANCELLED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_subscriptions_vehicle ON subscriptions (id_vehicle);
CREATE INDEX idx_subscriptions_status ON subscriptions (status);

-- Seed: demo tariff zone (Jakarta center area as example polygon)
INSERT INTO tariff_zones (id, name, description, boundary) VALUES (
    gen_random_uuid(),
    'Zone A - City Center',
    'Central business district on-street parking zone',
    ST_GeomFromText('POLYGON((106.8270 -6.2088, 106.8370 -6.2088, 106.8370 -6.1988, 106.8270 -6.1988, 106.8270 -6.2088))', 4326)
);

-- Seed: tariff rules for Zone A
WITH zone AS (SELECT id FROM tariff_zones WHERE name = 'Zone A - City Center')
INSERT INTO tariff_rules (id_tariff_zone, vehicle_type, first_duration_hours, first_rate, per_hour_rate, max_daily_rate)
SELECT zone.id, v.type, v.first_hours, v.first_rate, v.per_hour, v.max_daily FROM zone,
(VALUES
    ('MOTORCYCLE', 2, 2000, 1000, 10000),
    ('CAR',        2, 5000, 3000, 25000),
    ('TRUCK',      2, 10000, 5000, 50000)
) AS v(type, first_hours, first_rate, per_hour, max_daily);

-- Seed: demo street
WITH zone AS (SELECT id FROM tariff_zones WHERE name = 'Zone A - City Center')
INSERT INTO streets (name, segment, city, id_tariff_zone)
SELECT 'Jl. Sudirman', 'Blok A KM 1-3', 'Jakarta', zone.id FROM zone;

-- Seed: subscription plans
INSERT INTO subscription_plans (name, vehicle_type, price, duration_days, description) VALUES
    ('Monthly Motorcycle', 'MOTORCYCLE', 50000,  30, 'Monthly subscription for motorcycles'),
    ('Monthly Car',        'CAR',        150000, 30, 'Monthly subscription for cars'),
    ('Annual Motorcycle',  'MOTORCYCLE', 500000, 365,'Annual subscription for motorcycles'),
    ('Annual Car',         'CAR',        1500000,365,'Annual subscription for cars');
