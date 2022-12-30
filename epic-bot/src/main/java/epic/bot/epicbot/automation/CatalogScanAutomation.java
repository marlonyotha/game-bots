package epic.bot.epicbot.automation;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StopWatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import epic.bot.epicbot.events.CatalogItemRead;
import epic.bot.epicbot.model.CatalogItem;

@Slf4j
@RequiredArgsConstructor
public class CatalogScanAutomation {

    protected final WebDriver driver;
    protected final ApplicationEventPublisher applicationEventPublisher;

    public void scanAll() {

        int totalPageDown = 50;
        this.driver.get(
                "https://store.steampowered.com/search/?ignore_preferences=1&supportedlang=brazilian%2Cenglish&category1=998%2C994&os=win&hidef2p=1");

        pageDown(totalPageDown);

        WebElement searchResultsRows = this.driver.findElement(By.id("search_resultsRows"));
        List<WebElement> rows = searchResultsRows.findElements(By.xpath("//*[@id='search_resultsRows']/a[*]"));

        long current = 0;
        long count = rows.size();

        for (long i = current; i < count; i++) {

            log.info("Getting item: " + i + "/" + count);

            try {
                long index = i + 1;
                String xpath = "//*[@id='search_resultsRows']/a[" + index + "]";
                WebElement row = searchResultsRows.findElement(By.xpath(xpath));

                CatalogItem item = rowAdapter(index, row);
                applicationEventPublisher.publishEvent(new CatalogItemRead(this, item));

                if (index >= count) {

                    pageDown(totalPageDown);
                    rows = searchResultsRows.findElements(By.xpath("//*[@id='search_resultsRows']/a[*]"));
                    current = i;
                    count = rows.size();

                }

            } catch (Exception e) {
                log.error("Failed.", e);
            }
        }
    }

    private CatalogItem rowAdapter(long index, WebElement row) {

        StopWatch watch = new StopWatch();
        watch.start();

        CatalogItem item = new CatalogItem();

        // Required
        String appKey = row.getAttribute("data-ds-itemkey");
        item.setAppKey(appKey);

        try {
            String value = row.findElement(By.className("title")).getText();
            item.setTitle(value);

        } catch (Exception e) {
            item.setTitle("-");
        }

        try {
            String xpath = "//*[@id='search_resultsRows']/a[" + index + "]/div[2]/div[4]/div[2]";
            String value = row.findElement(By.xpath(xpath)).getText();

            String[] prices = value.split("\n");
            if (prices.length == 2) {
                item.setOldPrice(prices[0]);
                item.setPrice(prices[1]);
            } else {
                item.setOldPrice("");
                item.setPrice(prices[0]);
            }

        } catch (Exception e) {
            item.setPrice("-");
        }

        try {
            String link = row.getAttribute("href");
            item.setLink(link);

        } catch (Exception e) {
            item.setLink("-");
        }

        try {
            String xpath = "//*[@id='search_resultsRows']/a[" + index + "]/div[2]/div[2]";
            String value = row.findElement(By.xpath(xpath)).getText();
            item.setReleaseDate(value);

        } catch (Exception e) {
            item.setReleaseDate("-");
        }

        try {
            String xpath = "//*[@id='search_resultsRows']/a[" + index + "]/div[2]/div[3]/span";
            String value = row.findElement(By.xpath(xpath)).getText();
            item.setSummaryPositive(value);
        } catch (Exception e) {
            item.setSummaryPositive("");
        }

        try {
            WebElement img = row.findElement(By.cssSelector("img"));
            String src = img.getAttribute("src");
            String srcSet = img.getAttribute("srcset");
            item.setThumbnailImgSrc(src);
            item.setThumbnailImgSrcSet(srcSet);
        } catch (Exception e) {
            item.setThumbnailImgSrc("-");
            item.setThumbnailImgSrcSet("");
        }

        watch.stop();
        log.info(item.getAppKey() + " finished in " + watch.getTotalTimeSeconds() + " seconds");
        return item;
    }

    private void pageDown(int pageCount) {

        for (int i = 0; i < pageCount; i++) {
            Actions at = new Actions(driver);
            at.sendKeys(Keys.PAGE_DOWN).build().perform();
            ThreadSleep(500);
            log.info("Pagedown: " + i + "/" + pageCount + "...");

        }
    }

    private void ThreadSleep(long miliseconds) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.warn("Thread sleep", e);
        }

    }
}
