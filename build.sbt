ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "2.13.13"

lazy val defaultName = "address-lookup-lambda-functions"
lazy val name = getNameFromEnvironment(defaultName)

lazy val alsWelshNamesLambdaFunction = Project(name, file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-java-sdk-s3" % "1.12.669",
      "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
      "commons-io" % "commons-io" % "2.15.1",
      "org.apache.httpcomponents" % "httpclient" % "4.5.14",
      "org.seleniumhq.selenium" % "htmlunit-driver" % "4.13.0",
      "org.seleniumhq.selenium" % "selenium-support" % "4.16.1",
      "org.mockito" % "mockito-core" % "5.11.0" % "test",
      "org.scalatest" %% "scalatest" % "3.2.17" % "test"
    ),
    assembly / assemblyJarName := getJarNameFromEnvironment(s"${name}_${convertFullVersionToMajorMinor(scalaVersion.value)}-${version.value}"),
    assembly / assemblyMergeStrategy := {
      case PathList(ps @ _*) if ps.last == "module-info.class" => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  )

def convertFullVersionToMajorMinor(version: String): String =
  version.split("\\.") match {
    case Array(major, minor, _) => s"$major.$minor"
    case _ => version
  }

def getJarNameFromEnvironment(default: String): String = s"${sys.env.getOrElse("ARTEFACT", default)}.jar"
def getNameFromEnvironment(default: String): String = s"${sys.env.getOrElse("NAME", default)}"
