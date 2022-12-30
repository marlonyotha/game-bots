package epic.bot.epicbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import epic.bot.epicbot.model.LowPrice;

public interface LowPriceRepository extends JpaRepository<LowPrice, Long> {

}
