java_binary(
    name = "registry",
    srcs = glob(["src/main/java/**/*.java"]),
    main_class = "org.blab.blender.registry.Registry",
    resources = ["src/main/resources/application.properties"],
    deps = [
        "@maven//:org_apache_avro_avro",
        "@maven//:org_postgresql_postgresql",
        "@maven//:org_springframework_boot_spring_boot",
        "@maven//:org_springframework_boot_spring_boot_autoconfigure",
        "@maven//:org_springframework_boot_spring_boot_starter_jdbc",
        "@maven//:org_springframework_boot_spring_boot_starter_web",
        "@maven//:org_springframework_spring_beans",
        "@maven//:org_springframework_spring_context",
        "@maven//:org_springframework_spring_jdbc",
        "@maven//:org_springframework_spring_web",
    ],
)
