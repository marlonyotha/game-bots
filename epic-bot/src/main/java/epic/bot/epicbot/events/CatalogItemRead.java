package epic.bot.epicbot.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import epic.bot.epicbot.model.CatalogItem;

public class CatalogItemRead extends ApplicationEvent {

    @Getter
    private CatalogItem catalogItem;

    public CatalogItemRead(Object source, CatalogItem catalogItem) {
        super(source);
        this.catalogItem = catalogItem;
    }

}
