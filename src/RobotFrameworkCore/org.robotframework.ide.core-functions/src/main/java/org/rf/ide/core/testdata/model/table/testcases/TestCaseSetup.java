/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.model.table.testcases;

import java.util.ArrayList;
import java.util.List;

import org.rf.ide.core.testdata.model.AKeywordBaseSetting;
import org.rf.ide.core.testdata.model.ModelType;
import org.rf.ide.core.testdata.text.read.IRobotTokenType;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;

public class TestCaseSetup extends AKeywordBaseSetting<TestCase> {

    private static final long serialVersionUID = -8077850245108031679L;

    public TestCaseSetup(RobotToken declaration) {
        super(declaration);
    }

    @Override
    public ModelType getModelType() {
        return ModelType.TEST_CASE_SETUP;
    }

    @Override
    protected List<AKeywordBaseSetting<TestCase>> getAllThisKindSettings() {
        final List<AKeywordBaseSetting<TestCase>> settings = new ArrayList<>(0);
        settings.addAll(getParent().getSetups());

        return settings;
    }

    @Override
    public IRobotTokenType getKeywordNameType() {
        return RobotTokenType.TEST_CASE_SETTING_SETUP_KEYWORD_NAME;
    }

    @Override
    public IRobotTokenType getArgumentType() {
        return RobotTokenType.TEST_CASE_SETTING_SETUP_KEYWORD_ARGUMENT;
    }
}
