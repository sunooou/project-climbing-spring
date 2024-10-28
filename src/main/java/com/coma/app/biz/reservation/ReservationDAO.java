package com.coma.app.biz.reservation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationDAO {
	//예약 등록 RESERVATION_GYM_NUM, RESERVATION_DATE, RESERVATION_MEMBER_ID, RESERVATION_PRICE
	private final String INSERT = "INSERT INTO RESERVATION (RESERVATION_GYM_NUM, RESERVATION_DATE, RESERVATION_MEMBER_ID, RESERVATION_PRICE) " 
			+ "VALUES (?, ?, ?, ?);";
	
	//PK로 예약 정보 찾기 RESERVATION_NUM
	private final String ONE = "SELECT " 
			+ "    RESERVATION_NUM, " 
			+ "    RESERVATION_DATE, " 
			+ "    RESERVATION_GYM_NUM, " 
			+ "    RESERVATION_MEMBER_ID, " 
			+ "    RESERVATION_PRICE " 
			+ "FROM " 
			+ "    RESERVATION " 
			+ "WHERE " 
			+ "    RESERVATION_NUM = ?;";

	
	//해당 암벽장 예약 가능 개수 RESERVATION_GYM_NUM
	private final String ONE_COUNT = "SELECT G.GYM_RESERVATION_CNT - COUNT(*) AS RESERVATION_TOTAL "
			+ "FROM GYM G " 
			+ "LEFT JOIN RESERVATION R ON R.RESERVATION_GYM_NUM = G.GYM_NUM AND R.RESERVATION_DATE = ? " 
			+ "WHERE G.GYM_NUM = ? " 
			+ "GROUP BY G.GYM_RESERVATION_CNT;";

	
	//예약 취소 RESERVATION_NUM
	private final String DELETE = "DELETE FROM RESERVATION WHERE RESERVATION_NUM = ?;";

	
	//사용자 아이디로 예약한 내역 전부 출력 RESERVATION_MEMBER_ID
	private final String ALL = "SELECT\r\n"
			+ "	R.RESERVATION_NUM,\r\n"
			+ "	R.RESERVATION_DATE,\r\n"
			+ "	R.RESERVATION_GYM_NUM,\r\n"
			+ "	R.RESERVATION_MEMBER_ID,\r\n"
			+ "	R.RESERVATION_PRICE,\r\n"
			+ "	G.GYM_NAME\r\n"
			+ "FROM\r\n"
			+ "	RESERVATION R\r\n"
			+ "JOIN\r\n"
			+ "	MEMBER M\r\n"
			+ "ON \r\n"
			+ "	R.RESERVATION_MEMBER_ID = M.MEMBER_ID\r\n"
			+ "JOIN \r\n"
			+ "    GYM G\r\n"
			+ "ON \r\n"
			+ "    R.RESERVATION_GYM_NUM = G.GYM_NUM\r\n"
			+ "WHERE\r\n"
			+ "	R.RESERVATION_MEMBER_ID = ?";
	
	//사용자 아이디로 해당 암벽장에 예약한 날짜 중복 확인 RESERVATION_MEMBER_ID, RESERVATION_GYM_NUM, RESERVATION_DATE
	private final String ONE_RESERVATION = "SELECT\r\n"
			+ "	R.RESERVATION_NUM,\r\n"
			+ "	R.RESERVATION_DATE,\r\n"
			+ "	R.RESERVATION_GYM_NUM,\r\n"
			+ "	R.RESERVATION_MEMBER_ID,\r\n"
			+ "	R.RESERVATION_PRICE\r\n"
			+ "FROM\r\n"
			+ "	RESERVATION R\r\n"
			+ "JOIN\r\n"
			+ "	MEMBER M\r\n"
			+ "ON \r\n"
			+ "	R.RESERVATION_MEMBER_ID = M.MEMBER_ID\r\n"
			+ "WHERE\r\n"
			+ "	R.RESERVATION_MEMBER_ID = ? AND R.RESERVATION_GYM_NUM = ? AND R.RESERVATION_DATE = ?";

	//최근 1년 예약 개수 출력 // TODO 관리자 메인 페이지
	private final String ONE_COUNT_YEAR_ADMIN = "SELECT COUNT(*) AS RESERVATION_TOTAL\n"
			+ "FROM RESERVATION\n"
			+ "WHERE RESERVATION_DATE >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";

	//월별 예약수 출력 // TODO 관리자 메인 페이지
	private final String ALL_COUNT_MONTH_ADMIN = "SELECT \n"
			+ "    DATE_FORMAT(RESERVATION_DATE, '%Y-%m') AS RESERVATION_MONTH,\n"
			+ "    COUNT(*) AS RESERVATION_TOTAL\n"
			+ "FROM \n"
			+ "    RESERVATION\n"
			+ "GROUP BY \n"
			+ "    RESERVATION_MONTH\n"
			+ "ORDER BY \n"
			+ "    RESERVATION_MONTH";

	//예약 전체 출력(페이지네이션) // TODO 예약 관리 페이지
	private final String ALL_ADMIN = "SELECT R.RESERVATION_MEMBER_ID, G.GYM_NAME, R.RESERVATION_PRICE, R.RESERVATION_DATE\n"
			+ "FROM RESERVATION R\n"
			+ "JOIN GYM G ON R.RESERVATION_GYM_NUM = G.GYM_NUM"
			+ "ORDER BY R.RESERVATION_DATE DESC\n"
			+ "LIMIT ? , ?";

	//암벽장 이름으로 검색한 예약 전체 출력(페이지네이션) // TODO 예약 관리 페이지
	private final String ALL_ADMIN_SEARCH_GYM_NAME = "SELECT \n"
			+ "    R.RESERVATION_MEMBER_ID, \n"
			+ "    G.GYM_NAME, \n"
			+ "    R.RESERVATION_PRICE, \n"
			+ "    R.RESERVATION_DATE\n"
			+ "FROM \n"
			+ "    RESERVATION R\n"
			+ "JOIN \n"
			+ "    GYM G ON R.RESERVATION_GYM_NUM = G.GYM_NUM\n"
			+ "WHERE \n"
			+ "    G.GYM_NAME LIKE CONCAT('%', ?, '%')\n"
			+ "ORDER BY \n"
			+ "    R.RESERVATION_DATE DESC\n"
			+ "LIMIT ?, ?";

	//예약자 이름으로 검색한 예약 전체 출력(페이지네이션) // TODO 예약 관리 페이지
	private final String ALL_ADMIN_SEARCH_MEMBER_ID = "SELECT \n"
			+ "    R.RESERVATION_MEMBER_ID, \n"
			+ "    G.GYM_NAME, \n"
			+ "    R.RESERVATION_PRICE, \n"
			+ "    R.RESERVATION_DATE\n"
			+ "FROM \n"
			+ "    RESERVATION R\n"
			+ "JOIN \n"
			+ "    GYM G ON R.RESERVATION_GYM_NUM = G.GYM_NUM\n"
			+ "WHERE \n"
			+ "    R.RESERVATION_MEMBER_ID = ?\n"
			+ "ORDER BY \n"
			+ "    R.RESERVATION_DATE DESC\n"
			+ "LIMIT ?, ?";

	@Autowired
	private JdbcTemplate jdbcTemplate; // 스프링부트 내장객체

	public boolean insert(ReservationDTO reservationDTO) {
		//예약 등록 RESERVATION_GYM_NUM, RESERVATION_DATE, RESERVATION_MEMBER_ID, RESERVATION_PRICE
		int result=jdbcTemplate.update(INSERT,reservationDTO.getReservation_gym_num(), reservationDTO.getReservation_date(), reservationDTO.getReservation_member_id(), reservationDTO.getReservation_price());
		if(result<=0) {
			return false;
		}
		return true;
	}

	public boolean update(ReservationDTO reservationDTO) { // TODO 여기없는 CRUD
		return false;
	}

	public boolean delete(ReservationDTO reservationDTO) {
		//예약 취소 RESERVATION_NUM
		int result=jdbcTemplate.update(DELETE, reservationDTO.getReservation_num());
		if(result<=0) {
			return false;
		}
		return true;
	}

	public ReservationDTO selectOne(ReservationDTO reservationDTO){
		ReservationDTO data=null;
		Object[] args={reservationDTO.getReservation_num()};
		try {
			//PK로 예약 정보 찾기 RESERVATION_NUM
			data=jdbcTemplate.queryForObject(ONE, args, new ReservationSelectRowMapperOne());
		}
		catch (Exception e) {
		}
		return data;
	}

	public ReservationDTO selectOneCount(ReservationDTO reservationDTO){
		ReservationDTO data=null;
		Object[] args={reservationDTO.getReservation_gym_num(), reservationDTO.getReservation_date()};
		try {
			//해당 암벽장 예약 가능 개수 RESERVATION_GYM_NUM
			data= jdbcTemplate.queryForObject(ONE_COUNT, args, new ReservationCountRowMapperOne());
		}
		catch (Exception e) {
		}
		return data;
	}

	public ReservationDTO selectOneReservation(ReservationDTO reservationDTO){
		ReservationDTO data=null;
		Object[] args={reservationDTO.getReservation_member_id(), reservationDTO.getReservation_gym_num(), reservationDTO.getReservation_date()};
		try {
			//사용자 아이디로 해당 암벽장에 예약한 날짜 중복 확인 RESERVATION_MEMBER_ID, RESERVATION_GYM_NUM, RESERVATION_DATE
			data= jdbcTemplate.queryForObject(ONE_RESERVATION, args, new ReservationSelectRowMapperOne());
		}
		catch (Exception e) {
		}
		return data;
	}

	public ReservationDTO selectOneCountYearAdmin(ReservationDTO reservationDTO){
		ReservationDTO data=null;
		try {
			//최근 1년 예약 개수 출력 // TODO 관리자 메인 페이지
			data= jdbcTemplate.queryForObject(ONE_COUNT_YEAR_ADMIN, new ReservationCountRowMapperOne());
		}
		catch (Exception e) {
		}
		return data;
	}

	public List<ReservationDTO> selectAll(ReservationDTO reservationDTO){
		List<ReservationDTO> datas=null;
		Object[] args={reservationDTO.getReservation_member_id()};
		try {
			//사용자 아이디로 예약한 내역 전부 출력 RESERVATION_MEMBER_ID
			datas= jdbcTemplate.query(ALL, args, new ReservationRowMapperAll());
		}
		catch (Exception e) {
		}
		return datas;
	}

	public List<ReservationDTO> selectAllCountMonthAdmin(ReservationDTO reservationDTO){
		List<ReservationDTO> datas=null;
		try {
			//월별 예약수 출력 // TODO 관리자 메인 페이지
			datas= jdbcTemplate.query(ALL_COUNT_MONTH_ADMIN, new ReservationCountMonthRowMapperAll());
		}
		catch (Exception e) {
		}
		return datas;
	}

	public List<ReservationDTO> selectAllAdmin(ReservationDTO reservationDTO){
		List<ReservationDTO> datas=null;
		Object[] args={reservationDTO.getReservation_min_num(),6};
		try {
			//예약 전체 출력(페이지네이션) // TODO 예약 관리 페이지
			datas= jdbcTemplate.query(ALL_ADMIN, args, new ReservationAdminRowMapperAll());
		}
		catch (Exception e) {
		}
		return datas;
	}

	public List<ReservationDTO> selectAllAdminSearchGymName(ReservationDTO reservationDTO){
		List<ReservationDTO> datas=null;
		Object[] args={reservationDTO.getSearch_content(),reservationDTO.getReservation_min_num(),6};
		try {
			//암벽장 이름으로 검색한 예약 전체 출력(페이지네이션) // TODO 예약 관리 페이지
			datas= jdbcTemplate.query(ALL_ADMIN_SEARCH_GYM_NAME, args, new ReservationAdminRowMapperAll());
		}
		catch (Exception e) {
		}
		return datas;
	}

	public List<ReservationDTO> selectAllAdminSearchMemberId(ReservationDTO reservationDTO){
		List<ReservationDTO> datas=null;
		Object[] args={reservationDTO.getSearch_content(),reservationDTO.getReservation_min_num(),6};
		try {
			//예약자 이름으로 검색한 예약 전체 출력(페이지네이션) // TODO 예약 관리 페이지
			datas= jdbcTemplate.query(ALL_ADMIN_SEARCH_MEMBER_ID, args, new ReservationAdminRowMapperAll());
		}
		catch (Exception e) {
		}
		return datas;
	}

}

class ReservationCountRowMapperOne implements RowMapper<ReservationDTO> {

	public ReservationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReservationDTO reservationDTO=new ReservationDTO();
		System.out.print("DB에서 가져온 데이터 {");
		reservationDTO.setTotal(rs.getInt("RESERVATION_TOTAL"));
		System.err.print("reservation_total = ["+reservationDTO.getTotal()+"]");
		System.out.println("}");
		return reservationDTO;
	};
}

class ReservationCountMonthRowMapperAll implements RowMapper<ReservationDTO> {

	public ReservationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReservationDTO reservationDTO=new ReservationDTO();
		System.out.print("DB에서 가져온 데이터 {");
		reservationDTO.setReservation_month(rs.getString("RESERVATION_MONTH"));
		System.err.println("reservation_month = ["+reservationDTO.getReservation_month()+"]");
		reservationDTO.setTotal(rs.getInt("RESERVATION_TOTAL"));
		System.err.print("reservation_total = ["+reservationDTO.getTotal()+"]");
		System.out.println("}");
		return reservationDTO;
	};
}

class ReservationSelectRowMapperOne implements RowMapper<ReservationDTO> {

	public ReservationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReservationDTO reservationDTO=new ReservationDTO();
		System.out.print("DB에서 가져온 데이터 {");
		reservationDTO.setReservation_num(rs.getInt("RESERVATION_NUM"));
		System.err.println("reservation_num = ["+reservationDTO.getReservation_num()+"]");
		reservationDTO.setReservation_date(rs.getString("RESERVATION_DATE"));
		System.err.println("reservation_date = ["+reservationDTO.getReservation_date()+"]");
		reservationDTO.setReservation_gym_num(rs.getInt("RESERVATION_GYM_NUM"));
		System.err.println("reservation_gym_num = ["+reservationDTO.getReservation_gym_num()+"]");
		reservationDTO.setReservation_member_id(rs.getString("RESERVATION_MEMBER_ID"));
		System.err.println("reservation_member_id = ["+reservationDTO.getReservation_member_id()+"]");
		reservationDTO.setReservation_price(rs.getInt("RESERVATION_PRICE"));
		System.err.print("reservation_price = ["+reservationDTO.getReservation_price()+"]");
		System.out.println("}");
		return reservationDTO;
	};
}

class ReservationRowMapperAll implements RowMapper<ReservationDTO> {

	public ReservationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReservationDTO reservationDTO=new ReservationDTO();
		System.out.print("DB에서 가져온 데이터 {");
		reservationDTO.setReservation_num(rs.getInt("RESERVATION_NUM"));
		System.err.println("reservation_num = ["+reservationDTO.getReservation_num()+"]");
		reservationDTO.setReservation_date(rs.getString("RESERVATION_DATE"));
		System.err.println("reservation_date = ["+reservationDTO.getReservation_date()+"]");
		reservationDTO.setReservation_gym_num(rs.getInt("RESERVATION_GYM_NUM"));
		System.err.println("reservation_gym_num = ["+reservationDTO.getReservation_gym_num()+"]");
		reservationDTO.setReservation_member_id(rs.getString("RESERVATION_MEMBER_ID"));
		System.err.println("reservation_member_id = ["+reservationDTO.getReservation_member_id()+"]");
		reservationDTO.setReservation_price(rs.getInt("RESERVATION_PRICE"));
		System.err.println("reservation_price = ["+reservationDTO.getReservation_price()+"]");
		reservationDTO.setReservation_gym_name(rs.getString("GYM_NAME"));
		System.err.println("reservation_name = ["+reservationDTO.getReservation_gym_name()+"]");
		System.out.println("}");
		return reservationDTO;
	};
}

class ReservationAdminRowMapperAll implements RowMapper<ReservationDTO> {

	public ReservationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReservationDTO reservationDTO=new ReservationDTO();
		System.out.print("DB에서 가져온 데이터 {");
		reservationDTO.setReservation_member_id(rs.getString("RESERVATION_MEMBER_ID"));
		System.err.println("reservation_member_id = ["+reservationDTO.getReservation_member_id()+"]");
		reservationDTO.setReservation_gym_name(rs.getString("GYM_NAME"));
		System.err.println("reservation_name = ["+reservationDTO.getReservation_gym_name()+"]");
		reservationDTO.setReservation_price(rs.getInt("RESERVATION_PRICE"));
		System.err.println("reservation_price = ["+reservationDTO.getReservation_price()+"]");
		reservationDTO.setReservation_date(rs.getString("RESERVATION_DATE"));
		System.err.print("reservation_date = ["+reservationDTO.getReservation_date()+"]");
		System.out.println("}");
		return reservationDTO;
	};
}
