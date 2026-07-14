import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.catalyst.advanced.CatalystAdvancedIOHandler;
import com.zc.component.object.ZCObject;
import com.zc.component.object.ZCTable;
import com.zc.component.object.ZCRowObject;

public class EchoProtocolAPI implements CatalystAdvancedIOHandler {

    private static final Logger LOGGER =
        Logger.getLogger(EchoProtocolAPI.class.getName());

    @Override
    public void runner(
    HttpServletRequest request,
    HttpServletResponse response
    ) throws Exception {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // =====================================================
    // CORS HEADERS
    // Allows the local React frontend to call this API.
    // =====================================================
    response.setHeader(
        "Access-Control-Allow-Origin",
        "*"
    );

    response.setHeader(
        "Access-Control-Allow-Methods",
        "GET, POST, PUT, DELETE, OPTIONS"
    );

    response.setHeader(
        "Access-Control-Allow-Headers",
        "Content-Type, Authorization"
    );


    // =====================================================
    // HANDLE CORS PREFLIGHT REQUEST
    // =====================================================
    if ("OPTIONS".equalsIgnoreCase(
            request.getMethod()
    )) {

        response.setStatus(
            HttpServletResponse.SC_OK
        );

        return;
    }


    try {

            String path = request.getRequestURI();
            String method = request.getMethod();

            LOGGER.log(
                Level.INFO,
                "Request received: {0} {1}",
                new Object[]{method, path}
            );


            // =====================================================
            // 1. HEALTH CHECK
            // =====================================================
            if ("/".equals(path)
                    && "GET".equalsIgnoreCase(method)) {

                response.setStatus(
                    HttpServletResponse.SC_OK
                );

                response.getWriter().write(
                    "{"
                    + "\"success\":true,"
                    + "\"message\":\"Echo Protocol API is running\""
                    + "}"
                );

                return;
            }


            // =====================================================
            // 2. TEST CATALYST DATA STORE CONNECTION
            // =====================================================
            if ("/admin/test-datastore".equals(path)
                    && "GET".equalsIgnoreCase(method)) {

                LOGGER.info(
                    "Testing Catalyst Data Store connection..."
                );

                ZCObject datastore =
                    ZCObject.getInstance();

                ZCTable rankTable =
                    datastore.getTable("Rank");

                if (rankTable == null) {

                    throw new Exception(
                        "Unable to access Rank table"
                    );
                }

                response.setStatus(
                    HttpServletResponse.SC_OK
                );

                response.getWriter().write(
                    "{"
                    + "\"success\":true,"
                    + "\"message\":"
                    + "\"Catalyst Data Store connection successful. "
                    + "Rank table is accessible.\""
                    + "}"
                );

                return;
            }


            // =====================================================
            // 3. SEED ONLY RANK MASTER DATA
            // =====================================================
            if ("/admin/seed/ranks".equals(path)
                    && "POST".equalsIgnoreCase(method)) {

                LOGGER.info(
                    "Starting Rank master data seeding..."
                );

                int[] result = seedTable(
                    "Rank",
                    "RankName",
                    new String[]{
                        "RankName",
                        "RankCode",
                        "Description"
                    },
                    getRankData()
                );

                response.setStatus(
                    HttpServletResponse.SC_OK
                );

                response.getWriter().write(
                    "{"
                    + "\"success\":true,"
                    + "\"data\":{"
                    + "\"table\":\"Rank\","
                    + "\"inserted\":" + result[0] + ","
                    + "\"skipped\":" + result[1]
                    + "},"
                    + "\"message\":"
                    + "\"Rank master data seed completed\""
                    + "}"
                );

                return;
            }


            // =====================================================
            // 4. SEED ALL INDEPENDENT MASTER TABLES
            // =====================================================
            if ("/admin/seed/masters".equals(path)
                    && "POST".equalsIgnoreCase(method)) {

                LOGGER.info(
                    "Starting all independent master data seeding..."
                );


                // -------------------------------------------------
                // RANK
                // -------------------------------------------------
                int[] rankResult = seedTable(
                    "Rank",
                    "RankName",
                    new String[]{
                        "RankName",
                        "RankCode",
                        "Description"
                    },
                    getRankData()
                );


                // -------------------------------------------------
                // DESIGNATION
                // -------------------------------------------------
                String[][] designations = {
                    {
                        "Commissioner of Police",
                        "Heads a police commissionerate"
                    },
                    {
                        "Deputy Commissioner of Police",
                        "Supervises a division or functional wing"
                    },
                    {
                        "Assistant Commissioner of Police",
                        "Supervises a sub-division or functional unit"
                    },
                    {
                        "Station House Officer",
                        "Officer in charge of a police station"
                    },
                    {
                        "Investigating Officer",
                        "Officer responsible for investigation"
                    },
                    {
                        "Crime Branch Officer",
                        "Officer assigned to crime branch duties"
                    },
                    {
                        "Cyber Crime Officer",
                        "Officer assigned to cybercrime duties"
                    },
                    {
                        "Traffic Officer",
                        "Officer assigned to traffic duties"
                    },
                    {
                        "Duty Officer",
                        "Officer handling station duty operations"
                    }
                };

                int[] designationResult = seedTable(
                    "Designation",
                    "DesignationName",
                    new String[]{
                        "DesignationName",
                        "Description"
                    },
                    designations
                );


                // -------------------------------------------------
                // CASE CATEGORY
                // -------------------------------------------------
                String[][] caseCategories = {
                    {
                        "Cognizable",
                        "Cognizable case classification"
                    },
                    {
                        "Non-Cognizable",
                        "Non-cognizable case classification"
                    },
                    {
                        "Special and Local Laws",
                        "Cases under special or local legislation"
                    }
                };

                int[] caseCategoryResult = seedTable(
                    "CaseCategory",
                    "CategoryName",
                    new String[]{
                        "CategoryName",
                        "Description"
                    },
                    caseCategories
                );


                // -------------------------------------------------
                // CASE STATUS MASTER
                // -------------------------------------------------
                String[][] caseStatuses = {
                    {
                        "Registered",
                        "Case has been registered"
                    },
                    {
                        "Under Investigation",
                        "Investigation is in progress"
                    },
                    {
                        "Transferred",
                        "Case transferred to another competent unit"
                    },
                    {
                        "Charge Sheet Filed",
                        "Charge sheet has been filed"
                    },
                    {
                        "Final Report Filed",
                        "Final report has been filed"
                    },
                    {
                        "Closed",
                        "Case workflow is closed"
                    }
                };

                int[] caseStatusResult = seedTable(
                    "CaseStatusMaster",
                    "StatusName",
                    new String[]{
                        "StatusName",
                        "Description"
                    },
                    caseStatuses
                );


                // -------------------------------------------------
                // GRAVITY OF OFFENCE
                // -------------------------------------------------
                String[][] gravityOffences = {
                    {
                        "Petty",
                        "Lower-severity workflow classification"
                    },
                    {
                        "Serious",
                        "Serious offence classification"
                    },
                    {
                        "Heinous",
                        "Highest-severity offence classification"
                    }
                };

                int[] gravityResult = seedTable(
                    "GravityOffence",
                    "GravityName",
                    new String[]{
                        "GravityName",
                        "Description"
                    },
                    gravityOffences
                );


                // -------------------------------------------------
                // IDENTIFICATION TYPE
                // -------------------------------------------------
                String[][] identificationTypes = {
                    {"Aadhaar"},
                    {"Passport"},
                    {"Driving Licence"},
                    {"Voter ID"},
                    {"PAN"},
                    {"Other"}
                };

                int[] identificationResult = seedTable(
                    "IdentificationType",
                    "IDType",
                    new String[]{
                        "IDType"
                    },
                    identificationTypes
                );


                // -------------------------------------------------
                // NATIONALITY MASTER
                // -------------------------------------------------
                String[][] nationalities = {
                    {"Indian"},
                    {"Other"},
                    {"Unknown"}
                };

                int[] nationalityResult = seedTable(
                    "NationalityMaster",
                    "NationalityName",
                    new String[]{
                        "NationalityName"
                    },
                    nationalities
                );


                // -------------------------------------------------
                // RELIGION MASTER
                // -------------------------------------------------
                String[][] religions = {
                    {
                        "Hinduism",
                        "Reference master value"
                    },
                    {
                        "Islam",
                        "Reference master value"
                    },
                    {
                        "Christianity",
                        "Reference master value"
                    },
                    {
                        "Sikhism",
                        "Reference master value"
                    },
                    {
                        "Buddhism",
                        "Reference master value"
                    },
                    {
                        "Jainism",
                        "Reference master value"
                    },
                    {
                        "Other",
                        "Other or self-described"
                    },
                    {
                        "Not Stated",
                        "Not stated"
                    }
                };

                int[] religionResult = seedTable(
                    "ReligionMaster",
                    "ReligionName",
                    new String[]{
                        "ReligionName",
                        "Description"
                    },
                    religions
                );


                // -------------------------------------------------
                // OCCUPATION MASTER
                // -------------------------------------------------
                String[][] occupations = {
                    {
                        "Student",
                        "Student"
                    },
                    {
                        "Salaried Employee",
                        "Salaried employment"
                    },
                    {
                        "Self-Employed",
                        "Self-employed"
                    },
                    {
                        "Business",
                        "Business owner or operator"
                    },
                    {
                        "Government Employee",
                        "Government employment"
                    },
                    {
                        "Homemaker",
                        "Homemaker"
                    },
                    {
                        "Unemployed",
                        "Currently unemployed"
                    },
                    {
                        "Retired",
                        "Retired"
                    },
                    {
                        "Other",
                        "Other occupation"
                    },
                    {
                        "Unknown",
                        "Unknown occupation"
                    }
                };

                int[] occupationResult = seedTable(
                    "OccupationMaster",
                    "OccupationName",
                    new String[]{
                        "OccupationName",
                        "Description"
                    },
                    occupations
                );


                // -------------------------------------------------
                // PROFESSION MASTER
                // -------------------------------------------------
                String[][] professions = {
                    {
                        "Engineer",
                        "Engineering profession"
                    },
                    {
                        "Doctor",
                        "Medical profession"
                    },
                    {
                        "Teacher",
                        "Teaching profession"
                    },
                    {
                        "Lawyer",
                        "Legal profession"
                    },
                    {
                        "Accountant",
                        "Accounting profession"
                    },
                    {
                        "Driver",
                        "Professional driver"
                    },
                    {
                        "Police Personnel",
                        "Police service"
                    },
                    {
                        "Other",
                        "Other profession"
                    },
                    {
                        "Not Applicable",
                        "No specific profession recorded"
                    }
                };

                int[] professionResult = seedTable(
                    "ProfessionMaster",
                    "ProfessionName",
                    new String[]{
                        "ProfessionName",
                        "Description"
                    },
                    professions
                );


                // -------------------------------------------------
                // CASTE MASTER
                // -------------------------------------------------
                String[][] castes = {
                    {
                        "Not Stated",
                        "Not stated or not collected"
                    },
                    {
                        "Other",
                        "Other category where operationally required"
                    }
                };

                int[] casteResult = seedTable(
                    "CasteMaster",
                    "CasteName",
                    new String[]{
                        "CasteName",
                        "Description"
                    },
                    castes
                );


                // -------------------------------------------------
                // CRIME HEAD
                // -------------------------------------------------
                String[][] crimeHeads = {
                    {
                        "Offences Against Person",
                        "Cases primarily involving persons"
                    },
                    {
                        "Offences Against Property",
                        "Cases primarily involving property"
                    },
                    {
                        "Offences Against Women",
                        "Classification for offences involving women"
                    },
                    {
                        "Cyber Crime",
                        "Technology-enabled or cyber offences"
                    },
                    {
                        "Economic Offences",
                        "Financial and economic crime"
                    },
                    {
                        "Public Order",
                        "Public-order related offences"
                    },
                    {
                        "Traffic and Road Safety",
                        "Traffic and road-safety related cases"
                    },
                    {
                        "Other",
                        "Other crime classification"
                    }
                };

                int[] crimeHeadResult = seedTable(
                    "CrimeHead",
                    "CrimeHeadName",
                    new String[]{
                        "CrimeHeadName",
                        "Description"
                    },
                    crimeHeads
                );


                // -------------------------------------------------
                // BUILD SUCCESS RESPONSE
                // -------------------------------------------------
                response.setStatus(
                    HttpServletResponse.SC_OK
                );

                response.getWriter().write(
                    "{"
                    + "\"success\":true,"
                    + "\"summary\":{"

                    + "\"Rank\":{"
                    + "\"inserted\":" + rankResult[0] + ","
                    + "\"skipped\":" + rankResult[1]
                    + "},"

                    + "\"Designation\":{"
                    + "\"inserted\":" + designationResult[0] + ","
                    + "\"skipped\":" + designationResult[1]
                    + "},"

                    + "\"CaseCategory\":{"
                    + "\"inserted\":" + caseCategoryResult[0] + ","
                    + "\"skipped\":" + caseCategoryResult[1]
                    + "},"

                    + "\"CaseStatusMaster\":{"
                    + "\"inserted\":" + caseStatusResult[0] + ","
                    + "\"skipped\":" + caseStatusResult[1]
                    + "},"

                    + "\"GravityOffence\":{"
                    + "\"inserted\":" + gravityResult[0] + ","
                    + "\"skipped\":" + gravityResult[1]
                    + "},"

                    + "\"IdentificationType\":{"
                    + "\"inserted\":" + identificationResult[0] + ","
                    + "\"skipped\":" + identificationResult[1]
                    + "},"

                    + "\"NationalityMaster\":{"
                    + "\"inserted\":" + nationalityResult[0] + ","
                    + "\"skipped\":" + nationalityResult[1]
                    + "},"

                    + "\"ReligionMaster\":{"
                    + "\"inserted\":" + religionResult[0] + ","
                    + "\"skipped\":" + religionResult[1]
                    + "},"

                    + "\"OccupationMaster\":{"
                    + "\"inserted\":" + occupationResult[0] + ","
                    + "\"skipped\":" + occupationResult[1]
                    + "},"

                    + "\"ProfessionMaster\":{"
                    + "\"inserted\":" + professionResult[0] + ","
                    + "\"skipped\":" + professionResult[1]
                    + "},"

                    + "\"CasteMaster\":{"
                    + "\"inserted\":" + casteResult[0] + ","
                    + "\"skipped\":" + casteResult[1]
                    + "},"

                    + "\"CrimeHead\":{"
                    + "\"inserted\":" + crimeHeadResult[0] + ","
                    + "\"skipped\":" + crimeHeadResult[1]
                    + "}"

                    + "},"
                    + "\"message\":"
                    + "\"Independent master data seeding completed\""
                    + "}"
                );

                return;
            }

            // =====================================================
// DEBUG UNIT TYPE DATA
// =====================================================
if ("/admin/debug/unittypes".equals(path)
        && "GET".equalsIgnoreCase(method)) {

    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable unitTypeTable =
        datastore.getTable("UnitType");

    StringBuilder json =
        new StringBuilder();

    json.append(
        "{\"success\":true,\"rows\":["
    );

    boolean first = true;

    for (ZCRowObject row :
            unitTypeTable.getAllRows()) {

        if (!first) {
            json.append(",");
        }

        first = false;

        Object rowId =
            row.get("ROWID");

        Object unitType =
            row.get("UnitType");

        Object description =
            row.get("Description");

        json.append("{");

        json.append("\"ROWID\":\"")
            .append(
                rowId == null
                    ? "NULL"
                    : rowId.toString()
            )
            .append("\",");

        json.append("\"UnitType\":\"")
            .append(
                unitType == null
                    ? "NULL"
                    : unitType.toString()
                        .replace("\"", "\\\"")
            )
            .append("\",");

        json.append("\"Description\":\"")
            .append(
                description == null
                    ? "NULL"
                    : description.toString()
                        .replace("\"", "\\\"")
            )
            .append("\"");

        json.append("}");
    }

    json.append("]}");

    response.setStatus(
        HttpServletResponse.SC_OK
    );

    response.getWriter().write(
        json.toString()
    );

    return;
}
            // =====================================================
// REPAIR UNIT TYPE MASTER DATA
//
// Existing UnitType rows were created when the name
// column was UnitTypeName.
//
// The schema was later changed to:
// UnitType (varchar)
//
// This endpoint populates the new UnitType column
// using the existing ROWIDs.
// =====================================================
if ("/admin/repair/unittypes".equals(path)
        && "POST".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Starting UnitType master data repair..."
    );

    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable unitTypeTable =
        datastore.getTable("UnitType");

    if (unitTypeTable == null) {
        throw new Exception(
            "Unable to access UnitType table"
        );
    }


    // ROWID -> Correct UnitType value
    String[][] unitTypeRepairs = {

        {
            "53330000000036302",
            "Police Station"
        },

        {
            "53330000000036303",
            "Traffic Police Station"
        },

        {
            "53330000000036304",
            "Women Police Station"
        },

        {
            "53330000000036305",
            "Cyber Crime Police Station"
        },

        {
            "53330000000036306",
            "Crime Branch"
        },

        {
            "53330000000036307",
            "Special Branch"
        },

        {
            "53330000000036308",
            "Investigation Unit"
        },

        {
            "53330000000036309",
            "Special Task Force"
        },

        {
            "53330000000036310",
            "Control Room"
        },

        {
            "53330000000036311",
            "Commissionarate"
        },

        {
            "53330000000036312",
            "Outpost"
        }
    };


    int updated = 0;
    int skipped = 0;


    for (String[] repair : unitTypeRepairs) {

        Long rowId =
            Long.valueOf(repair[0]);

        String unitTypeName =
            repair[1];


        // Get the existing row directly by ROWID
        ZCRowObject row =
            unitTypeTable.getRow(rowId);

        if (row == null) {

            throw new Exception(
                "UnitType row not found for ROWID: "
                + rowId
            );
        }


        Object currentValue =
            row.get("UnitType");


        // If already populated correctly, skip it
        if (currentValue != null
                && unitTypeName.equalsIgnoreCase(
                    currentValue.toString().trim()
                )) {

            LOGGER.log(
                Level.INFO,
                "Skipping already repaired UnitType: {0}",
                unitTypeName
            );

            skipped++;
            continue;
        }


        // Populate the new UnitType column
        row.set(
            "UnitType",
            unitTypeName
        );


        // Update the existing row
        java.util.List<ZCRowObject> rowsToUpdate =
            new java.util.ArrayList<ZCRowObject>();

        rowsToUpdate.add(row);

        unitTypeTable.updateRows(
            rowsToUpdate
        );


        LOGGER.log(
            Level.INFO,
            "Repaired UnitType: {0}",
            unitTypeName
        );

        updated++;
    }


    response.setStatus(
        HttpServletResponse.SC_OK
    );

    response.getWriter().write(
        "{"
        + "\"success\":true,"
        + "\"data\":{"
        + "\"table\":\"UnitType\","
        + "\"updated\":" + updated + ","
        + "\"skipped\":" + skipped
        + "},"
        + "\"message\":"
        + "\"UnitType master data repair completed\""
        + "}"
    );

    return;
}

            // =====================================================
            // 5. SEED SYNTHETIC PROTOTYPE UNITS
            // =====================================================
            if ("/admin/seed/units".equals(path)
                    && "POST".equalsIgnoreCase(method)) {

                LOGGER.info(
                    "Starting synthetic Unit data seeding..."
                );

                int inserted = 0;
                int skipped = 0;


                // -------------------------------------------------
                // BENGALURU URBAN UNITS
                // -------------------------------------------------
                int[] result;

                result = seedUnit(
                    "BLR-PS-001",
                    "Central Bengaluru Police Station",
                    "Bengaluru Urban",
                    "Police Station",
                    "Demo jurisdiction - Central Bengaluru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "BLR-PS-002",
                    "East Bengaluru Police Station",
                    "Bengaluru Urban",
                    "Police Station",
                    "Demo jurisdiction - East Bengaluru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "BLR-PS-003",
                    "West Bengaluru Police Station",
                    "Bengaluru Urban",
                    "Police Station",
                    "Demo jurisdiction - West Bengaluru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "BLR-TRF-001",
                    "Bengaluru Central Traffic Police Station",
                    "Bengaluru Urban",
                    "Traffic Police Station",
                    "Demo traffic enforcement unit - Bengaluru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "BLR-WPS-001",
                    "Bengaluru Women Police Station",
                    "Bengaluru Urban",
                    "Women Police Station",
                    "Demo specialized women police unit - Bengaluru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "BLR-CYB-001",
                    "Bengaluru Cyber Crime Police Station",
                    "Bengaluru Urban",
                    "Cyber Crime Police Station",
                    "Demo cybercrime unit - Bengaluru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "BLR-CB-001",
                    "Bengaluru Crime Branch",
                    "Bengaluru Urban",
                    "Crime Branch",
                    "Demo crime branch unit - Bengaluru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "BLR-CR-001",
                    "Bengaluru Control Room",
                    "Bengaluru Urban",
                    "Control Room",
                    "Demo police control room - Bengaluru"
                );
                inserted += result[0];
                skipped += result[1];


                // -------------------------------------------------
                // MYSURU UNITS
                // -------------------------------------------------
                result = seedUnit(
                    "MYS-PS-001",
                    "Central Mysuru Police Station",
                    "Mysuru",
                    "Police Station",
                    "Demo jurisdiction - Central Mysuru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "MYS-PS-002",
                    "North Mysuru Police Station",
                    "Mysuru",
                    "Police Station",
                    "Demo jurisdiction - North Mysuru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "MYS-TRF-001",
                    "Mysuru Traffic Police Station",
                    "Mysuru",
                    "Traffic Police Station",
                    "Demo traffic enforcement unit - Mysuru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "MYS-CYB-001",
                    "Mysuru Cyber Crime Police Station",
                    "Mysuru",
                    "Cyber Crime Police Station",
                    "Demo cybercrime unit - Mysuru"
                );
                inserted += result[0];
                skipped += result[1];


                // -------------------------------------------------
                // MANGALURU / DAKSHINA KANNADA UNITS
                // -------------------------------------------------
                result = seedUnit(
                    "MLR-PS-001",
                    "Central Mangaluru Police Station",
                    "Mangaluru / Dakshina Kannada",
                    "Police Station",
                    "Demo jurisdiction - Central Mangaluru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "MLR-TRF-001",
                    "Mangaluru Traffic Police Station",
                    "Mangaluru / Dakshina Kannada",
                    "Traffic Police Station",
                    "Demo traffic enforcement unit - Mangaluru"
                );
                inserted += result[0];
                skipped += result[1];


                result = seedUnit(
                    "MLR-WPS-001",
                    "Mangaluru Women Police Station",
                    "Mangaluru / Dakshina Kannada",
                    "Women Police Station",
                    "Demo specialized women police unit - Mangaluru"
                );
                inserted += result[0];
                skipped += result[1];


                response.setStatus(
                    HttpServletResponse.SC_OK
                );

                response.getWriter().write(
                    "{"
                    + "\"success\":true,"
                    + "\"data\":{"
                    + "\"table\":\"Unit\","
                    + "\"inserted\":" + inserted + ","
                    + "\"skipped\":" + skipped
                    + "},"
                    + "\"message\":"
                    + "\"Synthetic prototype Unit data seeding completed\""
                    + "}"
                );

                return;
            }

            // =====================================================
// 6. SEED CRIME REFERENCE DATA
//
// Seeds:
// 1. Act
// 2. CrimeSubHead with CrimeHead foreign key
// 3. Section with Act foreign key
//
// Duplicate safe.
// =====================================================
if ("/admin/seed/crime-reference-data".equals(path)
        && "POST".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Starting crime reference data seeding..."
    );


    // =================================================
    // 1. SEED ACT MASTER DATA
    // =================================================
    String[][] acts = {

        {
            "BNS-2023",
            "Bharatiya Nyaya Sanhita, 2023",
            "Primary substantive criminal law reference"
        },

        {
            "BNSS-2023",
            "Bharatiya Nagarik Suraksha Sanhita, 2023",
            "Criminal procedure law reference"
        },

        {
            "IT-ACT-2000",
            "Information Technology Act, 2000",
            "Legal framework for electronic records and cyber offences"
        },

        {
            "PWDVA-2005",
            "Protection of Women from Domestic Violence Act, 2005",
            "Legal framework relating to protection from domestic violence"
        },

        {
            "MV-ACT-1988",
            "Motor Vehicles Act, 1988",
            "Legal framework for motor vehicles and road transport"
        }
    };


    int[] actResult = seedTable(
        "Act",
        "ActName",
        new String[]{
            "ActCode",
            "ActName",
            "Description"
        },
        acts
    );


    // =================================================
    // 2. SEED CRIME SUB HEADS
    // =================================================
    String[][] crimeSubHeads = {

        {
            "Murder",
            "Offences Against Person",
            "Cases involving unlawful killing"
        },

        {
            "Attempt to Murder",
            "Offences Against Person",
            "Cases involving alleged attempt to cause death"
        },

        {
            "Hurt and Assault",
            "Offences Against Person",
            "Cases involving bodily hurt or assault"
        },

        {
            "Theft",
            "Offences Against Property",
            "Cases involving theft of property"
        },

        {
            "Robbery",
            "Offences Against Property",
            "Cases involving robbery"
        },

        {
            "Burglary",
            "Offences Against Property",
            "Cases involving unlawful entry and property offences"
        },

        {
            "Motor Vehicle Theft",
            "Offences Against Property",
            "Cases involving theft of motor vehicles"
        },

        {
            "Sexual Offences",
            "Offences Against Women",
            "Cases classified as sexual offences"
        },

        {
            "Domestic Violence",
            "Offences Against Women",
            "Cases involving allegations of domestic violence"
        },

        {
            "Harassment",
            "Offences Against Women",
            "Cases involving harassment"
        },

        {
            "Online Financial Fraud",
            "Cyber Crime",
            "Technology-enabled financial fraud cases"
        },

        {
            "Identity Theft",
            "Cyber Crime",
            "Cases involving misuse of digital identity"
        },

        {
            "Cyber Harassment",
            "Cyber Crime",
            "Cases involving harassment through digital channels"
        },

        {
            "Unauthorized Access",
            "Cyber Crime",
            "Cases involving unauthorized access to computer systems"
        },

        {
            "Cheating and Fraud",
            "Economic Offences",
            "Cases involving cheating or fraudulent conduct"
        },

        {
            "Forgery",
            "Economic Offences",
            "Cases involving forged documents or records"
        },

        {
            "Criminal Breach of Trust",
            "Economic Offences",
            "Cases involving alleged breach of entrusted property"
        },

        {
            "Unlawful Assembly",
            "Public Order",
            "Cases classified as unlawful assembly"
        },

        {
            "Rioting",
            "Public Order",
            "Cases involving rioting or group violence"
        },

        {
            "Public Disturbance",
            "Public Order",
            "Cases involving disturbance of public order"
        },

        {
            "Dangerous Driving",
            "Traffic and Road Safety",
            "Cases involving dangerous driving"
        },

        {
            "Drunk Driving",
            "Traffic and Road Safety",
            "Cases involving driving under the influence"
        },

        {
            "Fatal Road Accident",
            "Traffic and Road Safety",
            "Road accident cases involving fatalities"
        },

        {
            "Other Offence",
            "Other",
            "Other or uncategorized offence"
        }
    };


    int crimeSubHeadInserted = 0;
    int crimeSubHeadSkipped = 0;


    for (String[] item : crimeSubHeads) {

        int[] result = seedCrimeSubHead(
            item[0],
            item[1],
            item[2]
        );

        crimeSubHeadInserted += result[0];
        crimeSubHeadSkipped += result[1];
    }


    // =================================================
    // 3. SEED PROTOTYPE SECTION REFERENCE DATA
    //
    // These are DEMO references for the prototype,
    // not an exhaustive legal section database.
    // =================================================
    String[][] sections = {

        {
            "BNS-DEMO-001",
            "Offences Against Person",
            "Bharatiya Nyaya Sanhita, 2023",
            "Prototype reference for offences against person"
        },

        {
            "BNS-DEMO-002",
            "Offences Against Property",
            "Bharatiya Nyaya Sanhita, 2023",
            "Prototype reference for offences against property"
        },

        {
            "BNS-DEMO-003",
            "Economic Offences",
            "Bharatiya Nyaya Sanhita, 2023",
            "Prototype reference for economic offences"
        },

        {
            "BNSS-DEMO-001",
            "Criminal Procedure Reference",
            "Bharatiya Nagarik Suraksha Sanhita, 2023",
            "Prototype criminal procedure reference"
        },

        {
            "IT-DEMO-001",
            "Cyber Crime Reference",
            "Information Technology Act, 2000",
            "Prototype reference for cybercrime"
        },

        {
            "IT-DEMO-002",
            "Digital Identity Reference",
            "Information Technology Act, 2000",
            "Prototype reference for digital identity offences"
        },

        {
            "PWDVA-DEMO-001",
            "Domestic Violence Reference",
            "Protection of Women from Domestic Violence Act, 2005",
            "Prototype reference for domestic violence"
        },

        {
            "MV-DEMO-001",
            "Dangerous Driving Reference",
            "Motor Vehicles Act, 1988",
            "Prototype road safety reference"
        },

        {
            "MV-DEMO-002",
            "Driving Under Influence Reference",
            "Motor Vehicles Act, 1988",
            "Prototype driving under influence reference"
        }
    };


    int sectionInserted = 0;
    int sectionSkipped = 0;


    for (String[] item : sections) {

        int[] result = seedSection(
            item[0],
            item[1],
            item[2],
            item[3]
        );

        sectionInserted += result[0];
        sectionSkipped += result[1];
    }


    // =================================================
    // SUCCESS RESPONSE
    // =================================================
    response.setStatus(
        HttpServletResponse.SC_OK
    );

    response.getWriter().write(
        "{"
        + "\"success\":true,"
        + "\"summary\":{"

        + "\"Act\":{"
        + "\"inserted\":" + actResult[0] + ","
        + "\"skipped\":" + actResult[1]
        + "},"

        + "\"CrimeSubHead\":{"
        + "\"inserted\":" + crimeSubHeadInserted + ","
        + "\"skipped\":" + crimeSubHeadSkipped
        + "},"

        + "\"Section\":{"
        + "\"inserted\":" + sectionInserted + ","
        + "\"skipped\":" + sectionSkipped
        + "}"

        + "},"
        + "\"message\":"
        + "\"Crime reference data seeding completed\""
        + "}"
    );

    return;
}

            // =====================================================
// SEED ORGANIZATION DATA
//
// Seeds:
// 1. Court
// 2. Employee
//
// Duplicate safe.
// =====================================================
if ("/admin/seed/organization-data".equals(path)
        && "POST".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Starting organization data seeding..."
    );


    // =================================================
    // 1. SEED COURTS
    // =================================================
    String[][] courts = {

        {
            "Bengaluru Urban District Court - Prototype",
            "Bengaluru Urban",
            "District Court",
            "Prototype court location - Bengaluru Urban"
        },

        {
            "Bengaluru Metropolitan Magistrate Court - Prototype",
            "Bengaluru Urban",
            "Magistrate Court",
            "Prototype court location - Bengaluru Urban"
        },

        {
            "Mysuru District Court - Prototype",
            "Mysuru",
            "District Court",
            "Prototype court location - Mysuru"
        },

        {
            "Mysuru Magistrate Court - Prototype",
            "Mysuru",
            "Magistrate Court",
            "Prototype court location - Mysuru"
        },

        {
            "Mangaluru District Court - Prototype",
            "Mangaluru / Dakshina Kannada",
            "District Court",
            "Prototype court location - Mangaluru"
        },

        {
            "Mangaluru Magistrate Court - Prototype",
            "Mangaluru / Dakshina Kannada",
            "Magistrate Court",
            "Prototype court location - Mangaluru"
        }
    };


    int courtInserted = 0;
    int courtSkipped = 0;


    for (String[] court : courts) {

        int[] result = seedCourt(
            court[0],
            court[1],
            court[2],
            court[3]
        );

        courtInserted += result[0];
        courtSkipped += result[1];
    }


    // =================================================
    // 2. SEED SYNTHETIC EMPLOYEES
    //
    // EmployeeCode
    // EmployeeName
    // RankName
    // DesignationName
    // UnitCode
    // =================================================
    String[][] employees = {

        // ---------------------------------------------
        // BENGALURU POLICE STATIONS
        // ---------------------------------------------
        {
            "EMP-BLR-001",
            "Prototype Officer BLR 001",
            "Inspector",
            "Station House Officer",
            "BLR-PS-001"
        },

        {
            "EMP-BLR-002",
            "Prototype Officer BLR 002",
            "Sub-Inspector",
            "Investigating Officer",
            "BLR-PS-001"
        },

        {
            "EMP-BLR-003",
            "Prototype Officer BLR 003",
            "Assistant Sub-Inspector",
            "Duty Officer",
            "BLR-PS-001"
        },

        {
            "EMP-BLR-004",
            "Prototype Officer BLR 004",
            "Inspector",
            "Station House Officer",
            "BLR-PS-002"
        },

        {
            "EMP-BLR-005",
            "Prototype Officer BLR 005",
            "Sub-Inspector",
            "Investigating Officer",
            "BLR-PS-002"
        },

        {
            "EMP-BLR-006",
            "Prototype Officer BLR 006",
            "Head Constable",
            "Duty Officer",
            "BLR-PS-002"
        },

        {
            "EMP-BLR-007",
            "Prototype Officer BLR 007",
            "Inspector",
            "Station House Officer",
            "BLR-PS-003"
        },

        {
            "EMP-BLR-008",
            "Prototype Officer BLR 008",
            "Sub-Inspector",
            "Investigating Officer",
            "BLR-PS-003"
        },

        {
            "EMP-BLR-009",
            "Prototype Officer BLR 009",
            "Police Constable",
            "Duty Officer",
            "BLR-PS-003"
        },


        // ---------------------------------------------
        // BENGALURU SPECIALIZED UNITS
        // ---------------------------------------------
        {
            "EMP-BLR-010",
            "Prototype Officer BLR 010",
            "Inspector",
            "Traffic Officer",
            "BLR-TRF-001"
        },

        {
            "EMP-BLR-011",
            "Prototype Officer BLR 011",
            "Sub-Inspector",
            "Traffic Officer",
            "BLR-TRF-001"
        },

        {
            "EMP-BLR-012",
            "Prototype Officer BLR 012",
            "Inspector",
            "Investigating Officer",
            "BLR-WPS-001"
        },

        {
            "EMP-BLR-013",
            "Prototype Officer BLR 013",
            "Sub-Inspector",
            "Investigating Officer",
            "BLR-WPS-001"
        },

        {
            "EMP-BLR-014",
            "Prototype Officer BLR 014",
            "Inspector",
            "Cyber Crime Officer",
            "BLR-CYB-001"
        },

        {
            "EMP-BLR-015",
            "Prototype Officer BLR 015",
            "Sub-Inspector",
            "Cyber Crime Officer",
            "BLR-CYB-001"
        },

        {
            "EMP-BLR-016",
            "Prototype Officer BLR 016",
            "Assistant Sub-Inspector",
            "Cyber Crime Officer",
            "BLR-CYB-001"
        },

        {
            "EMP-BLR-017",
            "Prototype Officer BLR 017",
            "Inspector",
            "Crime Branch Officer",
            "BLR-CB-001"
        },

        {
            "EMP-BLR-018",
            "Prototype Officer BLR 018",
            "Sub-Inspector",
            "Crime Branch Officer",
            "BLR-CB-001"
        },

        {
            "EMP-BLR-019",
            "Prototype Officer BLR 019",
            "Assistant Sub-Inspector",
            "Crime Branch Officer",
            "BLR-CB-001"
        },

        {
            "EMP-BLR-020",
            "Prototype Officer BLR 020",
            "Assistant Sub-Inspector",
            "Duty Officer",
            "BLR-CR-001"
        },


        // ---------------------------------------------
        // MYSURU
        // ---------------------------------------------
        {
            "EMP-MYS-001",
            "Prototype Officer MYS 001",
            "Inspector",
            "Station House Officer",
            "MYS-PS-001"
        },

        {
            "EMP-MYS-002",
            "Prototype Officer MYS 002",
            "Sub-Inspector",
            "Investigating Officer",
            "MYS-PS-001"
        },

        {
            "EMP-MYS-003",
            "Prototype Officer MYS 003",
            "Inspector",
            "Station House Officer",
            "MYS-PS-002"
        },

        {
            "EMP-MYS-004",
            "Prototype Officer MYS 004",
            "Sub-Inspector",
            "Investigating Officer",
            "MYS-PS-002"
        },

        {
            "EMP-MYS-005",
            "Prototype Officer MYS 005",
            "Sub-Inspector",
            "Traffic Officer",
            "MYS-TRF-001"
        },

        {
            "EMP-MYS-006",
            "Prototype Officer MYS 006",
            "Inspector",
            "Cyber Crime Officer",
            "MYS-CYB-001"
        },

        {
            "EMP-MYS-007",
            "Prototype Officer MYS 007",
            "Sub-Inspector",
            "Cyber Crime Officer",
            "MYS-CYB-001"
        },


        // ---------------------------------------------
        // MANGALURU
        // ---------------------------------------------
        {
            "EMP-MLR-001",
            "Prototype Officer MLR 001",
            "Inspector",
            "Station House Officer",
            "MLR-PS-001"
        },

        {
            "EMP-MLR-002",
            "Prototype Officer MLR 002",
            "Sub-Inspector",
            "Traffic Officer",
            "MLR-TRF-001"
        },

        {
            "EMP-MLR-003",
            "Prototype Officer MLR 003",
            "Sub-Inspector",
            "Investigating Officer",
            "MLR-WPS-001"
        }
    };


    int employeeInserted = 0;
    int employeeSkipped = 0;


    for (String[] employee : employees) {

        int[] result = seedEmployee(
            employee[0],
            employee[1],
            employee[2],
            employee[3],
            employee[4]
        );

        employeeInserted += result[0];
        employeeSkipped += result[1];
    }


    // =================================================
    // SUCCESS RESPONSE
    // =================================================
    response.setStatus(
        HttpServletResponse.SC_OK
    );

    response.getWriter().write(
        "{"
        + "\"success\":true,"
        + "\"summary\":{"

        + "\"Court\":{"
        + "\"inserted\":" + courtInserted + ","
        + "\"skipped\":" + courtSkipped
        + "},"

        + "\"Employee\":{"
        + "\"inserted\":" + employeeInserted + ","
        + "\"skipped\":" + employeeSkipped
        + "}"

        + "},"
        + "\"message\":"
        + "\"Organization data seeding completed\""
        + "}"
    );

    return;
}

            // =====================================================
            // SEED 100 SYNTHETIC PROTOTYPE CASES
            // =====================================================
            if ("/admin/seed/cases".equals(path)
                    && "POST".equalsIgnoreCase(method)) {

                LOGGER.info("Starting synthetic CaseMaster data seeding...");

                String[][] patterns = {
                    {"Bengaluru Urban","BLR-PS-001","EMP-BLR-002","Cognizable","Serious","Offences Against Property","Theft","Under Investigation"},
                    {"Bengaluru Urban","BLR-PS-002","EMP-BLR-005","Cognizable","Serious","Cyber Crime","Online Financial Fraud","Under Investigation"},
                    {"Bengaluru Urban","BLR-PS-003","EMP-BLR-008","Cognizable","Petty","Public Order","Public Disturbance","Registered"},
                    {"Bengaluru Urban","BLR-WPS-001","EMP-BLR-013","Cognizable","Serious","Offences Against Women","Domestic Violence","Charge Sheet Filed"},
                    {"Bengaluru Urban","BLR-CYB-001","EMP-BLR-015","Cognizable","Serious","Cyber Crime","Identity Theft","Under Investigation"},
                    {"Bengaluru Urban","BLR-CB-001","EMP-BLR-018","Cognizable","Heinous","Economic Offences","Cheating and Fraud","Charge Sheet Filed"},
                    {"Bengaluru Urban","BLR-TRF-001","EMP-BLR-011","Special and Local Laws","Petty","Traffic and Road Safety","Dangerous Driving","Closed"},
                    {"Mysuru","MYS-PS-001","EMP-MYS-002","Cognizable","Serious","Offences Against Property","Burglary","Under Investigation"},
                    {"Mysuru","MYS-PS-002","EMP-MYS-004","Cognizable","Serious","Offences Against Person","Hurt and Assault","Final Report Filed"},
                    {"Mysuru","MYS-CYB-001","EMP-MYS-007","Cognizable","Serious","Cyber Crime","Unauthorized Access","Registered"},
                    {"Mysuru","MYS-TRF-001","EMP-MYS-005","Special and Local Laws","Petty","Traffic and Road Safety","Drunk Driving","Closed"},
                    {"Mangaluru / Dakshina Kannada","MLR-PS-001","EMP-MLR-001","Cognizable","Serious","Offences Against Property","Motor Vehicle Theft","Transferred"},
                    {"Mangaluru / Dakshina Kannada","MLR-WPS-001","EMP-MLR-003","Cognizable","Serious","Offences Against Women","Harassment","Under Investigation"},
                    {"Mangaluru / Dakshina Kannada","MLR-TRF-001","EMP-MLR-002","Special and Local Laws","Heinous","Traffic and Road Safety","Fatal Road Accident","Charge Sheet Filed"}
                };

                int inserted = 0;
                int skipped = 0;

                for (int i = 1; i <= 100; i++) {
                    String[] p = patterns[(i - 1) % patterns.length];
                    String seq = String.format("%04d", i);

                    int[] result = seedCase(
                        "CR-2026-" + seq,
                        "FIR-2026-" + seq,
                        p[0], p[1], p[2], p[3],
                        p[4], p[5], p[6], p[7], i
                    );

                    inserted += result[0];
                    skipped += result[1];
                }

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(
                    "{"
                    + "\"success\":true,"
                    + "\"data\":{"
                    + "\"table\":\"CaseMaster\","
                    + "\"inserted\":" + inserted + ","
                    + "\"skipped\":" + skipped
                    + "},"
                    + "\"message\":\"Synthetic prototype CaseMaster data seeding completed\""
                    + "}"
                );
                return;
            }


            // =====================================================
// DASHBOARD KPI API - REAL DATA
// =====================================================
if ("/dashboard/kpis".equals(path)
        && "GET".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Calculating dashboard KPIs from Catalyst Data Store..."
    );

    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable caseTable =
        datastore.getTable("CaseMaster");

    ZCTable statusTable =
        datastore.getTable("CaseStatusMaster");

    ZCTable unitTable =
        datastore.getTable("Unit");


    if (caseTable == null) {
        throw new Exception(
            "Unable to access CaseMaster table"
        );
    }

    if (statusTable == null) {
        throw new Exception(
            "Unable to access CaseStatusMaster table"
        );
    }

    if (unitTable == null) {
        throw new Exception(
            "Unable to access Unit table"
        );
    }


    // =================================================
    // RESOLVE STATUS ROWIDS
    // =================================================
    Long closedStatusRowId =
        findRowId(
            "CaseStatusMaster",
            "StatusName",
            "Closed"
        );

    Long chargeSheetStatusRowId =
        findRowId(
            "CaseStatusMaster",
            "StatusName",
            "Charge Sheet Filed"
        );


    // =================================================
    // CALCULATE CASE KPIs
    // =================================================
    int totalCrimes = 0;
    int openCases = 0;
    int closedCases = 0;
    int chargeSheets = 0;


    for (ZCRowObject caseRow :
            caseTable.getAllRows()) {

        totalCrimes++;


        Object statusValue =
            caseRow.get("CaseStatus");


        if (statusValue == null) {

            // A case without a status is treated
            // as open for dashboard purposes.
            openCases++;

            continue;
        }


        String statusRowId =
            statusValue.toString().trim();


        if (closedStatusRowId
                .toString()
                .equals(statusRowId)) {

            closedCases++;

        } else {

            // All non-closed cases are currently
            // considered open.
            openCases++;
        }


        if (chargeSheetStatusRowId
                .toString()
                .equals(statusRowId)) {

            chargeSheets++;
        }
    }


    // =================================================
    // CALCULATE ACTIVE POLICE UNITS
    // =================================================
    int activePoliceStations = 0;


    for (ZCRowObject unitRow :
            unitTable.getAllRows()) {

        Object isActiveValue =
            unitRow.get("IsActive");


        if (isActiveValue == null) {
            continue;
        }


        String isActive =
            isActiveValue
                .toString()
                .trim();


        if ("true".equalsIgnoreCase(isActive)
                || "1".equals(isActive)) {

            activePoliceStations++;
        }
    }


    // =================================================
    // BUILD JSON RESPONSE
    // =================================================
    String jsonResponse =
        "{"
        + "\"success\":true,"
        + "\"data\":{"

        + "\"totalCrimes\":"
        + totalCrimes
        + ","

        + "\"openCases\":"
        + openCases
        + ","

        + "\"closedCases\":"
        + closedCases
        + ","

        + "\"chargeSheets\":"
        + chargeSheets
        + ","

        + "\"activePoliceStations\":"
        + activePoliceStations

        + "},"
        + "\"message\":"
        + "\"Dashboard KPIs retrieved successfully\""
        + "}";


    response.setStatus(
        HttpServletResponse.SC_OK
    );

    response.getWriter().write(
        jsonResponse
    );

    return;
}

            // =====================================================
// DASHBOARD - CASES BY STATUS
//
// Returns chart-ready case counts grouped by status.
// Example:
// [
//   {"status":"Registered","count":10},
//   {"status":"Under Investigation","count":20}
// ]
// =====================================================
if ("/dashboard/cases-by-status".equals(path)
        && "GET".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Calculating cases by status..."
    );


    // =================================================
    // ACCESS TABLES
    // =================================================
    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable caseTable =
        datastore.getTable("CaseMaster");

    ZCTable statusTable =
        datastore.getTable("CaseStatusMaster");


    if (caseTable == null) {
        throw new Exception(
            "Unable to access CaseMaster table"
        );
    }

    if (statusTable == null) {
        throw new Exception(
            "Unable to access CaseStatusMaster table"
        );
    }


    // =================================================
    // CREATE STATUS ROWID -> STATUS NAME MAP
    // =================================================
    java.util.Map<String, String> statusNameMap =
        new java.util.LinkedHashMap<String, String>();


    // Also initialize every status with count 0.
    // This ensures statuses with no cases can still
    // appear in the dashboard response.
    java.util.Map<String, Integer> statusCountMap =
        new java.util.LinkedHashMap<String, Integer>();


    for (ZCRowObject statusRow :
            statusTable.getAllRows()) {

        Object rowId =
            statusRow.get("ROWID");

        Object statusName =
            statusRow.get("StatusName");


        if (rowId == null
                || statusName == null) {

            continue;
        }


        String rowIdValue =
            rowId.toString().trim();

        String statusNameValue =
            statusName.toString().trim();


        statusNameMap.put(
            rowIdValue,
            statusNameValue
        );


        statusCountMap.put(
            statusNameValue,
            0
        );
    }


    // =================================================
    // COUNT CASES BY STATUS
    // =================================================
    int unknownStatusCount = 0;


    for (ZCRowObject caseRow :
            caseTable.getAllRows()) {

        Object caseStatus =
            caseRow.get("CaseStatus");


        if (caseStatus == null) {

            unknownStatusCount++;

            continue;
        }


        String statusRowId =
            caseStatus
                .toString()
                .trim();


        String statusName =
            statusNameMap.get(
                statusRowId
            );


        if (statusName == null) {

            unknownStatusCount++;

            continue;
        }


        Integer currentCount =
            statusCountMap.get(
                statusName
            );


        if (currentCount == null) {
            currentCount = 0;
        }


        statusCountMap.put(
            statusName,
            currentCount + 1
        );
    }


    // =================================================
    // BUILD JSON RESPONSE
    // =================================================
    StringBuilder json =
        new StringBuilder();


    json.append(
        "{"
        + "\"success\":true,"
        + "\"data\":["
    );


    boolean first = true;


    for (java.util.Map.Entry<String, Integer> entry :
            statusCountMap.entrySet()) {


        if (!first) {
            json.append(",");
        }


        first = false;


        String safeStatusName =
            entry.getKey()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");


        json.append("{");

        json.append(
            "\"status\":\""
        );

        json.append(
            safeStatusName
        );

        json.append(
            "\","
        );

        json.append(
            "\"count\":"
        );

        json.append(
            entry.getValue()
        );

        json.append(
            "}"
        );
    }


    // Add unknown/unmapped statuses only if they exist.
    if (unknownStatusCount > 0) {

        if (!first) {
            json.append(",");
        }


        json.append(
            "{"
            + "\"status\":\"Unknown\","
            + "\"count\":"
            + unknownStatusCount
            + "}"
        );
    }


    json.append(
        "],"
        + "\"message\":"
        + "\"Cases by status retrieved successfully\""
        + "}"
    );


    response.setStatus(
        HttpServletResponse.SC_OK
    );


    response.getWriter().write(
        json.toString()
    );


    return;
}

            // =====================================================
// DASHBOARD - CASES BY DISTRICT
//
// Returns chart-ready case counts grouped by district.
// =====================================================
if ("/dashboard/cases-by-district".equals(path)
        && "GET".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Calculating cases by district..."
    );


    // =================================================
    // ACCESS TABLES
    // =================================================
    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable caseTable =
        datastore.getTable("CaseMaster");

    ZCTable districtTable =
        datastore.getTable("District");


    if (caseTable == null) {
        throw new Exception(
            "Unable to access CaseMaster table"
        );
    }

    if (districtTable == null) {
        throw new Exception(
            "Unable to access District table"
        );
    }


    // =================================================
    // CREATE DISTRICT ROWID -> DISTRICT NAME MAP
    // =================================================
    java.util.Map<String, String> districtNameMap =
        new java.util.LinkedHashMap<String, String>();


    // Initialize every district with count 0
    java.util.Map<String, Integer> districtCountMap =
        new java.util.LinkedHashMap<String, Integer>();


    for (ZCRowObject districtRow :
            districtTable.getAllRows()) {

        Object rowId =
            districtRow.get("ROWID");

        Object districtName =
            districtRow.get("DistrictName");


        if (rowId == null
                || districtName == null) {

            continue;
        }


        String rowIdValue =
            rowId.toString().trim();

        String districtNameValue =
            districtName.toString().trim();


        districtNameMap.put(
            rowIdValue,
            districtNameValue
        );


        districtCountMap.put(
            districtNameValue,
            0
        );
    }


    // =================================================
    // COUNT CASES BY DISTRICT
    // =================================================
    int unknownDistrictCount = 0;


    for (ZCRowObject caseRow :
            caseTable.getAllRows()) {

        Object districtValue =
            caseRow.get("District");


        if (districtValue == null) {

            unknownDistrictCount++;

            continue;
        }


        String districtRowId =
            districtValue
                .toString()
                .trim();


        String districtName =
            districtNameMap.get(
                districtRowId
            );


        if (districtName == null) {

            unknownDistrictCount++;

            continue;
        }


        Integer currentCount =
            districtCountMap.get(
                districtName
            );


        if (currentCount == null) {
            currentCount = 0;
        }


        districtCountMap.put(
            districtName,
            currentCount + 1
        );
    }


    // =================================================
    // BUILD JSON RESPONSE
    // =================================================
    StringBuilder json =
        new StringBuilder();


    json.append(
        "{"
        + "\"success\":true,"
        + "\"data\":["
    );


    boolean first = true;


    for (java.util.Map.Entry<String, Integer> entry :
            districtCountMap.entrySet()) {


        // Skip districts with zero cases
        if (entry.getValue() == 0) {
            continue;
        }


        if (!first) {
            json.append(",");
        }


        first = false;


        String safeDistrictName =
            entry.getKey()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");


        json.append("{");

        json.append(
            "\"district\":\""
        );

        json.append(
            safeDistrictName
        );

        json.append(
            "\","
        );

        json.append(
            "\"count\":"
        );

        json.append(
            entry.getValue()
        );

        json.append(
            "}"
        );
    }


    // Add Unknown only if unmapped cases exist
    if (unknownDistrictCount > 0) {

        if (!first) {
            json.append(",");
        }


        json.append(
            "{"
            + "\"district\":\"Unknown\","
            + "\"count\":"
            + unknownDistrictCount
            + "}"
        );
    }


    json.append(
        "],"
        + "\"message\":"
        + "\"Cases by district retrieved successfully\""
        + "}"
    );


    response.setStatus(
        HttpServletResponse.SC_OK
    );


    response.getWriter().write(
        json.toString()
    );


    return;
}

            // =====================================================
// DASHBOARD - CASES BY CRIME HEAD
//
// Returns chart-ready case counts grouped by
// major crime classification.
// =====================================================
if ("/dashboard/cases-by-crime-head".equals(path)
        && "GET".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Calculating cases by crime head..."
    );


    // =================================================
    // ACCESS TABLES
    // =================================================
    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable caseTable =
        datastore.getTable("CaseMaster");

    ZCTable crimeHeadTable =
        datastore.getTable("CrimeHead");


    if (caseTable == null) {
        throw new Exception(
            "Unable to access CaseMaster table"
        );
    }

    if (crimeHeadTable == null) {
        throw new Exception(
            "Unable to access CrimeHead table"
        );
    }


    // =================================================
    // CREATE CRIME HEAD ROWID -> CRIME HEAD NAME MAP
    // =================================================
    java.util.Map<String, String> crimeHeadNameMap =
        new java.util.LinkedHashMap<String, String>();


    // Initialize every crime head with count 0
    java.util.Map<String, Integer> crimeHeadCountMap =
        new java.util.LinkedHashMap<String, Integer>();


    for (ZCRowObject crimeHeadRow :
            crimeHeadTable.getAllRows()) {

        Object rowId =
            crimeHeadRow.get("ROWID");

        Object crimeHeadName =
            crimeHeadRow.get("CrimeHeadName");


        if (rowId == null
                || crimeHeadName == null) {

            continue;
        }


        String rowIdValue =
            rowId.toString().trim();

        String crimeHeadNameValue =
            crimeHeadName
                .toString()
                .trim();


        crimeHeadNameMap.put(
            rowIdValue,
            crimeHeadNameValue
        );


        crimeHeadCountMap.put(
            crimeHeadNameValue,
            0
        );
    }


    // =================================================
    // COUNT CASES BY CRIME HEAD
    // =================================================
    int unknownCrimeHeadCount = 0;


    for (ZCRowObject caseRow :
            caseTable.getAllRows()) {

        // Current CaseMaster column
        Object crimeHeadValue =
            caseRow.get("CrimeHead");


        // Fallback to legacy column if needed
        if (crimeHeadValue == null) {

            crimeHeadValue =
                caseRow.get("CrimeMajorHead");
        }


        if (crimeHeadValue == null) {

            unknownCrimeHeadCount++;

            continue;
        }


        String crimeHeadRowId =
            crimeHeadValue
                .toString()
                .trim();


        String crimeHeadName =
            crimeHeadNameMap.get(
                crimeHeadRowId
            );


        if (crimeHeadName == null) {

            unknownCrimeHeadCount++;

            continue;
        }


        Integer currentCount =
            crimeHeadCountMap.get(
                crimeHeadName
            );


        if (currentCount == null) {
            currentCount = 0;
        }


        crimeHeadCountMap.put(
            crimeHeadName,
            currentCount + 1
        );
    }


    // =================================================
    // BUILD JSON RESPONSE
    // =================================================
    StringBuilder json =
        new StringBuilder();


    json.append(
        "{"
        + "\"success\":true,"
        + "\"data\":["
    );


    boolean first = true;


    for (java.util.Map.Entry<String, Integer> entry :
            crimeHeadCountMap.entrySet()) {


        // Skip crime heads with zero cases
        if (entry.getValue() == 0) {
            continue;
        }


        if (!first) {
            json.append(",");
        }


        first = false;


        String safeCrimeHeadName =
            entry.getKey()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");


        json.append("{");

        json.append(
            "\"crimeHead\":\""
        );

        json.append(
            safeCrimeHeadName
        );

        json.append(
            "\","
        );

        json.append(
            "\"count\":"
        );

        json.append(
            entry.getValue()
        );

        json.append(
            "}"
        );
    }


    // Add Unknown only if unmapped cases exist
    if (unknownCrimeHeadCount > 0) {

        if (!first) {
            json.append(",");
        }


        json.append(
            "{"
            + "\"crimeHead\":\"Unknown\","
            + "\"count\":"
            + unknownCrimeHeadCount
            + "}"
        );
    }


    json.append(
        "],"
        + "\"message\":"
        + "\"Cases by crime head retrieved successfully\""
        + "}"
    );


    response.setStatus(
        HttpServletResponse.SC_OK
    );


    response.getWriter().write(
        json.toString()
    );


    return;
}

            // =====================================================
// DASHBOARD - CASES BY GRAVITY
//
// Returns chart-ready case counts grouped by
// offence gravity:
// Petty, Serious, Heinous
// =====================================================
if ("/dashboard/cases-by-gravity".equals(path)
        && "GET".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Calculating cases by gravity..."
    );


    // =================================================
    // ACCESS TABLES
    // =================================================
    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable caseTable =
        datastore.getTable("CaseMaster");

    ZCTable gravityTable =
        datastore.getTable("GravityOffence");


    if (caseTable == null) {
        throw new Exception(
            "Unable to access CaseMaster table"
        );
    }

    if (gravityTable == null) {
        throw new Exception(
            "Unable to access GravityOffence table"
        );
    }


    // =================================================
    // CREATE GRAVITY ROWID -> GRAVITY NAME MAP
    // =================================================
    java.util.Map<String, String> gravityNameMap =
        new java.util.LinkedHashMap<String, String>();


    // Initialize every gravity category with count 0
    java.util.Map<String, Integer> gravityCountMap =
        new java.util.LinkedHashMap<String, Integer>();


    for (ZCRowObject gravityRow :
            gravityTable.getAllRows()) {

        Object rowId =
            gravityRow.get("ROWID");

        Object gravityName =
            gravityRow.get("GravityName");


        if (rowId == null
                || gravityName == null) {

            continue;
        }


        String rowIdValue =
            rowId.toString().trim();

        String gravityNameValue =
            gravityName
                .toString()
                .trim();


        gravityNameMap.put(
            rowIdValue,
            gravityNameValue
        );


        gravityCountMap.put(
            gravityNameValue,
            0
        );
    }


    // =================================================
    // COUNT CASES BY GRAVITY
    // =================================================
    int unknownGravityCount = 0;


    for (ZCRowObject caseRow :
            caseTable.getAllRows()) {

        Object gravityValue =
            caseRow.get("GravityOffence");


        if (gravityValue == null) {

            unknownGravityCount++;

            continue;
        }


        String gravityRowId =
            gravityValue
                .toString()
                .trim();


        String gravityName =
            gravityNameMap.get(
                gravityRowId
            );


        if (gravityName == null) {

            unknownGravityCount++;

            continue;
        }


        Integer currentCount =
            gravityCountMap.get(
                gravityName
            );


        if (currentCount == null) {
            currentCount = 0;
        }


        gravityCountMap.put(
            gravityName,
            currentCount + 1
        );
    }


    // =================================================
    // BUILD JSON RESPONSE
    // =================================================
    StringBuilder json =
        new StringBuilder();


    json.append(
        "{"
        + "\"success\":true,"
        + "\"data\":["
    );


    boolean first = true;


    for (java.util.Map.Entry<String, Integer> entry :
            gravityCountMap.entrySet()) {


        // Skip categories with zero cases
        if (entry.getValue() == 0) {
            continue;
        }


        if (!first) {
            json.append(",");
        }


        first = false;


        String safeGravityName =
            entry.getKey()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");


        json.append("{");

        json.append(
            "\"gravity\":\""
        );

        json.append(
            safeGravityName
        );

        json.append(
            "\","
        );

        json.append(
            "\"count\":"
        );

        json.append(
            entry.getValue()
        );

        json.append(
            "}"
        );
    }


    // Add Unknown only if unmapped cases exist
    if (unknownGravityCount > 0) {

        if (!first) {
            json.append(",");
        }


        json.append(
            "{"
            + "\"gravity\":\"Unknown\","
            + "\"count\":"
            + unknownGravityCount
            + "}"
        );
    }


    json.append(
        "],"
        + "\"message\":"
        + "\"Cases by gravity retrieved successfully\""
        + "}"
    );


    response.setStatus(
        HttpServletResponse.SC_OK
    );


    response.getWriter().write(
        json.toString()
    );


    return;
}

            // =====================================================
// DASHBOARD - CASES BY MONTH
//
// Returns chart-ready monthly case registration counts.
//
// Reads:
// CaseMaster.CrimeRegsiteredDate
//
// IMPORTANT:
// "CrimeRegsiteredDate" matches the current
// Catalyst schema spelling.
// =====================================================
if ("/dashboard/cases-by-month".equals(path)
        && "GET".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Calculating cases by month..."
    );


    // =================================================
    // ACCESS CASEMASTER TABLE
    // =================================================
    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable caseTable =
        datastore.getTable("CaseMaster");


    if (caseTable == null) {
        throw new Exception(
            "Unable to access CaseMaster table"
        );
    }


    // =================================================
    // MONTH COUNT MAP
    //
    // Key format:
    // YYYY-MM
    //
    // LinkedHashMap is used so the final response
    // can be returned in chronological order.
    // =================================================
    java.util.Map<String, Integer> monthCountMap =
        new java.util.TreeMap<String, Integer>();


    int unknownDateCount = 0;


    // =================================================
    // READ EVERY CASE
    // =================================================
    for (ZCRowObject caseRow :
            caseTable.getAllRows()) {


        Object registeredDateValue =
            caseRow.get(
                "CrimeRegsiteredDate"
            );


        if (registeredDateValue == null) {

            unknownDateCount++;

            continue;
        }


        String registeredDate =
            registeredDateValue
                .toString()
                .trim();


        // Expected formats may include:
        //
        // 2026-01-01
        // 2026-01-01 00:00:00
        // 2026-01-01T00:00:00
        //
        // We only need the first 7 characters:
        // YYYY-MM
        if (registeredDate.length() < 7) {

            unknownDateCount++;

            continue;
        }


        String monthKey =
            registeredDate.substring(
                0,
                7
            );


        // Basic validation:
        // YYYY-MM
        if (monthKey.length() != 7
                || monthKey.charAt(4) != '-') {

            unknownDateCount++;

            continue;
        }


        Integer currentCount =
            monthCountMap.get(
                monthKey
            );


        if (currentCount == null) {
            currentCount = 0;
        }


        monthCountMap.put(
            monthKey,
            currentCount + 1
        );
    }


    // =================================================
    // BUILD JSON RESPONSE
    // =================================================
    StringBuilder json =
        new StringBuilder();


    json.append(
        "{"
        + "\"success\":true,"
        + "\"data\":["
    );


    boolean first = true;


    for (java.util.Map.Entry<String, Integer> entry :
            monthCountMap.entrySet()) {


        if (!first) {
            json.append(",");
        }


        first = false;


        String monthKey =
            entry.getKey();


        String monthLabel =
            getMonthLabel(
                monthKey
            );


        json.append("{");


        // Machine-friendly key
        json.append(
            "\"month\":\""
        );

        json.append(
            monthKey
        );

        json.append(
            "\","
        );


        // Human-readable label
        json.append(
            "\"label\":\""
        );

        json.append(
            monthLabel
        );

        json.append(
            "\","
        );


        // Case count
        json.append(
            "\"count\":"
        );

        json.append(
            entry.getValue()
        );


        json.append(
            "}"
        );
    }


    // =================================================
    // OPTIONAL UNKNOWN DATE BUCKET
    // =================================================
    if (unknownDateCount > 0) {

        if (!first) {
            json.append(",");
        }


        json.append(
            "{"
            + "\"month\":\"Unknown\","
            + "\"label\":\"Unknown\","
            + "\"count\":"
            + unknownDateCount
            + "}"
        );
    }


    json.append(
        "],"
        + "\"message\":"
        + "\"Cases by month retrieved successfully\""
        + "}"
    );


    response.setStatus(
        HttpServletResponse.SC_OK
    );


    response.getWriter().write(
        json.toString()
    );


    return;
}

            // =====================================================
// CRIME API - LIST ALL CASES
//
// Returns CaseMaster records with both:
// 1. Foreign-key ROWIDs
// 2. Human-readable lookup values
// =====================================================
if ("/crime/list".equals(path)
        && "GET".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Retrieving enriched case list..."
    );

    ZCObject datastore =
        ZCObject.getInstance();

    // =================================================
    // LOAD TABLES
    // =================================================
    ZCTable caseTable =
        datastore.getTable("CaseMaster");

    ZCTable districtTable =
        datastore.getTable("District");

    ZCTable unitTable =
        datastore.getTable("Unit");

    ZCTable crimeHeadTable =
        datastore.getTable("CrimeHead");

    ZCTable crimeSubHeadTable =
        datastore.getTable("CrimeSubHead");

    ZCTable statusTable =
        datastore.getTable("CaseStatusMaster");

    ZCTable gravityTable =
        datastore.getTable("GravityOffence");


    // =================================================
    // VALIDATE TABLE ACCESS
    // =================================================
    if (caseTable == null
            || districtTable == null
            || unitTable == null
            || crimeHeadTable == null
            || crimeSubHeadTable == null
            || statusTable == null
            || gravityTable == null) {

        throw new Exception(
            "Unable to access one or more case lookup tables"
        );
    }


    // =================================================
    // BUILD ROWID -> DISPLAY VALUE MAPS
    // =================================================
    java.util.Map<String, String> districtMap =
        buildLookupMap(
            districtTable,
            "DistrictName"
        );

    java.util.Map<String, String> unitMap =
        buildLookupMap(
            unitTable,
            "UnitName"
        );

    java.util.Map<String, String> crimeHeadMap =
        buildLookupMap(
            crimeHeadTable,
            "CrimeHeadName"
        );

    java.util.Map<String, String> crimeSubHeadMap =
        buildLookupMap(
            crimeSubHeadTable,
            "CrimeSubHeadName"
        );

    java.util.Map<String, String> statusMap =
        buildLookupMap(
            statusTable,
            "StatusName"
        );

    java.util.Map<String, String> gravityMap =
        buildLookupMap(
            gravityTable,
            "GravityName"
        );


    // =================================================
    // BUILD JSON RESPONSE
    // =================================================
    StringBuilder json =
        new StringBuilder();

    json.append(
        "{"
        + "\"success\":true,"
        + "\"data\":["
    );

    boolean firstCase = true;
    int totalCases = 0;


    for (ZCRowObject caseRow :
            caseTable.getAllRows()) {

        if (!firstCase) {
            json.append(",");
        }

        firstCase = false;
        totalCases++;


        // =============================================
        // READ CASE VALUES
        // =============================================
        Object rowId =
            caseRow.get("ROWID");

        Object crimeNo =
            caseRow.get("CrimeNo");

        Object firNo =
            caseRow.get("FIRNo");

        Object firDate =
            caseRow.get("CrimeRegsiteredDate");

        Object districtId =
            caseRow.get("District");

        Object unitId =
            caseRow.get("PoliceStation");

        Object crimeHeadId =
            caseRow.get("CrimeHead");

        Object crimeSubHeadId =
            caseRow.get("CrimeSubHead");

        Object statusId =
            caseRow.get("CaseStatus");

        Object gravityId =
            caseRow.get("GravityOffence");

        Object incidentFromDate =
            caseRow.get("IncidentFromDate");


        // =============================================
        // CONVERT VALUES TO SAFE STRINGS
        // =============================================
        String rowIdValue =
            valueToString(rowId);

        String crimeNoValue =
            valueToString(crimeNo);

        String firNoValue =
            valueToString(firNo);

        String firDateValue =
            valueToString(firDate);

        String districtIdValue =
            valueToString(districtId);

        String unitIdValue =
            valueToString(unitId);

        String crimeHeadIdValue =
            valueToString(crimeHeadId);

        String crimeSubHeadIdValue =
            valueToString(crimeSubHeadId);

        String statusIdValue =
            valueToString(statusId);

        String gravityIdValue =
            valueToString(gravityId);

        String incidentFromDateValue =
            valueToString(incidentFromDate);


        // =============================================
        // RESOLVE HUMAN-READABLE VALUES
        // =============================================
        String districtName =
            districtMap.getOrDefault(
                districtIdValue,
                "Unknown"
            );

        String unitName =
            unitMap.getOrDefault(
                unitIdValue,
                "Unknown"
            );

        String crimeHeadName =
            crimeHeadMap.getOrDefault(
                crimeHeadIdValue,
                "Unknown"
            );

        String crimeSubHeadName =
            crimeSubHeadMap.getOrDefault(
                crimeSubHeadIdValue,
                "Unknown"
            );

        String statusName =
            statusMap.getOrDefault(
                statusIdValue,
                "Unknown"
            );

        String gravityName =
            gravityMap.getOrDefault(
                gravityIdValue,
                "Unknown"
            );


        // =============================================
        // APPEND CASE JSON
        // =============================================
        json.append("{");

        json.append(
            "\"rowId\":\""
            + escapeJson(rowIdValue)
            + "\","
        );

        json.append(
            "\"crimeNo\":\""
            + escapeJson(crimeNoValue)
            + "\","
        );

        json.append(
            "\"firNumber\":\""
            + escapeJson(firNoValue)
            + "\","
        );

        json.append(
            "\"firDate\":\""
            + escapeJson(firDateValue)
            + "\","
        );


        // District
        json.append(
            "\"districtId\":\""
            + escapeJson(districtIdValue)
            + "\","
        );

        json.append(
            "\"district\":\""
            + escapeJson(districtName)
            + "\","
        );


        // Police Station / Unit
        json.append(
            "\"unitId\":\""
            + escapeJson(unitIdValue)
            + "\","
        );

        json.append(
            "\"policeStation\":\""
            + escapeJson(unitName)
            + "\","
        );


        // Crime Head
        json.append(
            "\"crimeHeadId\":\""
            + escapeJson(crimeHeadIdValue)
            + "\","
        );

        json.append(
            "\"crimeHead\":\""
            + escapeJson(crimeHeadName)
            + "\","
        );


        // Crime Sub Head
        json.append(
            "\"crimeSubHeadId\":\""
            + escapeJson(crimeSubHeadIdValue)
            + "\","
        );

        json.append(
            "\"crimeSubHead\":\""
            + escapeJson(crimeSubHeadName)
            + "\","
        );


        // Status
        json.append(
            "\"caseStatusId\":\""
            + escapeJson(statusIdValue)
            + "\","
        );

        json.append(
            "\"status\":\""
            + escapeJson(statusName)
            + "\","
        );


        // Gravity
        json.append(
            "\"offenceGravityId\":\""
            + escapeJson(gravityIdValue)
            + "\","
        );

        json.append(
            "\"gravity\":\""
            + escapeJson(gravityName)
            + "\","
        );


        // Incident date
        json.append(
            "\"incidentFromDate\":\""
            + escapeJson(
                incidentFromDateValue
            )
            + "\""
        );

        json.append("}");
    }


    // =================================================
    // COMPLETE RESPONSE
    // =================================================
    json.append(
        "],"
        + "\"meta\":{"
        + "\"total\":"
        + totalCases
        + "},"
        + "\"message\":"
        + "\"Case list retrieved successfully\""
        + "}"
    );

    response.setStatus(
        HttpServletResponse.SC_OK
    );

    response.getWriter().write(
        json.toString()
    );

    return;
}

            // =====================================================
// CRIME API - CASE DETAILS
//
// Endpoint:
// GET /crime/details/{rowId}
//
// Returns:
// Complete case information with human-readable
// lookup values.
// =====================================================
if (path.startsWith("/crime/details/")
        && "GET".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Retrieving case details..."
    );


    // =================================================
    // EXTRACT CASE ROWID FROM URL
    // =================================================
    String caseRowId =
        path.substring(
            "/crime/details/".length()
        ).trim();


    if (caseRowId.isEmpty()) {

        response.setStatus(
            HttpServletResponse.SC_BAD_REQUEST
        );

        response.getWriter().write(
            "{"
            + "\"success\":false,"
            + "\"data\":{},"
            + "\"message\":"
            + "\"Case ROWID is required\""
            + "}"
        );

        return;
    }


    // =================================================
    // LOAD DATA STORE TABLES
    // =================================================
    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable caseTable =
        datastore.getTable("CaseMaster");

    ZCTable districtTable =
        datastore.getTable("District");

    ZCTable unitTable =
        datastore.getTable("Unit");

    ZCTable crimeHeadTable =
        datastore.getTable("CrimeHead");

    ZCTable crimeSubHeadTable =
        datastore.getTable("CrimeSubHead");

    ZCTable statusTable =
        datastore.getTable("CaseStatusMaster");

    ZCTable gravityTable =
        datastore.getTable("GravityOffence");

    ZCTable categoryTable =
        datastore.getTable("CaseCategory");

    ZCTable employeeTable =
        datastore.getTable("Employee");


    if (caseTable == null
            || districtTable == null
            || unitTable == null
            || crimeHeadTable == null
            || crimeSubHeadTable == null
            || statusTable == null
            || gravityTable == null
            || categoryTable == null
            || employeeTable == null) {

        throw new Exception(
            "Unable to access one or more case detail tables"
        );
    }


    // =================================================
    // BUILD LOOKUP MAPS
    // =================================================
    java.util.Map<String, String> districtMap =
        buildLookupMap(
            districtTable,
            "DistrictName"
        );

    java.util.Map<String, String> unitMap =
        buildLookupMap(
            unitTable,
            "UnitName"
        );

    java.util.Map<String, String> crimeHeadMap =
        buildLookupMap(
            crimeHeadTable,
            "CrimeHeadName"
        );

    java.util.Map<String, String> crimeSubHeadMap =
        buildLookupMap(
            crimeSubHeadTable,
            "CrimeSubHeadName"
        );

    java.util.Map<String, String> statusMap =
        buildLookupMap(
            statusTable,
            "StatusName"
        );

    java.util.Map<String, String> gravityMap =
        buildLookupMap(
            gravityTable,
            "GravityName"
        );

    java.util.Map<String, String> categoryMap =
        buildLookupMap(
            categoryTable,
            "CategoryName"
        );

    java.util.Map<String, String> employeeMap =
        buildLookupMap(
            employeeTable,
            "EmployeeName"
        );


    // =================================================
    // FIND REQUESTED CASE
    // =================================================
    ZCRowObject matchedCase =
        null;


    for (ZCRowObject caseRow :
            caseTable.getAllRows()) {

        Object rowId =
            caseRow.get("ROWID");


        if (rowId != null
                && caseRowId.equals(
                    rowId.toString().trim()
                )) {

            matchedCase =
                caseRow;

            break;
        }
    }


    // =================================================
    // CASE NOT FOUND
    // =================================================
    if (matchedCase == null) {

        response.setStatus(
            HttpServletResponse.SC_NOT_FOUND
        );

        response.getWriter().write(
            "{"
            + "\"success\":false,"
            + "\"data\":{},"
            + "\"message\":"
            + "\"Case not found\""
            + "}"
        );

        return;
    }


    // =================================================
    // READ CASE VALUES
    // =================================================
    String rowIdValue =
        valueToString(
            matchedCase.get("ROWID")
        );

    String crimeNoValue =
        valueToString(
            matchedCase.get("CrimeNo")
        );

    String firNoValue =
        valueToString(
            matchedCase.get("FIRNo")
        );

    String registeredDateValue =
        valueToString(
            matchedCase.get(
                "CrimeRegsiteredDate"
            )
        );

    String districtIdValue =
        valueToString(
            matchedCase.get("District")
        );

    String unitIdValue =
        valueToString(
            matchedCase.get(
                "PoliceStation"
            )
        );

    String categoryIdValue =
        valueToString(
            matchedCase.get(
                "CaseCategory"
            )
        );

    String gravityIdValue =
        valueToString(
            matchedCase.get(
                "GravityOffence"
            )
        );

    String crimeHeadIdValue =
        valueToString(
            matchedCase.get(
                "CrimeHead"
            )
        );

    String crimeSubHeadIdValue =
        valueToString(
            matchedCase.get(
                "CrimeSubHead"
            )
        );

    String statusIdValue =
        valueToString(
            matchedCase.get(
                "CaseStatus"
            )
        );

    String investigatingOfficerIdValue =
        valueToString(
            matchedCase.get(
                "InvestigatingOfficer"
            )
        );

    String incidentFromDateValue =
        valueToString(
            matchedCase.get(
                "IncidentFromDate"
            )
        );

    String incidentToDateValue =
        valueToString(
            matchedCase.get(
                "IncidentToDate"
            )
        );

    String informationReceivedDateValue =
        valueToString(
            matchedCase.get(
                "InformationReceivedDate"
            )
        );

    String latitudeValue =
        valueToString(
            matchedCase.get("Latitude")
        );

    String longitudeValue =
        valueToString(
            matchedCase.get("Longitude")
        );

    String briefFactsValue =
        valueToString(
            matchedCase.get("BriefFacts")
        );


    // =================================================
    // RESOLVE HUMAN-READABLE VALUES
    // =================================================
    String districtName =
        districtMap.getOrDefault(
            districtIdValue,
            "Unknown"
        );

    String policeStationName =
        unitMap.getOrDefault(
            unitIdValue,
            "Unknown"
        );

    String categoryName =
        categoryMap.getOrDefault(
            categoryIdValue,
            "Unknown"
        );

    String gravityName =
        gravityMap.getOrDefault(
            gravityIdValue,
            "Unknown"
        );

    String crimeHeadName =
        crimeHeadMap.getOrDefault(
            crimeHeadIdValue,
            "Unknown"
        );

    String crimeSubHeadName =
        crimeSubHeadMap.getOrDefault(
            crimeSubHeadIdValue,
            "Unknown"
        );

    String statusName =
        statusMap.getOrDefault(
            statusIdValue,
            "Unknown"
        );

    String investigatingOfficerName =
        employeeMap.getOrDefault(
            investigatingOfficerIdValue,
            "Unknown"
        );


    // =================================================
    // BUILD JSON RESPONSE
    // =================================================
    StringBuilder json =
        new StringBuilder();


    json.append(
        "{"
        + "\"success\":true,"
        + "\"data\":{"
    );


    json.append(
        "\"rowId\":\""
        + escapeJson(rowIdValue)
        + "\","
    );

    json.append(
        "\"crimeNo\":\""
        + escapeJson(crimeNoValue)
        + "\","
    );

    json.append(
        "\"firNumber\":\""
        + escapeJson(firNoValue)
        + "\","
    );

    json.append(
        "\"registeredDate\":\""
        + escapeJson(
            registeredDateValue
        )
        + "\","
    );


    // District
    json.append(
        "\"districtId\":\""
        + escapeJson(
            districtIdValue
        )
        + "\","
    );

    json.append(
        "\"district\":\""
        + escapeJson(
            districtName
        )
        + "\","
    );


    // Police Station
    json.append(
        "\"unitId\":\""
        + escapeJson(
            unitIdValue
        )
        + "\","
    );

    json.append(
        "\"policeStation\":\""
        + escapeJson(
            policeStationName
        )
        + "\","
    );


    // Category
    json.append(
        "\"caseCategoryId\":\""
        + escapeJson(
            categoryIdValue
        )
        + "\","
    );

    json.append(
        "\"caseCategory\":\""
        + escapeJson(
            categoryName
        )
        + "\","
    );


    // Crime classification
    json.append(
        "\"crimeHeadId\":\""
        + escapeJson(
            crimeHeadIdValue
        )
        + "\","
    );

    json.append(
        "\"crimeHead\":\""
        + escapeJson(
            crimeHeadName
        )
        + "\","
    );

    json.append(
        "\"crimeSubHeadId\":\""
        + escapeJson(
            crimeSubHeadIdValue
        )
        + "\","
    );

    json.append(
        "\"crimeSubHead\":\""
        + escapeJson(
            crimeSubHeadName
        )
        + "\","
    );


    // Status
    json.append(
        "\"caseStatusId\":\""
        + escapeJson(
            statusIdValue
        )
        + "\","
    );

    json.append(
        "\"status\":\""
        + escapeJson(
            statusName
        )
        + "\","
    );


    // Gravity
    json.append(
        "\"offenceGravityId\":\""
        + escapeJson(
            gravityIdValue
        )
        + "\","
    );

    json.append(
        "\"gravity\":\""
        + escapeJson(
            gravityName
        )
        + "\","
    );


    // Investigating officer
    json.append(
        "\"investigatingOfficerId\":\""
        + escapeJson(
            investigatingOfficerIdValue
        )
        + "\","
    );

    json.append(
        "\"investigatingOfficer\":\""
        + escapeJson(
            investigatingOfficerName
        )
        + "\","
    );


    // Incident timeline
    json.append(
        "\"incidentFromDate\":\""
        + escapeJson(
            incidentFromDateValue
        )
        + "\","
    );

    json.append(
        "\"incidentToDate\":\""
        + escapeJson(
            incidentToDateValue
        )
        + "\","
    );

    json.append(
        "\"informationReceivedDate\":\""
        + escapeJson(
            informationReceivedDateValue
        )
        + "\","
    );


    // Location
    json.append(
        "\"latitude\":\""
        + escapeJson(
            latitudeValue
        )
        + "\","
    );

    json.append(
        "\"longitude\":\""
        + escapeJson(
            longitudeValue
        )
        + "\","
    );


    // Brief facts
    json.append(
        "\"briefFacts\":\""
        + escapeJson(
            briefFactsValue
        )
        + "\""
    );


    json.append(
        "},"
        + "\"message\":"
        + "\"Case details retrieved successfully\""
        + "}"
    );


    response.setStatus(
        HttpServletResponse.SC_OK
    );

    response.getWriter().write(
        json.toString()
    );

    return;
}

            // =====================================================
// UNIT API - LIST ALL UNITS
//
// Endpoint:
// GET /unit/list
//
// Returns:
// Unit directory with human-readable District
// and Unit Type values.
// =====================================================
if ("/unit/list".equals(path)
        && "GET".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Retrieving unit directory..."
    );


    // =================================================
    // LOAD DATA STORE TABLES
    // =================================================
    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable unitTable =
        datastore.getTable("Unit");

    ZCTable districtTable =
        datastore.getTable("District");

    ZCTable unitTypeTable =
        datastore.getTable("UnitType");


    if (unitTable == null
            || districtTable == null
            || unitTypeTable == null) {

        throw new Exception(
            "Unable to access one or more unit tables"
        );
    }


    // =================================================
    // BUILD LOOKUP MAPS
    // =================================================
    java.util.Map<String, String> districtMap =
        buildLookupMap(
            districtTable,
            "DistrictName"
        );

    java.util.Map<String, String> unitTypeMap =
        buildLookupMap(
            unitTypeTable,
            "UnitType"
        );


    // =================================================
    // BUILD JSON RESPONSE
    // =================================================
    StringBuilder json =
        new StringBuilder();

    json.append(
        "{"
        + "\"success\":true,"
        + "\"data\":["
    );


    boolean firstUnit = true;
    int totalUnits = 0;
    int activeUnits = 0;
    int inactiveUnits = 0;


    for (ZCRowObject unitRow :
            unitTable.getAllRows()) {


        // =============================================
        // READ VALUES
        // =============================================
        String rowIdValue =
            valueToString(
                unitRow.get("ROWID")
            );

        String unitCodeValue =
            valueToString(
                unitRow.get("UnitCode")
            );

        String unitNameValue =
            valueToString(
                unitRow.get("UnitName")
            );

        String districtIdValue =
            valueToString(
                unitRow.get("District")
            );

        String unitTypeIdValue =
            valueToString(
                unitRow.get("UnitType")
            );

        String addressValue =
            valueToString(
                unitRow.get("Address")
            );

        String isActiveValue =
            valueToString(
                unitRow.get("IsActive")
            );


        // =============================================
        // RESOLVE LOOKUPS
        // =============================================
        String districtName =
            districtMap.getOrDefault(
                districtIdValue,
                "Unknown"
            );

        String unitTypeName =
            unitTypeMap.getOrDefault(
                unitTypeIdValue,
                "Unknown"
            );


        // =============================================
        // NORMALIZE ACTIVE STATUS
        // =============================================
        boolean isActive =
            "true".equalsIgnoreCase(
                isActiveValue
            )
            || "1".equals(
                isActiveValue
            );


        totalUnits++;

        if (isActive) {
            activeUnits++;
        } else {
            inactiveUnits++;
        }


        // =============================================
        // JSON COMMA HANDLING
        // =============================================
        if (!firstUnit) {
            json.append(",");
        }

        firstUnit = false;


        // =============================================
        // APPEND UNIT JSON
        // =============================================
        json.append("{");

        json.append(
            "\"rowId\":\""
            + escapeJson(
                rowIdValue
            )
            + "\","
        );

        json.append(
            "\"unitCode\":\""
            + escapeJson(
                unitCodeValue
            )
            + "\","
        );

        json.append(
            "\"unitName\":\""
            + escapeJson(
                unitNameValue
            )
            + "\","
        );


        // District
        json.append(
            "\"districtId\":\""
            + escapeJson(
                districtIdValue
            )
            + "\","
        );

        json.append(
            "\"district\":\""
            + escapeJson(
                districtName
            )
            + "\","
        );


        // Unit Type
        json.append(
            "\"unitTypeId\":\""
            + escapeJson(
                unitTypeIdValue
            )
            + "\","
        );

        json.append(
            "\"unitType\":\""
            + escapeJson(
                unitTypeName
            )
            + "\","
        );


        // Address
        json.append(
            "\"address\":\""
            + escapeJson(
                addressValue
            )
            + "\","
        );


        // Active Status
        json.append(
            "\"isActive\":"
            + isActive
        );

        json.append("}");
    }


    // =================================================
    // COMPLETE RESPONSE
    // =================================================
    json.append(
        "],"
        + "\"meta\":{"
        + "\"total\":"
        + totalUnits
        + ","
        + "\"active\":"
        + activeUnits
        + ","
        + "\"inactive\":"
        + inactiveUnits
        + "},"
        + "\"message\":"
        + "\"Unit directory retrieved successfully\""
        + "}"
    );


    response.setStatus(
        HttpServletResponse.SC_OK
    );

    response.getWriter().write(
        json.toString()
    );

    return;
}

            // =====================================================
// UNIT API - UNIT DETAILS
//
// Endpoint:
// GET /unit/details/{rowId}
//
// Returns:
// Unit profile and operational case statistics.
// =====================================================
if (path.startsWith("/unit/details/")
        && "GET".equalsIgnoreCase(method)) {

    LOGGER.info(
        "Retrieving unit details..."
    );


    // =================================================
    // EXTRACT UNIT ROWID
    // =================================================
    String unitRowId =
        path.substring(
            "/unit/details/".length()
        ).trim();


    if (unitRowId.isEmpty()) {

        response.setStatus(
            HttpServletResponse.SC_BAD_REQUEST
        );

        response.getWriter().write(
            "{"
            + "\"success\":false,"
            + "\"data\":{},"
            + "\"message\":\"Unit ROWID is required\""
            + "}"
        );

        return;
    }


    // =================================================
    // LOAD TABLES
    // =================================================
    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable unitTable =
        datastore.getTable("Unit");

    ZCTable districtTable =
        datastore.getTable("District");

    ZCTable unitTypeTable =
        datastore.getTable("UnitType");

    ZCTable caseTable =
        datastore.getTable("CaseMaster");

    ZCTable statusTable =
        datastore.getTable("CaseStatusMaster");

    ZCTable crimeHeadTable =
        datastore.getTable("CrimeHead");


    if (unitTable == null
            || districtTable == null
            || unitTypeTable == null
            || caseTable == null
            || statusTable == null
            || crimeHeadTable == null) {

        throw new Exception(
            "Unable to access one or more unit detail tables"
        );
    }


    // =================================================
    // BUILD LOOKUP MAPS
    // =================================================
    java.util.Map<String, String> districtMap =
        buildLookupMap(
            districtTable,
            "DistrictName"
        );

    java.util.Map<String, String> unitTypeMap =
        buildLookupMap(
            unitTypeTable,
            "UnitType"
        );

    java.util.Map<String, String> statusMap =
        buildLookupMap(
            statusTable,
            "StatusName"
        );

    java.util.Map<String, String> crimeHeadMap =
        buildLookupMap(
            crimeHeadTable,
            "CrimeHeadName"
        );


    // =================================================
    // FIND REQUESTED UNIT
    // =================================================
    ZCRowObject matchedUnit =
        null;


    for (ZCRowObject unitRow :
            unitTable.getAllRows()) {

        Object rowId =
            unitRow.get("ROWID");

        if (rowId != null
                && unitRowId.equals(
                    rowId.toString().trim()
                )) {

            matchedUnit =
                unitRow;

            break;
        }
    }


    // =================================================
    // UNIT NOT FOUND
    // =================================================
    if (matchedUnit == null) {

        response.setStatus(
            HttpServletResponse.SC_NOT_FOUND
        );

        response.getWriter().write(
            "{"
            + "\"success\":false,"
            + "\"data\":{},"
            + "\"message\":\"Unit not found\""
            + "}"
        );

        return;
    }


    // =================================================
    // READ UNIT VALUES
    // =================================================
    String rowIdValue =
        valueToString(
            matchedUnit.get("ROWID")
        );

    String unitCodeValue =
        valueToString(
            matchedUnit.get("UnitCode")
        );

    String unitNameValue =
        valueToString(
            matchedUnit.get("UnitName")
        );

    String districtIdValue =
        valueToString(
            matchedUnit.get("District")
        );

    String unitTypeIdValue =
        valueToString(
            matchedUnit.get("UnitType")
        );

    String addressValue =
        valueToString(
            matchedUnit.get("Address")
        );

    String isActiveValue =
        valueToString(
            matchedUnit.get("IsActive")
        );


    String districtName =
        districtMap.getOrDefault(
            districtIdValue,
            "Unknown"
        );

    String unitTypeName =
        unitTypeMap.getOrDefault(
            unitTypeIdValue,
            "Unknown"
        );


    boolean isActive =
        "true".equalsIgnoreCase(
            isActiveValue
        )
        || "1".equals(
            isActiveValue
        );


    // =================================================
    // CALCULATE UNIT CASE STATISTICS
    // =================================================
    int totalCases = 0;
    int openCases = 0;
    int closedCases = 0;
    int chargeSheets = 0;


    java.util.Map<String, Integer>
        crimeDistribution =
            new java.util.LinkedHashMap<
                String,
                Integer
            >();


    // Keep a limited recent-case collection.
    java.util.List<ZCRowObject> unitCases =
        new java.util.ArrayList<
            ZCRowObject
        >();


    for (ZCRowObject caseRow :
            caseTable.getAllRows()) {


        String caseUnitId =
            valueToString(
                caseRow.get(
                    "PoliceStation"
                )
            );


        if (!rowIdValue.equals(
                caseUnitId
        )) {

            continue;
        }


        totalCases++;

        unitCases.add(
            caseRow
        );


        // =============================================
        // STATUS COUNTS
        // =============================================
        String statusId =
            valueToString(
                caseRow.get(
                    "CaseStatus"
                )
            );

        String statusName =
            statusMap.getOrDefault(
                statusId,
                "Unknown"
            );


        if ("Closed".equalsIgnoreCase(
                statusName
        )) {

            closedCases++;

        } else {

            openCases++;

        }


        if ("Charge Sheet Filed"
                .equalsIgnoreCase(
                    statusName
                )) {

            chargeSheets++;
        }


        // =============================================
        // CRIME DISTRIBUTION
        // =============================================
        String crimeHeadId =
            valueToString(
                caseRow.get(
                    "CrimeHead"
                )
            );

        String crimeHeadName =
            crimeHeadMap.getOrDefault(
                crimeHeadId,
                "Unknown"
            );


        crimeDistribution.put(
            crimeHeadName,
            crimeDistribution
                .getOrDefault(
                    crimeHeadName,
                    0
                )
            + 1
        );
    }


    // =================================================
    // BUILD JSON RESPONSE
    // =================================================
    StringBuilder json =
        new StringBuilder();


    json.append(
        "{"
        + "\"success\":true,"
        + "\"data\":{"
    );


    // =================================================
    // UNIT PROFILE
    // =================================================
    json.append(
        "\"rowId\":\""
        + escapeJson(rowIdValue)
        + "\","
    );

    json.append(
        "\"unitCode\":\""
        + escapeJson(unitCodeValue)
        + "\","
    );

    json.append(
        "\"unitName\":\""
        + escapeJson(unitNameValue)
        + "\","
    );

    json.append(
        "\"districtId\":\""
        + escapeJson(districtIdValue)
        + "\","
    );

    json.append(
        "\"district\":\""
        + escapeJson(districtName)
        + "\","
    );

    json.append(
        "\"unitTypeId\":\""
        + escapeJson(unitTypeIdValue)
        + "\","
    );

    json.append(
        "\"unitType\":\""
        + escapeJson(unitTypeName)
        + "\","
    );

    json.append(
        "\"address\":\""
        + escapeJson(addressValue)
        + "\","
    );

    json.append(
        "\"isActive\":"
        + isActive
        + ","
    );


    // =================================================
    // OPERATIONAL SUMMARY
    // =================================================
    json.append(
        "\"statistics\":{"
        + "\"totalCases\":"
        + totalCases
        + ","
        + "\"openCases\":"
        + openCases
        + ","
        + "\"closedCases\":"
        + closedCases
        + ","
        + "\"chargeSheets\":"
        + chargeSheets
        + "},"
    );


    // =================================================
    // CRIME DISTRIBUTION
    // =================================================
    json.append(
        "\"crimeDistribution\":["
    );


    boolean firstCrime =
        true;


    for (java.util.Map.Entry<
            String,
            Integer
        > entry :
            crimeDistribution
                .entrySet()) {


        if (!firstCrime) {
            json.append(",");
        }

        firstCrime =
            false;


        json.append(
            "{"
            + "\"label\":\""
            + escapeJson(
                entry.getKey()
            )
            + "\","
            + "\"count\":"
            + entry.getValue()
            + "}"
        );
    }


    json.append("],");


    // =================================================
    // RECENT CASES
    //
    // Prototype:
    // Returns up to 5 cases associated with the unit.
    // =================================================
    json.append(
        "\"recentCases\":["
    );


    boolean firstCase =
        true;

    int recentCaseCount =
        0;


    for (ZCRowObject caseRow :
            unitCases) {


        if (recentCaseCount >= 5) {
            break;
        }


        if (!firstCase) {
            json.append(",");
        }

        firstCase =
            false;


        String caseRowId =
            valueToString(
                caseRow.get("ROWID")
            );

        String crimeNo =
            valueToString(
                caseRow.get("CrimeNo")
            );

        String firNo =
            valueToString(
                caseRow.get("FIRNo")
            );

        String registeredDate =
            valueToString(
                caseRow.get(
                    "CrimeRegsiteredDate"
                )
            );

        String statusId =
            valueToString(
                caseRow.get(
                    "CaseStatus"
                )
            );

        String statusName =
            statusMap.getOrDefault(
                statusId,
                "Unknown"
            );

        String crimeHeadId =
            valueToString(
                caseRow.get(
                    "CrimeHead"
                )
            );

        String crimeHeadName =
            crimeHeadMap.getOrDefault(
                crimeHeadId,
                "Unknown"
            );


        json.append(
            "{"
            + "\"rowId\":\""
            + escapeJson(caseRowId)
            + "\","
            + "\"crimeNo\":\""
            + escapeJson(crimeNo)
            + "\","
            + "\"firNumber\":\""
            + escapeJson(firNo)
            + "\","
            + "\"registeredDate\":\""
            + escapeJson(
                registeredDate
            )
            + "\","
            + "\"status\":\""
            + escapeJson(
                statusName
            )
            + "\","
            + "\"crimeHead\":\""
            + escapeJson(
                crimeHeadName
            )
            + "\""
            + "}"
        );


        recentCaseCount++;
    }


    json.append("]");


    // =================================================
    // COMPLETE RESPONSE
    // =================================================
    json.append(
        "},"
        + "\"message\":"
        + "\"Unit details retrieved successfully\""
        + "}"
    );


    response.setStatus(
        HttpServletResponse.SC_OK
    );

    response.getWriter().write(
        json.toString()
    );

    return;
}

            // =====================================================
            // 7. 404 - ENDPOINT NOT FOUND
            // =====================================================
            response.setStatus(
                HttpServletResponse.SC_NOT_FOUND
            );

            response.getWriter().write(
                "{"
                + "\"success\":false,"
                + "\"data\":{},"
                + "\"message\":\"API endpoint not found\""
                + "}"
            );


        } catch (Exception e) {

            LOGGER.log(
                Level.SEVERE,
                "Exception in EchoProtocolAPI",
                e
            );

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            String safeMessage =
                e.getMessage() == null
                    ? "Unknown error"
                    : e.getMessage()
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"");

            response.getWriter().write(
                "{"
                + "\"success\":false,"
                + "\"data\":{},"
                + "\"message\":\""
                + safeMessage
                + "\""
                + "}"
            );
        }

    } // END OF runner()

    // =============================================================
// JSON STRING ESCAPE HELPER
// =============================================================
private String escapeJson(String value) {
    
    if (value == null) {
        return "";
    }

    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
}

    // =============================================================
// SAFE OBJECT -> STRING CONVERSION
// =============================================================
private String valueToString(
    Object value
) {

    if (value == null) {
        return "";
    }

    return value
        .toString()
        .trim();
}


// =============================================================
// BUILD LOOKUP MAP
//
// Converts:
// ROWID -> Human-readable display value
//
// Examples:
// District ROWID -> DistrictName
// Unit ROWID     -> UnitName
// =============================================================
private java.util.Map<String, String> buildLookupMap(
    ZCTable table,
    String displayColumn
) throws Exception {

    java.util.Map<String, String> lookupMap =
        new java.util.LinkedHashMap<String, String>();

    for (ZCRowObject row :
            table.getAllRows()) {

        Object rowId =
            row.get("ROWID");

        Object displayValue =
            row.get(displayColumn);

        if (rowId == null
                || displayValue == null) {

            continue;
        }

        lookupMap.put(
            rowId.toString().trim(),
            displayValue.toString().trim()
        );
    }

    return lookupMap;
}

    // =============================================================
    // HELPER 1:
    // CHECK WHETHER A VALUE ALREADY EXISTS
    // =============================================================
    private boolean rowExists(
        ZCTable table,
        String columnName,
        String expectedValue
    ) throws Exception {

        for (ZCRowObject existingRow :
                table.getAllRows()) {

            Object value =
                existingRow.get(columnName);

            if (value != null
                    && expectedValue.equalsIgnoreCase(
                        value.toString().trim()
                    )) {

                return true;
            }
        }

        return false;
    }


    // =============================================================
    // HELPER 2:
    // GENERIC DUPLICATE-SAFE TABLE SEEDER
    //
    // result[0] = inserted count
    // result[1] = skipped count
    // =============================================================
    private int[] seedTable(
        String tableName,
        String uniqueColumn,
        String[] columnNames,
        String[][] data
    ) throws Exception {

        LOGGER.log(
            Level.INFO,
            "Starting seed for table: {0}",
            tableName
        );

        ZCObject datastore =
            ZCObject.getInstance();

        ZCTable table =
            datastore.getTable(tableName);

        if (table == null) {

            throw new Exception(
                "Unable to access table: "
                + tableName
            );
        }

        int inserted = 0;
        int skipped = 0;

        for (String[] record : data) {

            if (record.length
                    != columnNames.length) {

                throw new Exception(
                    "Column/data mismatch in table: "
                    + tableName
                );
            }

            String uniqueValue =
                record[0];

            if (rowExists(
                    table,
                    uniqueColumn,
                    uniqueValue
            )) {

                LOGGER.log(
                    Level.INFO,
                    "Skipping existing record in {0}: {1}",
                    new Object[]{
                        tableName,
                        uniqueValue
                    }
                );

                skipped++;
                continue;
            }

            ZCRowObject row =
                ZCRowObject.getInstance();

            for (int i = 0;
                    i < columnNames.length;
                    i++) {

                row.set(
                    columnNames[i],
                    record[i]
                );
            }

            table.insertRow(row);

            inserted++;

            LOGGER.log(
                Level.INFO,
                "Inserted record into {0}: {1}",
                new Object[]{
                    tableName,
                    uniqueValue
                }
            );
        }

        LOGGER.log(
            Level.INFO,
            "Completed seed for {0}. Inserted: {1}, Skipped: {2}",
            new Object[]{
                tableName,
                inserted,
                skipped
            }
        );

        return new int[]{
            inserted,
            skipped
        };
    }


    // =============================================================
    // HELPER 3:
    // FIND ROWID BY COLUMN VALUE
    // =============================================================
    private Long findRowId(
        String tableName,
        String searchColumn,
        String searchValue
    ) throws Exception {

        ZCObject datastore =
            ZCObject.getInstance();

        ZCTable table =
            datastore.getTable(tableName);

        if (table == null) {

            throw new Exception(
                "Unable to access table: "
                + tableName
            );
        }

        for (ZCRowObject row :
                table.getAllRows()) {

            Object value =
                row.get(searchColumn);

            if (value != null
                    && searchValue.equalsIgnoreCase(
                        value.toString().trim()
                    )) {

                Object rowId =
                    row.get("ROWID");

                if (rowId == null) {

                    throw new Exception(
                        "ROWID not found for "
                        + searchValue
                        + " in table "
                        + tableName
                    );
                }

                return Long.valueOf(
                    rowId.toString()
                );
            }
        }

        throw new Exception(
            "Record not found in table "
            + tableName
            + ": "
            + searchValue
        );
    }


    // =============================================================
// HELPER 4:
// SEED ONE UNIT WITH FOREIGN KEY LOOKUPS
//
// result[0] = inserted
// result[1] = skipped
// =============================================================
private int[] seedUnit(
    String unitCode,
    String unitName,
    String districtName,
    String unitTypeName,
    String address
) throws Exception {

    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable unitTable =
        datastore.getTable("Unit");

    if (unitTable == null) {

        throw new Exception(
            "Unable to access Unit table"
        );
    }


    // =========================================================
    // DUPLICATE PROTECTION USING UNIT CODE
    // =========================================================
    if (rowExists(
            unitTable,
            "UnitCode",
            unitCode
    )) {

        LOGGER.log(
            Level.INFO,
            "Skipping existing Unit: {0}",
            unitCode
        );

        return new int[]{0, 1};
    }


    // =========================================================
    // RESOLVE DISTRICT FOREIGN KEY
    //
    // District table:
    // DistrictName = Bengaluru Urban
    //               Mysuru
    //               Mangaluru / Dakshina Kannada
    // =========================================================
    Long districtRowId =
        findRowId(
            "District",
            "DistrictName",
            districtName
        );


    // =========================================================
    // RESOLVE UNIT TYPE FOREIGN KEY
    //
    // IMPORTANT:
    // UnitType master table now contains:
    //
    // ROWID
    // Description
    // UnitType  <-- varchar column
    //
    // Therefore searchColumn MUST be "UnitType",
    // NOT the old "UnitTypeName".
    // =========================================================
    Long unitTypeRowId =
        findRowId(
            "UnitType",
            "UnitType",
            unitTypeName
        );


    LOGGER.log(
        Level.INFO,
        "Resolved Unit {0}: District ROWID={1}, UnitType ROWID={2}",
        new Object[]{
            unitCode,
            districtRowId,
            unitTypeRowId
        }
    );


    // =========================================================
    // CREATE UNIT ROW
    // =========================================================
    ZCRowObject row =
        ZCRowObject.getInstance();

    row.set(
        "UnitCode",
        unitCode
    );

    row.set(
        "UnitName",
        unitName
    );

    // Foreign key to District
    row.set(
        "District",
        districtRowId
    );

    row.set(
        "Address",
        address
    );

    // Mandatory boolean field
    row.set(
        "IsActive",
        true
    );

    // Foreign key to UnitType
    row.set(
        "UnitType",
        unitTypeRowId
    );


    // ContactNumber and Email are optional,
    // so they are intentionally not populated.


    // =========================================================
    // INSERT INTO CATALYST DATA STORE
    // =========================================================
    unitTable.insertRow(row);


    LOGGER.log(
        Level.INFO,
        "Inserted Unit successfully: {0}",
        unitCode
    );


    return new int[]{1, 0};
}

    // =============================================================
// HELPER 5:
// SEED ONE CRIME SUB HEAD WITH CRIME HEAD FOREIGN KEY
//
// result[0] = inserted
// result[1] = skipped
// =============================================================
private int[] seedCrimeSubHead(
    String crimeSubHeadName,
    String crimeHeadName,
    String description
) throws Exception {

    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable crimeSubHeadTable =
        datastore.getTable("CrimeSubHead");

    if (crimeSubHeadTable == null) {

        throw new Exception(
            "Unable to access CrimeSubHead table"
        );
    }


    // Duplicate protection
    if (rowExists(
            crimeSubHeadTable,
            "CrimeSubHeadName",
            crimeSubHeadName
    )) {

        LOGGER.log(
            Level.INFO,
            "Skipping existing CrimeSubHead: {0}",
            crimeSubHeadName
        );

        return new int[]{0, 1};
    }


    // Resolve CrimeHead foreign key
    Long crimeHeadRowId =
        findRowId(
            "CrimeHead",
            "CrimeHeadName",
            crimeHeadName
        );


    // Create row
    ZCRowObject row =
        ZCRowObject.getInstance();

    row.set(
        "CrimeSubHeadName",
        crimeSubHeadName
    );

    row.set(
        "CrimeHead",
        crimeHeadRowId
    );

    row.set(
        "Description",
        description
    );


    // Insert
    crimeSubHeadTable.insertRow(row);


    LOGGER.log(
        Level.INFO,
        "Inserted CrimeSubHead: {0}",
        crimeSubHeadName
    );


    return new int[]{1, 0};
}

    // =============================================================
// HELPER 6:
// SEED ONE SECTION WITH ACT FOREIGN KEY
//
// result[0] = inserted
// result[1] = skipped
// =============================================================
private int[] seedSection(
    String sectionCode,
    String sectionName,
    String actName,
    String description
) throws Exception {

    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable sectionTable =
        datastore.getTable("Section");

    if (sectionTable == null) {

        throw new Exception(
            "Unable to access Section table"
        );
    }


    // Duplicate protection using SectionCode
    if (rowExists(
            sectionTable,
            "SectionCode",
            sectionCode
    )) {

        LOGGER.log(
            Level.INFO,
            "Skipping existing Section: {0}",
            sectionCode
        );

        return new int[]{0, 1};
    }


    // Resolve Act foreign key
    Long actRowId =
        findRowId(
            "Act",
            "ActName",
            actName
        );


    // Create row
    ZCRowObject row =
        ZCRowObject.getInstance();

    row.set(
        "SectionCode",
        sectionCode
    );

    row.set(
        "SectionName",
        sectionName
    );

    row.set(
        "Act",
        actRowId
    );

    row.set(
        "Description",
        description
    );


    // Insert
    sectionTable.insertRow(row);


    LOGGER.log(
        Level.INFO,
        "Inserted Section: {0}",
        sectionCode
    );


    return new int[]{1, 0};
}

    // =============================================================
// HELPER:
// SEED ONE COURT WITH DISTRICT FOREIGN KEY
//
// result[0] = inserted
// result[1] = skipped
// =============================================================
private int[] seedCourt(
    String courtName,
    String districtName,
    String courtType,
    String address
) throws Exception {

    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable courtTable =
        datastore.getTable("Court");

    if (courtTable == null) {
        throw new Exception(
            "Unable to access Court table"
        );
    }


    // Duplicate protection
    if (rowExists(
            courtTable,
            "CourtName",
            courtName
    )) {

        LOGGER.log(
            Level.INFO,
            "Skipping existing Court: {0}",
            courtName
        );

        return new int[]{0, 1};
    }


    // Resolve District foreign key
    Long districtRowId =
        findRowId(
            "District",
            "DistrictName",
            districtName
        );


    ZCRowObject row =
        ZCRowObject.getInstance();

    row.set(
        "CourtName",
        courtName
    );

    row.set(
        "District",
        districtRowId
    );

    row.set(
        "CourtType",
        courtType
    );

    row.set(
        "Address",
        address
    );


    courtTable.insertRow(row);


    LOGGER.log(
        Level.INFO,
        "Inserted Court: {0}",
        courtName
    );


    return new int[]{1, 0};
}

    // =============================================================
// HELPER:
// SEED ONE SYNTHETIC EMPLOYEE
//
// Resolves:
// RankName -> Rank ROWID
// DesignationName -> Designation ROWID
// UnitCode -> Unit ROWID
//
// result[0] = inserted
// result[1] = skipped
// =============================================================
private int[] seedEmployee(
    String employeeCode,
    String employeeName,
    String rankName,
    String designationName,
    String unitCode
) throws Exception {

    ZCObject datastore =
        ZCObject.getInstance();

    ZCTable employeeTable =
        datastore.getTable("Employee");

    if (employeeTable == null) {
        throw new Exception(
            "Unable to access Employee table"
        );
    }


    // Duplicate protection
    if (rowExists(
            employeeTable,
            "EmployeeCode",
            employeeCode
    )) {

        LOGGER.log(
            Level.INFO,
            "Skipping existing Employee: {0}",
            employeeCode
        );

        return new int[]{0, 1};
    }


    // Resolve foreign keys
    Long rankRowId =
        findRowId(
            "Rank",
            "RankName",
            rankName
        );

    Long designationRowId =
        findRowId(
            "Designation",
            "DesignationName",
            designationName
        );

    Long unitRowId =
        findRowId(
            "Unit",
            "UnitCode",
            unitCode
        );


    ZCRowObject row =
        ZCRowObject.getInstance();

    row.set(
        "EmployeeCode",
        employeeCode
    );

    row.set(
        "EmployeeName",
        employeeName
    );

    row.set(
        "Rank",
        rankRowId
    );

    row.set(
        "Designation",
        designationRowId
    );

    row.set(
        "Unit",
        unitRowId
    );


    // Synthetic contact details
    String normalizedCode =
        employeeCode
            .toLowerCase()
            .replace("-", ".");

    row.set(
        "Email",
        normalizedCode
            + "@prototype.invalid"
    );

    // Deliberately synthetic placeholder number
    row.set(
        "MobileNumber",
        "0000000000"
    );

    row.set(
        "IsActive",
        true
    );


    employeeTable.insertRow(row);


    LOGGER.log(
        Level.INFO,
        "Inserted Employee: {0}",
        employeeCode
    );


    return new int[]{1, 0};
}

    // =============================================================
    // HELPER: SEED ONE SYNTHETIC CASEMASTER RECORD
    // =============================================================
    private int[] seedCase(
        String crimeNo,
        String firNo,
        String districtName,
        String unitCode,
        String employeeCode,
        String categoryName,
        String gravityName,
        String crimeHeadName,
        String crimeSubHeadName,
        String statusName,
        int caseNumber
    ) throws Exception {

        ZCObject datastore = ZCObject.getInstance();
        ZCTable caseTable = datastore.getTable("CaseMaster");

        if (caseTable == null) {
            throw new Exception("Unable to access CaseMaster table");
        }

        if (rowExists(caseTable, "CrimeNo", crimeNo)) {
            LOGGER.log(Level.INFO, "Skipping existing CaseMaster record: {0}", crimeNo);
            return new int[]{0, 1};
        }

        Long districtRowId = findRowId("District", "DistrictName", districtName);
        Long unitRowId = findRowId("Unit", "UnitCode", unitCode);
        Long employeeRowId = findRowId("Employee", "EmployeeCode", employeeCode);
        Long categoryRowId = findRowId("CaseCategory", "CategoryName", categoryName);
        Long gravityRowId = findRowId("GravityOffence", "GravityName", gravityName);
        Long crimeHeadRowId = findRowId("CrimeHead", "CrimeHeadName", crimeHeadName);
        Long crimeSubHeadRowId = findRowId("CrimeSubHead", "CrimeSubHeadName", crimeSubHeadName);
        Long statusRowId = findRowId("CaseStatusMaster", "StatusName", statusName);

        int month = ((caseNumber - 1) % 6) + 1;
        int day = ((caseNumber - 1) % 27) + 1;

        java.sql.Date registeredDate = java.sql.Date.valueOf(
            String.format("2026-%02d-%02d", month, day)
        );

        java.sql.Date incidentFromDate = new java.sql.Date(
            registeredDate.getTime() - (24L * 60L * 60L * 1000L)
        );

        ZCRowObject row = ZCRowObject.getInstance();

        row.set("CrimeNo", crimeNo);
        row.set("FIRNo", firNo);

        // Keep this spelling because it matches the current Catalyst schema.
        row.set("CrimeRegsiteredDate", 
            registeredDate.toString()
            );

        // Populate both legacy and current columns.
        row.set("PolicePerson", employeeRowId);
        row.set("InvestigatingOfficer", employeeRowId);

        row.set("PoliceStation", unitRowId);
        row.set("District", districtRowId);
        row.set("CaseCategory", categoryRowId);
        row.set("GravityOffence", gravityRowId);

        row.set("CrimeMajorHead", crimeHeadRowId);
        row.set("CrimeHead", crimeHeadRowId);

        row.set("CrimeMinorHead", crimeSubHeadRowId);
        row.set("CrimeSubHead", crimeSubHeadRowId);

        row.set("CaseStatus", statusRowId);

        row.set("IncidentFromDate", incidentFromDate.toString());
        row.set("IncidentToDate", registeredDate.toString());
        row.set("InformationReceivedDate", registeredDate.toString());

        if ("Bengaluru Urban".equals(districtName)) {
            row.set("Latitude", "12.9716");
            row.set("Longitude", "77.5946");
        } else if ("Mysuru".equals(districtName)) {
            row.set("Latitude", "12.2958");
            row.set("Longitude", "76.6394");
        } else {
            row.set("Latitude", "12.9141");
            row.set("Longitude", "74.8560");
        }

        row.set(
            "BriefFacts",
            "Synthetic prototype case " + crimeNo
            + " classified as " + crimeSubHeadName
            + ". Generated only for dashboard demonstration."
        );

        // Court is optional and intentionally left null.
        caseTable.insertRow(row);

        LOGGER.log(Level.INFO, "Inserted CaseMaster record: {0}", crimeNo);
        return new int[]{1, 0};
    }


    // =============================================================
    // HELPER 5:
    // RANK MASTER DATA
    // =============================================================
    private String[][] getRankData() {

        return new String[][]{
            {
                "Director General of Police",
                "DGP",
                "Highest police rank"
            },
            {
                "Additional Director General of Police",
                "ADGP",
                "Senior leadership rank"
            },
            {
                "Inspector General of Police",
                "IGP",
                "Senior supervisory rank"
            },
            {
                "Deputy Inspector General of Police",
                "DIG",
                "Supervisory rank"
            },
            {
                "Superintendent of Police",
                "SP",
                "District-level senior rank"
            },
            {
                "Deputy Superintendent of Police",
                "DSP",
                "Sub-divisional supervisory rank"
            },
            {
                "Inspector",
                "INSP",
                "Police Inspector"
            },
            {
                "Sub-Inspector",
                "SI",
                "Sub-Inspector of Police"
            },
            {
                "Assistant Sub-Inspector",
                "ASI",
                "Assistant Sub-Inspector of Police"
            },
            {
                "Head Constable",
                "HC",
                "Senior constabulary rank"
            },
            {
                "Police Constable",
                "PC",
                "Constabulary rank"
            }
        };
    }
        // =============================================================
// HELPER:
// CONVERT YYYY-MM INTO READABLE MONTH LABEL
//
// Example:
// 2026-01 -> Jan 2026
// 2026-06 -> Jun 2026
// =============================================================
private String getMonthLabel(
    String monthKey
) {

    if (monthKey == null
            || monthKey.length() != 7) {

        return "Unknown";
    }


    String year =
        monthKey.substring(
            0,
            4
        );


    String month =
        monthKey.substring(
            5,
            7
        );


    String monthName;


    switch (month) {

        case "01":
            monthName = "Jan";
            break;

        case "02":
            monthName = "Feb";
            break;

        case "03":
            monthName = "Mar";
            break;

        case "04":
            monthName = "Apr";
            break;

        case "05":
            monthName = "May";
            break;

        case "06":
            monthName = "Jun";
            break;

        case "07":
            monthName = "Jul";
            break;

        case "08":
            monthName = "Aug";
            break;

        case "09":
            monthName = "Sep";
            break;

        case "10":
            monthName = "Oct";
            break;

        case "11":
            monthName = "Nov";
            break;

        case "12":
            monthName = "Dec";
            break;

        default:
            return monthKey;
    }


    return monthName
        + " "
        + year;
}
} // END OF EchoProtocolAPI