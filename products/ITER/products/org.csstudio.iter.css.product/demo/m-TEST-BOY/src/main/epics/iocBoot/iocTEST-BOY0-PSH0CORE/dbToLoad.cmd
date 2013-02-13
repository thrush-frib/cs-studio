#======================================================================
# Loading DBs
#======================================================================
cd $(TOP)/db
dbLoadRecords("PSH0-TEST-BOY0.db")





#======================================================================
# IOC Monitor
#======================================================================
cd $(EPICS_MODULES)/iocmon/db
dbLoadRecords("iocmon.db","CBS1=TEST, CBS2=BOY0, CTRLTYPE=H, IDX=0, IOCTYPE=CORE")