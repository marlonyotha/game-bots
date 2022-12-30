package steam.bot.steambot.service;

import java.time.LocalDateTime;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;
import steam.bot.steambot.automation.CatalogScanAutomation;
import steam.bot.steambot.events.CatalogItemRead;
import steam.bot.steambot.model.CatalogItem;
import steam.bot.steambot.model.LowPrice;
import steam.bot.steambot.repository.CatalogItemRespository;
import steam.bot.steambot.repository.LowPriceRepository;

@Slf4j
@Service
public class CatalogService implements ApplicationListener<CatalogItemRead> {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private WebDriver webDriver;

    @Autowired
    private CatalogItemRespository catalogItemRespository;

    @Autowired
    private LowPriceRepository lowPriceRepository;

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    @EventListener(ApplicationReadyEvent.class)
    public void onAplicationReady() {

        StopWatch watch = new StopWatch();
        try {
            watch.start();
            CatalogScanAutomation script = new CatalogScanAutomation(webDriver, applicationEventPublisher);
            script.scanAll();
            watch.stop();
            log.info("Catalog scan finished in " + watch.getTotalTimeSeconds() + " seconds");
        } catch (Exception e) {
            log.error("Catalog scan failed.", e);
        }
    }

    private void onLowPrice(CatalogItem catalogItem, Double from, Double to) {
        LowPrice lowPrice = new LowPrice();
        lowPrice.setAppKey(catalogItem.getAppKey());
        lowPrice.setCreatedAt(LocalDateTime.now());
        lowPrice.setLink(catalogItem.getLink());
        lowPrice.setPriceFrom(from);
        lowPrice.setPriceTo(to);
        lowPriceRepository.save(lowPrice);

    }

    public CatalogItem save(CatalogItem newCatalogItem) {

        CatalogItem saveCatalogItem = catalogItemRespository.findByAppKey(newCatalogItem.getAppKey());
        if (saveCatalogItem == null) {
            saveCatalogItem = new CatalogItem();
            saveCatalogItem.setCreatedAt(LocalDateTime.now());
        }

        Double newPrice = ConvertPriceFromString(newCatalogItem.getPrice());
        Double oldPrice = ConvertPriceFromString(saveCatalogItem.getPrice());

        // Notify if low price
        if (newPrice < oldPrice) {
            onLowPrice(saveCatalogItem, oldPrice, newPrice);
        }

        saveCatalogItem.setAppKey(newCatalogItem.getAppKey());
        saveCatalogItem.setLink(newCatalogItem.getLink());
        saveCatalogItem.setOldPrice(newCatalogItem.getOldPrice());
        saveCatalogItem.setPrice(newCatalogItem.getPrice());
        saveCatalogItem.setReleaseDate(newCatalogItem.getReleaseDate());
        saveCatalogItem.setSummaryPositive(newCatalogItem.getSummaryPositive());
        saveCatalogItem.setThumbnailImgSrc(newCatalogItem.getThumbnailImgSrc());
        saveCatalogItem.setThumbnailImgSrcSet(newCatalogItem.getThumbnailImgSrcSet());
        saveCatalogItem.setTitle(newCatalogItem.getTitle());
        saveCatalogItem.setCurrentPrice(newPrice);

        // Save min/max prices
        if (saveCatalogItem.getMinPrice() != null && newPrice < saveCatalogItem.getMinPrice()) {
            saveCatalogItem.setMinPrice(newPrice);
        } else if (saveCatalogItem.getMaxPrice() != null && newPrice > saveCatalogItem.getMaxPrice()) {
            saveCatalogItem.setMaxPrice(newPrice);
        } else {
            saveCatalogItem.setMinPrice(newPrice);
            saveCatalogItem.setMaxPrice(newPrice);
        }

        catalogItemRespository.save(saveCatalogItem);
        return saveCatalogItem;

    }

    @Override
    public synchronized void onApplicationEvent(CatalogItemRead catalogItemRead) {
        CatalogItem ci = catalogItemRead.getCatalogItem();
        if (ci == null)
            return;
        save(ci);
    }

    private Double ConvertPriceFromString(String price) {
        price = price.replace(".", ".");
        price = price.replace(",", ".");
        price = price.replace("R$", "");
        price = price.replace(" ", "");
        if ("".equals(price) || price == null) {
            price = "0";
        }
        Double value = 0D;
        try {
            value = Double.parseDouble(price);
        } catch (Exception e) {
            log.info("Convert price '" + price + "' failed. Default=0");
            value = 0D;
        }

        return value;
    }

}
