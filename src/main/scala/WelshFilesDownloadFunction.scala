import Retriever.Use
import WelshFilesDownloadFunction.{awsBucketAndRegion, baseUrl, doDownloadSetup, welshFileLinks}
import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger}
import com.amazonaws.services.s3.AmazonS3ClientBuilder

import java.io.{FileOutputStream, InputStream}

class WelshFilesDownloadFunction {
  def handler(notUsed: String, context: Context): Int = {
    val (bucket, region) = awsBucketAndRegion()

    val (countryNamesLinkText, overseasTerritoriesLinkText, crownDependenciesLinkText) = welshFileLinks()

    val logger = if(Option(context).isEmpty) new LambdaLogger {
      override def log(string: String): Unit = println(string)
    } else context.getLogger

    val (uKCrownDependenciesRetriever, uKOverseasTerritoriesRetriever, countryNamesRetriever) =
      doDownloadSetup(baseUrl, region, bucket, crownDependenciesLinkText, overseasTerritoriesLinkText,
        countryNamesLinkText, logger)
    doWrite(region, bucket, uKCrownDependenciesRetriever, uKOverseasTerritoriesRetriever, countryNamesRetriever)

    0
  }


  private def doWrite(region: String, bucket: String, uKCrownDependenciesRetriever: Retriever, uKOverseasTerritoriesRetriever: Retriever, countryNamesRetriever: Retriever): Unit = {

    val fileWriter = writer(region, bucket)

    fileWriter.write(uKCrownDependenciesRetriever.outputFileName, uKCrownDependenciesRetriever.retrieve())
    fileWriter.write(uKOverseasTerritoriesRetriever.outputFileName, uKOverseasTerritoriesRetriever.retrieve())
    fileWriter.write(countryNamesRetriever.outputFileName, countryNamesRetriever.retrieve())
  }

  protected def writer(region: String, bucket: String): Writer = {
    new Writer(AmazonS3ClientBuilder.standard().withRegion(region).build(), bucket)
  }
}


object WelshFilesDownloadFunction extends App {
  import java.nio.file.{Files, Paths, StandardOpenOption}

  val baseUrl = "https://www.gov.wales/bydtermcymru/international-place-names"

  def awsBucketAndRegion(): (String, String) = {
    val bucket = System.getenv.getOrDefault("BUCKET_NAME", "als-welsh-country-files")
    val region = System.getenv.getOrDefault("AWS_REGION", "eu-west-2")

    (bucket, region)
  }

  def welshFileLinks(): (String, String, String) = {
    val countryNamesLinkText =
      System.getenv.getOrDefault("ALS_COUNTRY_NAMES_LINK_TEXT", "Enwau gwledydd – Country names")
    val overseasTerritoriesLinkText =
      System.getenv.getOrDefault("ALS_OVERSEAS_TERRITORIES_LINK_TEXT", "Enwau Tiriogaethau Tramor y DU – UK Overseas Territories names")
    val crownDependenciesLinkText =
      System.getenv.getOrDefault("ALS_CROWN_DEPENDENCIES_LINK_TEXT", "Enwau Dibyniaethau Coron y DU – UK Crown Dependencies names")

    (countryNamesLinkText, overseasTerritoriesLinkText, crownDependenciesLinkText)
  }

  protected def doDownloadSetup(baseUrl: String, region: String, bucket: String, crownDependenciesLinkText: String, overseasTerritoriesLinkText: String, countryNamesLinkText: String, logger: LambdaLogger): (Retriever, Retriever, Retriever) = {
    val driver = new ExtendedHtmlUnitDriver()
    def baseRetriever = new Retriever(driver, baseUrl, logger, _, _)
    val uKCrownDependenciesRetriever =    baseRetriever(crownDependenciesLinkText, "welsh-crown-dependencies-names.csv")
    val uKOverseasTerritoriesRetriever =  baseRetriever(overseasTerritoriesLinkText, "welsh-overseas-territories-names.csv")
    val countryNamesRetriever =           baseRetriever(countryNamesLinkText, "welsh-country-names.csv")

    (uKCrownDependenciesRetriever, uKOverseasTerritoriesRetriever, countryNamesRetriever)
  }

  class LocalWriter(folder: String) extends Writer(null, folder) {
    override def write(fileName: String, inputStream: Use): Unit = inputStream{ in =>
      val outputStream = new FileOutputStream(fileName)
      in.transferTo(outputStream)
      outputStream.close()
    }
  }

  private val handler = new WelshFilesDownloadFunction(){
    override def writer(region: String, bucket: String): Writer =
      new LocalWriter(bucket)
  }
  handler.handler("", null)
}
