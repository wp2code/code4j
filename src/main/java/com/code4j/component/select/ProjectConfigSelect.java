package com.code4j.component.select;

import com.code4j.pojo.ProjectCodeConfigInfo;
import com.code4j.util.SQLiteUtil;
import org.apache.commons.collections4.CollectionUtils;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author :lwp
 * @date :Created in 2022-03-14
 */
public class ProjectConfigSelect extends JComboBox {

    public ProjectConfigSelect(Component component, String tableName, BiConsumer<Component, ProjectCodeConfigInfo> function) {
        ProjectConfigSelect projectConfigSelect = this;
        ProjectCodeConfigInfo defaultConfig = new ProjectCodeConfigInfo(tableName);
        this.addItem(defaultConfig);
        this.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                List<ProjectCodeConfigInfo> items = SQLiteUtil.select(new ProjectCodeConfigInfo());
                ProjectCodeConfigInfo selectedItem = (ProjectCodeConfigInfo) projectConfigSelect.getSelectedItem();
                projectConfigSelect.removeAllItems();
                projectConfigSelect.addItem(defaultConfig);
                if (CollectionUtils.isNotEmpty(items)) {
                    for (ProjectCodeConfigInfo item : items) {
                        if (selectedItem.getId() != null && selectedItem.getId().equals(item.getId())) {
                            selectedItem = item;
                        }
                        projectConfigSelect.addItem(item);
                    }
                    projectConfigSelect.setSelectedItem(selectedItem);
                    projectConfigSelect.addItemListener(new SelectItem(component, function));
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
    }

    public class SelectItem implements ItemListener {
        private Component component;
        private BiConsumer<Component, ProjectCodeConfigInfo> function;

        public SelectItem(Component component, BiConsumer<Component, ProjectCodeConfigInfo> function) {
            this.component = component;
            this.function = function;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            Object item = e.getItem();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                function.accept(component, (ProjectCodeConfigInfo) item);
            }
        }
    }
}
