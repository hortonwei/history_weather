create or replace package body pkg_in_controller is

procedure get_max_temp_by_city_name(v_city_name in varchar2, r_res out number) is

begin
  if v_city_name is not null then
    select max(max_temp)
    into r_res
    from weather
    where city_name = v_city_name;

    insert into PKG_LOG(p_in, text, num, stamp) values (v_city_name, '-', r_res, sysdate);

  end if;
end get_max_temp_by_city_name;

procedure get_weather_by_stamp(v_stamp in varchar2, r_res out varchar2) is
  begin
    select WEATHER_ABBR
    into r_res
    from WEATHER
    where stamp = v_stamp;
    insert into PKG_LOG(p_in, text, num, stamp) values (v_stamp, r_res, -1, sysdate);
  end get_weather_by_stamp;
end pkg_in_controller;
