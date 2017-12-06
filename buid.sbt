lazy val commonSettings = Seq(
  version := "0.0.1",
  resolvers ++= Seq(
      Resolver.mavenLocal
    , Resolver.sonatypeRepo("releases")
    , Resolver.sonatypeRepo("snapshots")
    , "Bintray " at "https://dl.bintray.com/projectseptemberinc/maven"
  ),
  scalaVersion := "2.12.4",
  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
  addCompilerPlugin("org.spire-math" %% "kind-projector"  % "0.8.0"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"),

  libraryDependencies ++= Seq(
    "org.typelevel"                %% "cats-core"                     % "1.0.0-RC1" withSources() withJavadoc(),
    "com.github.mpilquist"         %% "simulacrum"                    % "0.11.0",
    "joda-time"                     % "joda-time"                     % "2.9.1",
    "org.joda"                      % "joda-convert"                  % "1.8.1",
    "io.spray"                     %% "spray-json"                    % "1.3.2",
    "com.typesafe.akka"            %% "akka-actor"                    % "2.5.7",
    "com.typesafe.akka"            %% "akka-persistence"              % "2.5.7",
    "com.typesafe.akka"            %% "akka-stream"                   % "2.5.7",
    "com.typesafe.scala-logging"   %% "scala-logging"                 % "3.7.2",
    "com.typesafe.slick"           %% "slick"                         % "3.2.1",
    "com.h2database"                % "h2"                            % "1.4.196",
//    "com.zaxxer"                    % "HikariCP-java6"                % "2.7.4",
    "ch.qos.logback"                % "logback-classic"               % "1.2.3",
    "org.scalacheck"               %% "scalacheck"                    % "1.13.5"       % "test"
    )
)

ensimeIgnoreScalaMismatch in ThisBuild := true
transitiveClassifiers := Seq("sources", "javadoc") //  sbt updateSbtClassifiers

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "frdomain_cats",
    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked",
      "-language:higherKinds",
      "-language:postfixOps",
      "-deprecation",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-language:existentials",
      "-language:experimental.macros",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-Ypartial-unification",
          // "-Xlint:-missing-interpolator,_",
          // "-Yno-adapted-args",
          //  "-Ywarn-dead-code",
          // "-Xfatal-warnings",
      "-Xfuture"
    )
  )
