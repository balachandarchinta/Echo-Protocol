# Echo Protocol API Design

## Overview

Echo Protocol follows a REST-based architecture.

Each dashboard module communicates with Catalyst Serverless Functions through REST APIs exposed using Catalyst API Gateway.

All APIs return JSON.

---

# Authentication

All APIs require authenticated users.

Roles

- DGP
- Commissioner
- SP
- Inspector
- Police Station User

---

# Dashboard APIs

| API | Method | Purpose |
|------|--------|---------|
| /dashboard | GET | Executive Dashboard |
| /dashboard/kpis | GET | KPI Cards |
| /dashboard/trends | GET | Crime Trends |
| /dashboard/districts | GET | District Analytics |

---

# Crime APIs

| API | Method | Purpose |
|------|--------|---------|
| /crime/list | GET | List FIRs |
| /crime/details/{id} | GET | FIR Details |
| /crime/search | POST | Search FIR |

---

# Analytics APIs

| API | Method | Purpose |
|------|--------|---------|
| /analytics/hotspots | GET | Crime Hotspots |
| /analytics/repeat-offenders | GET | Repeat Offenders |
| /analytics/network | GET | Criminal Network |
| /analytics/trends | GET | Crime Trends |
| /analytics/anomalies | GET | Anomaly Detection |

---

# AI APIs

| API | Method | Purpose |
|------|--------|---------|
| /ai/predict | POST | Crime Prediction |
| /ai/risk-score | GET | Risk Scoring |
| /ai/assistant | POST | AI Chat Assistant |

---

# Report APIs

| API | Method | Purpose |
|------|--------|---------|
| /reports/pdf | POST | Generate PDF |
| /reports/export | GET | Export Excel |

---

# Response Format

Every API returns

{
    "success": true,
    "data": {},
    "message": ""
}