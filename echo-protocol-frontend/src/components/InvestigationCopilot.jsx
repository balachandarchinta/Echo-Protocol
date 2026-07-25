import React, { useState, useRef, useEffect } from "react";
import {
  Bot,
  X,
  Send,
  Sparkles,
  AlertCircle,
  CheckCircle2,
  FileText,
  ShieldAlert,
  ArrowRight,
} from "lucide-react";
import { askInvestigationCopilot } from "../services/copilotService";
import { getStatusClass, getStatusTooltip } from "../utils/badgeUtils";
import "./InvestigationCopilot.css";

const SUGGESTED_PROMPTS = [
  "Summarize FIR 2045",
  "Which district has increasing cybercrime?",
  "Show similar burglary cases",
  "List repeat offenders",
  "Recommend investigation steps",
];

export default function InvestigationCopilot({ isOpen: controlledIsOpen, onClose, showTrigger = false }) {
  const [internalIsOpen, setInternalIsOpen] = useState(false);
  const isOpen = controlledIsOpen !== undefined ? controlledIsOpen : internalIsOpen;

  const handleClose = () => {
    if (onClose) onClose();
    setInternalIsOpen(false);
  };
  const [inputQuery, setInputQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [messages, setMessages] = useState([
    {
      id: 1,
      sender: "ai",
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      data: {
        answer: "Welcome officer. I am your Echo Protocol AI Investigation Copilot. Ask me any query regarding registered FIRs, crime trends, repeat offenders, or tactical investigation steps.",
        summary: "Ready to assist law enforcement operations.",
        keyFindings: ["Connected to Catalyst Data Store case telemetry"],
        confidence: 0.98,
      },
    },
  ]);

  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    if (isOpen) {
      scrollToBottom();
    }
  }, [messages, isOpen]);

  const handleSend = async (queryText) => {
    const textToSend = queryText || inputQuery;
    if (!textToSend || !textToSend.trim() || loading) return;

    const userMsg = {
      id: Date.now(),
      sender: "user",
      text: textToSend,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMsg]);
    if (!queryText) setInputQuery("");
    setLoading(true);

    try {
      const resData = await askInvestigationCopilot(textToSend);
      const aiMsg = {
        id: Date.now() + 1,
        sender: "ai",
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        data: resData,
      };
      setMessages((prev) => [...prev, aiMsg]);
    } catch (err) {
      const errorMsg = {
        id: Date.now() + 1,
        sender: "ai",
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        data: {
          answer: "Unable to complete request at this moment. Please check backend connection.",
          summary: "Error processing query.",
          confidence: 0.0,
        },
      };
      setMessages((prev) => [...prev, errorMsg]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {/* FLOATING TRIGGER BUTTON (RENDERED ONLY IF SHOWTRIGGER IS TRUE) */}
      {showTrigger && (
        <button
          className="copilot-trigger-btn"
          onClick={() => setInternalIsOpen(true)}
          title="Open AI Investigation Copilot"
        >
          <Bot size={18} />
          <span>AI Copilot</span>
          <span className="copilot-badge">Active</span>
        </button>
      )}

      {/* DRAWER & BACKDROP */}
      {isOpen && (
        <div className="copilot-overlay" onClick={handleClose}>
          <div
            className="copilot-drawer"
            onClick={(e) => e.stopPropagation()}
          >
            {/* HEADER */}
            <div className="copilot-header">
              <div className="copilot-brand">
                <div className="copilot-icon-wrapper">
                  <Bot size={22} />
                </div>
                <div className="copilot-title-area">
                  <h2>Investigation Copilot</h2>
                  <p>AI-Powered Crime Intelligence Assistant</p>
                </div>
              </div>
              <button
                className="copilot-close-btn"
                onClick={handleClose}
              >
                <X size={20} />
              </button>
            </div>

            {/* SUGGESTED PROMPTS */}
            <div className="copilot-prompts-bar">
              {SUGGESTED_PROMPTS.map((prompt, index) => (
                <button
                  key={index}
                  className="prompt-chip"
                  onClick={() => handleSend(prompt)}
                >
                  {prompt}
                </button>
              ))}
            </div>

            {/* MESSAGES */}
            <div className="copilot-messages">
              {messages.map((msg) => (
                <div
                  key={msg.id}
                  className={`copilot-message-row ${msg.sender}`}
                >
                  {msg.sender === "user" ? (
                    <div className="message-bubble">{msg.text}</div>
                  ) : (
                    <div className="message-bubble">
                      <div className="ai-response-card">
                        {(() => {
                          const data = msg.data || {};
                          return (
                            <>

                        {/* 1. EXECUTIVE REPORT SCHEMA */}
                        {data.executiveSummary && (
                          <div className="ai-section">
                            <div className="ai-section-title"><FileText size={14} /> Executive Summary</div>
                            <p style={{ margin: 0, fontSize: "0.85rem", color: "#cbd5e1" }}>{data.executiveSummary}</p>
                          </div>
                        )}
                        {data.crimeOverview && (
                          <div className="ai-section">
                            <div className="ai-section-title"><CheckCircle2 size={14} /> Crime Overview</div>
                            <p style={{ margin: 0, fontSize: "0.85rem", color: "#cbd5e1" }}>{data.crimeOverview}</p>
                          </div>
                        )}
                        {data.districtPerformance && data.districtPerformance.length > 0 && (
                          <div className="ai-section">
                            <div className="ai-section-title"><ArrowRight size={14} /> District Performance</div>
                            <ul className="ai-list">
                              {data.districtPerformance.map((dp, i) => <li key={i}>{dp}</li>)}
                            </ul>
                          </div>
                        )}
                        {data.highRiskAreas && data.highRiskAreas.length > 0 && (
                          <div className="ai-section">
                            <div className="ai-section-title" style={{ color: "#ef4444" }}><ShieldAlert size={14} /> High Risk Areas</div>
                            <div style={{ display: "flex", gap: "6px", flexWrap: "wrap", marginTop: "4px" }}>
                              {data.highRiskAreas.map((area, i) => (
                                <span key={i} className="case-badge" style={{ background: "rgba(239, 68, 68, 0.2)", color: "#fca5a5" }}>{area}</span>
                              ))}
                            </div>
                          </div>
                        )}
                        {data.emergingCrimeTypes && data.emergingCrimeTypes.length > 0 && (
                          <div className="ai-section">
                            <div className="ai-section-title" style={{ color: "#f59e0b" }}><AlertCircle size={14} /> Emerging Crime Types</div>
                            <ul className="ai-list">
                              {data.emergingCrimeTypes.map((ect, i) => <li key={i}>{ect}</li>)}
                            </ul>
                          </div>
                        )}

                        {/* 2. COMPARE DISTRICTS SCHEMA */}
                        {data.comparisonTable && data.comparisonTable.length > 0 && (
                          <div className="ai-section">
                            <div className="ai-section-title"><FileText size={14} /> District Comparison Table</div>
                            <div className="related-cases-grid">
                              {data.comparisonTable.map((row, i) => (
                                <div key={i} className="related-case-item" style={{ flexDirection: "column", alignItems: "flex-start", gap: "4px" }}>
                                  <div style={{ display: "flex", width: "100%", justifyContent: "space-between" }}>
                                    <span className="case-fir">{row.district}</span>
                                    <span className="case-badge" style={{ background: row.riskLevel === "High" ? "rgba(239, 68, 68, 0.2)" : "rgba(37, 99, 235, 0.2)", color: row.riskLevel === "High" ? "#fca5a5" : "#93c5fd" }}>Risk: {row.riskLevel}</span>
                                  </div>
                                  <div style={{ fontSize: "0.78rem", color: "#94a3b8", display: "flex", gap: "10px" }}>
                                    <span>Crimes: <strong>{row.crimeCount}</strong></span>
                                    <span>Detection: <strong>{row.detectionRate}</strong></span>
                                    <span>Pending: <strong>{row.pendingCases}</strong></span>
                                    <span>Trend: <strong>{row.trendDifference}</strong></span>
                                  </div>
                                </div>
                              ))}
                            </div>
                          </div>
                        )}

                        {/* 3. FIR SUMMARY SCHEMA */}
                        {data.firDetails && (
                          <div className="ai-section">
                            <div className="ai-section-title"><FileText size={14} /> FIR Details</div>
                            <div className="related-case-item" style={{ flexDirection: "column", alignItems: "flex-start", gap: "4px" }}>
                              <div style={{ display: "flex", width: "100%", justifyContent: "space-between" }}>
                                <span className="case-fir">{data.firDetails.firNo}</span>
                                <span className={getStatusClass(data.firDetails.status)} title={getStatusTooltip(data.firDetails.status)}>{data.firDetails.status}</span>
                              </div>
                              <div style={{ fontSize: "0.78rem", color: "#cbd5e1" }}>
                                <div><strong>District:</strong> {data.firDetails.district} ({data.firDetails.policeStation})</div>
                                <div><strong>Category:</strong> {data.firDetails.crimeHead}</div>
                                <div><strong>Incident Date:</strong> {data.firDetails.incidentDate}</div>
                              </div>
                            </div>
                          </div>
                        )}
                        {data.incidentSummary && (
                          <div className="ai-section">
                            <div className="ai-section-title"><CheckCircle2 size={14} /> Incident Summary</div>
                            <p style={{ margin: 0, fontSize: "0.85rem", color: "#cbd5e1" }}>{data.incidentSummary}</p>
                          </div>
                        )}
                        {data.victim && (
                          <div className="ai-section">
                            <div className="ai-section-title"><AlertCircle size={14} /> Victim Information</div>
                            <p style={{ margin: 0, fontSize: "0.85rem", color: "#cbd5e1" }}>{data.victim}</p>
                          </div>
                        )}
                        {data.suspects && data.suspects.length > 0 && (
                          <div className="ai-section">
                            <div className="ai-section-title" style={{ color: "#f59e0b" }}><ShieldAlert size={14} /> Suspects / Persons of Interest</div>
                            <ul className="ai-list">
                              {data.suspects.map((s, i) => <li key={i}>{s}</li>)}
                            </ul>
                          </div>
                        )}
                        {data.investigationStatus && (
                          <div className="ai-section">
                            <div className="ai-section-title" style={{ color: "#3b82f6" }}><ArrowRight size={14} /> Investigation Status</div>
                            <p style={{ margin: 0, fontSize: "0.85rem", color: "#cbd5e1" }}>{data.investigationStatus}</p>
                          </div>
                        )}
                        {data.nextActions && data.nextActions.length > 0 && (
                          <div className="ai-section">
                            <div className="ai-section-title" style={{ color: "#10b981" }}><CheckCircle2 size={14} /> Next Actions</div>
                            <ul className="ai-list">
                              {data.nextActions.map((na, i) => <li key={i}>{na}</li>)}
                            </ul>
                          </div>
                        )}

                        {/* 4. SIMILAR CASES SCHEMA */}
                        {data.matchingCases && data.matchingCases.length > 0 && (
                          <div className="ai-section">
                            <div className="ai-section-title"><ArrowRight size={14} /> Matching Cases</div>
                            <div className="related-cases-grid">
                              {data.matchingCases.map((mc, i) => (
                                <div key={i} className="related-case-item">
                                  <span className="case-fir">{mc.firNo}</span>
                                  <span style={{ color: "#94a3b8" }}>{mc.crimeHead} ({mc.district})</span>
                                  <span className="case-badge" style={{ background: "rgba(16, 185, 129, 0.2)", color: "#6ee7b7" }}>Similarity: {mc.similarityScore}</span>
                                </div>
                              ))}
                            </div>
                          </div>
                        )}
                        {data.commonModusOperandi && (
                          <div className="ai-section">
                            <div className="ai-section-title"><CheckCircle2 size={14} /> Common Modus Operandi</div>
                            <p style={{ margin: 0, fontSize: "0.85rem", color: "#cbd5e1" }}>{data.commonModusOperandi}</p>
                          </div>
                        )}
                        {data.commonLocations && data.commonLocations.length > 0 && (
                          <div className="ai-section">
                            <div className="ai-section-title"><AlertCircle size={14} /> Common Hotspot Locations</div>
                            <div style={{ display: "flex", gap: "6px", flexWrap: "wrap", marginTop: "4px" }}>
                              {data.commonLocations.map((loc, i) => (
                                <span key={i} className="case-badge">{loc}</span>
                              ))}
                            </div>
                          </div>
                        )}
                        {data.investigationNotes && data.investigationNotes.length > 0 && (
                          <div className="ai-section">
                            <div className="ai-section-title" style={{ color: "#10b981" }}><CheckCircle2 size={14} /> Investigation Notes</div>
                            <ul className="ai-list">
                              {data.investigationNotes.map((note, i) => <li key={i}>{note}</li>)}
                            </ul>
                          </div>
                        )}

                        {/* 5. CRIME TRENDS SCHEMA */}
                        {data.monthlyTrend && (
                          <div className="ai-section">
                            <div className="ai-section-title"><FileText size={14} /> Monthly Trend Summary</div>
                            <p style={{ margin: 0, fontSize: "0.85rem", color: "#cbd5e1" }}>{data.monthlyTrend}</p>
                          </div>
                        )}
                        {data.fastestGrowingCrime && (
                          <div className="ai-section">
                            <div className="ai-section-title" style={{ color: "#ef4444" }}><AlertCircle size={14} /> Fastest Growing Crime</div>
                            <p style={{ margin: 0, fontSize: "0.85rem", color: "#fca5a5", fontWeight: 600 }}>{data.fastestGrowingCrime}</p>
                          </div>
                        )}
                        {data.highRiskDistricts && data.highRiskDistricts.length > 0 && (
                          <div className="ai-section">
                            <div className="ai-section-title" style={{ color: "#f59e0b" }}><ShieldAlert size={14} /> High Risk Districts</div>
                            <ul className="ai-list">
                              {data.highRiskDistricts.map((hrd, i) => <li key={i}>{hrd}</li>)}
                            </ul>
                          </div>
                        )}
                        {data.prediction && (
                          <div className="ai-section">
                            <div className="ai-section-title" style={{ color: "#8b5cf6" }}><Sparkles size={14} /> AI Predictive Forecast</div>
                            <p style={{ margin: 0, fontSize: "0.85rem", color: "#c084fc" }}>{data.prediction}</p>
                          </div>
                        )}

                        {/* 6. STANDARD / FALLBACK SECTIONS */}
                        {data.summary && !data.executiveSummary && !data.firDetails && !data.monthlyTrend && (
                          <div className="ai-section">
                            <div className="ai-section-title"><FileText size={14} /> Executive Summary</div>
                            <p style={{ margin: 0, fontSize: "0.85rem", color: "#cbd5e1" }}>{data.summary}</p>
                          </div>
                        )}
                        {data.keyFindings && data.keyFindings.length > 0 && (
                          <div className="ai-section">
                            <div className="ai-section-title"><CheckCircle2 size={14} /> Key Findings</div>
                            <ul className="ai-list">
                              {data.keyFindings.map((item, i) => <li key={i}>{item}</li>)}
                            </ul>
                          </div>
                        )}
                        {data.relatedCases && data.relatedCases.length > 0 && !data.matchingCases && (
                          <div className="ai-section">
                            <div className="ai-section-title"><ArrowRight size={14} /> Related Cases</div>
                            <div className="related-cases-grid">
                              {data.relatedCases.map((rc, i) => (
                                <div key={i} className="related-case-item">
                                  <span className="case-fir">{rc.firNo}</span>
                                  <span style={{ color: "#94a3b8" }}>{rc.crimeHead}</span>
                                  <span className="case-badge">{rc.district}</span>
                                </div>
                              ))}
                            </div>
                          </div>
                        )}
                        {data.riskAssessment && (
                          <div className="ai-section">
                            <div className="ai-section-title" style={{ color: "#ef4444" }}><ShieldAlert size={14} /> Risk Assessment</div>
                            <p style={{ margin: 0, fontSize: "0.85rem", color: "#fca5a5" }}>{data.riskAssessment}</p>
                          </div>
                        )}
                        {(data.recommendations || data.recommendedActions) && (data.recommendations?.length > 0 || data.recommendedActions?.length > 0) && !data.nextActions && (
                          <div className="ai-section">
                            <div className="ai-section-title" style={{ color: "#10b981" }}><AlertCircle size={14} /> Tactical Recommendations</div>
                            <ul className="ai-list">
                              {(data.recommendations || data.recommendedActions).map((act, i) => <li key={i}>{act}</li>)}
                            </ul>
                          </div>
                        )}

                        <div className="ai-confidence-footer">
                          <span>AI Intelligence Synthesis</span>
                          <span className="confidence-pill">
                            Confidence: {Math.round((msg.data.confidence || 0.9) * 100)}%
                          </span>
                        </div>
                            </>
                          );
                        })()}
                      </div>
                    </div>
                  )}
                </div>
              ))}

              {loading && (
                <div className="copilot-message-row ai">
                  <div className="message-bubble">
                    <div className="copilot-loading">
                      <div className="typing-dot" />
                      <div className="typing-dot" />
                      <div className="typing-dot" />
                      <span>Synthesizing intelligence...</span>
                    </div>
                  </div>
                </div>
              )}
              <div ref={messagesEndRef} />
            </div>

            {/* INPUT FORM */}
            <form
              className="copilot-input-area"
              onSubmit={(e) => {
                e.preventDefault();
                handleSend();
              }}
            >
              <input
                type="text"
                placeholder="Ask Investigation Copilot..."
                value={inputQuery}
                onChange={(e) => setInputQuery(e.target.value)}
                disabled={loading}
              />
              <button
                type="submit"
                className="copilot-send-btn"
                disabled={loading || !inputQuery.trim()}
              >
                <Send size={18} />
              </button>
            </form>
          </div>
        </div>
      )}
    </>
  );
}
