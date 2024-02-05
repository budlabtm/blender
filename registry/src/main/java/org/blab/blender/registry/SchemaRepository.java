package org.blab.blender.registry;

public interface SchemaRepository {
  String findById(String id);
  void save(String id, String schema);
}
