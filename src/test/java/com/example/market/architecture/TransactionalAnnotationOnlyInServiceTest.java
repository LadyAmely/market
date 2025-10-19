package com.example.market.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

class TransactionalAnnotationOnlyInServiceTest {

    @Test
    void checkTransactionalAnnotationOnlyIsService() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.example.market");
        ArchRule methodLevelTransactional = methods()
                .that().areAnnotatedWith(Transactional.class)
                .should().beDeclaredInClassesThat().areAnnotatedWith(org.springframework.stereotype.Service.class);

        ArchRule compositeRule = CompositeArchRule.of(methodLevelTransactional);
        compositeRule.check(importedClasses);
    }
}
