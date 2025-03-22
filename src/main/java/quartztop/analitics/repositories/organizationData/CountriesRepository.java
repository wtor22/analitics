package quartztop.analitics.repositories.organizationData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.organizationData.CountriesEntity;

import java.util.UUID;

@Repository

public interface CountriesRepository extends JpaRepository<CountriesEntity, UUID> {

}
