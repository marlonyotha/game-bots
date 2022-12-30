package steam.bot.steambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import steam.bot.steambot.model.CatalogItem;

public interface CatalogItemRespository extends JpaRepository<CatalogItem, Long> {

    CatalogItem findByAppKey(String apppKey);

}
