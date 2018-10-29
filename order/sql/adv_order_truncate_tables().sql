CREATE OR REPLACE FUNCTION truncate_order_tables()
   RETURNS TEXT AS $$
BEGIN

   RAISE INFO 'Current timestamp: %', timeofday()::TIMESTAMP;

   RAISE INFO 'Truncate ORDER tables...';

   --TRUNCATE TABLE category, attribute, category_attributes_filter RESTART IDENTITY CASCADE;
   TRUNCATE TABLE order_header, order_lines,shopping_cart RESTART IDENTITY CASCADE;

   RAISE INFO 'Truncate ORDER tables...Done!';

   RAISE INFO 'Woke up. Current timestamp: %', timeofday()::TIMESTAMP;

   RETURN 'Completed Successfully';

END; $$

LANGUAGE plpgsql STRICT;