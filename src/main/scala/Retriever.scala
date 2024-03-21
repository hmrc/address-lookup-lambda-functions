import Retriever.Use
import com.amazonaws.services.lambda.runtime.LambdaLogger
import org.openqa.selenium.{By, WebElement}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

import java.io.InputStream
import java.time.Duration
import scala.util.{Failure, Success, Try, Using}

class Retriever(
                 driver: ExtendedHtmlUnitDriver,
                 baseUrl: String,
                 logger: LambdaLogger,
                 linkText: String,
                 val outputFileName: String
               ) {

  def retrieve(): Use = {
    Using {
      driver.get(baseUrl)

      waitForElementToBeClickable(By.partialLinkText(linkText), s"Could not find link with text '$linkText'.").click()

      driver.getPage.getWebResponse.getContentAsStream
    }(_)
  }

  private def waitForElementToBeClickable(by: By, errorMessage: String): WebElement = {
    Try {
      new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.elementToBeClickable(by))
    } match {
      case Success(element)   ⇒ element
      case Failure(exception) ⇒ logger.log(errorMessage)
        throw exception
    }
  }
}

object Retriever {
  type Use = (InputStream => Unit) => Try[Unit]
}
