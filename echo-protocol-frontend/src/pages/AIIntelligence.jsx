import React from "react";
import {
  Bot,
  Layers,
  TrendingUp,
  Sparkles,
  ArrowRight,
  ShieldCheck,
  Zap,
  CheckCircle2,
  BrainCircuit,
} from "lucide-react";
import "./AIIntelligence.css";

export default function AIIntelligence({
  onOpenCopilot,
  onOpenMO,
  onOpenPredictive,
}) {
  return (
    <div className="ai-hub-container">
      {/* PAGE HEADER */}
      <div className="ai-hub-header">
        <div className="ai-hub-title">
          <h1>
            <Sparkles size={24} style={{ color: "#38bdf8" }} />
            AI Intelligence Hub
          </h1>
          <p>
            Centralized tactical AI suite for investigation assistance, modus operandi pattern matching, and crime risk forecasting.
          </p>
        </div>

        <div className="ai-status-banner">
          <div className="ai-status-pill active">
            <ShieldCheck size={16} style={{ color: "#34d399" }} />
            <span>3 Active AI Engines</span>
          </div>
          <div className="ai-status-pill">
            <Zap size={16} style={{ color: "#38bdf8" }} />
            <span>Catalyst Real-Time Telemetry</span>
          </div>
        </div>
      </div>

      {/* THREE AI MODULE CARDS */}
      <div className="ai-cards-grid">
        {/* CARD 1: AI INVESTIGATION COPILOT */}
        <div className="ai-module-card">
          <div className="ai-card-header">
            <div
              className="ai-card-icon"
              style={{
                background: "rgba(56, 189, 248, 0.15)",
                color: "#38bdf8",
                border: "1px solid rgba(56, 189, 248, 0.3)",
              }}
            >
              <Bot size={28} />
            </div>
            <span
              className="ai-card-tag"
              style={{
                background: "rgba(56, 189, 248, 0.15)",
                color: "#38bdf8",
              }}
            >
              Module 1 · Natural Query
            </span>
          </div>

          <div className="ai-card-body">
            <h2>AI Investigation Copilot</h2>
            <p>
              Interactive natural language assistant to query case details, summarize FIR records, identify repeat offenders, and recommend tactical investigation steps.
            </p>

            <div className="ai-capabilities-list">
              <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                <CheckCircle2 size={14} style={{ color: "#38bdf8" }} />
                <span>Automated FIR Record Summaries</span>
              </div>
              <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                <CheckCircle2 size={14} style={{ color: "#38bdf8" }} />
                <span>Repeat Offender & Modus Analysis</span>
              </div>
              <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                <CheckCircle2 size={14} style={{ color: "#38bdf8" }} />
                <span>Tactical Action Recommendations</span>
              </div>
            </div>
          </div>

          <button
            className="ai-launch-btn"
            style={{
              background: "linear-gradient(135deg, #0284c7 0%, #0369a1 100%)",
              color: "#ffffff",
            }}
            onClick={onOpenCopilot}
          >
            <span>Launch AI Copilot</span>
            <ArrowRight size={18} />
          </button>
        </div>

        {/* CARD 2: MO PATTERN ENGINE */}
        <div className="ai-module-card">
          <div className="ai-card-header">
            <div
              className="ai-card-icon"
              style={{
                background: "rgba(167, 139, 250, 0.15)",
                color: "#a78bfa",
                border: "1px solid rgba(167, 139, 250, 0.3)",
              }}
            >
              <Layers size={28} />
            </div>
            <span
              className="ai-card-tag"
              style={{
                background: "rgba(167, 139, 250, 0.15)",
                color: "#a78bfa",
              }}
            >
              Module 2 · Pattern Engine
            </span>
          </div>

          <div className="ai-card-body">
            <h2>MO & Pattern Analysis Engine</h2>
            <p>
              Modus Operandi similarity matching engine detecting crime entry methods, vehicle vectors, location proximities, and score factor breakdowns.
            </p>

            <div className="ai-capabilities-list">
              <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                <CheckCircle2 size={14} style={{ color: "#a78bfa" }} />
                <span>Modus Operandi Similarity Scoring</span>
              </div>
              <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                <CheckCircle2 size={14} style={{ color: "#a78bfa" }} />
                <span>Score Factors: Type, Location, Time, Entry</span>
              </div>
              <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                <CheckCircle2 size={14} style={{ color: "#a78bfa" }} />
                <span>Inter-District Highway Transit Analysis</span>
              </div>
            </div>
          </div>

          <button
            className="ai-launch-btn"
            style={{
              background: "linear-gradient(135deg, #7c3aed 0%, #6d28d9 100%)",
              color: "#ffffff",
            }}
            onClick={onOpenMO}
          >
            <span>Launch MO Engine</span>
            <ArrowRight size={18} />
          </button>
        </div>

        {/* CARD 3: PREDICTIVE CRIME INTELLIGENCE */}
        <div className="ai-module-card">
          <div className="ai-card-header">
            <div
              className="ai-card-icon"
              style={{
                background: "rgba(52, 211, 153, 0.15)",
                color: "#34d399",
                border: "1px solid rgba(52, 211, 153, 0.3)",
              }}
            >
              <TrendingUp size={28} />
            </div>
            <span
              className="ai-card-tag"
              style={{
                background: "rgba(52, 211, 153, 0.15)",
                color: "#34d399",
              }}
            >
              Module 3 · Forecasting
            </span>
          </div>

          <div className="ai-card-body">
            <h2>Predictive Crime Intelligence</h2>
            <p>
              Trend-based predictive intelligence engine calculating 7-day, 30-day, and 90-day district risk scores, high-risk stations, and patrol deployments.
            </p>

            <div className="ai-capabilities-list">
              <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                <CheckCircle2 size={14} style={{ color: "#34d399" }} />
                <span>7, 30 & 90 Day Dynamic Forecast Horizons</span>
              </div>
              <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                <CheckCircle2 size={14} style={{ color: "#34d399" }} />
                <span>High Risk District & Station Identification</span>
              </div>
              <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                <CheckCircle2 size={14} style={{ color: "#34d399" }} />
                <span>Actionable Patrol & Awareness Deployment</span>
              </div>
            </div>
          </div>

          <button
            className="ai-launch-btn"
            style={{
              background: "linear-gradient(135deg, #059669 0%, #047857 100%)",
              color: "#ffffff",
            }}
            onClick={onOpenPredictive}
          >
            <span>Launch Predictive AI</span>
            <ArrowRight size={18} />
          </button>
        </div>
      </div>
    </div>
  );
}
