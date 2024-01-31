package org.blab.blender.registry;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SchemaRepository {
  private final JdbcTemplate template;

  @Autowired
  public SchemaRepository(JdbcTemplate template) {
    this.template = template;
  }

  Record findByName(String name) {
    String query = "SELECT * FROM SCHEMA WHERE NAME = ?";
    return template.queryForObject(query, new RecordMapper(), name);
  }

  void save(Record record) {
    String update = "UPDATE SCHEMA SET SCHEMA=? WHERE NAME=?";
    String create = "INSERT INTO SCHEMA (NAME, SCHEMA) VALUES (?, ?)";
    template.update(update, record.schema(), record.name());
    template.update(create, record.name(), record.schema());
  }

  static class RecordMapper implements RowMapper<Record> {
    @Override
    public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
      String name = rs.getString("NAME");
      String schema = rs.getString("SCHEMA");
      return name == null || schema == null ? null : new Record(name, schema);
    }
  }
}
