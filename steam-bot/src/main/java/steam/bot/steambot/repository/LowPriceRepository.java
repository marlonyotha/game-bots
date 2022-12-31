package steam.bot.steambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import steam.bot.steambot.model.LowPrice;

public interface LowPriceRepository extends JpaRepository<LowPrice, Long> {

    LowPrice findByAppKey(String appKey);

}
