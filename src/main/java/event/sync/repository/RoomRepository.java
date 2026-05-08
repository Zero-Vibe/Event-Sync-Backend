package event.sync.repository;

import event.sync.datasource.DataSourceConfig;
import event.sync.dto.room.RoomRequest;
import event.sync.dto.room.RoomResponse;
import event.sync.model.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class RoomRepository {

    private final DataSourceConfig dataSource;

    public RoomRepository(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
    }


    private final String GET_ALL_ROOMS = "SELECT id, name, created_at, updated_at FROM rooms ";

    public Optional<List<RoomResponse>> getAllRooms(){
        Connection conn = dataSource.getConnection();

        List<RoomResponse> rooms = new ArrayList<RoomResponse>();

        try (PreparedStatement stmt = conn.prepareStatement(GET_ALL_ROOMS)){
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                rooms.add(RoomResponseRowMapper(rs));
            }

        } catch ( SQLException e) {
            throw new RuntimeException(e);
        }finally {
            dataSource.closeConnection(conn);
        }

        return rooms.isEmpty() ? Optional.empty() : Optional.of(rooms);
    }

    private final String CREATE_ROOM = "INSERT INTO rooms (id, name, created_at, updated_at) VALUES (?::uuid, ?, ?, ?)";

    public Optional<RoomResponse> createRoom(RoomRequest roomRequest){
        Connection conn = dataSource.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(CREATE_ROOM)){
            String uuid = UUID.randomUUID().toString();
            ps.setString(1, uuid);
            ps.setString(2, roomRequest.getName());
            ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));

            ps.executeUpdate();

            return Optional.ofNullable(RoomResponse.builder()
                    .id(UUID.fromString(uuid)).name(roomRequest.getName()).build());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            dataSource.closeConnection(conn);
        }
    }



    private RoomResponse RoomResponseRowMapper(ResultSet rs) throws SQLException {
        return RoomResponse.builder()
                .id(rs.getObject("id", UUID.class))
                .name(rs.getString("name"))
                .build();

    }

    private Room RoomRowMapper(ResultSet rs) throws SQLException {
        return Room.builder()
                .id(rs.getObject("id", UUID.class))
                .name(rs.getString("name"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }
}
