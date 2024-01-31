java_binary(
    name = "vcas",
    srcs = glob(["src/main/java/**/*.java"]),
    create_executable = False,
    resources = glob(
        ["src/main/resources/**"],
        exclude = ["src/main/resources/default.properties"],
    ),
    deps = [
        "//connect:api",
        "//connect/common",
        "@maven//:org_apache_avro_avro",
    ],
)
