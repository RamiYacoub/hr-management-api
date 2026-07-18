INSERT INTO departments (name)
VALUES
    ('Information Technology'),
    ('Human Resources'),
    ('Finance')
ON CONFLICT (name) DO NOTHING;

INSERT INTO employees (name, email, address, department_id)
SELECT
    'Rami Yacoub',
    'rami.yacoub@example.com',
    'Ramallah',
    id
FROM departments
WHERE name = 'Information Technology'
ON CONFLICT (email) DO NOTHING;

INSERT INTO employees (name, email, address, department_id)
SELECT
    'Sara Ahmad',
    'sara.ahmad@example.com',
    'Jerusalem',
    id
FROM departments
WHERE name = 'Human Resources'
ON CONFLICT (email) DO NOTHING;

INSERT INTO employees (name, email, address, department_id)
SELECT
    'Omar Ali',
    'omar.ali@example.com',
    'Nablus',
    id
FROM departments
WHERE name = 'Finance'
ON CONFLICT (email) DO NOTHING;

INSERT INTO leave_types (name)
VALUES
    ('Annual'),
    ('Sick'),
    ('Unpaid')
ON CONFLICT (name) DO NOTHING;

INSERT INTO expense_types (name)
VALUES
    ('Travel'),
    ('Meals'),
    ('Accommodation'),
    ('Office Supplies')
ON CONFLICT (name) DO NOTHING;