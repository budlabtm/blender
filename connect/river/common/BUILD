java_library(
    name = "common",
    srcs = glob(["src/main/java/**/*.java"]),
    visibility = ["//connect/river:__subpackages__"],
    deps = [
        "//connect",
        "@maven//:org_apache_avro_avro",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

java_test(
    name = "BufferTest",
    srcs = ["src/test/java/org/blab/blender/connect/river/BufferTest.java"],
    deps = [
        ":common",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "EventTest",
    srcs = ["src/test/java/org/blab/blender/connect/river/EventTest.java"],
    deps = [
        ":common",
        "@maven//:junit_junit",
    ],
)
