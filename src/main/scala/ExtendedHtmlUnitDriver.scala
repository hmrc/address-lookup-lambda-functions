import com.gargoylesoftware.htmlunit.Page
import org.openqa.selenium.htmlunit.HtmlUnitDriver

class ExtendedHtmlUnitDriver extends HtmlUnitDriver {
  def getPage: Page = {
    getWebClient
    getCurrentWindow.lastPage()
  }
}
