package vn.candicode;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vn.candicode.core.StorageService;

@SpringBootApplication
@Log4j2
public class CandicodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CandicodeApplication.class, args);

        log.info("Candicode was running on port 5000");
        log.info("Current home dir {}", System.getProperty("user.home"));
        log.info("Current root dir {}", System.getProperty("user.dir"));
    }

}
