import "./App.css";
import { useEffect, useState } from "react";
import {
  Activity,
  BadgeCheck,
  BarChart3,
  Building2,
  CheckCircle2,
  ClipboardList,
  FileCheck2,
  RefreshCw,
  Shield,
  ShieldAlert,
  Upload,
  Sparkles,
} from "lucide-react";

import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  LabelList,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

import { getDashboardData } from "./services/api";

const renderPieCustomLabel = (props) => {
  const { cx, cy, midAngle, outerRadius, percent, value, count } = props;
  if (percent !== undefined && percent < 0.02) return null;
  const RADIAN = Math.PI / 180;
  const radius = outerRadius + 18;
  const x = cx + radius * Math.cos(-midAngle * RADIAN);
  const y = cy + radius * Math.sin(-midAngle * RADIAN);
  const labelValue = value !== undefined ? value : count;
  return (
    <text
      x={x}
      y={y}
      fill="#1e293b"
      textAnchor={x > cx ? "start" : "end"}
      dominantBaseline="central"
      fontSize={14}
      fontWeight={700}
    >
      {labelValue}
    </text>
  );
};

const renderLineCustomLabel = (props) => {
  const { x, y, value } = props;
  if (value === undefined || value === null) return null;
  return (
    <text
      x={x}
      y={y - 12}
      fill="#1e293b"
      textAnchor="middle"
      fontSize={14}
      fontWeight={600}
    >
      {value}
    </text>
  );
};
import Cases from "./pages/Cases";
import CaseDetails from "./pages/CaseDetails";
import Units from "./pages/Units";
import UnitDetails from "./pages/UnitDetails";
import Analytics from "./pages/Analytics";
import SmartDataImport from "./pages/SmartDataImport";
import AIIntelligence from "./pages/AIIntelligence";
import InvestigationCopilot from "./components/InvestigationCopilot";
import ModusOperandiAI from "./components/ModusOperandiAI";
import PredictiveIntelligence from "./components/PredictiveIntelligence";

const STATUS_COLORS = [
  "#2563eb",
  "#f59e0b",
  "#8b5cf6",
  "#16a34a",
  "#64748b",
  "#dc2626",
];

const GRAVITY_COLORS = [
  "#f59e0b",
  "#2563eb",
  "#dc2626",
];


function KpiCard({
  title,
  value,
  icon: Icon,
  description,
}) {
  return (
    <article className="kpi-card">
      <div className="kpi-card-header">
        <div>
          <p className="kpi-label">
            {title}
          </p>

          <p className="kpi-value">
            {value}
          </p>
        </div>

        <div className="kpi-icon">
          <Icon size={22} />
        </div>
      </div>

      <p className="kpi-description">
        {description}
      </p>
    </article>
  );
}


function ChartCard({
  title,
  subtitle,
  children,
  className = "",
}) {
  return (
    <section
      className={`chart-card ${className}`}
    >
      <div className="chart-header">
        <h2>{title}</h2>
        <p>{subtitle}</p>
      </div>

      <div className="chart-content">
        {children}
      </div>
    </section>
  );
}


const getInitialPage = () => {
  if (typeof window === "undefined") return "dashboard";
  const hash = window.location.hash.replace("#/", "").replace("#", "").trim();
  const validPages = ["dashboard", "cases", "case-details", "units", "unit-details", "analytics", "smart-import", "ai-intelligence"];
  if (hash && validPages.includes(hash)) {
    return hash;
  }
  const path = window.location.pathname.replace("/", "").trim();
  if (path && validPages.includes(path)) {
    return path;
  }
  const saved = localStorage.getItem("echo_active_page");
  if (saved && validPages.includes(saved)) {
    return saved;
  }
  return "dashboard";
};

function Dashboard() {
  const [activePage, setActivePageState] = useState(getInitialPage);

  const setActivePage = (page) => {
    setActivePageState(page);
    try {
      window.location.hash = `#/${page}`;
      localStorage.setItem("echo_active_page", page);
    } catch (e) {
      // Ignore storage errors
    }
  };

  useEffect(() => {
    const handleHashChange = () => {
      const page = getInitialPage();
      setActivePageState(page);
    };
    window.addEventListener("hashchange", handleHashChange);
    window.addEventListener("popstate", handleHashChange);
    return () => {
      window.removeEventListener("hashchange", handleHashChange);
      window.removeEventListener("popstate", handleHashChange);
    };
  }, []);

  const [selectedCaseId, setSelectedCaseId] =
    useState(null);

  const [selectedUnitId, setSelectedUnitId] =
    useState(null);

  const [activeAIModule, setActiveAIModule] =
    useState(null);

  const [dashboardData, setDashboardData] =
    useState(null);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState(null);

  const [refreshing, setRefreshing] =
    useState(false);


  async function loadDashboard(
    isRefresh = false
  ) {
    try {
      if (isRefresh) {
        setRefreshing(true);
      } else {
        setLoading(true);
      }

      setError(null);

      const data =
        await getDashboardData();

      setDashboardData(data);

    } catch (err) {

      console.error(
        "Dashboard loading failed:",
        err
      );

      setError(
        err.message ||
        "Unable to load dashboard"
      );

    } finally {

      setLoading(false);
      setRefreshing(false);
    }
  }


  useEffect(() => {
    loadDashboard();
  }, []);


  if (loading) {
    return (
      <div className="state-screen">
        <Shield
          size={48}
          strokeWidth={1.5}
        />

        <h1>Echo Protocol</h1>

        <p>
          Loading operational intelligence...
        </p>
      </div>
    );
  }


  if (error) {
    return (
      <div className="state-screen">
        <ShieldAlert
          size={48}
          strokeWidth={1.5}
        />

        <h1>
          Dashboard unavailable
        </h1>

        <p>{error}</p>

        <button
          className="primary-button"
          onClick={() =>
            loadDashboard()
          }
        >
          Try Again
        </button>
      </div>
    );
  }


  const {
    kpis,
    casesByStatus,
    casesByDistrict,
    casesByCrimeHead,
    casesByGravity,
    casesByMonth,
  } = dashboardData;


  return (
    <div className="app-shell">

      {/* =========================================
          SIDEBAR
      ========================================== */}
      <aside className="sidebar">

        <div className="brand">
          <div className="brand-mark">
            <Shield size={24} />
          </div>

          <div>
            <h1>
              Echo Protocol
            </h1>

            <p>
              Crime Intelligence
            </p>
          </div>
        </div>


        <nav className="navigation">

          <button
            className={
              activePage === "dashboard"
                ? "nav-item active"
                : "nav-item"
            }
            onClick={() => {
              setSelectedCaseId(null);
              setActivePage("dashboard");
            }}
          >
            <Activity size={19} />
            Dashboard
          </button>

          <button
            className={
              activePage === "cases" ||
                activePage === "case-details"
                ? "nav-item active"
                : "nav-item"
            }
            onClick={() => {
              setSelectedCaseId(null);
              setSelectedUnitId(null);
              setActivePage("cases");
            }}
          >
            <ClipboardList size={19} />
            Cases
          </button>

          <button
            className={
              activePage === "units" ||
                activePage === "unit-details"
                ? "nav-item active"
                : "nav-item"
            }
            onClick={() => {
              setSelectedCaseId(null);
              setActivePage("units");
            }}
          >
            <Building2 size={19} />
            Units
          </button>

          <button
            className={
              activePage === "analytics"
                ? "nav-item active"
                : "nav-item"
            }
            onClick={() => {
              setSelectedCaseId(null);
              setSelectedUnitId(null);
              setActivePage("analytics");
            }}
          >
            <BarChart3 size={19} />
            Analytics
          </button>
          <button
            className={
              activePage === "smart-import"
                ? "nav-item active"
                : "nav-item"
            }
            onClick={() => {
              setSelectedCaseId(null);
              setSelectedUnitId(null);
              setActivePage("smart-import");
            }}
          >
            <Upload size={19} />
            Smart Data Import
          </button>

          <button
            className={
              activePage === "ai-intelligence"
                ? "nav-item active"
                : "nav-item"
            }
            onClick={() => {
              setSelectedCaseId(null);
              setSelectedUnitId(null);
              setActivePage("ai-intelligence");
            }}
          >
            <Sparkles size={19} />
            AI Intelligence
          </button>

        </nav>


        <div className="sidebar-footer">
          <div className="system-status">
            <span className="status-dot" />

            <div>
              <strong>
                System Operational
              </strong>

              <span>
                Catalyst connected
              </span>
            </div>
          </div>
        </div>

      </aside>


      {/* =========================================
          MAIN CONTENT
      ========================================== */}
      <main className="main-content">

        {activePage === "case-details" ? (

          <CaseDetails
            rowId={selectedCaseId}
            onBack={() => {
              setSelectedCaseId(null);
              setActivePage("cases");
            }}
          />
        ) : activePage === "unit-details" ? (

          <UnitDetails
            rowId={selectedUnitId}
            onBack={() => {
              setSelectedUnitId(null);
              setActivePage("units");
            }}
            onSelectCase={(rowId) => {
              setSelectedCaseId(rowId);
              setActivePage("case-details");
            }}
          />
        ) : activePage === "cases" ? (

          <Cases
            onSelectCase={(rowId) => {
              setSelectedCaseId(rowId);
              setActivePage("case-details");
            }}
          />
        ) : activePage === "units" ? (

          <Units
            onSelectUnit={(rowId) => {
              setSelectedUnitId(rowId);
              setActivePage("unit-details");
            }}
          />

        ) : activePage === "analytics" ? (

          <Analytics
            onSelectUnit={(rowId) => {
              setSelectedUnitId(rowId);
              setActivePage("unit-details");
            }}
          />
        ) : activePage === "smart-import" ? (

          <SmartDataImport onGoToDashboard={() => {
            loadDashboard(true);
            setActivePage("dashboard");
          }} />

        ) : activePage === "ai-intelligence" ? (

          <AIIntelligence
            onOpenCopilot={() => setActiveAIModule("copilot")}
            onOpenMO={() => setActiveAIModule("mo")}
            onOpenPredictive={() => setActiveAIModule("predictive")}
          />

        ) : (

          <>
            {/* HEADER */}
            <header className="topbar">

              <div>
                <p className="eyebrow">
                  OPERATIONAL OVERVIEW
                </p>

                <h1>
                  Crime Intelligence Dashboard
                </h1>

                <p className="header-description">
                  Real-time operational view of
                  registered cases and police units.
                </p>
              </div>


              <button
                className="refresh-button"
                onClick={() =>
                  loadDashboard(true)
                }
                disabled={refreshing}
              >
                <RefreshCw
                  size={17}
                  className={
                    refreshing
                      ? "spin"
                      : ""
                  }
                />

                {refreshing
                  ? "Refreshing..."
                  : "Refresh"}
              </button>

            </header>


            {/* =========================================
            KPI CARDS
        ========================================== */}
            <section className="kpi-grid">

              <KpiCard
                title="Total Crimes"
                value={kpis.totalCrimes}
                icon={ClipboardList}
                description="All registered cases"
              />

              <KpiCard
                title="Open Cases"
                value={kpis.openCases}
                icon={Activity}
                description="Cases requiring action"
              />

              <KpiCard
                title="Closed Cases"
                value={kpis.closedCases}
                icon={CheckCircle2}
                description="Completed case workflows"
              />

              <KpiCard
                title="Charge Sheets"
                value={kpis.chargeSheets}
                icon={FileCheck2}
                description="Charge sheets filed"
              />

              <KpiCard
                title="Active Units"
                value={kpis.activePoliceStations}
                icon={BadgeCheck}
                description="Operational police units"
              />

            </section>


            {/* =========================================
            MONTHLY TREND + STATUS
        ========================================== */}
            <section className="dashboard-grid">

              <ChartCard
                title="Monthly Crime Trend"
                subtitle="Registered cases by month"
                className="wide-card"
              >
                <ResponsiveContainer
                  width="100%"
                  height={300}
                >
                  <LineChart
                    data={casesByMonth}
                    margin={{
                      top: 25,
                      right: 25,
                      left: -10,
                      bottom: 5,
                    }}
                  >
                    <CartesianGrid
                      stroke="#e2e8f0"
                      strokeDasharray="3 3"
                      vertical={false}
                    />

                    <XAxis
                      dataKey="label"
                      tickLine={false}
                      axisLine={false}
                      tick={{ fill: "#475569", fontSize: 12, fontWeight: 500 }}
                    />

                    <YAxis
                      allowDecimals={false}
                      tickLine={false}
                      axisLine={false}
                      tick={{ fill: "#475569", fontSize: 12, fontWeight: 500 }}
                    />

                    <Tooltip />

                    <Line
                      type="monotone"
                      dataKey="count"
                      stroke="#2563eb"
                      strokeWidth={3}
                      dot={{
                        r: 4,
                        fill: "#2563eb",
                      }}
                      activeDot={{
                        r: 6,
                      }}
                    >
                      <LabelList
                        dataKey="count"
                        content={renderLineCustomLabel}
                      />
                    </Line>
                  </LineChart>
                </ResponsiveContainer>
              </ChartCard>


              <ChartCard
                title="Case Status"
                subtitle="Current workflow distribution"
              >
                <ResponsiveContainer
                  width="100%"
                  height={300}
                >
                  <PieChart>
                    <Pie
                      data={casesByStatus}
                      dataKey="count"
                      nameKey="status"
                      innerRadius={55}
                      outerRadius={80}
                      paddingAngle={2}
                      label={renderPieCustomLabel}
                      labelLine={{ stroke: "#64748b", strokeWidth: 1.5 }}
                    >
                      {casesByStatus.map(
                        (_, index) => (
                          <Cell
                            key={index}
                            fill={
                              STATUS_COLORS[
                              index %
                              STATUS_COLORS.length
                              ]
                            }
                          />
                        )
                      )}
                    </Pie>

                    <Tooltip />

                    <Legend wrapperStyle={{ paddingTop: "10px", fontWeight: 600, fontSize: "12px", color: "#1e293b" }} />
                  </PieChart>
                </ResponsiveContainer>
              </ChartCard>

            </section>


            {/* =========================================
            CRIME HEAD + GRAVITY
        ========================================== */}
            <section className="dashboard-grid">

              <ChartCard
                title="Crime Classification"
                subtitle="Cases by major crime head"
                className="wide-card"
              >
                <ResponsiveContainer
                  width="100%"
                  height={340}
                >
                  <BarChart
                    data={casesByCrimeHead}
                    layout="vertical"
                    margin={{
                      top: 10,
                      right: 60,
                      left: 35,
                      bottom: 5,
                    }}
                  >
                    <CartesianGrid
                      stroke="#e2e8f0"
                      strokeDasharray="3 3"
                      horizontal={false}
                    />

                    <XAxis
                      type="number"
                      allowDecimals={false}
                      axisLine={false}
                      tickLine={false}
                      tick={{ fill: "#475569", fontSize: 12, fontWeight: 500 }}
                    />

                    <YAxis
                      type="category"
                      dataKey="crimeHead"
                      width={170}
                      axisLine={false}
                      tickLine={false}
                      tick={{ fill: "#475569", fontSize: 12, fontWeight: 500 }}
                    />

                    <Tooltip />

                    <Bar
                      dataKey="count"
                      fill="#2563eb"
                      radius={[0, 5, 5, 0]}
                    >
                      <LabelList
                        dataKey="count"
                        position="right"
                        fill="#1e293b"
                        fontSize={14}
                        fontWeight={600}
                        offset={10}
                      />
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </ChartCard>


              <ChartCard
                title="Offence Gravity"
                subtitle="Severity classification"
              >
                <ResponsiveContainer
                  width="100%"
                  height={340}
                >
                  <PieChart>
                    <Pie
                      data={casesByGravity}
                      dataKey="count"
                      nameKey="gravity"
                      outerRadius={85}
                      paddingAngle={3}
                      label={renderPieCustomLabel}
                      labelLine={{ stroke: "#64748b", strokeWidth: 1.5 }}
                    >
                      {casesByGravity.map(
                        (_, index) => (
                          <Cell
                            key={index}
                            fill={
                              GRAVITY_COLORS[
                              index %
                              GRAVITY_COLORS.length
                              ]
                            }
                          />
                        )
                      )}
                    </Pie>

                    <Tooltip />

                    <Legend wrapperStyle={{ paddingTop: "10px", fontWeight: 600, fontSize: "12px", color: "#1e293b" }} />
                  </PieChart>
                </ResponsiveContainer>
              </ChartCard>

            </section>


            {/* =========================================
            DISTRICT DISTRIBUTION
        ========================================== */}
            <section className="single-grid">

              <ChartCard
                title="District Distribution"
                subtitle="Registered cases across operational districts"
              >
                <ResponsiveContainer
                  width="100%"
                  height={300}
                >
                  <BarChart
                    data={casesByDistrict}
                    margin={{
                      top: 35,
                      right: 20,
                      left: -10,
                      bottom: 20,
                    }}
                  >
                    <CartesianGrid
                      stroke="#e2e8f0"
                      strokeDasharray="3 3"
                      vertical={false}
                    />

                    <XAxis
                      dataKey="district"
                      tickLine={false}
                      axisLine={false}
                      tick={{ fill: "#475569", fontSize: 12, fontWeight: 500 }}
                    />

                    <YAxis
                      allowDecimals={false}
                      tickLine={false}
                      axisLine={false}
                      tick={{ fill: "#475569", fontSize: 12, fontWeight: 500 }}
                    />

                    <Tooltip />

                    <Bar
                      dataKey="count"
                      fill="#1e3a5f"
                      radius={[6, 6, 0, 0]}
                    >
                      <LabelList
                        dataKey="count"
                        position="top"
                        fill="#1e293b"
                        fontSize={14}
                        fontWeight={600}
                        offset={10}
                      />
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </ChartCard>

            </section>


            <footer className="dashboard-footer">
              Echo Protocol · Prototype Crime
              Intelligence Platform
            </footer>

          </>
        )}

      </main>

      <InvestigationCopilot
        isOpen={activeAIModule === "copilot"}
        onClose={() => setActiveAIModule(null)}
        showTrigger={false}
      />
      <ModusOperandiAI
        isOpen={activeAIModule === "mo"}
        onClose={() => setActiveAIModule(null)}
        showTrigger={false}
      />
      <PredictiveIntelligence
        isOpen={activeAIModule === "predictive"}
        onClose={() => setActiveAIModule(null)}
        showTrigger={false}
      />

    </div>
  );
}


export default Dashboard;