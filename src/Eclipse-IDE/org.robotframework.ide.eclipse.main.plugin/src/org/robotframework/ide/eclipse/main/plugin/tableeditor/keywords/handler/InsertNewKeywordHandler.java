/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.tableeditor.keywords.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.robotframework.ide.eclipse.main.plugin.model.RobotElement;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordCall;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordDefinition;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordsSection;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSuiteFileSection;
import org.robotframework.ide.eclipse.main.plugin.model.cmd.CreateFreshKeywordCallCommand;
import org.robotframework.ide.eclipse.main.plugin.model.cmd.CreateFreshKeywordDefinitionCommand;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.EditorCommand;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.RobotEditorCommandsStack;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.keywords.handler.InsertNewKeywordHandler.E4InsertNewKeywordHandler;
import org.robotframework.red.commands.DIParameterizedHandler;
import org.robotframework.red.viewers.Selections;

import com.google.common.base.Optional;

public class InsertNewKeywordHandler extends DIParameterizedHandler<E4InsertNewKeywordHandler> {

    public InsertNewKeywordHandler() {
        super(E4InsertNewKeywordHandler.class);
    }

    public static class E4InsertNewKeywordHandler {

        @Inject
        private RobotEditorCommandsStack stack;

        @Execute
        public Object addNewUserDefinedKeyword(@Named(Selections.SELECTION) final IStructuredSelection selection) {
            final Optional<RobotElement> selectedElement = Selections.getOptionalFirstElement(selection, RobotElement.class);

            EditorCommand newKeywordCommand = null;
            RobotKeywordDefinition definition = null;
            if (selectedElement.isPresent()) {
                if (selectedElement.get() instanceof RobotKeywordCall) {
                    definition = (RobotKeywordDefinition) selectedElement.get().getParent();
                    final int index = definition.getChildren().indexOf(selectedElement);
                    if (index >= 0 && index < definition.getChildren().size()) {
                        newKeywordCommand = new CreateFreshKeywordCallCommand(definition, index);
                    }
                } else if (selectedElement.get() instanceof RobotKeywordDefinition) {
                    definition = (RobotKeywordDefinition) selectedElement.get();
                    final RobotSuiteFileSection section = definition.getParent();
                    if (section != null) {
                        final int index = section.getChildren().indexOf(definition);
                        if (index >= 0 && index < section.getChildren().size()) {
                            newKeywordCommand = new CreateFreshKeywordDefinitionCommand((RobotKeywordsSection) section,
                                    index);
                        }
                    }
                }
            }

            if (newKeywordCommand == null || definition == null) {
                return null;
            }

            stack.execute(newKeywordCommand);
            
            return null;
        }
    }
}
