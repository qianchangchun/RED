/*
 * Copyright 2017 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.launch.script;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

public class ScriptRobotLaunchConfigurationShortcut implements ILaunchShortcut2 {

    @Override
    public void launch(final ISelection selection, final String mode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void launch(final IEditorPart editor, final String mode) {
        // TODO Auto-generated method stub

    }

    @Override
    public ILaunchConfiguration[] getLaunchConfigurations(final ISelection selection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ILaunchConfiguration[] getLaunchConfigurations(final IEditorPart editorpart) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IResource getLaunchableResource(final ISelection selection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IResource getLaunchableResource(final IEditorPart editorpart) {
        // TODO Auto-generated method stub
        return null;
    }
}