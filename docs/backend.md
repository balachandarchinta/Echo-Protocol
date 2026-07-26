# Backend Architecture

# Echo Protocol Backend

---

# 1. Overview

Echo Protocol follows a modular serverless architecture built on **Zoho Catalyst**.

The backend is implemented using **Catalyst Serverless Functions (Java Advanced I/O)**, where each service is responsible for a specific business capability. This modular approach enables scalability, maintainability, and independent deployment while allowing seamless integration with AI services such as Catalyst QuickML.

The backend acts as the central layer between the React frontend, Catalyst Data Store, and AI Intelligence modules.

---

# 2. Backend Technology Stack

| Component | Technology |
|------------|------------|
| Runtime | Java Advanced I/O |
| Platform | Zoho Catalyst Serverless Functions |
| Database | Catalyst Data Store |
| AI Services | Catalyst QuickML |
| File Storage | Catalyst Stratus |
| API Response | JSON |
| Deployment | Catalyst CLI |

---

# 3. Backend Services

Echo Protocol consists of the following backend services.

| Service | Responsibility |
|----------|----------------|
| Dashboard Service | Dashboard KPIs and operational statistics |
| Crime Service | Crime registration, retrieval, filtering and search |
| Analytics Service | Crime analytics and reporting |
| Network Service | Criminal relationship and link analysis *(Future)* |
| AI Intelligence Service | Machine Learning and AI-powered insights |
| Report Service | Report generation and export |
| Master Data Service | Districts, Police Stations, Crime Heads and lookup data |
| System Operational Service | System monitoring and operational statistics |

> **Note:** Authentication and Role-Based Access Control (RBAC) are planned for a future release.

---

# 4. High-Level Backend Architecture

```
                 React + Vite Frontend
                          │
                          ▼
              Catalyst API Gateway
                          │
                          ▼
          Java Advanced I/O Functions
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
        ▼                 ▼                 ▼
 Catalyst Data Store   QuickML AI      Catalyst Stratus
        │              Prediction           Storage
        └─────────────────┼─────────────────┘
                          │
                          ▼
                    JSON Response
                          │
                          ▼
                    React Dashboard
```

---

# 5. Service Responsibilities

## Dashboard Service

Provides dashboard KPIs and operational statistics.

Responsibilities

- Total Registered Cases
- Open Cases
- Closed Cases
- Crime Distribution
- Monthly Trends
- District Statistics
- Police Station Statistics

---

## Crime Service

Handles crime management operations.

Responsibilities

- Retrieve crime records
- Search by Crime Number
- Search by FIR Number
- Filter by District
- Filter by Crime Head
- Filter by Status
- Case Details
- Pagination

---

## Analytics Service

Provides analytical insights.

Responsibilities

- District-wise Crime Analysis
- Crime Classification Analysis
- Crime Head Analysis
- Monthly Crime Trends
- Status Analysis
- Gravity Analysis
- Comparative Reports

---

## Network Service *(Future)*

Provides criminal relationship analysis.

Planned Features

- Suspect Networks
- Victim Networks
- Vehicle Relationships
- Mobile Number Links
- Address Relationships
- Criminal Associations

---

## AI Intelligence Service

Provides Artificial Intelligence capabilities for Echo Protocol.

### Current Features

- Crime Severity Prediction
- Random Forest Prediction
- Feature Importance Analysis
- AI Model Integration

### Future Features

- Crime Trend Forecasting
- Similar Case Recommendation
- Crime Hotspot Prediction
- AI Investigation Assistant
- Explainable AI

---

## Report Service

Generates downloadable reports.

Supported Formats

- Excel
- CSV
- PDF *(Future)*

Report Types

- Crime Reports
- District Reports
- Analytics Reports
- AI Prediction Reports

---

## Master Data Service

Provides lookup and reference data used throughout the application.

Responsibilities

- Districts
- Police Stations
- Crime Heads
- Crime Classification
- Gravity
- Case Status

---

## System Operational Service

Provides application health and operational monitoring.

Responsibilities

- Service Health
- API Status
- Database Connectivity
- AI Model Status
- System Statistics
- Error Monitoring

---

# 6. AI Prediction Workflow

The AI Intelligence Service integrates with Catalyst QuickML to predict crime severity.

```
Crime Details

        │

        ▼

AI Intelligence Service

        │

        ▼

Catalyst QuickML Model

        │

        ▼

Prediction Result

        │

        ▼

JSON Response

        │

        ▼

React Dashboard
```

Prediction Input

- District
- Police Station
- Crime Classification
- Crime Head
- Status

Prediction Output

- Heinous
- Serious
- Petty

---

# 7. Data Flow

```
React Frontend

      │

      ▼

Backend API

      │

      ▼

Business Logic

      │

 ┌────┴───────────┐

 ▼                ▼

Data Store     AI Intelligence

 └──────┬────────┘

        ▼

JSON Response

        ▼

React Dashboard
```

---

# 8. Design Principles

The backend is designed around the following principles:

- Modular service architecture
- Stateless serverless functions
- Separation of concerns
- JSON-based REST APIs
- Independent deployment
- Scalable AI integration
- Cloud-native architecture

---

# 9. Future Enhancements

Future backend capabilities include:

- Authentication
- Role-Based Access Control
- API Rate Limiting
- Audit Logging
- AI Investigation Assistant APIs
- Crime Forecast APIs
- Hotspot Prediction APIs
- Notification Services
- Explainable AI APIs
- Model Version Management

---

# 10. Conclusion

The Echo Protocol backend provides a scalable, serverless architecture built on Zoho Catalyst. By combining Java Advanced I/O functions, Catalyst Data Store, and Catalyst QuickML, the backend delivers operational crime management, analytics, and AI-powered crime severity prediction. Its modular design allows future AI services and advanced intelligence capabilities to be integrated with minimal changes to the existing architecture.