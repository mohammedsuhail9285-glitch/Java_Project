package com.example.lmchecker.repository;

import com.example.lmchecker.entity.ExtractedField;
import com.example.lmchecker.entity.FieldSource;
import com.example.lmchecker.entity.FieldType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtractedFieldRepository extends JpaRepository<ExtractedField, Long> {
    List<ExtractedField> findByProductId(Long productId);
    List<ExtractedField> findByProductIdAndSource(Long productId, FieldSource source);
    Optional<ExtractedField> findFirstByProductIdAndFieldTypeAndSource(Long productId, FieldType fieldType, FieldSource source);
    void deleteByProductId(Long productId);
}
