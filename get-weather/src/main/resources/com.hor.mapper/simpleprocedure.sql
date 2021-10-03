create PROCEDURE simpleprocedure (inval NUMBER)
IS
  tmpvar   NUMBER;
  tmpvar2   NUMBER;
  total     NUMBER;
BEGIN
  tmpvar := 0;
  tmpvar2 := 0;
  total := 0;
  FOR lcv IN 1 .. inval
  LOOP
      total := 2 * total + 1 - tmpvar2;
      tmpvar2 := tmpvar;
      tmpvar := total;
  END LOOP;
  DBMS_OUTPUT.put_line ('TOTAL IS: ' || total);
END simpleprocedure;
/
