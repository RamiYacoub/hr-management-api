# Assumptions

This document lists the assumptions made during the implementation of the HR Management API where the assessment requirements did not explicitly define the expected behavior.

---

## General

- Entity IDs are generated automatically by the database.
- Department names, Leave Type names, and Expense Type names are unique.
- Employee email addresses are unique.

---

## Leave Management

- A leave request must be associated with an existing employee.
- A leave request must reference an existing leave type.
- The leave start date cannot be after the end date.
- Leave duration is calculated as the inclusive number of days between the start and end dates.
- Multiple leave requests are allowed for the same employee.

---

## Expense Claims

- Every expense claim belongs to one employee.
- Every expense claim contains at least one expense entry.
- Every expense entry references an existing expense type.
- The expense claim total is calculated automatically as the sum of all expense entry totals.
- A newly created expense claim is assigned the `DRAFT` status by default.

---

## Delete Operations

To preserve data integrity, the following entities cannot be deleted while they are referenced by other records:

- Department
- Leave Type
- Expense Type

An appropriate error response is returned instead.

---

## Search

- Employee name and email searches support partial and case-insensitive matching.