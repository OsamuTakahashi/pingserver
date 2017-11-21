import sbt.Keys._

val projectScalaVersion = "2.11.8"

resolvers in Global += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "OSS Sonatype" at "https://repo1.maven.org/maven2/"

val akkaVersion = "2.5.6"

val akkaLibraries = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  //"com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "com.google.protobuf" % "protobuf-java" % "2.4.1" % Runtime
)

val kamonVersion = "0.6.7"

val kamonLibraries = Seq(
  "io.kamon" %% "kamon-core" % kamonVersion,
  "io.kamon" %% "kamon-scala" % kamonVersion,
  "io.kamon" %% "kamon-annotation" % kamonVersion,
  "io.kamon" %% "kamon-system-metrics" % kamonVersion,
  //"io.kamon" %% "kamon-jmx" % kamonVersion,
  //"io.kamon" %% "kamon-jdbc" % kamonVersion % Runtime,
  "io.kamon" %% "kamon-akka-2.5" % "0.6.8",
  "io.kamon" %% "kamon-akka-remote-2.4" % kamonVersion,
  "io.kamon" %% "kamon-influxdb" % kamonVersion,
  //"io.kamon" %% "kamon-log-reporter" % kamonVersion,
  "org.aspectj" %  "aspectjweaver" % "1.8.1"
)

val liftVersion = "2.6"

def liftLibraries = Seq(
  "net.liftweb" %% "lift-webkit" % liftVersion,
  "net.liftweb" %% "lift-json" % liftVersion,
  "net.liftweb" %% "lift-common" % liftVersion,
  "net.liftweb" %% "lift-mongodb-record" % liftVersion,
  "net.liftweb" %% "lift-mapper" % liftVersion,
  "net.liftweb" %% "lift-testkit" % liftVersion % "test"
)

val slickVersion = "3.1.1"

def slickLibraries = Seq(
  "com.typesafe.slick" %% "slick" % slickVersion
)
val infinispanVersion = "8.2.5.Final"

def infinispanLibraries = Seq(
  "org.infinispan" % "infinispan-core" % infinispanVersion,
  "org.infinispan" % "infinispan-query" % infinispanVersion,
  "org.infinispan" % "infinispan-query-dsl" % infinispanVersion,
  "org.infinispan" % "infinispan-cachestore-remote" % infinispanVersion
)

val mysqlConnectorVersion = "6.0.5"

def mysqlConnectorLibraries = Seq(
  "mysql" % "mysql-connector-java" % mysqlConnectorVersion
)

val jodaTimeLibraries = Seq(
  "joda-time" % "joda-time" % "2.9.6",
  "org.joda" % "joda-convert" % "1.8"
)

val loggingLibraries = Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.7"
)

val testLibraries = Seq(
  "org.specs2" %% "specs2-core" % "3.6.2" % "test",
  "org.specs2" %% "specs2-mock" % "3.6.2" % "test"
)

resolvers += "RustyRaven" at "http://rustyraven.github.io"

val codebookLibraries = Seq(
  "com.rusty-raven" %% "codebook-runtime" % "1.3.6-SNAPSHOT"
)

aspectjSettings

javaOptions <++= AspectjKeys.weaverOptions in Aspectj

fork in run := true

lazy val root = (project in file("."))
  .enablePlugins(JavaServerAppPackaging)
  .settings(
    scalaVersion := projectScalaVersion,
    name := "pingserver",
    version := "0.1.2",
    rpmVendor := "sopranoworks",
    rpmLicense := Some("MIT"),
    rpmGroup := Some("Server"),
    bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/application.conf"""",
    libraryDependencies ++= akkaLibraries ++
      kamonLibraries ++
      //liftLibraries ++
      //slickLibraries ++
      //infinispanLibraries ++
      //mysqlConnectorLibraries ++
      //jodaTimeLibraries ++
      loggingLibraries ++
      testLibraries ++
      codebookLibraries
  )
