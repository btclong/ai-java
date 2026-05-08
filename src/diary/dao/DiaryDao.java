package diary.dao;

import diary.db.DBUtil;
import diary.dto.DiaryDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DiaryDao {
    private static final String INSERT_SQL = "INSERT INTO diary (title, content, weather) VALUES (?, ?, ?)";
    private static final String FIND_ALL_SQL = "SELECT id, title, content, weather, created_date FROM diary ORDER BY id DESC";
    private static final String FIND_BY_ID_SQL = "SELECT id, title, content, weather, created_date FROM diary WHERE id = ?";
    private static final String UPDATE_SQL = "UPDATE diary SET title = ?, content = ?, weather = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM diary WHERE id = ?";


    public boolean insert(DiaryDto diary) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {

            pstmt.setString(1, diary.getTitle());
            pstmt.setString(2, diary.getContent());
            pstmt.setString(3, diary.getWeather());
            return pstmt.executeUpdate() == 1;
        }
    }

    public List<DiaryDto> findAll() throws SQLException {
        List<DiaryDto> diaryList = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                diaryList.add(toDiaryDto(rs));
            }
        }

        return diaryList;
    }

    public DiaryDto findById(int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toDiaryDto(rs);
                }
            }
        }

        return null;
    }

    public boolean update(DiaryDto diary) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

            pstmt.setString(1, diary.getTitle());
            pstmt.setString(2, diary.getContent());
            pstmt.setString(3, diary.getWeather());
            pstmt.setInt(4, diary.getId());
            return pstmt.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() == 1;
        }
    }

    private DiaryDto toDiaryDto(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("created_date");

        return new DiaryDto(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("weather"),
                timestamp == null ? null : timestamp.toLocalDateTime()
        );
    }
}
