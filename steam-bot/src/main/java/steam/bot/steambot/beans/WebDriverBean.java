package steam.bot.steambot.beans;

import java.io.File;
import java.io.FileNotFoundException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebDriverBean {

    @Value("${application.chorme-driver-file}")
    private String driverFile;

    @Bean
    public WebDriver webDriver() throws FileNotFoundException {

        log.info("Using chrome driver " + driverFile);

        ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new File(driverFile))
                .build();

        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--no-sandbox"); // Bypass OS security model, MUST BE
        // THE VERY FIRST OPTION
        // options.addArguments("--headless");
        // options.setExperimentalOption("useAutomationExtension", false);
        // options.addArguments("start-maximized"); // open Browser in maximized mode
        // options.addArguments("disable-infobars"); // disabling infobars
        // options.addArguments("--disable-extensions"); // disabling extensions
        // options.addArguments("--disable-gpu"); // applicable to windows os only
        // options.addArguments("--disable-dev-shm-usage"); // overcome limited resource
        // problems
        return new ChromeDriver(service, options);
    }
}
