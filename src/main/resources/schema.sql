-- =============================================================================
-- ConnectFlow CRM (DialPulse) Database Schema & SQL Guide
-- Database: PostgreSQL
-- Target: connectflow_crm
-- =============================================================================

-- Step 1: Create Database (Run while connected to postgres or another DB)
-- CREATE DATABASE connectflow_crm;
-- \c connectflow_crm

-- -----------------------------------------------------------------------------
-- 1. Table: roles
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    role_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_role_name
        CHECK (role_name IN (
            'Administrative',
            'Calling Team',
            'On-field Employee'
        ))
);

INSERT INTO roles (role_name, description)
VALUES
    ('Administrative', 'Can manage institutes and access all CRM activities.'),
    ('Calling Team', 'Can access assigned institutes and record calls.'),
    ('On-field Employee', 'Can access assigned institutes and manage visits and calls.')
ON CONFLICT (role_name) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 2. Table: employees
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS employees (
    employee_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password_hash TEXT,
    role_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_employees_role
        FOREIGN KEY (role_id)
        REFERENCES roles(role_id)
        ON DELETE RESTRICT
);

-- Note: BCrypt hash below corresponds to 'password123'
INSERT INTO employees (full_name, email, phone, password_hash, role_id)
VALUES
    ('Amit Patil', 'amit.patil@example.com', NULL, '$2a$10$wW5g1tFpA5rO02LdZpG/e.1k9ZzS/H.aD9p1PjS8pL1/4v5b7y9sS',
        (SELECT role_id FROM roles WHERE role_name = 'Administrative')),
    ('Neha Shah', 'neha.shah@example.com', NULL, '$2a$10$wW5g1tFpA5rO02LdZpG/e.1k9ZzS/H.aD9p1PjS8pL1/4v5b7y9sS',
        (SELECT role_id FROM roles WHERE role_name = 'Calling Team')),
    ('Rohit More', 'rohit.more@example.com', NULL, '$2a$10$wW5g1tFpA5rO02LdZpG/e.1k9ZzS/H.aD9p1PjS8pL1/4v5b7y9sS',
        (SELECT role_id FROM roles WHERE role_name = 'On-field Employee'))
ON CONFLICT (email) DO UPDATE
    SET password_hash = COALESCE(employees.password_hash, EXCLUDED.password_hash);

-- -----------------------------------------------------------------------------
-- 3. Table: institutes
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS institutes (
    institute_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    institute_name VARCHAR(255) NOT NULL,
    street_address VARCHAR(255),
    village VARCHAR(100),
    taluka VARCHAR(100),
    district VARCHAR(100),
    state VARCHAR(100),
    pin_code VARCHAR(6),
    branch_count INTEGER,
    reference_by VARCHAR(150),
    reference_from VARCHAR(50),
    assigned_employee_id BIGINT,
    lead_status VARCHAR(30) NOT NULL DEFAULT 'New',
    priority VARCHAR(20) NOT NULL DEFAULT 'Medium',
    next_follow_up_at TIMESTAMPTZ,
    notes TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_institutes_assigned_employee
        FOREIGN KEY (assigned_employee_id)
        REFERENCES employees(employee_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_institutes_created_by
        FOREIGN KEY (created_by)
        REFERENCES employees(employee_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_institutes_updated_by
        FOREIGN KEY (updated_by)
        REFERENCES employees(employee_id)
        ON DELETE SET NULL,
    CONSTRAINT chk_institutes_branch_count
        CHECK (branch_count IS NULL OR branch_count >= 1),
    CONSTRAINT chk_institutes_pin_code
        CHECK (pin_code IS NULL OR pin_code ~ '^[0-9]{6}$'),
    CONSTRAINT chk_institutes_reference_from
        CHECK (reference_from IS NULL OR reference_from IN (
            'Employee', 'Existing Institute', 'Website', 'WhatsApp', 'Other'
        )),
    CONSTRAINT chk_institutes_lead_status
        CHECK (lead_status IN (
            'New', 'Contacted', 'Interested', 'Follow-up',
            'Converted', 'Not Interested'
        )),
    CONSTRAINT chk_institutes_priority
        CHECK (priority IN ('High', 'Medium', 'Low'))
);

-- -----------------------------------------------------------------------------
-- 4. Table: institute_contacts
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS institute_contacts (
    contact_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    institute_id BIGINT NOT NULL,
    contact_name VARCHAR(150) NOT NULL,
    designation VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(255),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_institute_contacts_institute
        FOREIGN KEY (institute_id)
        REFERENCES institutes(institute_id)
        ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- 5. Table: call_logs
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS call_logs (
    call_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    institute_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    contact_id BIGINT,
    call_type VARCHAR(20) NOT NULL,
    call_datetime TIMESTAMPTZ NOT NULL,
    contact_person VARCHAR(150),
    contact_phone VARCHAR(20),
    call_outcome VARCHAR(30) NOT NULL,
    next_follow_up_at TIMESTAMPTZ,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_call_logs_institute
        FOREIGN KEY (institute_id)
        REFERENCES institutes(institute_id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_call_logs_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_call_logs_contact
        FOREIGN KEY (contact_id)
        REFERENCES institute_contacts(contact_id)
        ON DELETE SET NULL,
    CONSTRAINT chk_call_logs_type
        CHECK (call_type IN ('Incoming', 'Outgoing')),
    CONSTRAINT chk_call_logs_outcome
        CHECK (call_outcome IN (
            'Interested', 'Call Back', 'No Answer', 'Not Interested'
        ))
);

-- -----------------------------------------------------------------------------
-- 6. Table: visits
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS visits (
    visit_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    institute_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    contact_id BIGINT,
    visit_datetime TIMESTAMPTZ NOT NULL,
    contact_person VARCHAR(150),
    contact_phone VARCHAR(20),
    visit_status VARCHAR(20) NOT NULL DEFAULT 'Scheduled',
    purpose VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_visits_institute
        FOREIGN KEY (institute_id)
        REFERENCES institutes(institute_id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_visits_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_visits_contact
        FOREIGN KEY (contact_id)
        REFERENCES institute_contacts(contact_id)
        ON DELETE SET NULL,
    CONSTRAINT chk_visits_status
        CHECK (visit_status IN ('Scheduled', 'Completed', 'Cancelled'))
);

-- -----------------------------------------------------------------------------
-- 7. Table: follow_ups
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS follow_ups (
    follow_up_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    institute_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    related_call_id BIGINT,
    scheduled_at TIMESTAMPTZ NOT NULL,
    follow_up_status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    notes TEXT,
    completed_at TIMESTAMPTZ,
    completed_by BIGINT,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_follow_ups_institute
        FOREIGN KEY (institute_id)
        REFERENCES institutes(institute_id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_follow_ups_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_follow_ups_related_call
        FOREIGN KEY (related_call_id)
        REFERENCES call_logs(call_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_follow_ups_completed_by
        FOREIGN KEY (completed_by)
        REFERENCES employees(employee_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_follow_ups_created_by
        FOREIGN KEY (created_by)
        REFERENCES employees(employee_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_follow_ups_updated_by
        FOREIGN KEY (updated_by)
        REFERENCES employees(employee_id)
        ON DELETE SET NULL,
    CONSTRAINT chk_follow_ups_status
        CHECK (follow_up_status IN ('Pending', 'Completed', 'Cancelled')),
    CONSTRAINT chk_follow_ups_completion
        CHECK (
            (follow_up_status = 'Completed' AND completed_at IS NOT NULL)
            OR
            (follow_up_status <> 'Completed' AND completed_at IS NULL)
        )
);

-- -----------------------------------------------------------------------------
-- 8. Table: audit_logs
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_logs (
    audit_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(30) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_logs_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id)
        ON DELETE SET NULL,
    CONSTRAINT chk_audit_logs_action
        CHECK (action IN ('CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT'))
);

-- -----------------------------------------------------------------------------
-- 9. Indexes
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_employees_role_id
    ON employees(role_id);
CREATE INDEX IF NOT EXISTS idx_institutes_assigned_employee
    ON institutes(assigned_employee_id);
CREATE INDEX IF NOT EXISTS idx_institutes_lead_status
    ON institutes(lead_status);
CREATE INDEX IF NOT EXISTS idx_institutes_priority
    ON institutes(priority);
CREATE INDEX IF NOT EXISTS idx_institute_contacts_institute_id
    ON institute_contacts(institute_id);
CREATE INDEX IF NOT EXISTS idx_call_logs_institute_datetime
    ON call_logs(institute_id, call_datetime DESC);
CREATE INDEX IF NOT EXISTS idx_call_logs_employee_datetime
    ON call_logs(employee_id, call_datetime DESC);
CREATE INDEX IF NOT EXISTS idx_visits_institute_datetime
    ON visits(institute_id, visit_datetime DESC);
CREATE INDEX IF NOT EXISTS idx_visits_employee_datetime
    ON visits(employee_id, visit_datetime DESC);
CREATE INDEX IF NOT EXISTS idx_follow_ups_institute_scheduled
    ON follow_ups(institute_id, scheduled_at);
CREATE INDEX IF NOT EXISTS idx_follow_ups_employee_status_date
    ON follow_ups(employee_id, follow_up_status, scheduled_at);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity
    ON audit_logs(entity_type, entity_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_logs_employee
    ON audit_logs(employee_id, created_at DESC);

-- -----------------------------------------------------------------------------
-- 10. Automatic updated_at timestamps (Trigger function and triggers)
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_roles_updated_at ON roles;
CREATE TRIGGER trg_roles_updated_at
BEFORE UPDATE ON roles
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_employees_updated_at ON employees;
CREATE TRIGGER trg_employees_updated_at
BEFORE UPDATE ON employees
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_institutes_updated_at ON institutes;
CREATE TRIGGER trg_institutes_updated_at
BEFORE UPDATE ON institutes
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_institute_contacts_updated_at ON institute_contacts;
CREATE TRIGGER trg_institute_contacts_updated_at
BEFORE UPDATE ON institute_contacts
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_call_logs_updated_at ON call_logs;
CREATE TRIGGER trg_call_logs_updated_at
BEFORE UPDATE ON call_logs
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_visits_updated_at ON visits;
CREATE TRIGGER trg_visits_updated_at
BEFORE UPDATE ON visits
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_follow_ups_updated_at ON follow_ups;
CREATE TRIGGER trg_follow_ups_updated_at
BEFORE UPDATE ON follow_ups
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- -----------------------------------------------------------------------------
-- 11. Sample Data (Section 17 from Guide)
-- -----------------------------------------------------------------------------
INSERT INTO institutes (
    institute_name, street_address, village, taluka, district, state,
    pin_code, branch_count, reference_by, reference_from,
    assigned_employee_id, lead_status, priority, notes, created_by
)
SELECT
    'Shree Ganesh Co Op Credit Society Ltd.',
    'Demo Market Road', 'Shivajinagar', 'Haveli', 'Pune',
    'Maharashtra', '411005', 5, 'Amit Patil', 'Employee',
    (SELECT employee_id FROM employees WHERE email = 'amit.patil@example.com'),
    'Interested', 'High',
    'Interested in the software demonstration.',
    (SELECT employee_id FROM employees WHERE email = 'amit.patil@example.com')
WHERE NOT EXISTS (
    SELECT 1 FROM institutes WHERE institute_name = 'Shree Ganesh Co Op Credit Society Ltd.'
);

INSERT INTO institute_contacts (
    institute_id, contact_name, designation, phone, is_primary
)
SELECT institute_id, 'Sunil Patil', 'Manager', '9876543210', TRUE
FROM institutes
WHERE institute_name = 'Shree Ganesh Co Op Credit Society Ltd.'
  AND NOT EXISTS (
    SELECT 1 FROM institute_contacts WHERE contact_name = 'Sunil Patil'
);

-- =============================================================================
-- Verification Queries:
-- =============================================================================
-- 1. Check tables:
-- SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE' ORDER BY table_name;
--
-- 2. Check institutes with assigned employee:
-- SELECT i.institute_id, i.institute_name, i.district, i.state, i.branch_count,
--        i.lead_status, i.priority, e.full_name AS assigned_employee, r.role_name AS employee_role
-- FROM institutes i
-- LEFT JOIN employees e ON i.assigned_employee_id = e.employee_id
-- LEFT JOIN roles r ON e.role_id = r.role_id
-- ORDER BY i.institute_id;
