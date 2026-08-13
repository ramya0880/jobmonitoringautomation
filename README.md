# erpjobs-poc-repo

Isolated, self-contained POC slice for the Obsidian Job Monitoring Automation POC
(`obsidian-automate`). This is **not** the real erpjobs codebase and is not connected to it in
any way — it exists only so that `obsidian-automate`'s automated RCA/code-fix pipeline has a safe
place to read, modify, build, commit, and open pull requests, without any risk of touching the real
corporate erpjobs repository (`https://bitbucket.org/erpglobus/erpjobs.git`).

## What's here

- 11 real, unmodified files copied read-only from the local `erpjobs` checkout (same package paths):
  `DBConnectionWrapper.java`, `GmDBManager.java`, `GmDynamicProcedureNoParamJob.java`, `GmBean.java`,
  `GmDataStoreVO.java`, `GmDataVO.java`, `GmActionJob.java`, `GmJob.java`,
  `GmDynamicProcedureBean.java`, `GmExceptionBean.java`, `GmLogger.java`
- Two documented POC stand-ins (`GmCommonClass.java`, `GmCommonEmailBean.java`) - the real files they
  replace pull in unrelated PDF/email/barcode/OCR dependencies not needed for this POC and not all
  declared in erpjobs' own `pom.xml`; these contain only the specific methods the files above
  actually call, with bodies copied verbatim from the real source where applicable
- `GmDynamicProcedureStatusUpdateJob.java` - a genuinely new job class (POC #2), dedicated to one
  fixed H2 procedure
- `GmDynamicProcedureBadConfigJob.java` - a **deliberate demo-failure job** (POC #3), used to seed a
  realistic, diagnosable failure for the automation pipeline to detect and fix

## What this is not

Not a mirror of the full erpjobs project (~300 job classes) - only the narrow slice this POC's job
classes actually depend on. Not connected to erpjobs' Bitbucket history in any way - this is a fresh,
independent git history.
