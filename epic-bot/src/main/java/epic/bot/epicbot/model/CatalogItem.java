package epic.bot.epicbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@ToString
@EqualsAndHashCode(callSuper = false)
@Table(indexes = { @Index(name = CatalogItem.UK_BOT_APP_KEY, columnList = "appKey", unique = true) })
public class CatalogItem extends AbstractModel {

    public static final String UK_BOT_APP_KEY = "uk-app-key_cpf";

    @Column(length = 5000)
    private String appKey;

    @Column(length = 5000)
    private String link;

    @Column(length = 5000)
    private String thumbnailImgSrc;

    @Column(length = 5000)
    private String thumbnailImgSrcSet;

    @Column(length = 5000)
    private String title;

    @Column(length = 5000)
    private String releaseDate;

    @Column(length = 5000)
    private String summaryPositive;

    @Column(length = 5000)
    private String oldPrice;

    @Column(length = 5000)
    private String price;

    private Double currentPrice;
    private Double minPrice;
    private Double maxPrice;

}
