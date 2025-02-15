package com.code4j.component.dialog;

import com.code4j.component.panel.CommonPanel;
import com.code4j.util.CustomDialogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.CompletableFuture;

/**
 * @author liu_wp
 * @date 2020/11/18
 * @see
 */
public abstract class BaseDialog extends JDialog {
    protected static final Logger log = LoggerFactory.getLogger(BaseDialog.class);
    private String title;
    protected Component parentComponent;
    protected Component contentPanel;
    protected Object extObj;
    protected boolean isUpdate;

    public BaseDialog(Component parentComponent, String title, boolean modal, Object extObj) {
        this(parentComponent, title, modal, extObj, false);
    }

    public BaseDialog(Component parentComponent, String title, boolean modal, Object extObj, boolean isUpdate) {
        super(parentComponent == null ? CustomDialogUtil.getRootFrame() : (Frame) SwingUtilities.windowForComponent(parentComponent));
        this.isUpdate = isUpdate;
        this.parentComponent = parentComponent;
        this.extObj = extObj;
        this.title = title;
        setTitle(title);
        setModal(modal);
        init();
    }

    public void init() {
        beforeInit();
        Box box = Box.createVerticalBox();
        box.add(contentPanel = this.content());
        box.add(this.bottom());
        this.setContentPane(box);
        afterInit();
    }

    public void beforeInit() {

    }

    public void afterInit() {
        this.pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * @return
     */
    protected abstract Component content();

    protected CommonPanel bottom() {
        CommonPanel commonPanel = new CommonPanel();
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                cancelClick();
            }
        });
        JButton okBtn = new JButton("确定");
        okBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                CompletableFuture.runAsync(() -> {
                    okClick();
                }).thenRun(() -> {
                    afterOkClick();
                });
            }
        });
        commonPanel.add(cancelBtn);
        CommonPanel bottomMid = bottomMid();
        if (bottomMid != null) {
            commonPanel.add(bottomMid);
        }
        commonPanel.add(okBtn);
        return commonPanel;
    }

    public CommonPanel bottomMid() {
        return null;
    }

    protected abstract void okClick();

    public void afterOkClick() {

    }

    /**
     *
     */
    public void cancelClick() {
        this.close();
    }

    public void close() {
        this.dispose();
    }

    public Component getContentPanel() {
        return contentPanel;
    }

    public void setContentPanel(final Component contentPanel) {
        this.contentPanel = contentPanel;
    }

    public Object getExtObj() {
        return extObj;
    }

    public void setExtObj(final Object extObj) {
        this.extObj = extObj;
    }

    public Component getParentComponent() {
        return parentComponent;
    }

    public void setParentComponent(final Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }
}
