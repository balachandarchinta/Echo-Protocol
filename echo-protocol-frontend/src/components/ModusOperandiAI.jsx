import React, { useState } from "react";
import {
  Layers,
  X,
  Search,
  CheckCircle2,
  FileText,
  ShieldAlert,
  MapPin,
  Clock,
  Sparkles,
  ArrowRight,
} from "lucide-react";
import { analyzeModusOperandi } from "../services/moService";
import { getStatusClass, getStatusTooltip } from "../utils/badgeUtils";
import "./ModusOperandiAI.css";

const SUGGESTED_QUERIES = [
  "Show similar burglary cases",
  "Find cybercrime patterns",
  "Compare robbery incidents",
  "Detect repeat crime methods",
  "Show similar FIRs",
  "Find related theft cases",
  "Analyse burglary trends",
  "Show common modus operandi",
  "Compare crime patterns across districts",
  "Identify recurring crime clusters",
];

export default function ModusOperandiAI({ isOpen: controlledIsOpen, onClose, showTrigger = false }) {
  const [internalIsOpen, setInternalIsOpen] = useState(false);
  const isOpen = controlledIsOpen !== undefined ? controlledIsOpen : internalIsOpen;

  const handleClose = () => {
    if (onClose) onClose();
    setInternalIsOpen(false);
  };
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const handleSearch = async (queryText) => {
    const textToSearch = queryText || query;
    if (!textToSearch.trim()) return;

    setLoading(true);
    setResult(null);
    try {
      const data = await analyzeModusOperandi(textToSearch);
      setResult(data);
    } catch (err) {
      console.error("Failed to analyze MO pattern:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleChipClick = (chipText) => {
    setQuery(chipText);
    handleSearch(chipText);
  };

  return (
    <>
      {/* GLOBAL TRIGGER BUTTON (RENDERED ONLY IF SHOWTRIGGER IS TRUE) */}
      {showTrigger && (
        <button
          className="mo-trigger-btn"
          onClick={() => setInternalIsOpen(true)}
          title="Open AI Modus Operandi & Pattern Analysis Engine"
        >
          <Layers size={18} />
          <span>MO Pattern Engine</span>
        </button>
      )}

      {/* OVERLAY & DRAWER */}
      {isOpen && (
        <div className="mo-overlay" onClick={handleClose}>
          <div className="mo-drawer" onClick={(e) => e.stopPropagation()}>
            {/* FIXED HEADER */}
            <div className="mo-header">
              <div className="mo-brand">
                <div className="mo-icon-wrapper">
                  <Layers size={22} />
                </div>
                <div className="mo-title-area">
                  <h2>Modus Operandi Engine</h2>
                  <p>Rule-Based Crime Similarity & Pattern Analysis</p>
                </div>
              </div>
              <button
                className="mo-close-btn"
                onClick={handleClose}
              >
                <X size={20} />
              </button>
            </div>

            {/* FIXED SEARCH & SUGGESTED PROMPTS BAR */}
            <div className="mo-search-bar">
              <form
                className="mo-input-area"
                onSubmit={(e) => {
                  e.preventDefault();
                  handleSearch();
                }}
              >
                <input
                  type="text"
                  placeholder="Enter query (e.g. Show similar burglary cases)..."
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                  disabled={loading}
                />
                <button
                  type="submit"
                  className="mo-search-btn"
                  disabled={loading || !query.trim()}
                >
                  <Search size={18} />
                </button>
              </form>

              <div className="mo-prompts-bar">
                {SUGGESTED_QUERIES.map((sq, i) => (
                  <button
                    key={i}
                    className="prompt-chip"
                    onClick={() => handleChipClick(sq)}
                  >
                    {sq}
                  </button>
                ))}
              </div>
            </div>

            {/* INDEPENDENTLY SCROLLABLE CONTENT BODY (MATCHES .copilot-messages) */}
            <div className="mo-messages">
              {/* LOADING STATE */}
              {loading && (
                <div className="mo-loading-spinner">
                  <Sparkles className="animate-spin" size={20} />
                  <span>Running rule-based similarity analysis...</span>
                </div>
              )}

              {/* RESULT DISPLAY */}
              {result && !loading && (
                <div className="ai-response-card">
                  {/* PATTERN SUMMARY CARD & SIMILARITY SCORE */}
                  <div className="mo-card">
                    <div className="mo-score-header">
                      <div className="mo-card-title">
                        <FileText size={16} style={{ color: "#38bdf8" }} />
                        <span>Pattern Summary</span>
                      </div>
                      <span
                        className={`mo-score-pill ${
                          result.similarityScore > 0 ? "high" : "zero"
                        }`}
                      >
                        Similarity: {result.similarityScore}%
                      </span>
                    </div>
                    <p style={{ margin: 0, fontSize: "0.85rem", color: "#cbd5e1" }}>
                      {result.summary}
                    </p>

                    {/* SCORE FACTORS BREAKDOWN */}
                    {result.scoreFactors && (
                      <div style={{ marginTop: "4px" }}>
                        <div style={{ fontSize: "0.76rem", color: "#94a3b8", marginBottom: "4px" }}>
                          Score Factors Breakdown:
                        </div>
                        <div className="mo-factors-grid">
                          {Object.entries(result.scoreFactors).map(([factor, pts], i) => (
                            <div key={i} className="mo-factor-item">
                              <span>{factor}:</span>
                              <strong>+{pts} pts</strong>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>

                  {/* CRIME PATTERN BREAKDOWN */}
                  {result.crimePattern && (
                    <div className="mo-card">
                      <div className="mo-card-title">
                        <Clock size={16} style={{ color: "#a855f7" }} />
                        <span>Crime Pattern Breakdown</span>
                      </div>
                      <div
                        style={{
                          whiteSpace: "pre-line",
                          fontSize: "0.83rem",
                          color: "#e2e8f0",
                          lineHeight: "1.5",
                        }}
                      >
                        {result.crimePattern}
                      </div>
                    </div>
                  )}

                  {/* MATCHING FIR CARDS */}
                  {result.matchingCases && result.matchingCases.length > 0 && (
                    <div className="mo-card">
                      <div className="mo-card-title">
                        <ArrowRight size={16} style={{ color: "#34d399" }} />
                        <span>Matching Case Records</span>
                      </div>
                      <div className="mo-cases-list">
                        {result.matchingCases.map((mc, i) => (
                          <div key={i} className="mo-case-card">
                            <div className="mo-case-header">
                              <span className="mo-fir-no">{mc.firNo}</span>
                              <span className={getStatusClass(mc.status)} title={getStatusTooltip(mc.status)}>{mc.status}</span>
                            </div>
                            <div style={{ fontSize: "0.78rem", color: "#cbd5e1" }}>
                              <div><strong>Classification:</strong> {mc.crimeHead}</div>
                              <div><strong>Jurisdiction:</strong> {mc.district} ({mc.policeStation})</div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* COMMON CHARACTERISTICS */}
                  {result.commonCharacteristics && result.commonCharacteristics.length > 0 && (
                    <div className="mo-card">
                      <div className="mo-card-title">
                        <CheckCircle2 size={16} style={{ color: "#fbbf24" }} />
                        <span>Common Modus Operandi Characteristics</span>
                      </div>
                      <ul className="mo-bullet-list">
                        {result.commonCharacteristics.map((cc, i) => (
                          <li key={i}>{cc}</li>
                        ))}
                      </ul>
                    </div>
                  )}

                  {/* DISTRICT ANALYSIS */}
                  {result.districtAnalysis && (
                    <div className="mo-card">
                      <div className="mo-card-title">
                        <MapPin size={16} style={{ color: "#f43f5e" }} />
                        <span>District Cluster Analysis</span>
                      </div>
                      <p style={{ margin: 0, fontSize: "0.85rem", color: "#cbd5e1" }}>
                        {result.districtAnalysis}
                      </p>
                    </div>
                  )}

                  {/* RISK LEVEL & RECOMMENDATIONS */}
                  {result.recommendations && result.recommendations.length > 0 && (
                    <div className="mo-card">
                      <div className="mo-card-title" style={{ color: "#10b981" }}>
                        <ShieldAlert size={16} />
                        <span>Risk Level ({result.riskLevel}) & Actionable Recommendations</span>
                      </div>
                      <ul className="mo-bullet-list">
                        {result.recommendations.map((rec, i) => (
                          <li key={i}>{rec}</li>
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
