package quartztop.analitics.models.actions;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@Table(name = "action")
public class ActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 3900)
    private String content;
    @Column(length = 1000)
    private String description;
    @Column(name = "title_image")
    private String titleImageUrl;
    @Column(name = "start_date")
    private LocalDate startActionDate;
    @Column(name = "end_date")
    private LocalDate endActionDate;
}
