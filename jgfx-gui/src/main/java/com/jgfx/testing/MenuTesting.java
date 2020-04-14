package com.jgfx.testing;

import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.All;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.gui.components.text.FontCmp;
import com.jgfx.gui.components.text.TextCmp;
import com.jgfx.gui.elements.containers.Root;
import com.jgfx.gui.elements.containers.stack.HStack;
import com.jgfx.gui.elements.controls.Image;
import com.jgfx.gui.elements.controls.Text;
import com.jgfx.gui.elements.containers.stack.VStack;
import com.jgfx.gui.enums.Alignment;
import com.jgfx.gui.enums.Colors;
import lombok.SneakyThrows;

import java.io.File;

/**
 * Here we test the menu to make sure things are working
 */
@AutoRegister
public class MenuTesting extends EntitySystem {
    @All({TextCmp.class, FontCmp.class}) Group textElements;
    //The root container
    @In Root root;

    @Override
    public void initialize() {
        createElements();
    }

    /**
     * We create some elements here
     */
    @SneakyThrows
    private void createElements() {
        root.with(
                () -> new VStack(20)
//                        .background(Colors.SUN_FLOWER)
                        .align(Alignment.TOP)
                        .with(

                                () -> new Image("button")
                                        .relScale(0.5f, 0.5f)
                                        .align(Alignment.TRAILING),
                                () -> new HStack(20)
                                        .align(Alignment.CENTER)
                                        .with(
                                                () -> new Image("button")
                                                        .relScale(0.5f, 0.5f),
                                                () -> new Image("button")
                                                        .relScale(0.5f, 0.5f),
                                                () -> new Image("button")
                                                        .relScale(0.5f, 0.5f),
                                                () -> new Image("button")
                                                        .relScale(0.5f, 0.5f)
                                        ),
                                () -> new Image("button")
                                        .relScale(0.5f, 0.5f)
                                        .align(Alignment.TRAILING),
                                () -> new HStack(20)
                                        .align(Alignment.CENTER)
                                        .with(
                                                () -> new Image("button")
                                                        .relScale(0.5f, 0.5f),
                                                () -> new Image("button")
                                                        .relScale(0.5f, 0.5f),
                                                () -> new Image("button")
                                                        .relScale(0.5f, 0.5f),
                                                () -> new Image("button")
                                                        .relScale(0.5f, 0.5f)
                                        )
                        )
        ).build();
    }
}
