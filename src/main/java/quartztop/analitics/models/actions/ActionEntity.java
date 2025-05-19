package quartztop.analitics.models.actions;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.organizationData.Organization;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "is_active")
    private boolean isActive;

    @ManyToMany
    @JoinTable(
            name = "action_organization", // Название промежуточной таблицы
            joinColumns = @JoinColumn(name = "action_id"),  // Связь с таблицей контрагентов
            inverseJoinColumns = @JoinColumn(name = "organization_id") // Связь с таблицей групп
    )
    private List<Organization> actionOrganizationList = new ArrayList<>();
}
