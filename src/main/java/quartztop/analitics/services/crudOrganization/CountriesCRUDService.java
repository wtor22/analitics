package quartztop.analitics.services.crudOrganization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.organizationData.CountriesDTO;
import quartztop.analitics.models.organizationData.CountriesEntity;
import quartztop.analitics.repositories.organizationData.CountriesRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountriesCRUDService {

    private final CountriesRepository countriesRepository;

    public CountriesEntity create(CountriesDTO countriesDTO) {
        return countriesRepository.save(mapToEntity(countriesDTO));
    }

    public Optional<CountriesEntity> getOptionalEntity(CountriesDTO countriesDTO) {
        return countriesRepository.findById(countriesDTO.getId());
    }


    public CountriesEntity mapToEntity(CountriesDTO countries) {
        CountriesEntity countriesEntity = new CountriesEntity();

        countriesEntity.setCode(countries.getCode());
        countriesEntity.setId(countries.getId());
        countriesEntity.setName(countries.getName());
        return countriesEntity;
    }

    public CountriesDTO mapToDTO(CountriesEntity countries) {

        CountriesDTO countriesDTO = new CountriesDTO();
        countriesDTO.setCode(countries.getCode());
        countriesDTO.setId(countries.getId());
        countriesDTO.setName(countries.getName());
        return countriesDTO;
    }
}
