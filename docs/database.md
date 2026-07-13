# Echo Protocol Database Design

## Overview

Echo Protocol uses Zoho Catalyst Data Store as the primary relational database.

The solution is based on the Karnataka Police FIR database schema and has been designed to support crime analytics, criminological network analysis, predictive policing, and AI-powered intelligence.

The central entity of the system is **CaseMaster**, which connects all transactional and master data.

---

# Database Design Strategy

Instead of creating every table at once, the implementation follows three phases.

## Phase 1 (MVP)

- CaseMaster
- Victim
- Accused
- ComplainantDetails
- ArrestSurrender
- District
- Unit
- Employee
- CrimeHead
- CrimeSubHead
- Court
- CaseStatusMaster

These tables support dashboards and analytics.

---

## Phase 2

Master tables

- Act
- Section
- CrimeHeadActSection
- ActSectionAssociation
- Rank
- Designation
- OccupationMaster
- ReligionMaster
- CasteMaster
- State
- UnitType
- GravityOffence
- CaseCategory

These tables enrich analytics.

---

## Phase 3

Operational tables

- ChargesheetDetails
- Remaining lookup tables
- Junction tables

This phase completes the original police schema.

---

## Design Principles

- Normalize lookup data.
- Keep CaseMaster as the transactional hub.
- Support AI.
- Support dashboard drill-down.
- Support future scalability.