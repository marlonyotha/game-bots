package epic.bot.epicbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import epic.bot.epicbot.model.CatalogItem;

public interface CatalogItemRespository extends JpaRepository<CatalogItem, Long> {

    CatalogItem findByAppKey(String apppKey);

}
