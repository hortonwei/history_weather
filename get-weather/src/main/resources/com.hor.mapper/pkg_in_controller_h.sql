create or replace package pkg_in_controller is

procedure get_max_temp_by_city_name(v_city_name in varchar2, r_res out number);

procedure get_weather_by_stamp(v_stamp in varchar2, r_res out varchar2);


end pkg_in_controller;





