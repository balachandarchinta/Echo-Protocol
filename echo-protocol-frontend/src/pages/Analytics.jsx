import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  CircleMarker,
  MapContainer,
  Popup,
  TileLayer,
} from "react-leaflet";

import "leaflet/dist/leaflet.css";

import {
  AlertTriangle,
  Crosshair,
  MapPin,
  RefreshCw,
  Search,
  ShieldAlert,
} from "lucide-react";

import {
  getCrimeHotspots,
} from "../services/api";


function Analytics() {

  const [hotspots, setHotspots] =
    useState([]);

  const [meta, setMeta] =
    useState({
      totalHotspots: 0,
      mappedCases: 0,
      unmappedCases: 0,
    });

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState(null);

  const [searchTerm, setSearchTerm] =
    useState("");

  const [districtFilter, setDistrictFilter] =
    useState("All");

  const [riskFilter, setRiskFilter] =
    useState("All");


  async function loadHotspots() {

    try {

      setLoading(true);
      setError(null);

      const data =
        await getCrimeHotspots();

      setHotspots(
        Array.isArray(data?.hotspots)
          ? data.hotspots
          : []
      );

      setMeta(
        data?.meta || {
          totalHotspots: 0,
          mappedCases: 0,
          unmappedCases: 0,
        }
      );

    } catch (err) {

      console.error(
        "Hotspot analytics loading failed:",
        err
      );

      setError(
        err.message ||
          "Unable to load hotspot analytics"
      );

    } finally {

      setLoading(false);

    }
  }


  useEffect(() => {

    loadHotspots();

  }, []);


  const districts =
    useMemo(() => {

      return [
        "All",
        ...Array.from(
          new Set(
            hotspots
              .map(
                (item) =>
                  item.district
              )
              .filter(Boolean)
          )
        ).sort(),
      ];

    }, [hotspots]);


  const filteredHotspots =
    useMemo(() => {

      const normalizedSearch =
        searchTerm
          .trim()
          .toLowerCase();


      return hotspots
        .filter((item) => {

          const matchesSearch =
            !normalizedSearch ||
            [
              item.policeStation,
              item.district,
              item.dominantCrime,
              item.riskLevel,
            ]
              .filter(Boolean)
              .some(
                (value) =>
                  String(value)
                    .toLowerCase()
                    .includes(
                      normalizedSearch
                    )
              );


          const matchesDistrict =
            districtFilter === "All" ||
            item.district ===
              districtFilter;


          const matchesRisk =
            riskFilter === "All" ||
            item.riskLevel ===
              riskFilter;


          return (
            matchesSearch &&
            matchesDistrict &&
            matchesRisk
          );

        })
        .sort(
          (a, b) =>
            b.caseCount -
            a.caseCount
        );

    }, [
      hotspots,
      searchTerm,
      districtFilter,
      riskFilter,
    ]);


  const highRiskCount =
    hotspots.filter(
      (item) =>
        item.riskLevel === "Critical" ||
        item.riskLevel === "High"
    ).length;


  const seriousCases =
    hotspots.reduce(
      (total, item) =>
        total +
        Number(
          item.seriousCases || 0
        ),
      0
    );


  const heinousCases =
    hotspots.reduce(
      (total, item) =>
        total +
        Number(
          item.heinousCases || 0
        ),
      0
    );


  if (loading) {

    return (
      <div className="cases-state">

        <RefreshCw
          size={34}
          className="spin"
        />

        <h2>
          Loading crime intelligence
        </h2>

        <p>
          Calculating geographic
          crime concentrations...
        </p>

      </div>
    );
  }


  if (error) {

    return (
      <div className="cases-state">

        <ShieldAlert size={42} />

        <h2>
          Analytics unavailable
        </h2>

        <p>{error}</p>

        <button
          className="primary-button"
          onClick={loadHotspots}
        >
          Try Again
        </button>

      </div>
    );
  }


  return (
    <div className="analytics-page">


      {/* HEADER */}

      <section className="analytics-header">

        <div>

          <p className="eyebrow">
            CRIME INTELLIGENCE
          </p>

          <h1>
            Hotspot Analytics
          </h1>

          <p>
            Identify geographic
            concentrations of crime
            and prioritize operational
            response.
          </p>

        </div>


        <div className="analytics-live-badge">

          <Crosshair size={16} />

          Live Intelligence

        </div>

      </section>


      {/* KPI CARDS */}

      <section className="analytics-kpis">

        <article>

          <span>
            Hotspots
          </span>

          <strong>
            {meta.totalHotspots}
          </strong>

        </article>


        <article>

          <span>
            Mapped Cases
          </span>

          <strong>
            {meta.mappedCases}
          </strong>

        </article>


        <article>

          <span>
            High-Risk Zones
          </span>

          <strong>
            {highRiskCount}
          </strong>

        </article>


        <article>

          <span>
            Serious Cases
          </span>

          <strong>
            {seriousCases}
          </strong>

        </article>


        <article>

          <span>
            Heinous Cases
          </span>

          <strong>
            {heinousCases}
          </strong>

        </article>

      </section>


      {/* FILTERS */}

      <section className="analytics-filter-bar">

        <div className="unit-search-box">

          <Search size={17} />

          <input
            type="text"
            placeholder={
              "Search station, district or crime type..."
            }
            value={searchTerm}
            onChange={
              (event) =>
                setSearchTerm(
                  event.target.value
                )
            }
          />

        </div>


        <select
          value={districtFilter}
          onChange={
            (event) =>
              setDistrictFilter(
                event.target.value
              )
          }
        >

          {districts.map(
            (district) => (
              <option
                key={district}
                value={district}
              >
                {district === "All"
                  ? "All Districts"
                  : district}
              </option>
            )
          )}

        </select>


        <select
          value={riskFilter}
          onChange={
            (event) =>
              setRiskFilter(
                event.target.value
              )
          }
        >

          <option value="All">
            All Risk Levels
          </option>

          <option value="Critical">
            Critical
          </option>

          <option value="High">
            High
          </option>

          <option value="Moderate">
            Moderate
          </option>

          <option value="Low">
            Low
          </option>

        </select>

      </section>


      {/* INTELLIGENCE PANEL */}

      <section className="analytics-layout">


        {/* HOTSPOT VISUALIZATION */}

        <article className="hotspot-visual-card">

          <div className="analytics-card-header">

            <div>

              <h2>
                Geographic Concentration
              </h2>

              <p>
                Relative crime intensity
                by operational unit
              </p>

            </div>

            <MapPin size={20} />

          </div>


          <div className="hotspot-map-wrapper">

  <MapContainer
    center={[12.75, 76.65]}
    zoom={7}
    scrollWheelZoom={true}
    className="hotspot-map"
  >

    <TileLayer
      attribution="&copy; OpenStreetMap contributors"
      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
    />


    {filteredHotspots.map(
      (item, index) => {

        const latitude =
          Number(item.latitude);

        const longitude =
          Number(item.longitude);


        if (
          !Number.isFinite(latitude) ||
          !Number.isFinite(longitude)
        ) {
          return null;
        }


        const riskLevel =
          item.riskLevel || "Low";


        const riskColor =
          riskLevel === "Critical"
            ? "#dc2626"
            : riskLevel === "High"
            ? "#f97316"
            : riskLevel === "Moderate"
            ? "#eab308"
            : "#16a34a";


        const radius =
          Math.max(
            10,
            Math.min(
              24,
              Number(item.caseCount || 0)
            )
          );


        return (
          <CircleMarker
            key={
              `${item.policeStation}-${index}`
            }
            center={[
              latitude,
              longitude,
            ]}
            radius={radius}
            pathOptions={{
              color: riskColor,
              fillColor: riskColor,
              fillOpacity: 0.55,
              weight: 2,
            }}
          >

            <Popup>

              <div className="hotspot-popup">

                <strong>
                  {item.policeStation}
                </strong>

                <span>
                  {item.district}
                </span>

                <hr />

                <p>
                  <b>Cases:</b>{" "}
                  {item.caseCount}
                </p>

                <p>
                  <b>Risk:</b>{" "}
                  {item.riskLevel}
                </p>

                <p>
                  <b>Serious Cases:</b>{" "}
                  {item.seriousCases}
                </p>

                <p>
                  <b>Heinous Cases:</b>{" "}
                  {item.heinousCases}
                </p>

                <p>
                  <b>Dominant Crime:</b>{" "}
                  {item.dominantCrime}
                </p>

              </div>

            </Popup>

          </CircleMarker>
        );
      }
    )}

  </MapContainer>

</div>


          <div className="hotspot-legend">

            <span>
              <i className="legend-dot critical" />
              Critical
            </span>

            <span>
              <i className="legend-dot high" />
              High
            </span>

            <span>
              <i className="legend-dot moderate" />
              Moderate
            </span>

            <span>
              <i className="legend-dot low" />
              Low
            </span>

          </div>

        </article>


        {/* PRIORITY ZONES */}

        <article className="priority-zones-card">

          <div className="analytics-card-header">

            <div>

              <h2>
                Priority Zones
              </h2>

              <p>
                Ranked by case volume
              </p>

            </div>

            <AlertTriangle size={19} />

          </div>


          <div className="priority-zone-list">

            {filteredHotspots
              .slice(0, 7)
              .map(
                (item, index) => (

                  <div
                    key={
                      `${item.policeStation}-${index}`
                    }
                    className="priority-zone-row"
                  >

                    <span className="priority-rank">
                      {index + 1}
                    </span>


                    <div className="priority-zone-info">

                      <strong>
                        {item.policeStation}
                      </strong>

                      <span>
                        {item.district}
                        {" · "}
                        {item.dominantCrime}
                      </span>

                    </div>


                    <div className="priority-zone-count">

                      <strong>
                        {item.caseCount}
                      </strong>

                      <span>
                        cases
                      </span>

                    </div>

                  </div>

                )
              )}

          </div>

        </article>

      </section>


      {/* HOTSPOT TABLE */}

      <section className="analytics-table-card">

        <div className="analytics-card-header">

          <div>

            <h2>
              Hotspot Intelligence Register
            </h2>

            <p>
              Detailed operational
              hotspot assessment
            </p>

          </div>

        </div>


        <div className="analytics-table-wrap">

          <table className="analytics-table">

            <thead>

              <tr>

                <th>
                  Police Station
                </th>

                <th>
                  District
                </th>

                <th>
                  Cases
                </th>

                <th>
                  Serious
                </th>

                <th>
                  Heinous
                </th>

                <th>
                  Dominant Crime
                </th>

                <th>
                  Risk
                </th>

              </tr>

            </thead>


            <tbody>

              {filteredHotspots.map(
                (item, index) => (

                  <tr
                    key={
                      `${item.policeStation}-${index}`
                    }
                  >

                    <td>
                      <strong>
                        {item.policeStation}
                      </strong>
                    </td>

                    <td>
                      {item.district}
                    </td>

                    <td>
                      {item.caseCount}
                    </td>

                    <td>
                      {item.seriousCases}
                    </td>

                    <td>
                      {item.heinousCases}
                    </td>

                    <td>
                      {item.dominantCrime}
                    </td>

                    <td>

                      <span
                        className={
                          `analytics-risk risk-${item.riskLevel.toLowerCase()}`
                        }
                      >
                        {item.riskLevel}
                      </span>

                    </td>

                  </tr>

                )
              )}

            </tbody>

          </table>

        </div>

      </section>

    </div>
  );
}


export default Analytics;