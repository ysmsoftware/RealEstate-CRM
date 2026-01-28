---------------------------------------------------------------------------------------------------
 Copy Database
---------------------------------------------------------------------------------------------------

## FROM Machine

pg_dump -h postgres -U postgres -F c -d realestate -f realestate.dump

## To Machine

postgres=# DROP DATABASE realestate;
postgres=# CREATE DATABASE realestate;
postgres=# exit
@Nyazul ➜ /workspace (austin) $ pg_restore -h postgres -U postgres -d realestate realestate.dump 

