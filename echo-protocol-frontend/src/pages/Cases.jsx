import { useEffect, useMemo, useState } from "react";
import {
  ChevronLeft,
  ChevronRight,
  RefreshCw,
  Search,
  ShieldAlert,
} from "lucide-react";

import { getCases } from "../services/api";

const PAGE_SIZE = 10;

function Cases() {
  const [cases, setCases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState(null);

  const [searchTerm, setSearchTerm] = useState("");
  const [districtFilter, setDistrictFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [gravityFilter, setGravityFilter] = useState("");
  const [crimeHeadFilter, setCrimeHeadFilter] = useState("");

  const [currentPage, setCurrentPage] = useState(1);

  async function loadCases(isRefresh = false) {
    try {
      if (isRefresh) {
        setRefreshing(true);
      } else {
        setLoading(true);
      }

      setError(null);

      const data = await getCases();

      setCases(
        Array.isArray(data)
          ? data
          : []
      );
    } catch (err) {
      console.error(
        "Case list loading failed:",
        err
      );

      setError(
        err.message ||
          "Unable to load case records"
      );
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }

  useEffect(() => {
    loadCases();
  }, []);

  const districts = useMemo(() => {
    return [
      ...new Set(
        cases
          .map((item) => item.district)
          .filter(Boolean)
      ),
    ].sort();
  }, [cases]);

  const statuses = useMemo(() => {
    return [
      ...new Set(
        cases
          .map((item) => item.status)
          .filter(Boolean)
      ),
    ].sort();
  }, [cases]);

  const gravities = useMemo(() => {
    return [
      ...new Set(
        cases
          .map((item) => item.gravity)
          .filter(Boolean)
      ),
    ].sort();
  }, [cases]);

  const crimeHeads = useMemo(() => {
    return [
      ...new Set(
        cases
          .map((item) => item.crimeHead)
          .filter(Boolean)
      ),
    ].sort();
  }, [cases]);

  const filteredCases = useMemo(() => {
    const normalizedSearch =
      searchTerm
        .trim()
        .toLowerCase();

    return cases.filter((item) => {
      const matchesSearch =
        !normalizedSearch ||
        [
          item.crimeNo,
          item.firNumber,
          item.policeStation,
          item.crimeHead,
          item.crimeSubHead,
        ].some((value) =>
          String(value || "")
            .toLowerCase()
            .includes(normalizedSearch)
        );

      const matchesDistrict =
        !districtFilter ||
        item.district === districtFilter;

      const matchesStatus =
        !statusFilter ||
        item.status === statusFilter;

      const matchesGravity =
        !gravityFilter ||
        item.gravity === gravityFilter;

      const matchesCrimeHead =
        !crimeHeadFilter ||
        item.crimeHead === crimeHeadFilter;

      return (
        matchesSearch &&
        matchesDistrict &&
        matchesStatus &&
        matchesGravity &&
        matchesCrimeHead
      );
    });
  }, [
    cases,
    searchTerm,
    districtFilter,
    statusFilter,
    gravityFilter,
    crimeHeadFilter,
  ]);

  const totalPages = Math.max(
    1,
    Math.ceil(
      filteredCases.length / PAGE_SIZE
    )
  );

  const safeCurrentPage = Math.min(
    currentPage,
    totalPages
  );

  const paginatedCases = useMemo(() => {
    const startIndex =
      (safeCurrentPage - 1) *
      PAGE_SIZE;

    return filteredCases.slice(
      startIndex,
      startIndex + PAGE_SIZE
    );
  }, [
    filteredCases,
    safeCurrentPage,
  ]);

  function updateFilter(setter, value) {
    setter(value);
    setCurrentPage(1);
  }

  function clearFilters() {
    setSearchTerm("");
    setDistrictFilter("");
    setStatusFilter("");
    setGravityFilter("");
    setCrimeHeadFilter("");
    setCurrentPage(1);
  }

  function getStatusClass(status) {
    const normalized =
      String(status || "")
        .toLowerCase()
        .replace(/\s+/g, "-");

    return `case-badge status-${normalized}`;
  }

  function getGravityClass(gravity) {
    const normalized =
      String(gravity || "")
        .toLowerCase();

    return `case-badge gravity-${normalized}`;
  }

  if (loading) {
    return (
      <div className="cases-state">
        <RefreshCw
          size={34}
          className="spin"
        />

        <h2>Loading cases</h2>

        <p>
          Retrieving registered case records...
        </p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="cases-state">
        <ShieldAlert size={42} />

        <h2>Cases unavailable</h2>

        <p>{error}</p>

        <button
          className="primary-button"
          onClick={() => loadCases()}
        >
          Try Again
        </button>
      </div>
    );
  }

  const startResult =
    filteredCases.length === 0
      ? 0
      : (safeCurrentPage - 1) *
          PAGE_SIZE +
        1;

  const endResult = Math.min(
    safeCurrentPage * PAGE_SIZE,
    filteredCases.length
  );

  return (
    <div className="cases-page">

      {/* HEADER */}
      <header className="topbar">
        <div>
          <p className="eyebrow">
            CASE INTELLIGENCE
          </p>

          <h1>
            Registered Cases
          </h1>

          <p className="header-description">
            Search, filter, and review registered
            crime records across operational
            districts.
          </p>
        </div>

        <button
          className="refresh-button"
          onClick={() =>
            loadCases(true)
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


      {/* FILTER PANEL */}
      <section className="case-filter-card">

        <div className="case-search-wrapper">
          <Search
            size={18}
            className="case-search-icon"
          />

          <input
            type="text"
            className="case-search-input"
            placeholder="Search Crime No, FIR No, police station..."
            value={searchTerm}
            onChange={(event) =>
              updateFilter(
                setSearchTerm,
                event.target.value
              )
            }
          />
        </div>


        <div className="case-filter-grid">

          <select
            value={districtFilter}
            onChange={(event) =>
              updateFilter(
                setDistrictFilter,
                event.target.value
              )
            }
          >
            <option value="">
              All Districts
            </option>

            {districts.map((district) => (
              <option
                key={district}
                value={district}
              >
                {district}
              </option>
            ))}
          </select>


          <select
            value={statusFilter}
            onChange={(event) =>
              updateFilter(
                setStatusFilter,
                event.target.value
              )
            }
          >
            <option value="">
              All Statuses
            </option>

            {statuses.map((status) => (
              <option
                key={status}
                value={status}
              >
                {status}
              </option>
            ))}
          </select>


          <select
            value={gravityFilter}
            onChange={(event) =>
              updateFilter(
                setGravityFilter,
                event.target.value
              )
            }
          >
            <option value="">
              All Gravities
            </option>

            {gravities.map((gravity) => (
              <option
                key={gravity}
                value={gravity}
              >
                {gravity}
              </option>
            ))}
          </select>


          <select
            value={crimeHeadFilter}
            onChange={(event) =>
              updateFilter(
                setCrimeHeadFilter,
                event.target.value
              )
            }
          >
            <option value="">
              All Crime Heads
            </option>

            {crimeHeads.map((crimeHead) => (
              <option
                key={crimeHead}
                value={crimeHead}
              >
                {crimeHead}
              </option>
            ))}
          </select>


          <button
            className="clear-filter-button"
            onClick={clearFilters}
          >
            Clear Filters
          </button>

        </div>

      </section>


      {/* CASE TABLE */}
      <section className="case-table-card">

        <div className="case-table-header">
          <div>
            <h2>Case Records</h2>

            <p>
              {filteredCases.length} of{" "}
              {cases.length} registered cases
            </p>
          </div>
        </div>


        <div className="case-table-wrapper">

          <table className="case-table">

            <thead>
              <tr>
                <th>Crime No</th>
                <th>FIR No</th>
                <th>Registered</th>
                <th>District</th>
                <th>Police Station</th>
                <th>Crime Classification</th>
                <th>Status</th>
                <th>Gravity</th>
              </tr>
            </thead>

            <tbody>

              {paginatedCases.length === 0 ? (
                <tr>
                  <td
                    colSpan="8"
                    className="case-empty-state"
                  >
                    No cases match the selected
                    search and filters.
                  </td>
                </tr>
              ) : (
                paginatedCases.map((item) => (
                  <tr key={item.rowId}>

                    <td>
                      <strong className="case-number">
                        {item.crimeNo}
                      </strong>
                    </td>

                    <td>
                      {item.firNumber}
                    </td>

                    <td>
                      {item.firDate}
                    </td>

                    <td>
                      {item.district}
                    </td>

                    <td>
                      {item.policeStation}
                    </td>

                    <td>
                      <div className="crime-classification">
                        <strong>
                          {item.crimeHead}
                        </strong>

                        <span>
                          {item.crimeSubHead}
                        </span>
                      </div>
                    </td>

                    <td>
                      <span
                        className={
                          getStatusClass(
                            item.status
                          )
                        }
                      >
                        {item.status}
                      </span>
                    </td>

                    <td>
                      <span
                        className={
                          getGravityClass(
                            item.gravity
                          )
                        }
                      >
                        {item.gravity}
                      </span>
                    </td>

                  </tr>
                ))
              )}

            </tbody>

          </table>

        </div>


        {/* PAGINATION */}
        <div className="case-pagination">

          <p>
            Showing {startResult}–{endResult} of{" "}
            {filteredCases.length}
          </p>


          <div className="pagination-controls">

            <button
              onClick={() =>
                setCurrentPage((page) =>
                  Math.max(1, page - 1)
                )
              }
              disabled={
                safeCurrentPage === 1
              }
            >
              <ChevronLeft size={17} />
              Previous
            </button>


            <span>
              Page {safeCurrentPage} of{" "}
              {totalPages}
            </span>


            <button
              onClick={() =>
                setCurrentPage((page) =>
                  Math.min(
                    totalPages,
                    page + 1
                  )
                )
              }
              disabled={
                safeCurrentPage ===
                totalPages
              }
            >
              Next
              <ChevronRight size={17} />
            </button>

          </div>

        </div>

      </section>

    </div>
  );
}

export default Cases;