/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.project;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.rf.ide.core.fileWatcher.IWatchEventHandler;
import org.robotframework.ide.eclipse.main.plugin.model.RobotProject;
import org.robotframework.ide.eclipse.main.plugin.project.RobotProjectConfig.LibraryType;
import org.robotframework.ide.eclipse.main.plugin.project.RobotProjectConfig.ReferencedLibrary;
import org.robotframework.ide.eclipse.main.plugin.project.library.KeywordSpecification;
import org.robotframework.ide.eclipse.main.plugin.project.library.LibrarySpecification;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

public class LibrariesWatchHandlerTest {

    @ClassRule
    public static TemporaryFolder testFolder = new TemporaryFolder();

    private static final String PYTHON_LIBRARY_NAME = "libTest";

    private static final String PYTHON_LIBRARY_FILE_NAME = "libTest.py";

    private static final String PYTHON_MODULE_LIBRARY_NAME = "moduleLib";

    private static final String PYTHON_MODULE_LIBRARY_INIT_FILE_NAME = "__init__.py";

    private static final String PYTHON_MODULE_LIBRARY_FILE_NAME = "moduleClass.py";

    private static final String JAVA_LIBRARY_NAME = "jlibTest";

    private static final String JAVA_LIBRARY_FILE_NAME = "jlibTest.java";

    private static File pythonLibraryFile = null;

    private static File pythonModuleLibraryFolder = null;

    private static File pythonModuleLibraryInitFile = null;

    private static File pythonModuleLibraryFile = null;

    private static File javaLibraryFile = null;

    @BeforeClass
    public static void setUp() throws IOException {
        pythonLibraryFile = testFolder.newFile(PYTHON_LIBRARY_FILE_NAME);
        pythonModuleLibraryFolder = testFolder.newFolder(PYTHON_MODULE_LIBRARY_NAME);
        pythonModuleLibraryInitFile = testFolder
                .newFile(PYTHON_MODULE_LIBRARY_NAME + File.separator + PYTHON_MODULE_LIBRARY_INIT_FILE_NAME);
        pythonModuleLibraryFile = testFolder
                .newFile(PYTHON_MODULE_LIBRARY_NAME + File.separator + PYTHON_MODULE_LIBRARY_FILE_NAME);
        testFolder.newFile(PYTHON_MODULE_LIBRARY_NAME + File.separator + "someFileInModule.txt");
        javaLibraryFile = testFolder.newFile(JAVA_LIBRARY_FILE_NAME);
    }

    @Test
    public void testRegisterPythonLibrary_whenLibraryPathIsNotDirectory() {
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(null);
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary(PYTHON_LIBRARY_NAME,
                pythonLibraryFile.getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec = createNewLibSpec(referencedLibrary);

        librariesWatchHandler.registerLibrary(referencedLibrary, libSpec);

        Map<String, String> registeredPaths = librariesWatchHandler.getRegisteredPaths();
        assertFalse(registeredPaths.isEmpty());
        assertEquals(pythonLibraryFile.getParentFile().getPath(), registeredPaths.get(PYTHON_LIBRARY_FILE_NAME));
        assertEquals(new Path(pythonLibraryFile.getPath()).toPortableString(),
                librariesWatchHandler.getRegisteredRefLibraries().get(referencedLibrary));
        assertEquals(newArrayList(pythonLibraryFile.getName()),
                librariesWatchHandler.getLibrarySpecifications().get(libSpec));
    }

    @Test
    public void testRegisterPythonLibrary_whenLibraryPathIsDirectory() {
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(null);
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary(PYTHON_LIBRARY_NAME,
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec = createNewLibSpec(referencedLibrary);

        librariesWatchHandler.registerLibrary(referencedLibrary, libSpec);

        Map<String, String> registeredPaths = librariesWatchHandler.getRegisteredPaths();
        assertFalse(registeredPaths.isEmpty());
        assertEquals(pythonLibraryFile.getParentFile().getPath(), registeredPaths.get(PYTHON_LIBRARY_FILE_NAME));
        assertEquals(new Path(pythonLibraryFile.getPath()).toPortableString(),
                librariesWatchHandler.getRegisteredRefLibraries().get(referencedLibrary));
        assertEquals(newArrayList(pythonLibraryFile.getName()),
                librariesWatchHandler.getLibrarySpecifications().get(libSpec));
    }

    @Test
    public void testRegisterPythonLibrary_whenMultipleClassesFromLibraryAreUsed() {
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(null);
        ReferencedLibrary referencedLibrary1 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME,
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec1 = createNewLibSpec(referencedLibrary1);
        ReferencedLibrary referencedLibrary2 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME + ".PythonClass1",
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec2 = createNewLibSpec(referencedLibrary2);
        ReferencedLibrary referencedLibrary3 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME + ".PythonClass2",
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec3 = createNewLibSpec(referencedLibrary3);

        librariesWatchHandler.registerLibrary(referencedLibrary1, libSpec1);
        librariesWatchHandler.registerLibrary(referencedLibrary2, libSpec2);
        librariesWatchHandler.registerLibrary(referencedLibrary3, libSpec3);

        Map<String, String> registeredPaths = librariesWatchHandler.getRegisteredPaths();
        assertFalse(registeredPaths.isEmpty());
        assertEquals(pythonLibraryFile.getParentFile().getPath(), registeredPaths.get(PYTHON_LIBRARY_FILE_NAME));
        assertEquals(new Path(pythonLibraryFile.getPath()).toPortableString(),
                librariesWatchHandler.getRegisteredRefLibraries().get(referencedLibrary1));
        assertEquals(new Path(pythonLibraryFile.getPath()).toPortableString(),
                librariesWatchHandler.getRegisteredRefLibraries().get(referencedLibrary2));
        assertEquals(new Path(pythonLibraryFile.getPath()).toPortableString(),
                librariesWatchHandler.getRegisteredRefLibraries().get(referencedLibrary3));
        assertEquals(newArrayList(pythonLibraryFile.getName()),
                librariesWatchHandler.getLibrarySpecifications().get(libSpec1));
        assertEquals(newArrayList(pythonLibraryFile.getName()),
                librariesWatchHandler.getLibrarySpecifications().get(libSpec2));
        assertEquals(newArrayList(pythonLibraryFile.getName()),
                librariesWatchHandler.getLibrarySpecifications().get(libSpec3));
    }

    @Test
    public void testRegisterPythonLibrary_whenLibraryIsModule() {
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(null);
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary(PYTHON_MODULE_LIBRARY_NAME,
                pythonModuleLibraryFolder.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec = createNewLibSpec(referencedLibrary);

        librariesWatchHandler.registerLibrary(referencedLibrary, libSpec);

        Map<String, String> registeredPaths = librariesWatchHandler.getRegisteredPaths();
        assertTrue(registeredPaths.size() == 2);
        assertEquals(pythonModuleLibraryFolder.getPath(), registeredPaths.get(PYTHON_MODULE_LIBRARY_INIT_FILE_NAME));
        assertEquals(pythonModuleLibraryFolder.getPath(), registeredPaths.get(PYTHON_MODULE_LIBRARY_FILE_NAME));
        assertEquals(new Path(pythonModuleLibraryInitFile.getPath()).toPortableString(),
                librariesWatchHandler.getRegisteredRefLibraries().get(referencedLibrary));
        List<String> moduleFilesNames = librariesWatchHandler.getLibrarySpecifications().get(libSpec);
        assertTrue(moduleFilesNames.size() == 2);
        assertTrue(moduleFilesNames.contains(pythonModuleLibraryFile.getName()));
        assertTrue(moduleFilesNames.contains(pythonModuleLibraryInitFile.getName()));
    }

    @Test
    public void testRegisterJavaLibrary() {
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(null);
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary(JAVA_LIBRARY_NAME,
                javaLibraryFile.getParentFile().getPath(), LibraryType.JAVA);
        LibrarySpecification libSpec = createNewLibSpec(referencedLibrary);

        librariesWatchHandler.registerLibrary(referencedLibrary, libSpec);

        Map<String, String> registeredPaths = librariesWatchHandler.getRegisteredPaths();
        assertFalse(registeredPaths.isEmpty());
        assertEquals(javaLibraryFile.getParentFile().getPath(), registeredPaths.get(JAVA_LIBRARY_FILE_NAME));
        assertEquals(new Path(javaLibraryFile.getPath()).toPortableString(),
                librariesWatchHandler.getRegisteredRefLibraries().get(referencedLibrary));
        assertEquals(newArrayList(javaLibraryFile.getName()),
                librariesWatchHandler.getLibrarySpecifications().get(libSpec));
    }

    @Test
    public void testRegisterVirtualLibrary() {
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(null);
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary("virtualLibTest", "", LibraryType.VIRTUAL);

        librariesWatchHandler.registerLibrary(referencedLibrary, new LibrarySpecification());

        assertTrue(librariesWatchHandler.getRegisteredPaths().isEmpty());
        assertTrue(librariesWatchHandler.getRegisteredRefLibraries().isEmpty());
        assertTrue(librariesWatchHandler.getLibrarySpecifications().isEmpty());
    }

    @Test
    public void testRegisterNonExistenceLibrary() {
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(null);
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary("libTest",
                pythonModuleLibraryFolder.getPath() + "/libTest.py", LibraryType.PYTHON);

        librariesWatchHandler.registerLibrary(referencedLibrary, new LibrarySpecification());

        assertTrue(librariesWatchHandler.getRegisteredPaths().isEmpty());
        assertTrue(librariesWatchHandler.getRegisteredRefLibraries().isEmpty());
        assertTrue(librariesWatchHandler.getLibrarySpecifications().isEmpty());
    }

    @Test
    public void testRegisterPythonLibrary_whenTheSameLibSpecWithDifferentKeywordsIsRegistered() {
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(null);
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary(PYTHON_LIBRARY_NAME,
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec = createNewLibSpec(referencedLibrary);

        librariesWatchHandler.registerLibrary(referencedLibrary, libSpec);

        referencedLibrary = createNewReferencedLibrary(PYTHON_LIBRARY_NAME, pythonLibraryFile.getParentFile().getPath(),
                LibraryType.PYTHON);
        libSpec = createNewLibSpec(referencedLibrary);
        KeywordSpecification kwSpec = createNewKeywordSpec("someNewKeyword", newArrayList("arg"));
        libSpec.setKeywords(newArrayList(kwSpec));

        librariesWatchHandler.registerLibrary(referencedLibrary, libSpec);

        Map<String, String> registeredPaths = librariesWatchHandler.getRegisteredPaths();
        assertFalse(registeredPaths.isEmpty());
        assertEquals(pythonLibraryFile.getParentFile().getPath(), registeredPaths.get(PYTHON_LIBRARY_FILE_NAME));
        Map<ReferencedLibrary, String> registeredRefLibraries = librariesWatchHandler.getRegisteredRefLibraries();
        assertTrue(registeredRefLibraries.size() == 1);
        assertEquals(new Path(pythonLibraryFile.getPath()).toPortableString(),
                registeredRefLibraries.get(referencedLibrary));
        ListMultimap<LibrarySpecification, String> librarySpecifications = librariesWatchHandler
                .getLibrarySpecifications();
        assertTrue(librarySpecifications.size() == 1);
        assertEquals(newArrayList(pythonLibraryFile.getName()), librarySpecifications.get(libSpec));
        assertEquals(kwSpec, librarySpecifications.keySet().iterator().next().getKeywords().get(0));
    }

    @Test
    public void testUnregisterLibraries() {
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(null);
        ReferencedLibrary referencedLibrary1 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME + ".PythonClass1",
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec1 = createNewLibSpec(referencedLibrary1);
        librariesWatchHandler.registerLibrary(referencedLibrary1, libSpec1);
        ReferencedLibrary referencedLibrary2 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME + ".PythonClass2",
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec2 = createNewLibSpec(referencedLibrary2);
        librariesWatchHandler.registerLibrary(referencedLibrary2, libSpec2);

        librariesWatchHandler.unregisterLibraries(newArrayList(referencedLibrary1, referencedLibrary2));

        List<String> unregisteredFiles = librariesWatchHandler.getUnregisteredFiles();
        assertTrue(unregisteredFiles.size() == 2);
        assertEquals(pythonLibraryFile.getName(), unregisteredFiles.get(0));
        assertEquals(pythonLibraryFile.getName(), unregisteredFiles.get(1));
        assertTrue(librariesWatchHandler.getRegisteredRefLibraries().isEmpty());
        assertTrue(librariesWatchHandler.getLibrarySpecifications().isEmpty());
    }

    @Test
    public void testUnregisterModuleLibrary() {
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(null);
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary(PYTHON_MODULE_LIBRARY_NAME,
                pythonModuleLibraryFolder.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec = createNewLibSpec(referencedLibrary);
        librariesWatchHandler.registerLibrary(referencedLibrary, libSpec);

        librariesWatchHandler.unregisterLibraries(newArrayList(referencedLibrary));

        List<String> unregisteredFiles = librariesWatchHandler.getUnregisteredFiles();
        assertTrue(unregisteredFiles.size() == 2);
        assertTrue(unregisteredFiles.contains(pythonModuleLibraryFile.getName()));
        assertTrue(unregisteredFiles.contains(pythonModuleLibraryInitFile.getName()));
        assertTrue(librariesWatchHandler.getRegisteredRefLibraries().isEmpty());
        assertTrue(librariesWatchHandler.getLibrarySpecifications().isEmpty());
    }

    @Test
    public void testHandleModifyEvent_whenAutoReloadIsEnabled() {
        IProject project = createNewProjectMock(true);
        RobotProject robotProject = createNewRobotProjectMock(project, true);
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(robotProject);
        ReferencedLibrary referencedLibrary1 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME + ".PythonClass1",
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec1 = createNewLibSpec(referencedLibrary1);
        librariesWatchHandler.registerLibrary(referencedLibrary1, libSpec1);
        ReferencedLibrary referencedLibrary2 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME + ".PythonClass2",
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec2 = createNewLibSpec(referencedLibrary2);
        librariesWatchHandler.registerLibrary(referencedLibrary2, libSpec2);

        librariesWatchHandler.setRebuildTasksQueueSizeBeforeBuilderInvoke(1);
        librariesWatchHandler.handleModifyEvent(PYTHON_LIBRARY_FILE_NAME);

        librariesWatchHandler.execAllAwaitingMessages();
        assertTrue(librariesWatchHandler.getSpecificationsToRebuild().size() == 2);
        assertTrue(librariesWatchHandler.getSpecificationsToRebuild().get(project).contains(libSpec1));
        assertTrue(librariesWatchHandler.getSpecificationsToRebuild().get(project).contains(libSpec2));
        assertTrue(librariesWatchHandler.getRebuildTasksQueueSize() == 0);
    }

    @Test
    public void testHandleModifyEvent_whenAutoReloadIsEnabledAndLibraryIsModule() {
        IProject project = createNewProjectMock(true);
        RobotProject robotProject = createNewRobotProjectMock(project, true);
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(robotProject);
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary(PYTHON_MODULE_LIBRARY_NAME,
                pythonModuleLibraryFolder.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec = createNewLibSpec(referencedLibrary);
        librariesWatchHandler.registerLibrary(referencedLibrary, libSpec);
        librariesWatchHandler.setRebuildTasksQueueSizeBeforeBuilderInvoke(1);

        librariesWatchHandler.handleModifyEvent(PYTHON_MODULE_LIBRARY_INIT_FILE_NAME);

        librariesWatchHandler.execAllAwaitingMessages();
        assertTrue(librariesWatchHandler.getSpecificationsToRebuild().size() == 1);
        assertTrue(librariesWatchHandler.getSpecificationsToRebuild().get(project).contains(libSpec));
        assertTrue(librariesWatchHandler.getRebuildTasksQueueSizeAfterEachBuilderInvoke().equals(newArrayList(1)));
        assertTrue(librariesWatchHandler.getRebuildTasksQueueSize() == 0);

        librariesWatchHandler.getSpecificationsToRebuild().clear();

        librariesWatchHandler.handleModifyEvent(PYTHON_MODULE_LIBRARY_FILE_NAME);

        librariesWatchHandler.execAllAwaitingMessages();
        assertTrue(librariesWatchHandler.getSpecificationsToRebuild().size() == 1);
        assertTrue(librariesWatchHandler.getSpecificationsToRebuild().get(project).contains(libSpec));
        assertTrue(librariesWatchHandler.getRebuildTasksQueueSizeAfterEachBuilderInvoke().equals(newArrayList(1, 1)));
        assertTrue(librariesWatchHandler.getRebuildTasksQueueSize() == 0);
    }

    @Test
    public void testHandleModifyEvent_whenAutoReloadIsEnabledAndDuplicatedTasksAreRemoved() {
        IProject project = createNewProjectMock(true);
        RobotProject robotProject = createNewRobotProjectMock(project, true);
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(robotProject);
        ReferencedLibrary referencedLibrary1 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME,
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec1 = createNewLibSpec(referencedLibrary1);
        librariesWatchHandler.registerLibrary(referencedLibrary1, libSpec1);
        ReferencedLibrary referencedLibrary2 = createNewReferencedLibrary(PYTHON_MODULE_LIBRARY_NAME,
                pythonModuleLibraryFolder.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec2 = createNewLibSpec(referencedLibrary2);
        librariesWatchHandler.registerLibrary(referencedLibrary2, libSpec2);

        librariesWatchHandler.setRebuildTasksQueueSizeBeforeBuilderInvoke(8);
        librariesWatchHandler.handleModifyEvent(PYTHON_MODULE_LIBRARY_FILE_NAME);
        for (int i = 1; i <= 7; i++) {
            librariesWatchHandler.handleModifyEvent(PYTHON_LIBRARY_FILE_NAME);
        }

        librariesWatchHandler.execAllAwaitingMessages();
        assertTrue(librariesWatchHandler.getSpecificationsToRebuild().size() == 2);
        assertTrue(librariesWatchHandler.getSpecificationsToRebuild().get(project).contains(libSpec1));
        assertTrue(librariesWatchHandler.getSpecificationsToRebuild().get(project).contains(libSpec2));
        assertTrue(librariesWatchHandler.getRebuildTasksQueueSizeAfterEachBuilderInvoke().equals(newArrayList(8, 1)));
        assertTrue(librariesWatchHandler.getRebuildTasksQueueSize() == 0);
    }

    @Test
    public void testHandleModifyEvent_whenAutoReloadIsDisabled() {
        ReferencedLibrary referencedLibrary1 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME + ".PythonClass1",
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec1 = createNewLibSpec(referencedLibrary1);
        ReferencedLibrary referencedLibrary2 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME + ".PythonClass2",
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec2 = createNewLibSpec(referencedLibrary2);
        Map<ReferencedLibrary, LibrarySpecification> refLibs = new HashMap<>();
        refLibs.put(referencedLibrary1, libSpec1);
        refLibs.put(referencedLibrary2, libSpec2);
        IProject project = createNewProjectMock(true);
        RobotProject robotProject = createNewRobotProjectMock(project, false, refLibs);
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(robotProject);
        librariesWatchHandler.registerLibrary(referencedLibrary1, libSpec1);
        librariesWatchHandler.registerLibrary(referencedLibrary2, libSpec2);

        librariesWatchHandler.handleModifyEvent(PYTHON_LIBRARY_FILE_NAME);

        librariesWatchHandler.execAllAwaitingMessages();
        assertTrue(librariesWatchHandler.isLibSpecDirty(libSpec1));
        assertTrue(librariesWatchHandler.isLibSpecDirty(libSpec2));
        assertTrue(libSpec1.isModified());
        assertTrue(libSpec2.isModified());
    }

    @Test
    public void testHandleModifyEvent_whenAutoReloadIsDisabledAndDuplicatedEventsAreHandled() {
        ReferencedLibrary referencedLibrary1 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME + ".PythonClass1",
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec1 = createNewLibSpec(referencedLibrary1);
        ReferencedLibrary referencedLibrary2 = createNewReferencedLibrary(PYTHON_LIBRARY_NAME + ".PythonClass2",
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec2 = createNewLibSpec(referencedLibrary2);
        Map<ReferencedLibrary, LibrarySpecification> refLibs = new HashMap<>();
        refLibs.put(referencedLibrary1, libSpec1);
        refLibs.put(referencedLibrary2, libSpec2);
        IProject project = createNewProjectMock(true);
        RobotProject robotProject = createNewRobotProjectMock(project, false, refLibs);
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(robotProject);
        librariesWatchHandler.registerLibrary(referencedLibrary1, libSpec1);
        librariesWatchHandler.registerLibrary(referencedLibrary2, libSpec2);

        for (int i = 1; i <= 5; i++) {
            librariesWatchHandler.handleModifyEvent(PYTHON_LIBRARY_FILE_NAME);
        }

        librariesWatchHandler.execAllAwaitingMessages();
        assertTrue(librariesWatchHandler.isLibSpecDirty(libSpec1));
        assertTrue(librariesWatchHandler.isLibSpecDirty(libSpec2));
        assertTrue(libSpec1.isModified());
        assertTrue(libSpec2.isModified());
        verify(robotProject, times(1)).getReferencedLibraries();
    }

    @Test
    public void testHandleModifyEvent_whenAutoReloadIsDisabledAndLibraryIsModule() {
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary(PYTHON_MODULE_LIBRARY_NAME,
                pythonModuleLibraryFolder.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec = createNewLibSpec(referencedLibrary);
        Map<ReferencedLibrary, LibrarySpecification> refLibs = new HashMap<>();
        refLibs.put(referencedLibrary, libSpec);
        IProject project = createNewProjectMock(true);
        RobotProject robotProject = createNewRobotProjectMock(project, false, refLibs);
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(robotProject);
        librariesWatchHandler.registerLibrary(referencedLibrary, libSpec);

        librariesWatchHandler.handleModifyEvent(PYTHON_MODULE_LIBRARY_INIT_FILE_NAME);
        librariesWatchHandler.handleModifyEvent(PYTHON_MODULE_LIBRARY_FILE_NAME);

        librariesWatchHandler.execAllAwaitingMessages();
        assertTrue(librariesWatchHandler.isLibSpecDirty(libSpec));
        assertTrue(libSpec.isModified());
        verify(robotProject, times(1)).getReferencedLibraries();
    }

    @Test
    public void testRemoveDirtySpecs() {
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary(PYTHON_MODULE_LIBRARY_NAME,
                pythonModuleLibraryFolder.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec = createNewLibSpec(referencedLibrary);
        Map<ReferencedLibrary, LibrarySpecification> refLibs = new HashMap<>();
        refLibs.put(referencedLibrary, libSpec);
        IProject project = createNewProjectMock(true);
        RobotProject robotProject = createNewRobotProjectMock(project, false, refLibs);
        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(robotProject);
        librariesWatchHandler.registerLibrary(referencedLibrary, libSpec);
        librariesWatchHandler.handleModifyEvent(PYTHON_MODULE_LIBRARY_INIT_FILE_NAME);
        librariesWatchHandler.execAllAwaitingMessages();

        assertTrue(librariesWatchHandler.isLibSpecDirty(libSpec));
        assertTrue(libSpec.isModified());

        librariesWatchHandler.removeDirtySpecs(refLibs.values());

        assertFalse(librariesWatchHandler.isLibSpecDirty(libSpec));
    }

    @Test
    public void testHandleModifyEvent_whenProjectNotExists() {
        IProject project = createNewProjectMock(false);
        RobotProject robotProject = createNewRobotProjectMock(project, true);

        DummyLibrariesWatchHandler librariesWatchHandler = new DummyLibrariesWatchHandler(robotProject);
        ReferencedLibrary referencedLibrary = createNewReferencedLibrary(PYTHON_LIBRARY_NAME,
                pythonLibraryFile.getParentFile().getPath(), LibraryType.PYTHON);
        LibrarySpecification libSpec = createNewLibSpec(referencedLibrary);
        librariesWatchHandler.registerLibrary(referencedLibrary, libSpec);

        librariesWatchHandler.handleModifyEvent(PYTHON_LIBRARY_FILE_NAME);

        assertTrue(librariesWatchHandler.getSpecificationsToRebuild().isEmpty());
        assertTrue(librariesWatchHandler.getLibrarySpecifications().isEmpty());
        assertTrue(librariesWatchHandler.getRegisteredRefLibraries().isEmpty());
    }

    private IProject createNewProjectMock(final boolean projectExists) {
        IProject project = mock(IProject.class);
        when(project.exists()).thenReturn(projectExists);
        return project;
    }

    private RobotProject createNewRobotProjectMock(final IProject project, final boolean isAutoReloadEnabled,
            final Map<ReferencedLibrary, LibrarySpecification> referencedLibraries) {
        RobotProject robotProject = createNewRobotProjectMock(project, isAutoReloadEnabled);
        when(robotProject.getReferencedLibraries()).thenReturn(referencedLibraries);
        return robotProject;
    }

    private RobotProject createNewRobotProjectMock(final IProject project, final boolean isAutoReloadEnabled) {
        RobotProject robotProject = mock(RobotProject.class);
        when(robotProject.getProject()).thenReturn(project);
        RobotProjectConfig projectConfig = mock(RobotProjectConfig.class);
        when(projectConfig.isReferencedLibrariesAutoReloadEnabled()).thenReturn(isAutoReloadEnabled);
        when(robotProject.getRobotProjectConfig()).thenReturn(projectConfig);
        return robotProject;
    }

    private ReferencedLibrary createNewReferencedLibrary(final String name, final String path, final LibraryType type) {
        ReferencedLibrary referencedLibrary = new ReferencedLibrary();
        referencedLibrary.setName(name);
        referencedLibrary.setPath(path);
        referencedLibrary.setType(type.name());
        return referencedLibrary;
    }

    private LibrarySpecification createNewLibSpec(final ReferencedLibrary referencedLibrary) {
        return createNewLibSpec(referencedLibrary.getName(), referencedLibrary);
    }

    private LibrarySpecification createNewLibSpec(final String name, final ReferencedLibrary referencedLibrary) {
        final LibrarySpecification libSpec = new LibrarySpecification();
        libSpec.setName(name);
        libSpec.setSecondaryKey("");
        final KeywordSpecification keywordSpec = createNewKeywordSpec("keyword1", newArrayList("arg1"));
        libSpec.setKeywords(newArrayList(keywordSpec));
        libSpec.setReferenced(referencedLibrary);
        libSpec.setRemoteLocation(null);
        libSpec.setIsModified(false);
        return libSpec;
    }

    private KeywordSpecification createNewKeywordSpec(final String name, final List<String> args) {
        final KeywordSpecification keywordSpec = new KeywordSpecification();
        keywordSpec.setName(name);
        keywordSpec.setArguments(args);
        return keywordSpec;
    }

    static class DummyLibrariesWatchHandler extends LibrariesWatchHandler {

        private Map<String, String> registeredPaths = new HashMap<>();

        private List<String> unregisteredFiles = newArrayList();

        private Multimap<IProject, LibrarySpecification> specificationsToRebuild = LinkedHashMultimap.create();

        private List<Integer> rebuildTasksQueueSizeAfterEachBuilderInvoke = newArrayList();

        private int rebuildTasksQueueSizeBeforeBuilderInvoke = 0;

        public DummyLibrariesWatchHandler(RobotProject robotProject) {
            super(robotProject);
        }

        @Override
        public void registerPath(java.nio.file.Path dir, String fileName, IWatchEventHandler handler) {
            registeredPaths.put(fileName, dir.toString());
        }

        @Override
        public void unregisterFile(String fileName, IWatchEventHandler handler) {
            unregisteredFiles.add(fileName);
        }

        @Override
        protected void invokeLibrariesBuilder(IProgressMonitor monitor,
                Multimap<IProject, LibrarySpecification> groupedSpecifications) {
            int sleepCounter = 0;
            while (sleepCounter < 20 && getRebuildTasksQueueSize() < rebuildTasksQueueSizeBeforeBuilderInvoke
                    && specificationsToRebuild.isEmpty()) {
                sleep(100);
                sleepCounter++;
            }
            specificationsToRebuild.putAll(groupedSpecifications);
            rebuildTasksQueueSizeAfterEachBuilderInvoke.add(getRebuildTasksQueueSize());
        }

        public Map<String, String> getRegisteredPaths() {
            return registeredPaths;
        }

        public List<String> getUnregisteredFiles() {
            return unregisteredFiles;
        }

        public Multimap<IProject, LibrarySpecification> getSpecificationsToRebuild() {
            return specificationsToRebuild;
        }

        public void execAllAwaitingMessages() {
            while (Display.getDefault().readAndDispatch()) {
                ;
            }
        }

        private void sleep(final long millis) {
            try {
                Thread.sleep(millis);
            } catch (final InterruptedException e) {
                throw new IllegalStateException("Shouldn't be interrupted!", e);
            }
        }

        public void setRebuildTasksQueueSizeBeforeBuilderInvoke(int rebuildTasksQueueSizeBeforeBuilderInvoke) {
            this.rebuildTasksQueueSizeBeforeBuilderInvoke = rebuildTasksQueueSizeBeforeBuilderInvoke;
        }

        public List<Integer> getRebuildTasksQueueSizeAfterEachBuilderInvoke() {
            return rebuildTasksQueueSizeAfterEachBuilderInvoke;
        }

    }
}
