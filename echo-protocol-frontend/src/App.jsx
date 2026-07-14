import "./App.css";
import { useEffect, useState } from "react";
import {
  Activity,
  BadgeCheck,
  Building2,
  CheckCircle2,
  ClipboardList,
  FileCheck2,
  RefreshCw,
  Shield,
  ShieldAlert,
} from "lucide-react";

import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
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
import Cases from "./pages/Cases";
import CaseDetails from "./pages/CaseDetails";
import Units from "./pages/Units";

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


function Dashboard() {
  const [activePage, setActivePage] =
    useState("dashboard");
  
  const [selectedCaseId, setSelectedCaseId] =
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
  activePage === "cases" ||
  activePage === "case-details"
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
    activePage === "cases"
      ? "nav-item active"
      : "nav-item"
  }
  onClick={() => {
  setSelectedCaseId(null);
  setActivePage("cases");
}}
>
  <ClipboardList size={19} />
  Cases
</button>

          <button
  className={
    activePage === "units"
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

) : activePage === "cases" ? (

  <Cases
    onSelectCase={(rowId) => {
      setSelectedCaseId(rowId);
      setActivePage("case-details");
    }}
  />
) : activePage === "units" ? (

  <Units />
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
                  top: 10,
                  right: 15,
                  left: -15,
                  bottom: 0,
                }}
              >
                <CartesianGrid
                  strokeDasharray="3 3"
                  vertical={false}
                />

                <XAxis
                  dataKey="label"
                  tickLine={false}
                  axisLine={false}
                />

                <YAxis
                  allowDecimals={false}
                  tickLine={false}
                  axisLine={false}
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
                />
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
                  innerRadius={65}
                  outerRadius={95}
                  paddingAngle={2}
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

                <Legend />
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
                  top: 5,
                  right: 25,
                  left: 35,
                  bottom: 5,
                }}
              >
                <CartesianGrid
                  strokeDasharray="3 3"
                  horizontal={false}
                />

                <XAxis
                  type="number"
                  allowDecimals={false}
                  axisLine={false}
                  tickLine={false}
                />

                <YAxis
                  type="category"
                  dataKey="crimeHead"
                  width={170}
                  axisLine={false}
                  tickLine={false}
                />

                <Tooltip />

                <Bar
                  dataKey="count"
                  fill="#2563eb"
                  radius={[0, 5, 5, 0]}
                />
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
                  outerRadius={100}
                  paddingAngle={3}
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

                <Legend />
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
                  top: 10,
                  right: 20,
                  left: -10,
                  bottom: 20,
                }}
              >
                <CartesianGrid
                  strokeDasharray="3 3"
                  vertical={false}
                />

                <XAxis
                  dataKey="district"
                  tickLine={false}
                  axisLine={false}
                />

                <YAxis
                  allowDecimals={false}
                  tickLine={false}
                  axisLine={false}
                />

                <Tooltip />

                <Bar
                  dataKey="count"
                  fill="#1e3a5f"
                  radius={[6, 6, 0, 0]}
                />
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

    </div>
  );
}


export default Dashboard;