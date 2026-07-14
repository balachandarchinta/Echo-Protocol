    import { useEffect, useState } from "react";
import {
  ArrowLeft,
  CalendarDays,
  FileText,
  MapPin,
  RefreshCw,
  ShieldAlert,
  UserRound,
  Building2,
} from "lucide-react";

import {
  getCaseDetails,
} from "../services/api";


function DetailItem({
  label,
  value,
}) {
  return (
    <div className="case-detail-item">
      <span>{label}</span>
      <strong>
        {value || "Not available"}
      </strong>
    </div>
  );
}


function CaseDetails({
  rowId,
  onBack,
}) {

  const [caseData, setCaseData] =
    useState(null);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState(null);


  async function loadCaseDetails() {

    try {

      setLoading(true);
      setError(null);

      const data =
        await getCaseDetails(
          rowId
        );

      setCaseData(data);

    } catch (err) {

      console.error(
        "Case details loading failed:",
        err
      );

      setError(
        err.message ||
          "Unable to load case details"
      );

    } finally {

      setLoading(false);

    }
  }


  useEffect(() => {

    if (rowId) {
      loadCaseDetails();
    }

  }, [rowId]);


  function getStatusClass(status) {

    const normalized =
      String(status || "")
        .toLowerCase()
        .replace(/\s+/g, "-");

    return (
      `case-badge status-${normalized}`
    );
  }


  function getGravityClass(gravity) {

    const normalized =
      String(gravity || "")
        .toLowerCase();

    return (
      `case-badge gravity-${normalized}`
    );
  }


  if (loading) {

    return (
      <div className="cases-state">

        <RefreshCw
          size={34}
          className="spin"
        />

        <h2>
          Loading case intelligence
        </h2>

        <p>
          Retrieving complete case
          information...
        </p>

      </div>
    );
  }


  if (error) {

    return (
      <div className="cases-state">

        <ShieldAlert size={42} />

        <h2>
          Case details unavailable
        </h2>

        <p>{error}</p>

        <div className="case-detail-error-actions">

          <button
            className="primary-button"
            onClick={loadCaseDetails}
          >
            Try Again
          </button>

          <button
            className="clear-filter-button"
            onClick={onBack}
          >
            Back to Cases
          </button>

        </div>

      </div>
    );
  }


  if (!caseData) {

    return null;

  }


  return (
    <div className="case-details-page">


      {/* BACK NAVIGATION */}

      <button
        className="case-back-button"
        onClick={onBack}
      >
        <ArrowLeft size={17} />
        Back to Cases
      </button>


      {/* CASE HEADER */}

      <section className="case-details-hero">

        <div>

          <p className="eyebrow">
            CASE INTELLIGENCE PROFILE
          </p>

          <div className="case-details-title-row">

            <h1>
              {caseData.crimeNo}
            </h1>

            <span
              className={
                getStatusClass(
                  caseData.status
                )
              }
            >
              {caseData.status}
            </span>

            <span
              className={
                getGravityClass(
                  caseData.gravity
                )
              }
            >
              {caseData.gravity}
            </span>

          </div>

          <p className="case-details-subtitle">
            FIR {caseData.firNumber}
            {" · "}
            Registered{" "}
            {caseData.registeredDate}
          </p>

        </div>

      </section>


      {/* PRIMARY INFORMATION */}

      <section className="case-details-grid">


        {/* CASE INFORMATION */}

        <article className="case-detail-card">

          <div className="case-detail-card-header">

            <FileText size={19} />

            <div>
              <h2>
                Case Information
              </h2>

              <p>
                Registration and
                classification details
              </p>
            </div>

          </div>


          <div className="case-detail-list">

            <DetailItem
              label="Crime Number"
              value={caseData.crimeNo}
            />

            <DetailItem
              label="FIR Number"
              value={caseData.firNumber}
            />

            <DetailItem
              label="Registered Date"
              value={
                caseData.registeredDate
              }
            />

            <DetailItem
              label="Case Category"
              value={
                caseData.caseCategory
              }
            />

            <DetailItem
              label="Crime Head"
              value={caseData.crimeHead}
            />

            <DetailItem
              label="Crime Sub Head"
              value={
                caseData.crimeSubHead
              }
            />

          </div>

        </article>


        {/* JURISDICTION */}

        <article className="case-detail-card">

          <div className="case-detail-card-header">

            <Building2 size={19} />

            <div>
              <h2>
                Jurisdiction
              </h2>

              <p>
                Operational ownership
                and investigation
              </p>
            </div>

          </div>


          <div className="case-detail-list">

            <DetailItem
              label="District"
              value={caseData.district}
            />

            <DetailItem
              label="Police Station"
              value={
                caseData.policeStation
              }
            />

            <DetailItem
              label="Investigating Officer"
              value={
                caseData
                  .investigatingOfficer
              }
            />

            <DetailItem
              label="Case Status"
              value={caseData.status}
            />

            <DetailItem
              label="Offence Gravity"
              value={caseData.gravity}
            />

          </div>

        </article>


        {/* INCIDENT TIMELINE */}

        <article className="case-detail-card">

          <div className="case-detail-card-header">

            <CalendarDays size={19} />

            <div>
              <h2>
                Incident Timeline
              </h2>

              <p>
                Key dates associated
                with the case
              </p>
            </div>

          </div>


          <div className="case-detail-list">

            <DetailItem
              label="Incident From"
              value={
                caseData
                  .incidentFromDate
              }
            />

            <DetailItem
              label="Incident To"
              value={
                caseData
                  .incidentToDate
              }
            />

            <DetailItem
              label="Information Received"
              value={
                caseData
                  .informationReceivedDate
              }
            />

            <DetailItem
              label="Case Registered"
              value={
                caseData.registeredDate
              }
            />

          </div>

        </article>


        {/* LOCATION */}

        <article className="case-detail-card">

          <div className="case-detail-card-header">

            <MapPin size={19} />

            <div>
              <h2>
                Incident Location
              </h2>

              <p>
                Geographic reference
                information
              </p>
            </div>

          </div>


          <div className="case-detail-list">

            <DetailItem
              label="District"
              value={caseData.district}
            />

            <DetailItem
              label="Latitude"
              value={caseData.latitude}
            />

            <DetailItem
              label="Longitude"
              value={caseData.longitude}
            />

          </div>


          <div className="case-coordinate-box">

            <MapPin size={18} />

            <span>
              {caseData.latitude},
              {" "}
              {caseData.longitude}
            </span>

          </div>

        </article>

      </section>


      {/* BRIEF FACTS */}

      <section className="case-brief-card">

        <div className="case-detail-card-header">

          <ShieldAlert size={19} />

          <div>
            <h2>
              Brief Facts
            </h2>

            <p>
              Available case narrative
            </p>
          </div>

        </div>

        <p className="case-brief-text">
          {caseData.briefFacts ||
            "No brief facts available."}
        </p>

      </section>


      {/* INVESTIGATION SUMMARY */}

      <section className="case-investigation-strip">

        <div>

          <UserRound size={19} />

          <div>
            <span>
              Investigating Officer
            </span>

            <strong>
              {
                caseData
                  .investigatingOfficer
              }
            </strong>
          </div>

        </div>

        <div>

          <span>
            Current Status
          </span>

          <strong>
            {caseData.status}
          </strong>

        </div>

      </section>


    </div>
  );
}


export default CaseDetails;