package fr.lbonade.example.demo.infra.repository;

import fr.lbonade.example.demo.business.entity.MainEntity;
import fr.lbonade.example.demo.business.entity.MainSummaryEntity;
import fr.lbonade.example.demo.business.entity.VersionOnly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaMainEntityRepository extends JpaRepository<MainEntity, String> {

    Page<MainSummaryEntity> findAllProjectedOnEntityLightBy(Pageable pageable);
    Optional<VersionOnly> findVersionById(String id);
}
