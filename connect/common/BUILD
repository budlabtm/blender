java_library(
    name = "common",
    srcs = glob(["src/main/java/**/*.java"]),
    visibility = ["//connect:__subpackages__"],
    deps = [
        "//connect:api",
        "@maven//:org_apache_avro_avro",
    ],
)

java_test(
    name = "BufferTest",
    srcs = ["src/test/java/org/blab/blender/connect/common/BufferTest.java"],
    deps = [
        ":common",
        "@maven//:junit_junit",
    ],
)
