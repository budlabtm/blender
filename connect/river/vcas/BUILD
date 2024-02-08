java_binary(
    name = "source",
    srcs = glob(["src/main/java/**/*.java"]),
    create_executable = False,
    resources = glob(["src/main/resources/META-INF/**"]),
    deps = [
        "//connect/river/common",
        "@maven//:org_slf4j_slf4j_api",
    ],
)
