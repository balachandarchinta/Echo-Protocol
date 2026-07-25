import React, { useState, useEffect } from "react";
import {
  TrendingUp,
  X,
  ShieldAlert,
  BarChart3,
  AlertTriangle,
  Building2,
  CheckCircle2,
  Sparkles,
  Layers,
} from "lucide-react";
import { predictCrimeTrends } from "../services/predictiveService";
import "./PredictiveIntelligence.css";

const PERIODS = [
  { id: "7days", label: "7 Days Forecast" },
  { id: "30days", label: "30 Days Forecast" },
  { id: "90days", label: "90 Days Forecast" },
];

export default function PredictiveIntelligence({ isOpen: controlledIsOpen, onClose, showTrigger = false }) {
  const [internalIsOpen, setInternalIsOpen] = useState(false);
  const isOpen = controlledIsOpen !== undefined ? controlledIsOpen : internalIsOpen;

  const handleClose = () => {
    if (onClose) onClose();
    setInternalIsOpen(false);
  };
  const [selectedPeriod, setSelectedPeriod] = useState("30days");
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState(null);

  const fetchPredictions = async (periodId) => {
    setLoading(true);
    try {
      const res = await predictCrimeTrends(periodId);
      setData(res);
    } catch (err) {
      console.error("Failed to fetch predictive intelligence:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isOpen) {
      fetchPredictions(selectedPeriod);
    }
  }, [isOpen, selectedPeriod]);

  const handlePeriodChange = (periodId) => {
    setSelectedPeriod(periodId);
  };

  return (
    <>
      {/* GLOBAL TRIGGER BUTTON (RENDERED ONLY IF SHOWTRIGGER IS TRUE) */}
      {showTrigger && (
        <button
          className="predictive-trigger-btn"
          onClick={() => setInternalIsOpen(true)}
          title="Open AI Predictive Crime Intelligence"
        >
          <TrendingUp size={18} />
          <span>Predictive Intelligence</span>
        </button>
      )}

      {/* OVERLAY & DRAWER */}
      {isOpen && (
        <div className="predictive-overlay" onClick={handleClose}>
          <div
            className="predictive-drawer"
            onClick={(e) => e.stopPropagation()}
          >
            {/* FIXED HEADER */}
            <div className="predictive-header">
              <div className="predictive-brand">
                <div className="predictive-icon-wrapper">
                  <TrendingUp size={22} />
                </div>
                <div className="predictive-title-area">
                  <h2>Predictive Intelligence</h2>
                  <p>Trend-Based Crime Pattern & Risk Forecasting</p>
                </div>
              </div>
              <button
                className="predictive-close-btn"
                onClick={handleClose}
              >
                <X size={20} />
              </button>
            </div>

            {/* FIXED PERIOD SELECTOR PROMPTS BAR */}
            <div className="predictive-prompts-bar">
              {PERIODS.map((period) => (
                <button
                  key={period.id}
                  className={`predictive-period-btn ${
                    selectedPeriod === period.id ? "active" : ""
                  }`}
                  onClick={() => handlePeriodChange(period.id)}
                >
                  {period.label}
                </button>
              ))}
            </div>

            {/* INDEPENDENTLY SCROLLABLE CONTENT BODY (MATCHES .copilot-messages) */}
            <div className="predictive-messages">
              {/* LOADING STATE */}
              {loading && (
                <div className="predictive-loading-spinner">
                  <Sparkles className="animate-spin" size={20} />
                  <span>Computing trend-based predictive intelligence...</span>
                </div>
              )}

              {/* RESULTS DISPLAY */}
              {data && !loading && (
                <div className="ai-response-card">
                  {/* OVERALL RISK CARD */}
                  <div className="predictive-card">
                    <div className="predictive-card-title">
                      <ShieldAlert size={16} style={{ color: "#ef4444" }} />
                      <span>Overall Risk Assessment</span>
                    </div>

                    <div
                      className={`predictive-risk-badge ${
                        data.overallRisk === "Low" ? "low" : ""
                      }`}
                    >
                      <div>
                        <div style={{ fontSize: "0.75rem", color: "#94a3b8" }}>
                          Forecasted Threat Matrix
                        </div>
                        <div
                          style={{
                            fontSize: "1.1rem",
                            fontWeight: 700,
                            color:
                              data.overallRisk === "Low" ? "#6ee7b7" : "#fca5a5",
                          }}
                        >
                          Overall Risk: {data.overallRisk}
                        </div>
                      </div>
                      <span className="confidence-pill">
                        Confidence: {Math.round((data.confidence || 0.94) * 100)}%
                      </span>
                    </div>

                    <p style={{ margin: 0, fontSize: "0.85rem", color: "#cbd5e1" }}>
                      {data.summary}
                    </p>
                  </div>

                  {/* DISTRICT RISK SCORES */}
                  {data.districtRiskScores && data.districtRiskScores.length > 0 && (
                    <div className="predictive-card">
                      <div className="predictive-card-title">
                        <BarChart3 size={16} style={{ color: "#38bdf8" }} />
                        <span>District Risk Scores & Trends</span>
                      </div>

                      <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
                        {data.districtRiskScores.map((item, idx) => (
                          <div key={idx} className="predictive-district-row">
                            <div className="predictive-district-header">
                              <span style={{ fontWeight: 600, fontSize: "0.85rem", color: "#f8fafc" }}>
                                {item.district}
                              </span>
                              <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                                <span className={`predictive-trend-pill ${item.trend}`}>
                                  {item.trend}
                                </span>
                                <span style={{ fontWeight: 700, fontSize: "0.85rem", color: "#38bdf8" }}>
                                  {item.riskScore}%
                                </span>
                              </div>
                            </div>

                            <div className="predictive-progress-bg">
                              <div
                                className="predictive-progress-fill"
                                style={{
                                  width: `${item.riskScore}%`,
                                  background:
                                    item.riskScore >= 80
                                      ? "#ef4444"
                                      : item.riskScore >= 65
                                      ? "#f59e0b"
                                      : "#10b981",
                                }}
                              />
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* HIGH RISK POLICE STATIONS */}
                  {data.highRiskPoliceStations && data.highRiskPoliceStations.length > 0 && (
                    <div className="predictive-card">
                      <div className="predictive-card-title">
                        <Building2 size={16} style={{ color: "#f43f5e" }} />
                        <span>High Risk Police Stations</span>
                      </div>
                      <div className="predictive-tags-grid">
                        {data.highRiskPoliceStations.map((station, idx) => (
                          <span key={idx} className="predictive-tag" style={{ borderLeft: "3px solid #f43f5e" }}>
                            {station}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* EMERGING CRIME TYPES */}
                  {data.emergingCrimeTypes && data.emergingCrimeTypes.length > 0 && (
                    <div className="predictive-card">
                      <div className="predictive-card-title">
                        <AlertTriangle size={16} style={{ color: "#fbbf24" }} />
                        <span>Emerging Crime Categories</span>
                      </div>
                      <div className="predictive-tags-grid">
                        {data.emergingCrimeTypes.map((crime, idx) => (
                          <span key={idx} className="predictive-tag" style={{ borderLeft: "3px solid #fbbf24" }}>
                            {crime}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* PATROL RECOMMENDATIONS */}
                  {data.recommendations && data.recommendations.length > 0 && (
                    <div className="predictive-card">
                      <div className="predictive-card-title" style={{ color: "#10b981" }}>
                        <CheckCircle2 size={16} />
                        <span>Actionable Patrol Recommendations</span>
                      </div>
                      <ul className="predictive-bullet-list">
                        {data.recommendations.map((rec, idx) => (
                          <li key={idx}>{rec}</li>
                        ))}
                      </ul>
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
}
