import {
  useEffect,
  useState,
} from "react";

import {
  ArrowLeft,
  Building2,
  CheckCircle2,
  FileText,
  MapPin,
  RefreshCw,
  ShieldAlert,
} from "lucide-react";

import {
  getUnitDetails,
} from "../services/api";


function UnitDetails({
  rowId,
  onBack,
  onSelectCase,
}) {

  const [unitData, setUnitData] =
    useState(null);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState(null);


  async function loadUnitDetails() {

    try {

      setLoading(true);
      setError(null);

      const data =
        await getUnitDetails(rowId);

      setUnitData(data);

    } catch (err) {

      console.error(
        "Unit details loading failed:",
        err
      );

      setError(
        err.message ||
          "Unable to load unit details"
      );

    } finally {

      setLoading(false);

    }
  }


  useEffect(() => {

    if (rowId) {
      loadUnitDetails();
    }

  }, [rowId]);


  if (loading) {

    return (
      <div className="cases-state">

        <RefreshCw
          size={34}
          className="spin"
        />

        <h2>
          Loading unit intelligence
        </h2>

        <p>
          Retrieving operational profile...
        </p>

      </div>
    );
  }


  if (error) {

    return (
      <div className="cases-state">

        <ShieldAlert size={42} />

        <h2>
          Unit details unavailable
        </h2>

        <p>{error}</p>

        <div className="case-detail-error-actions">

          <button
            className="primary-button"
            onClick={loadUnitDetails}
          >
            Try Again
          </button>

          <button
            className="clear-filter-button"
            onClick={onBack}
          >
            Back to Units
          </button>

        </div>

      </div>
    );
  }


  if (!unitData) {
    return null;
  }


  const statistics =
    unitData.statistics || {};


  return (
    <div className="unit-details-page">


      {/* BACK */}

      <button
        className="case-back-button"
        onClick={onBack}
      >
        <ArrowLeft size={17} />
        Back to Units
      </button>


      {/* HERO */}

      <section className="unit-details-hero">

        <div className="unit-details-hero-main">

          <div className="unit-details-icon">

            <Building2 size={24} />

          </div>


          <div>

            <p className="eyebrow">
              UNIT INTELLIGENCE PROFILE
            </p>

            <h1>
              {unitData.unitName}
            </h1>

            <p>
              {unitData.unitCode}
              {" · "}
              {unitData.unitType}
              {" · "}
              {unitData.district}
            </p>

          </div>

        </div>


        <span
          className={
            unitData.isActive
              ? "unit-status active"
              : "unit-status inactive"
          }
        >

          <CheckCircle2 size={14} />

          {unitData.isActive
            ? "Active Unit"
            : "Inactive Unit"}

        </span>

      </section>


      {/* KPI CARDS */}

      <section className="unit-detail-kpis">

        <article>

          <span>
            Total Cases
          </span>

          <strong>
            {statistics.totalCases ?? 0}
          </strong>

        </article>


        <article>

          <span>
            Open Cases
          </span>

          <strong>
            {statistics.openCases ?? 0}
          </strong>

        </article>


        <article>

          <span>
            Closed Cases
          </span>

          <strong>
            {statistics.closedCases ?? 0}
          </strong>

        </article>


        <article>

          <span>
            Charge Sheets
          </span>

          <strong>
            {statistics.chargeSheets ?? 0}
          </strong>

        </article>

      </section>


      {/* PROFILE + DISTRIBUTION */}

      <section className="unit-details-grid">


        {/* UNIT PROFILE */}

        <article className="case-detail-card">

          <div className="case-detail-card-header">

            <Building2 size={19} />

            <div>

              <h2>
                Unit Information
              </h2>

              <p>
                Operational and jurisdiction details
              </p>

            </div>

          </div>


          <div className="case-detail-list">

            <div className="case-detail-item">

              <span>
                Unit Code
              </span>

              <strong>
                {unitData.unitCode}
              </strong>

            </div>


            <div className="case-detail-item">

              <span>
                Unit Name
              </span>

              <strong>
                {unitData.unitName}
              </strong>

            </div>


            <div className="case-detail-item">

              <span>
                Unit Type
              </span>

              <strong>
                {unitData.unitType}
              </strong>

            </div>


            <div className="case-detail-item">

              <span>
                District
              </span>

              <strong>
                {unitData.district}
              </strong>

            </div>


            <div className="case-detail-item">

              <span>
                Status
              </span>

              <strong>
                {unitData.isActive
                  ? "Active"
                  : "Inactive"}
              </strong>

            </div>

          </div>

        </article>


        {/* LOCATION */}

        <article className="case-detail-card">

          <div className="case-detail-card-header">

            <MapPin size={19} />

            <div>

              <h2>
                Jurisdiction
              </h2>

              <p>
                Geographic and address information
              </p>

            </div>

          </div>


          <div className="case-detail-list">

            <div className="case-detail-item">

              <span>
                District
              </span>

              <strong>
                {unitData.district}
              </strong>

            </div>


            <div className="case-detail-item">

              <span>
                Address
              </span>

              <strong>
                {unitData.address ||
                  "Not available"}
              </strong>

            </div>

          </div>


          <div className="unit-address-box">

            <MapPin size={17} />

            <span>
              {unitData.address ||
                "Address not available"}
            </span>

          </div>

        </article>

      </section>


      {/* CRIME DISTRIBUTION */}

      <section className="unit-section-card">

        <div className="case-detail-card-header">

          <ShieldAlert size={19} />

          <div>

            <h2>
              Crime Distribution
            </h2>

            <p>
              Cases handled by crime classification
            </p>

          </div>

        </div>


        {unitData.crimeDistribution?.length ? (

          <div className="unit-crime-distribution">

            {unitData.crimeDistribution.map(
              (item) => {

                const percentage =
                  statistics.totalCases
                    ? Math.round(
                        (
                          item.count /
                          statistics.totalCases
                        ) * 100
                      )
                    : 0;


                return (
                  <div
                    key={item.label}
                    className="unit-crime-row"
                  >

                    <div className="unit-crime-row-header">

                      <span>
                        {item.label}
                      </span>

                      <strong>
                        {item.count}
                      </strong>

                    </div>


                    <div className="unit-crime-track">

                      <div
                        className="unit-crime-fill"
                        style={{
                          width:
                            `${percentage}%`,
                        }}
                      />

                    </div>

                  </div>
                );
              }
            )}

          </div>

        ) : (

          <p className="unit-empty-text">
            No case distribution available.
          </p>

        )}

      </section>


      {/* RECENT CASES */}

      <section className="unit-section-card">

        <div className="case-detail-card-header">

          <FileText size={19} />

          <div>

            <h2>
              Recent Cases
            </h2>

            <p>
              Cases currently associated with this unit
            </p>

          </div>

        </div>


        {unitData.recentCases?.length ? (

          <div className="unit-recent-cases">

            {unitData.recentCases.map(
              (caseItem) => (

                <button
                  key={caseItem.rowId}
                  className="unit-recent-case-row"
                  onClick={() =>
                    onSelectCase(
                      caseItem.rowId
                    )
                  }
                >

                  <div>

                    <strong>
                      {caseItem.crimeNo}
                    </strong>

                    <span>
                      {caseItem.firNumber}
                    </span>

                  </div>


                  <div>

                    <span>
                      {caseItem.crimeHead}
                    </span>

                    <small>
                      {caseItem.registeredDate}
                    </small>

                  </div>


                  <span className="unit-case-status">
                    {caseItem.status}
                  </span>

                </button>

              )
            )}

          </div>

        ) : (

          <p className="unit-empty-text">
            No cases are currently associated
            with this unit.
          </p>

        )}

      </section>


    </div>
  );
}


export default UnitDetails;