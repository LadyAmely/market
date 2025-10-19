package com.example.market.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class PackageContentTest {

    @ParameterizedTest
    @MethodSource("componentRules")
    @DisplayName("check if annotated components are in correct packages")
    void checkPackageContent(ArchRule rule) {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.example.market");
        rule.check(importedClasses);
    }

    static Stream<ArchRule> componentRules() {
        return Stream.of(
                classes()
                        .that()
                        .areAnnotatedWith(RestController.class)
                        .should()
                        .resideInAPackage("..controller.."),

                classes()
                        .that()
                        .areAnnotatedWith(Service.class)
                        .and().resideOutsideOfPackage("..mapper..")
                        .should()
                        .resideInAPackage("..service.."),

                classes()
                        .that()
                        .areAnnotatedWith(Entity.class)
                        .should()
                        .resideInAPackage("..model..")
        );
    }
}
