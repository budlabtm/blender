java_binary(
    name = "source",
    srcs = glob(["src/main/java/**/*.java"]),
    create_executable = False,
    resources = glob(["src/main/resources/META-INF/**"]),
    deps = [
        "//connect/river/common",
        "@maven//:org_eclipse_paho_org_eclipse_paho_mqttv5_client",
        "@maven//:org_slf4j_slf4j_api",
    ],
)
