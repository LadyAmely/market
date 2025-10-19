package com.example.market.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class SpringAnnotationTest {
    private final JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example.market");

    @Test
    @DisplayName("Repository interfaces should extend Spring Data JPA Repository")
    void repositoriesShouldExtendJpaRepository() {
        classes()
                .that().areAnnotatedWith(Repository.class)
                .or().resideInAPackage("..repository..")
                .and().areInterfaces()
                .should().beAssignableTo("org.springframework.data.jpa.repository.JpaRepository")
                .check(importedClasses);
    }
}
