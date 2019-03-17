/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.spring.mdc;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import org.vaadin.spring.mdc.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.vaadin.spring.mdc.service.UserService;
import org.vaadin.spring.mdc.service.UserServiceImpl;

@StyleSheet("grid-style.css")
@Route("")
public class RootComponent extends Div {

    private RouterLink link;
    private Grid<User> grid;
    private List<User> userList;
    private UserService userService;
    private Grid.Column<User> titleColumn;
    private ListDataProvider<User> dataProvider;
    @Autowired
    public RootComponent(UserService userService) {
        this.userService = userService;
        this.userList = userService.getAllUsers();
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(userList);
        this.grid.setDataProvider(dataProvider);
        this.addGridColumns();
        this.addFiltersAndSortersForTitleField();
        this.addEditorForTitleField();
        add(grid);
    }
    private void addGridColumns() {
        grid.addColumn(User::getId).setHeader("Id").setSortable(true).setResizable(true);
        grid.addColumn(User::getUserId).setHeader("UserId").setSortable(true).setResizable(true);
        this.titleColumn = grid.addColumn(User::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(User::isCompleted).setHeader("Completed").setSortable(true);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
    }




    private void addFiltersAndSortersForTitleField(){
        HeaderRow filterRow = grid.appendHeaderRow();
        TextField titleField = new TextField();
        titleField.addValueChangeListener(event -> dataProvider.addFilter(
                user -> StringUtils.containsIgnoreCase(user.getTitle(),
                        titleField.getValue())));
        titleField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(this.titleColumn).setComponent(titleField);
        titleField.setSizeFull();
        titleField.setPlaceholder("Filter");
    }
    private void addEditorForTitleField() {

        Binder<User> binder = new Binder<>(User.class);
        Editor<User> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField titleField = new TextField();
        binder.bind(titleField, "title");
        titleColumn.setEditorComponent(titleField);

        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());

        Grid.Column<User> editorColumn = grid.addComponentColumn(user -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem(user);
                titleField.focus();
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        editor.addOpenListener(e -> editButtons.stream()
                .forEach((button) -> {
                    button.setEnabled(!editor.isOpen());
                    editor.setBuffered(false);
                }));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach((button) -> {
                    button.setEnabled(!editor.isOpen());
                    editor.setBuffered(true);
                }));

        Button save = new Button("Save", e -> editor.save());
        save.addClassName("save");

        Button cancel = new Button("Cancel", e -> editor.cancel());
        cancel.addClassName("cancel");

        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);
    }

}
