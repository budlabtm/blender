package org.blab.blender.registry;

import org.apache.avro.Schema;
import org.apache.avro.SchemaParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schemas")
public class SchemaController {
  private final SchemaRepository schemaRepository;

  @Autowired
  public SchemaController(SchemaRepository schemaRepository) {
    this.schemaRepository = schemaRepository;
  }

  @GetMapping("/{name}")
  public ResponseEntity<Object> get(@PathVariable(name = "name") String name) {
    try {
      Record record = schemaRepository.findByName(name);

      if (record != null) return ResponseEntity.status(200).body(record.schema());
      else return ResponseEntity.status(404).body(new Error("Schema not found."));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(new Error(e.getMessage()));
    }
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody String s) {
    try {
      Schema schema;

      try {
        schema = new Schema.Parser().parse(s);
      } catch (SchemaParseException e) {
        return ResponseEntity.status(400)
            .body(new Error("The schema does not meet Avro specifications."));
      }

      schemaRepository.save(new Record(schema.getFullName(), schema.toString()));
      return ResponseEntity.status(201).body(schema.toString());
    } catch (Exception e) {
      return ResponseEntity.status(500).body(new Error(e.getMessage()));
    }
  }
}
