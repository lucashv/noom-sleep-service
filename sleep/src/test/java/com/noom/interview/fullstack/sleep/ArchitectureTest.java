package com.noom.interview.fullstack.sleep;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    private static final String CONTROLLER_LAYER = "controllerLayer";
    private static final String CONTROLLER_PACKAGE = "..controller..";
    private static final String SERVICE_LAYER = "serviceLayer";
    private static final String SERVICE_PACKAGE = "..service..";
    private static final String REPOSITORY_LAYER = "repositoryLayer";
    private static final String REPOSITORY_PACKAGE = "..repository..";
    private static final String MODEL_LAYER = "modelLayer";
    private static final String MODEL_PACKAGE = "..model..";
    private static final String DTO_LAYER = "dtoLayer";
    private static final String DTO_PACKAGE = "..dto..";
    private static final String SHOULD_FOLLOW_LAYERED_ARCHITECTURE = "Should follow layered architecture";


    @Test
    public void shouldKeepLayeredArchitecture() {
        var mainPackage = this.getClass().getPackageName();
        var importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(mainPackage);

        Architectures.layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer(CONTROLLER_LAYER).definedBy(CONTROLLER_PACKAGE)
                .layer(SERVICE_LAYER).definedBy(SERVICE_PACKAGE)
                .layer(REPOSITORY_LAYER).definedBy(REPOSITORY_PACKAGE)
                .layer(MODEL_LAYER).definedBy(MODEL_PACKAGE)
                .layer(DTO_LAYER).definedBy(DTO_PACKAGE)
                .whereLayer(CONTROLLER_LAYER).mayNotBeAccessedByAnyLayer()
                .whereLayer(CONTROLLER_LAYER).mayOnlyAccessLayers(SERVICE_LAYER, DTO_LAYER, MODEL_LAYER)
                .whereLayer(DTO_LAYER).mayOnlyBeAccessedByLayers(CONTROLLER_LAYER)
                .whereLayer(DTO_LAYER).mayNotAccessAnyLayer()
                .whereLayer(SERVICE_LAYER).mayOnlyBeAccessedByLayers(CONTROLLER_LAYER)
                .whereLayer(SERVICE_LAYER).mayOnlyAccessLayers(REPOSITORY_LAYER, MODEL_LAYER)
                .whereLayer(MODEL_LAYER).mayOnlyBeAccessedByLayers(CONTROLLER_LAYER, SERVICE_LAYER, REPOSITORY_LAYER)
                .whereLayer(MODEL_LAYER).mayNotAccessAnyLayer()
                .whereLayer(REPOSITORY_LAYER).mayOnlyBeAccessedByLayers(SERVICE_LAYER)
                .whereLayer(REPOSITORY_LAYER).mayOnlyAccessLayers(MODEL_LAYER)
                .as(SHOULD_FOLLOW_LAYERED_ARCHITECTURE)
                .check(importedClasses);
    }
}
