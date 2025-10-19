package com.example.market.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class AccessModifierCheckTest {

    JavaClasses importedClasses = new ClassFileImporter()
            .importPackages("com.example.market");

    @ParameterizedTest
    @MethodSource("testClassesProvider")
    @DisplayName("Test classes should not be public")
    void checkTestClassesAreNotPublic(String packageIdentifier) {
        ArchRule rule = classes()
                .that().resideInAPackage(packageIdentifier)
                .and().haveSimpleNameEndingWith("Test")
                .should().bePackagePrivate()
                .allowEmptyShould(true);

        rule.check(importedClasses);
    }

    static Stream<Arguments> testClassesProvider() {
        return Stream.of(
                Arguments.of("..architecture.."),
                Arguments.of("..controller.."),
                Arguments.of("..mapper.."),
                Arguments.of("..service.."),
                Arguments.of("..validator..")
        );
    }
}
