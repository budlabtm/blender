java_binary(
    name = "source",
    srcs = glob(["src/main/java/**/*.java"]),
    create_executable = False,
    deps = [
        "//connect/river/common",
        "@maven//:org_eclipse_paho_org_eclipse_paho_mqttv5_client",
    ],
)
