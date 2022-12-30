package epic.bot.epicbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@ToString
@EqualsAndHashCode(callSuper = false)
public class LowPrice extends AbstractModel {

    @Column(length = 5000)
    private String appKey;

    @Column(length = 5000)
    private String link;

    @Column(length = 5000)
    private String title;

    private Double priceFrom;
    
    private Double priceTo;

}
