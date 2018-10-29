CREATE OR REPLACE FUNCTION truncate_catalog_tables()
   RETURNS TEXT AS $$
BEGIN

   RAISE INFO 'Current timestamp: %', timeofday()::TIMESTAMP;

   RAISE INFO 'Truncate CATALOG tables...';

   --TRUNCATE TABLE category, attribute, category_attributes_filter RESTART IDENTITY CASCADE;
   TRUNCATE TABLE category, attribute, category_attributes_filter RESTART IDENTITY CASCADE;
   
   RAISE INFO 'Truncate CATALOG tables...Done!';

   RAISE INFO 'Woke up. Current timestamp: %', timeofday()::TIMESTAMP;

   RETURN 'Completed Successfully';

END; $$
 
LANGUAGE plpgsql STRICT;