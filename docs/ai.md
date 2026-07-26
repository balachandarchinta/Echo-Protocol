# AI.md

# Echo Protocol – Artificial Intelligence Module

## Overview

The AI module in **Echo Protocol** enhances traditional crime analytics by incorporating Machine Learning (ML) to predict the severity of registered crimes. The solution leverages **Zoho Catalyst QuickML** to train, evaluate, and deploy a classification model capable of predicting the **Gravity** of a crime based on historical crime records.

The AI system assists law enforcement agencies by providing intelligent recommendations during crime registration and investigation, enabling faster prioritization of cases and efficient resource allocation.

---

# Objectives

The AI module is designed to:

- Predict crime severity automatically.
- Assist police officers during FIR registration.
- Improve crime prioritization.
- Support analytical dashboards with predictive intelligence.
- Reduce manual assessment of crime seriousness.
- Provide explainable machine learning predictions.

---

# AI Architecture

```
                +----------------------+
                |  Catalyst Data Store |
                +----------+-----------+
                           |
                           |
                 Crime Prediction Dataset
                           |
                           ▼
                Catalyst QuickML Dataset
                           |
                           ▼
              Data Transformation Pipeline
                           |
                           ▼
             Machine Learning Pipeline
                           |
                           ▼
          Random Forest Classification Model
                           |
                           ▼
              Prediction REST Endpoint
                           |
                           ▼
         Java Advanced I/O Backend API
                           |
                           ▼
             React Frontend Dashboard
```

---

# AI Technology Stack

| Component | Technology |
|------------|------------|
| Machine Learning Platform | Zoho Catalyst QuickML |
| ML Algorithm | Random Forest Classification |
| Backend | Java Advanced I/O |
| Frontend | React + Vite |
| Database | Catalyst Data Store |
| API | Catalyst Functions |

---

# Dataset

## Dataset Name

```
Crime_Prediction_dataset
```

The dataset was generated from crime records stored in Catalyst Data Store.

---

# Dataset Columns

| Column | Description |
|----------|-------------|
| District | Crime District |
| Police Station | Police Station Name |
| Crime Classification | Classification of crime |
| Crime Head | Crime Category |
| Status | Current Investigation Status |
| Gravity | Crime Severity (Target Variable) |

---

# Excluded Columns

The following fields were removed before training because they do not contribute to prediction.

- Crime No
- FIR No
- Registered Date

---

# Target Variable

```
Gravity
```

Possible values:

- Heinous
- Serious
- Petty

---

# Data Preprocessing

The following preprocessing operations were performed.

## Feature Selection

Selected Features

- District
- Police Station
- Crime Classification
- Crime Head
- Status

Dropped Features

- Crime No
- FIR No
- Registered

---

## Encoding

Categorical values were converted into numerical representations using

**Ordinal Encoder**

Encoded Columns

- District
- Police Station
- Crime Classification
- Crime Head
- Status

Target column (Gravity) was excluded from encoding.

---

# Machine Learning Pipeline

Pipeline Name

```
EchoProtocol_CrimeSeverity_Pipeline
```

Pipeline Stages

```
Source Dataset

↓

Select / Drop

↓

Ordinal Encoder

↓

Random Forest Classification

↓

Model Output
```

---

# Model Information

Model Name

```
EchoProtocol_CrimeSeverity_Model
```

Model Type

```
Prediction
```

Status

```
Ready
```

Target Column

```
Gravity
```

---

# Machine Learning Algorithm

## Random Forest Classification

Random Forest is an ensemble learning algorithm that constructs multiple decision trees and combines their outputs to improve prediction accuracy.

Advantages

- High accuracy
- Handles categorical features effectively
- Resistant to overfitting
- Performs well with mixed datasets
- Provides feature importance

Configuration

| Parameter | Value |
|------------|---------|
| Estimators | 100 |
| Criterion | Gini |
| Max Depth | Default |
| Min Samples Split | 2 |

---

# Feature Importance

The trained model identified the following feature importance.

| Feature | Importance |
|-----------|-----------|
| Crime Head | ~46% |
| Crime Classification | ~41% |
| Status | ~10% |
| Police Station | ~2% |
| District | <1% |

This indicates that **Crime Head** and **Crime Classification** are the most influential factors in predicting crime severity.

---

# Model Evaluation

Evaluation Metrics

| Metric | Score |
|---------|-------|
| Accuracy | 1.00 |
| Precision | 1.00 |
| Recall | 1.00 |
| F1 Score | 1.00 |
| AUC | 1.00 |
| Sensitivity | 1.00 |
| Specificity | 1.00 |

The trained model successfully classified the training dataset with excellent predictive performance.

---

# Confusion Matrix

The confusion matrix generated by QuickML demonstrates the relationship between predicted and actual crime severity classes.

Target Classes

- Heinous
- Serious
- Petty

The visualization confirms successful classification across all categories.

---

# AI Prediction Workflow

```
Crime Registration

↓

Capture Crime Details

↓

Send Features to ML Model

↓

Predict Gravity

↓

Return Prediction

↓

Display Result on Dashboard
```

---

# Business Use Cases

## Crime Registration

Suggests crime severity during FIR registration.

---

## Investigation Prioritization

Allows police departments to prioritize investigations based on predicted crime gravity.

---

## Resource Allocation

Helps administrators allocate officers and resources efficiently.

---

## Crime Intelligence Dashboard

Displays AI-predicted crime severity alongside operational analytics.

---

## Predictive Analytics

Supports strategic crime analysis and decision-making using historical patterns.

---

# Integration with Echo Protocol

The AI model integrates seamlessly with the existing application architecture.

```
React Dashboard

↓

Java Advanced I/O API

↓

Catalyst QuickML Prediction Endpoint

↓

Prediction Response

↓

Display Predicted Gravity
```

---

# Benefits

- AI-assisted crime classification
- Faster decision-making
- Improved investigation prioritization
- Intelligent dashboard insights
- Reduced manual effort
- Better operational efficiency
- Scalable ML architecture

---

# Future Enhancements

The current implementation focuses on supervised classification using historical data.

Future enhancements include:

- Explainable AI (Model Explainer)
- Real-time prediction APIs
- Crime hotspot prediction
- Crime trend forecasting
- Repeat offender prediction
- Anomaly detection
- Geospatial AI analytics
- Deep Learning models
- AutoML experimentation
- Continuous model retraining
- Multi-model comparison
- Predictive policing dashboards

---

# Conclusion

The AI module transforms Echo Protocol from a traditional crime analytics platform into an intelligent decision-support system.

Using Zoho Catalyst QuickML and a Random Forest Classification model, the platform predicts the gravity of crimes based on historical crime data. The solution provides accurate, scalable, and explainable predictions that assist law enforcement agencies in prioritizing investigations, optimizing resource allocation, and improving operational efficiency.

The modular architecture enables seamless integration with the React frontend and Java backend while allowing future enhancements such as explainable AI, forecasting, and advanced predictive analytics.