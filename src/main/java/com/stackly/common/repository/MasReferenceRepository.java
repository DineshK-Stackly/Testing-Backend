package com.stackly.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stackly.common.entity.MasReference;

public interface MasReferenceRepository extends JpaRepository<MasReference, Long>{

	Optional<MasReference> findByReferenceNameIn(List<String> names);

}
