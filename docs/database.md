# Echo Protocol Database Design

# Zoho Catalyst Data Store Schema

---

# 1. Overview

Echo Protocol uses **Zoho Catalyst Data Store** as its primary relational database for storing crime records, master data, and operational information.

The database design is inspired by the Karnataka State Police FIR schema and follows a normalized relational model. The schema supports crime management, operational analytics, AI-powered crime severity prediction, and future intelligence capabilities.

The central entity of the system is **CaseMaster**, which stores each registered crime and references multiple master tables using foreign keys.

---

# 2. Database Objectives

The database has been designed to:

- Centralize crime records
- Maintain normalized master data
- Support interactive dashboards
- Enable advanced analytics
- Provide datasets for Machine Learning
- Support future AI Intelligence modules
- Ensure scalability and maintainability

---

# 3. Database Platform

| Component | Technology |
|------------|------------|
| Database | Zoho Catalyst Data Store |
| Type | Relational Database |
| Primary Entity | CaseMaster |
| Storage | Structured Tables |
| AI Dataset Source | Catalyst Data Store |

---

# 4. Database Architecture

```
                  +---------------------+
                  |     CaseMaster      |
                  +----------+----------+
                             |
        +--------------------+--------------------+
        |         |          |         |          |
        ▼         ▼          ▼         ▼          ▼
   District   PoliceStation CrimeHead Gravity  CaseStatus
        |                                   |
        ▼                                   ▼
   Master Tables                     Analytics & AI
                                             |
                                             ▼
                                 Crime Prediction Dataset
                                             |
                                             ▼
                                      Catalyst QuickML
```

---

# 5. Core Transaction Table

## CaseMaster

The **CaseMaster** table is the primary transactional table and acts as the central hub of the database.

### Responsibilities

- Store crime records
- Maintain FIR information
- Reference lookup tables
- Support dashboard analytics
- Provide training data for AI models

### Key Fields

- Crime Number
- FIR Number
- Registered Date
- District ID
- Police Station ID
- Crime Head ID
- Crime Classification
- Gravity
- Case Status

---

# 6. Master Tables

Master tables eliminate redundancy and maintain data consistency.

## Administrative Masters

- District
- Police Station (Unit)
- State *(Future)*
- Unit Type *(Future)*

---

## Crime Masters

- Crime Head
- Crime Sub Head
- Gravity Offence
- Case Category
- Case Status Master

---

## Legal Masters *(Future)*

- Act
- Section
- Crime Head Act Section
- Act Section Association

---

## Personnel Masters *(Future)*

- Employee
- Rank
- Designation

---

## Demographic Masters *(Future)*

- Occupation
- Religion
- Caste

---

# 7. Operational Tables

Operational tables store investigation-specific information.

Current

- Victim
- Accused
- Complainant Details
- Arrest / Surrender
- Court

Future

- Chargesheet Details
- Evidence
- Witness
- Investigation Log

---

# 8. Database Relationships

```
District

    │

    ▼

Police Station

    │

    ▼

CaseMaster

 ┌──┼──────────┬──────────┬─────────┐

 ▼  ▼          ▼          ▼         ▼

CrimeHead  Gravity  CaseStatus  Victim  Accused
```

The CaseMaster table references multiple master tables through foreign keys, ensuring a normalized and scalable database structure.

---

# 9. AI Dataset Generation

Crime records stored in CaseMaster are transformed into a machine learning dataset for Catalyst QuickML.

### Features Used

- District
- Police Station
- Crime Classification
- Crime Head
- Status

### Target Variable

- Gravity

### Excluded Fields

- Crime Number
- FIR Number
- Registered Date

This processed dataset is used to train the Random Forest Classification model for crime severity prediction.

---

# 10. Design Principles

The database follows these principles:

- Third Normal Form (3NF)
- Centralized transaction management
- Normalized lookup tables
- Referential integrity
- Scalable schema design
- AI-ready data structure
- Separation of transactional and master data

---

# 11. Scalability Strategy

The implementation is divided into phases.

## Phase 1 – MVP

Implemented tables:

- CaseMaster
- Victim
- Accused
- Complainant Details
- Arrest / Surrender
- District
- Police Station (Unit)
- Crime Head
- Crime Sub Head
- Court
- Case Status Master
- Gravity Offence

These tables support crime management, dashboards, analytics, and AI model training.

---

## Phase 2

Additional master tables:

- Act
- Section
- Crime Head Act Section
- Act Section Association
- Rank
- Designation
- Occupation
- Religion
- Caste
- State
- Unit Type
- Case Category

---

## Phase 3

Advanced operational modules:

- Chargesheet Details
- Witness
- Evidence
- Investigation Logs
- Remaining lookup tables
- Junction tables

---

# 12. Future Enhancements

Future database enhancements include:

- Geospatial crime coordinates
- CCTV metadata
- Vehicle information
- Criminal network relationships
- Digital evidence management
- AI feature store
- Audit history
- Model prediction history

---

# 13. Conclusion

The Echo Protocol database is built on Zoho Catalyst Data Store using a normalized relational schema centered around the **CaseMaster** table. The design supports operational crime management, analytical reporting, and AI-powered crime severity prediction while remaining scalable for future intelligence capabilities such as hotspot prediction, criminal relationship analysis, and predictive policing.