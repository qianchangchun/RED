/*
 * Copyright 2016 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.red.nattable.edit;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.robotframework.red.nattable.edit.DetailCellEditorEntriesComposite.MainControlChooser;

/**
 * @author Michal Anglart
 *
 */
class DetailCellEditorComposite<D> extends Composite {

    private final DetailCellEditorEditingSupport<D> editSupport;

    private final Text text;

    private final DetailCellEditorEntriesComposite<D> detailsEntriesComposite;

    private final DetailCellEditorEntriesControlsSwitcher<D> switcher;

    DetailCellEditorComposite(final Composite parent, final DetailCellEditorEditingSupport<D> editSupport) {
        super(parent, SWT.NONE);
        this.editSupport = editSupport;

        setBackground(parent.getBackground());
        GridLayoutFactory.fillDefaults().applyTo(this);

        this.switcher = new DetailCellEditorEntriesControlsSwitcher<>(this, editSupport, new MainControlChooser() {
            @Override
            public void focusMainControl() {
                text.setFocus();
            }
        });
        this.text = createText();
        this.detailsEntriesComposite = switcher.createEntriesPanel();
    }

    private Text createText() {
        final Text text = new Text(this, SWT.SINGLE);
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.keyCode == SWT.ARROW_DOWN) {

                    switcher.selectFirstEntry();
                } else if (e.keyCode == SWT.CR && e.stateMask == 0 && !text.getText().isEmpty()) {
                    editSupport.addNewDetailElement(text.getText());

                    text.setText("");
                    detailsEntriesComposite.refresh();
                }
            }
        });
        text.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(final PaintEvent e) {
                if (text.getText().isEmpty() && !text.isFocusControl()) {
                    final Color current = e.gc.getForeground();
                    e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_GRAY));
                    e.gc.drawString("new entry", 3, 1);
                    e.gc.setForeground(current);
                }
            }
        });
        GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
        return text;
    }

    Text getText() {
        return text;
    }

    void setInput(final int column, final int row) {
        detailsEntriesComposite.setInput(column, row);
    }
}