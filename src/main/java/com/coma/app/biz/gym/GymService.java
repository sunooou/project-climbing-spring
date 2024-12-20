package com.coma.app.biz.gym;

import java.util.List;


public interface GymService {
	List<GymDTO> selectAll(GymDTO gymDTO);
	List<GymDTO> selectAllLocationCountAdmin(GymDTO gymDTO);
	List<GymDTO> selectAllAdmin(GymDTO gymDTO);
	List<GymDTO> selectAllAdminVerified(GymDTO gymDTO);
	List<GymDTO> selectAllAdminSearch(GymDTO gymDTO);

	GymDTO selectOne(GymDTO gymDTO);
	GymDTO selectOneCount(GymDTO gymDTO);
	GymDTO selectOneAdminVerifiedCount(GymDTO gymDTO);
	GymDTO selectOneAdminSearchCount(GymDTO gymDTO);

	boolean insert(GymDTO gymDTO);
	boolean insertAdmin(GymDTO gymDTO);

	boolean update(GymDTO gymDTO);
	boolean updateAdminBattleVerified(GymDTO gymDTO);

	boolean delete(GymDTO gymDTO);
}
