/**
 * Unified Enterprise Badge Helper Utility for Echo Protocol
 */

export function getStatusClass(status) {
  const normalized = String(status || "")
    .toLowerCase()
    .trim()
    .replace(/\s+/g, "-");
  return `case-badge status-${normalized}`;
}

export function getStatusTooltip(status) {
  const val = String(status || "").toLowerCase().trim();
  if (val.includes("open") || val === "registered") {
    return "Case registered and awaiting primary officer assignment.";
  }
  if (val.includes("under investigation") || val.includes("investigation")) {
    return "Active investigation and evidence gathering in progress.";
  }
  if (val.includes("charge sheet")) {
    return "Formal charges filed in court following investigation.";
  }
  if (val.includes("final report")) {
    return "Investigation concluded and final report submitted to court.";
  }
  if (val.includes("closed")) {
    return "Case officially resolved and closed.";
  }
  return "Case transferred or reassigned to external jurisdiction.";
}

export function getGravityClass(gravity) {
  const normalized = String(gravity || "")
    .toLowerCase()
    .trim();
  return `case-badge gravity-${normalized}`;
}

export function getGravityTooltip(gravity) {
  const val = String(gravity || "").toLowerCase().trim();
  if (val.includes("petty")) {
    return "Minor offence with minimal statutory penalty or public impact.";
  }
  if (val.includes("serious")) {
    return "Significant offence requiring dedicated investigative oversight.";
  }
  if (val.includes("heinous")) {
    return "Severe or violent crime requiring high-priority emergency response.";
  }
  return "Offence gravity classification.";
}
