# erpjobs-h2-poc

The single ERPJobs POC source of truth for the Obsidian Job Monitoring Automation POC.
This is what Obsidian's live local instance (`localhost:8080`) actually runs its POC
jobs from (`jetty/webapps/ROOT.xml` extraClasspath points here) - not a mirror or a
snapshot, this is the working project.

Not connected to, and never written back to, the real corporate erpjobs repository
(`https://bitbucket.org/erpglobus/erpjobs.git`).

## Layout

- `erp-stubs/` - the actual Java source, real package layout (`com/globus/...`):
  - 11 real, unmodified files copied read-only from the local `erpjobs` checkout
    (`DBConnectionWrapper.java`, `GmDBManager.java`, `GmDynamicProcedureNoParamJob.java`,
    `GmBean.java`, `GmDataStoreVO.java`, `GmDataVO.java`, `GmActionJob.java`, `GmJob.java`,
    `GmDynamicProcedureBean.java`, `GmExceptionBean.java`, `GmLogger.java`)
  - Two documented POC stand-ins (`GmCommonClass.java`, `GmCommonEmailBean.java`) - the
    real files they replace pull in PDF/email/barcode/OCR dependencies unrelated to this
    POC and not all declared in erpjobs' own `pom.xml`
  - Three POC job classes: `GmDynamicProcedureStatusUpdateJob` (working),
    `GmDynamicProcedureBadConfigJob` (deliberate demo failure - misspelled procedure
    name `SP_ERP_JOB_STAUS_UPDATE`)
- `src/poc/` - the JNDI shim (`PocInitialContextFactory`, `PocContext`,
  `PocOracleCompatConnection`) that lets the real `DBConnectionWrapper`/`GmDBManager`
  code above resolve `jdbc/globus_test` against H2 instead of Oracle, plus the H2-side
  procedure bodies (`PocProcedures`) and the original smoke-test harness
- `sql/` - H2 schema/seed/procedure-alias scripts for the POC business database
- `erp-out/`, `out/`, `data/` - build output and the H2 database file, all
  git-ignored (regenerated locally, not committed)

## Building

Compiled with a targeted `javac` (not `mvn`) against `obsidian.jar` + a small set of
Central dependencies (log4j, commons-beanutils, javax.mail, h2) - see the session
history for the exact classpath. A full `mvn` build of the real erpjobs project isn't
attempted here; only the narrow slice these POC job classes actually depend on.
