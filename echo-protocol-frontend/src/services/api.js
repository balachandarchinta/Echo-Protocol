const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ||
  "http://localhost:3000/server/echo-protocol-api";


async function fetchApi(endpoint) {
  const response = await fetch(
    `${API_BASE_URL}${endpoint}`
  );

  if (!response.ok) {
    throw new Error(
      `API request failed: ${response.status} ${response.statusText}`
    );
  }

  const result = await response.json();

  if (!result.success) {
    throw new Error(
      result.message || "API request failed"
    );
  }

  return result.data;
}


// =====================================================
// DASHBOARD KPI CARDS
// =====================================================
export function getDashboardKpis() {
  return fetchApi(
    "/dashboard/kpis"
  );
}


// =====================================================
// CASES BY STATUS
// =====================================================
export function getCasesByStatus() {
  return fetchApi(
    "/dashboard/cases-by-status"
  );
}


// =====================================================
// CASES BY DISTRICT
// =====================================================
export function getCasesByDistrict() {
  return fetchApi(
    "/dashboard/cases-by-district"
  );
}


// =====================================================
// CASES BY CRIME HEAD
// =====================================================
export function getCasesByCrimeHead() {
  return fetchApi(
    "/dashboard/cases-by-crime-head"
  );
}


// =====================================================
// CASES BY GRAVITY
// =====================================================
export function getCasesByGravity() {
  return fetchApi(
    "/dashboard/cases-by-gravity"
  );
}


// =====================================================
// CASES BY MONTH
// =====================================================
export function getCasesByMonth() {
  return fetchApi(
    "/dashboard/cases-by-month"
  );
}


// =====================================================
// LOAD COMPLETE DASHBOARD
//
// All six API requests execute in parallel.
// =====================================================
export async function getDashboardData() {
  const [
    kpis,
    casesByStatus,
    casesByDistrict,
    casesByCrimeHead,
    casesByGravity,
    casesByMonth,
  ] = await Promise.all([
    getDashboardKpis(),
    getCasesByStatus(),
    getCasesByDistrict(),
    getCasesByCrimeHead(),
    getCasesByGravity(),
    getCasesByMonth(),
  ]);

  return {
    kpis,
    casesByStatus,
    casesByDistrict,
    casesByCrimeHead,
    casesByGravity,
    casesByMonth,
  };
}
// =====================================================
// CASE LIST
// =====================================================
export function getCases() {
  return fetchApi(
    "/crime/list"
  );
}
// =====================================================
// CASE DETAILS
// =====================================================
export function getCaseDetails(rowId) {
  return fetchApi(
    `/crime/details/${rowId}`
  );
}

// =====================================================
// UNIT DIRECTORY
// =====================================================
export function getUnits() {
  return fetchApi(
    "/unit/list"
  );
}