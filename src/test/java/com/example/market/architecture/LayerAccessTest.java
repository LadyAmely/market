package com.example.market.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class LayerAccessTest {

    JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example.market");

    @Test
    @DisplayName("Layered architecture should enforce correct layer access rules")
    void checkLayersAccess() {

        Architectures.LayeredArchitecture layeredArchitectureRules = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Persistence").definedBy("..repository..")
                .layer("Model").definedBy("..model..")
                .layer("Mapper").definedBy("..mapper..")
                .layer("DTO").definedBy("..dto..")

                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
                .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service")
                .whereLayer("Model").mayOnlyBeAccessedByLayers(
                        "Controller", "Service", "Persistence", "Mapper", "DTO");

        layeredArchitectureRules.check(importedClasses);
    }
}
