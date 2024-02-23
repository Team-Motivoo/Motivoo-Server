package sopt.org.motivoo.domain.mission.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.mission.entity.CompletedStatus;
import sopt.org.motivoo.domain.mission.entity.Mission;
import sopt.org.motivoo.domain.user.entity.User;

@Repository
@RequiredArgsConstructor
public class UserMissionChoicesJdbcRepository {

	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public void bulkChoicesSave(List<User> users, LocalDate date, Mission mission) {
		batchChoicesInsert(users.size(), users, date, mission);
	}

	private void batchChoicesInsert(int batchSize, List<User> users, LocalDate date, Mission mission) {
		String sql = "INSERT INTO user_mission_choices (completed_status, created_at, updated_at, mission_id, user_id) values (?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, CompletedStatus.NONE.toString());
					ps.setDate(2, Date.valueOf(date));
					ps.setDate(3, Date.valueOf(date));
					ps.setLong(4, mission.getId());
					ps.setLong(5, users.get(i).getId());
				}

				@Override
				public int getBatchSize() {
					return users.size();
				}
			});
	}
}
