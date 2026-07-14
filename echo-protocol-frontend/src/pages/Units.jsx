import {
  useEffect,
  useMemo,
  useState,
} from "react";

import {
  Building2,
  MapPin,
  RefreshCw,
  Search,
  ShieldCheck,
} from "lucide-react";

import {
  getUnits,
} from "../services/api";


function Units({
  onSelectUnit,
}) {

  const [units, setUnits] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState(null);

  const [searchTerm, setSearchTerm] =
    useState("");

  const [districtFilter, setDistrictFilter] =
    useState("All");

  const [typeFilter, setTypeFilter] =
    useState("All");


  async function loadUnits() {

    try {

      setLoading(true);
      setError(null);

      const data =
        await getUnits();

      setUnits(
        Array.isArray(data)
          ? data
          : []
      );

    } catch (err) {

      console.error(
        "Units loading failed:",
        err
      );

      setError(
        err.message ||
          "Unable to load units"
      );

    } finally {

      setLoading(false);

    }
  }


  useEffect(() => {

    loadUnits();

  }, []);


  const districts =
    useMemo(() => {

      return [
        "All",
        ...Array.from(
          new Set(
            units
              .map(
                (item) =>
                  item.district
              )
              .filter(Boolean)
          )
        ).sort(),
      ];

    }, [units]);


  const unitTypes =
    useMemo(() => {

      return [
        "All",
        ...Array.from(
          new Set(
            units
              .map(
                (item) =>
                  item.unitType
              )
              .filter(Boolean)
          )
        ).sort(),
      ];

    }, [units]);


  const filteredUnits =
    useMemo(() => {

      const normalizedSearch =
        searchTerm
          .trim()
          .toLowerCase();

      return units.filter(
        (item) => {

          const matchesSearch =
            !normalizedSearch ||
            [
              item.unitCode,
              item.unitName,
              item.district,
              item.unitType,
              item.address,
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


          const matchesType =
            typeFilter === "All" ||
            item.unitType ===
              typeFilter;


          return (
            matchesSearch &&
            matchesDistrict &&
            matchesType
          );

        }
      );

    }, [
      units,
      searchTerm,
      districtFilter,
      typeFilter,
    ]);


  const activeCount =
    units.filter(
      (item) => item.isActive
    ).length;


  if (loading) {

    return (
      <div className="cases-state">

        <RefreshCw
          size={34}
          className="spin"
        />

        <h2>
          Loading unit directory
        </h2>

        <p>
          Retrieving operational units...
        </p>

      </div>
    );
  }


  if (error) {

    return (
      <div className="cases-state">

        <Building2 size={42} />

        <h2>
          Unit directory unavailable
        </h2>

        <p>{error}</p>

        <button
          className="primary-button"
          onClick={loadUnits}
        >
          Try Again
        </button>

      </div>
    );
  }


  return (
    <div className="units-page">


      {/* HEADER */}

      <section className="units-header">

        <div>

          <p className="eyebrow">
            OPERATIONAL DIRECTORY
          </p>

          <h1>
            Police Units
          </h1>

          <p>
            Explore police stations,
            specialized units and
            operational jurisdictions.
          </p>

        </div>


        <div className="units-summary">

          <div>
            <span>
              Total Units
            </span>

            <strong>
              {units.length}
            </strong>
          </div>

          <div>
            <span>
              Active
            </span>

            <strong>
              {activeCount}
            </strong>
          </div>

        </div>

      </section>


      {/* FILTERS */}

      <section className="unit-filter-bar">


        <div className="unit-search-box">

          <Search size={17} />

          <input
            type="text"
            placeholder={
              "Search unit name, code or location..."
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
          value={typeFilter}
          onChange={
            (event) =>
              setTypeFilter(
                event.target.value
              )
          }
        >

          {unitTypes.map(
            (unitType) => (
              <option
                key={unitType}
                value={unitType}
              >
                {unitType === "All"
                  ? "All Unit Types"
                  : unitType}
              </option>
            )
          )}

        </select>

      </section>


      {/* RESULT COUNT */}

      <div className="unit-result-count">

        Showing{" "}
        <strong>
          {filteredUnits.length}
        </strong>{" "}
        of{" "}
        <strong>
          {units.length}
        </strong>{" "}
        units

      </div>


      {/* UNIT GRID */}

      {filteredUnits.length === 0 ? (

        <div className="cases-state">

          <Building2 size={36} />

          <h2>
            No units found
          </h2>

          <p>
            Try changing your search
            or filters.
          </p>

        </div>

      ) : (

        <section className="unit-card-grid">

          {filteredUnits.map(
            (unit) => (

              <article
  key={unit.rowId}
  className="unit-card unit-card-clickable"
  onClick={() =>
    onSelectUnit(unit.rowId)
  }
>

                <div className="unit-card-top">

                  <div className="unit-icon-box">

                    <Building2
                      size={20}
                    />

                  </div>


                  <span
                    className={
                      unit.isActive
                        ? "unit-status active"
                        : "unit-status inactive"
                    }
                  >

                    <ShieldCheck
                      size={13}
                    />

                    {unit.isActive
                      ? "Active"
                      : "Inactive"}

                  </span>

                </div>


                <div className="unit-card-body">

                  <span className="unit-code">
                    {unit.unitCode}
                  </span>

                  <h2>
                    {unit.unitName}
                  </h2>

                  <p className="unit-type">
                    {unit.unitType}
                  </p>

                </div>


                <div className="unit-card-location">

                  <MapPin size={15} />

                  <div>

                    <strong>
                      {unit.district}
                    </strong>

                    <span>
                      {unit.address ||
                        "Address not available"}
                    </span>

                  </div>

                </div>

              </article>

            )
          )}

        </section>

      )}

    </div>
  );
}


export default Units;